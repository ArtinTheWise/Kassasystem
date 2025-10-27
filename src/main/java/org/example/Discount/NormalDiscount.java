package org.example.Discount;

import org.example.Money;
import org.example.Product.Product;
import org.example.Product.ProductGroup;
import org.example.Product.Quantity;

import java.time.Clock;
import java.time.LocalDateTime;

public class NormalDiscount extends ProductDecorator {
    private final int discount;

    public NormalDiscount(Product product, int discount, LocalDateTime endTime){
        this(product, discount, LocalDateTime.now(), endTime, Clock.systemDefaultZone());
    }

    public NormalDiscount(Product product, int discount, LocalDateTime startTime, LocalDateTime endTime){
        this(product, discount, startTime, endTime, Clock.systemDefaultZone());
    }

    public NormalDiscount(Product product, int discount, LocalDateTime startTime, LocalDateTime endTime, Clock clock){
        super(product, startTime, endTime, clock);
        if(discount < 0 || discount > product.calculatePrice(new Quantity(1, product.getPriceModel().getUnit())).getAmountInMinorUnits()) throw new IllegalArgumentException();
        this.discount = discount;
    }

    @Override
    public Money calculatePrice(Quantity q) {
        Money net = getProduct().calculatePrice(q);

        if (!isActive()) return net;
        if (net == null) return null; // undvik NPE

        long amount = net.getAmountInMinorUnits();
        long discounted = Math.max(0L, amount - discount * (long) q.getAmount());

        return new Money(discounted);
    }

    @Override
    public Money calculatePriceWithVat(Quantity q) {
        Money gross = getProduct().calculatePriceWithVat(q);

        if (!isActive()) return gross;
        if (gross == null) return null; // undvik NPE

        long amount = gross.getAmountInMinorUnits();
        long discounted = Math.max(0L, amount - (long) discount);

        return new Money(discounted);
    }

    public ProductGroup discountGroup(ProductGroup productGroup, LocalDateTime startTime, LocalDateTime endTime) {
        ProductGroup discountedProductGroup = new ProductGroup(productGroup.getName());

        for(Product p : productGroup.getProductGroup()){
            discountedProductGroup.addProduct(new NormalDiscount(p, discount, startTime, endTime, clock));
        }

        return discountedProductGroup;
    }

    public ProductGroup discountGroup(ProductGroup productGroup, LocalDateTime endTime){
        return discountGroup(productGroup, LocalDateTime.now(), endTime);
    }

}