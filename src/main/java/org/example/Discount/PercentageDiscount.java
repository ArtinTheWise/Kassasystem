package org.example.Discount;

import org.example.Money;
import org.example.Product.Product;
import org.example.Product.ProductGroup;
import org.example.Product.Quantity;

import java.time.Clock;
import java.time.LocalDateTime;

public class PercentageDiscount extends ProductDecorator {
    private final int percent;

    public PercentageDiscount(Product product, int percent, LocalDateTime endTime){
        this(product, percent, LocalDateTime.now(), endTime, Clock.systemDefaultZone());
    }

    public PercentageDiscount(Product product, int percent, LocalDateTime startTime, LocalDateTime endTime){
        this(product, percent, startTime, endTime, Clock.systemDefaultZone());
    }

    public PercentageDiscount(Product product, int percent, LocalDateTime startTime, LocalDateTime endTime, Clock clock){
        super(product, startTime, endTime, clock);
        if(percent < 0 || percent > 100) throw new IllegalArgumentException();
        this.percent = percent;
    }

    @Override
    public Money calculatePrice(Quantity q) {
        Money net = getProduct().calculatePrice(q);

        if (!isActive()) return net;
        if (net == null) return null; // undvik null pointer excp.

        long discounted = Math.round(net.getAmountInMinorUnits() * (1 - (percent / 100.0)));
        return new Money(discounted);
    }

    @Override
    public Money calculatePriceWithVat(Quantity q) {
        if (!isActive()) return getProduct().calculatePriceWithVat(q);
        Money gross = getProduct().calculatePriceWithVat(q);
        if (gross == null) return null; 
        long discounted = Math.round(gross.getAmountInMinorUnits() * (1 - (percent / 100.0)));

        return new Money(discounted);
    }

    public ProductGroup discountGroup(ProductGroup productGroup, LocalDateTime startTime, LocalDateTime endTime) {
        ProductGroup discountedProductGroup = new ProductGroup(productGroup.getName());

        for(Product p : productGroup.getProductGroup()){
            discountedProductGroup.addProduct(new PercentageDiscount(p, percent, startTime, endTime, clock));
        }

        return discountedProductGroup;
    }

    public ProductGroup discountGroup(ProductGroup productGroup, LocalDateTime endTime){
        return discountGroup(productGroup, LocalDateTime.now(), endTime);
    }
}
