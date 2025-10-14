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
    public Money calculatePrice(Quantity quantity){
        if(isActive()){
            long discounted = Math.round(getProduct().calculatePrice(quantity).getAmountInMinorUnits() * (1-(percent/100.0)));
            return new Money(discounted);
        }
        return getProduct().calculatePrice(quantity);
    }

    public static ProductGroup discountGroup(ProductGroup productGroup, int discount, LocalDateTime startTime, LocalDateTime endTime) {
        ProductGroup discountedProductGroup = new ProductGroup(productGroup.getName(), productGroup.getVatGroup());

        for(Product p : productGroup.getProductGroup()){
            discountedProductGroup.addProduct(new PercentageDiscount(p, discount, startTime, endTime));
        }

        return discountedProductGroup;
    }

    public static ProductGroup discountGroup(ProductGroup productGroup, int discount, LocalDateTime endTime){
        return discountGroup(productGroup, discount, LocalDateTime.now(), endTime);
    }
}
