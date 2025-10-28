package org.example.Discount;

import org.example.Membership.Customer;
import org.example.Money;
import org.example.Product.Product;
import org.example.Product.Quantity;

import java.util.Map;

public class OverXTotalDiscount extends ProductDecorator {
    private final ProductDecorator discount;
    private final Money money;

    public OverXTotalDiscount(ProductDecorator discount, Money money){
        super(discount.getProduct(), discount.getStartTime(), discount.getEndTime(), discount.clock);
        this.discount = discount;
        this.money = money;
    }

    @Override
    public Money calculatePrice(Quantity q) {
        if(!isActive() || !discount.isActive()) return discount.getProduct().calculatePrice(q);
        if(discount.getProduct().calculatePrice(q).compareTo(money) > 0) return discount.calculatePrice(q);
        return discount.getProduct().calculatePrice(q);
    }

    @Override
    public Money calculatePriceWithVat(Quantity q) {
        if(!isActive() || !discount.isActive()) return discount.getProduct().calculatePriceWithVat(q);
        if(discount.getProduct().calculatePriceWithVat(q).compareTo(money) > 0) return discount.calculatePriceWithVat(q);
        return discount.getProduct().calculatePriceWithVat(q);
    }

    public Money calculatePrice(Map<Product, Quantity> items) {
        return new Money(0);
    }

    public Money calculatePriceWithVat(Map<Product, Quantity> items) {
        return new Money(0);
    }
}
