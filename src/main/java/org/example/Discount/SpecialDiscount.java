package org.example.Discount;

import org.example.Money;
import org.example.Product.Quantity;

public class SpecialDiscount extends ProductDecorator{
    private final int age;
    private final boolean student;

    public SpecialDiscount(ProductDecorator discount, int age, boolean student){
        super(discount.getProduct(), discount.getStartTime(), discount.getEndTime(), discount.clock);
        this.age = age;
        this.student = student;
    }

    @Override
    public Money calculatePrice(Quantity q) {
        return new Money(0);
    }

    @Override
    public Money calculatePriceWithVat(Quantity q) {
        return new Money(0);
    }
}
