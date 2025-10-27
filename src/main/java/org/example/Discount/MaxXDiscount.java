package org.example.Discount;

import org.example.Membership.Customer;
import org.example.Money;
import org.example.Product.Quantity;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class MaxXDiscount extends ProductDecorator {
    private final int max;
    private final ProductDecorator discount;

    public MaxXDiscount(ProductDecorator discount, int max){
        super(discount.getProduct(), discount.getStartTime(), discount.getEndTime(), discount.clock);
        if(max < 1) throw new IllegalArgumentException();
        this.max = max;
        this.discount = discount;
    }

    @Override
    public Money calculatePrice(Quantity quantity) {
        if (isActive()) {
            if((int) quantity.getAmount() > max){
                int notDiscountedAmount = (int) quantity.getAmount() - max;
                long discounted = discount.calculatePrice(new Quantity(max, quantity.getUnit())).getAmountInMinorUnits();
                long notDiscounted = discount.getProduct().calculatePrice(new Quantity(notDiscountedAmount, quantity.getUnit())).getAmountInMinorUnits();
                return new Money(discounted+notDiscounted);
            }
            return discount.calculatePrice(quantity);
        }
        return discount.getProduct().calculatePrice(quantity);
    }

    @Override
    public Money calculatePriceWithVat(Quantity quantity) {
        if (isActive()) {
            if((int) quantity.getAmount() > max){
                int notDiscountedAmount = (int) quantity.getAmount() - max;
                long discounted = discount.calculatePriceWithVat(new Quantity(max, quantity.getUnit())).getAmountInMinorUnits();
                long notDiscounted = discount.getProduct().calculatePriceWithVat(new Quantity(notDiscountedAmount, quantity.getUnit())).getAmountInMinorUnits();
                return new Money(discounted+notDiscounted);
            }
            return discount.calculatePriceWithVat(quantity);
        }
        return discount.getProduct().calculatePriceWithVat(quantity);
    }

}