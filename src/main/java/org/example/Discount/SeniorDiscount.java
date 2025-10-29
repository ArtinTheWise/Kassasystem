package org.example.Discount;

import org.example.Membership.Customer;
import org.example.Money;
import org.example.Product.Product;
import org.example.Product.Quantity;

public class SeniorDiscount extends ProductDecorator {
    private final int age;
    private final ProductDecorator discountType;

    public SeniorDiscount(ProductDecorator discountType, int age){
        super(discountType.getProduct(), discountType.getStartTime(), discountType.getEndTime(), discountType.clock);
        if(age > 120 || age < 0) throw new IllegalArgumentException();
        this.age = age;
        this.discountType = discountType;
    }

    @Override
    public Money calculatePrice(Quantity q) {
        return discountType.getProduct().calculatePrice(q);
    }

    @Override
    public Money calculatePriceWithVat(Quantity q) {
        return discountType.getProduct().calculatePriceWithVat(q);
    }

    @Override
    public Money calculatePrice(Quantity q, Customer c) {
        if(!isActive()) return discountType.getProduct().calculatePrice(q);
        if(c.getAge() >= age){
            return discountType.calculatePrice(q);
        }
        return discountType.getProduct().calculatePrice(q);
    }

    @Override
    public Money calculatePriceWithVat(Quantity q, Customer c) {
        if(!isActive()) return discountType.getProduct().calculatePriceWithVat(q);
        if(c.getAge() >= age){
            return discountType.calculatePriceWithVat(q);
        }
        return discountType.getProduct().calculatePriceWithVat(q);
    }

    @Override
    public ProductDecorator createFor(Product product) {
        return new SeniorDiscount(discountType.createFor(product), age);
    }

}
