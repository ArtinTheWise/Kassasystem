package org.example.Discount;

import org.example.Membership.Customer;
import org.example.Money;
import org.example.Product.Quantity;

public class SpecialDiscount extends ProductDecorator {
    private final int age;
    private final boolean student;
    private final ProductDecorator discount;

    public SpecialDiscount(ProductDecorator discount, boolean student){
        this(discount, 18, student);
    }

    public SpecialDiscount(ProductDecorator discount, int age){
        this(discount, age, false);
    }

    public SpecialDiscount(ProductDecorator discount, int age, boolean student){
        super(discount.getProduct(), discount.getStartTime(), discount.getEndTime(), discount.clock);
        if(age > 100 || age < 0) throw new IllegalArgumentException();
        this.age = age;
        this.student = student;
        this.discount = discount;
    }

    @Override
    public Money calculatePrice(Quantity q) {
        return getProduct().calculatePrice(q);
    }

    @Override
    public Money calculatePriceWithVat(Quantity q) {
        return getProduct().calculatePriceWithVat(q);
    }

    @Override
    public Money calculatePrice(Quantity q, Customer c) {
        if(!isActive()) return getProduct().calculatePrice(q);
        if((c.isStudent() && student) || (c.getAge() >= age && age >= 65)){
            return discount.calculatePrice(q);
        }
        return getProduct().calculatePrice(q);
    }

    @Override
    public Money calculatePriceWithVat(Quantity q, Customer c) {
        if(!isActive()) return getProduct().calculatePriceWithVat(q);
        if((c.isStudent() && student) || (c.getAge() >= age && age >= 65)){
            return discount.calculatePriceWithVat(q);
        }
        return getProduct().calculatePriceWithVat(q);
    }

}
