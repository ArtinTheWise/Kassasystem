package org.example.Discount;

import org.example.Membership.Customer;
import org.example.Money;
import org.example.Product.Product;
import org.example.Product.Quantity;

public class StudentDiscount extends ProductDecorator {
    private final ProductDecorator discountType;

    public StudentDiscount(ProductDecorator discountType){
        super(discountType.getProduct(), discountType.getStartTime(), discountType.getEndTime(), discountType.clock);
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
        if(c.isStudent()){
            return discountType.calculatePrice(q);
        }
        return discountType.getProduct().calculatePrice(q);
    }

    @Override
    public Money calculatePriceWithVat(Quantity q, Customer c) {
        if(!isActive()) return discountType.getProduct().calculatePriceWithVat(q);
        if(c.isStudent()){
            return discountType.calculatePriceWithVat(q);
        }
        return discountType.getProduct().calculatePriceWithVat(q);
    }

    @Override
    public ProductDecorator createFor(Product product) {
        return new StudentDiscount(discountType.createFor(product));
    }

}
