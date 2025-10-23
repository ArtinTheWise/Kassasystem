package org.example.Discount;

import org.example.Money;
import org.example.Product.Product;
import org.example.Product.ProductGroup;
import org.example.Product.Quantity;

import java.time.LocalDateTime;

public class PercentageDiscount extends ProductDecorator {
    private final int percent;

    public PercentageDiscount(Product product, int percent, LocalDateTime endTime){
        this(product, percent, LocalDateTime.now(), endTime);
    }

    public PercentageDiscount(Product product, int percent, LocalDateTime startTime, LocalDateTime endTime){
        super(product, startTime, endTime);
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

    public static ProductGroup discountGroup(ProductGroup productGroup, int discount, LocalDateTime startTime, LocalDateTime endTime) {
        ProductGroup discountedProductGroup = new ProductGroup(productGroup.getName());

        for(Product p : productGroup.getProductGroup()){
            discountedProductGroup.addProduct(new PercentageDiscount(p, discount, startTime, endTime));
        }

        return discountedProductGroup;
    }

    public static ProductGroup discountGroup(ProductGroup productGroup, int discount, LocalDateTime endTime){
        return discountGroup(productGroup, discount, LocalDateTime.now(), endTime);
    }
}
