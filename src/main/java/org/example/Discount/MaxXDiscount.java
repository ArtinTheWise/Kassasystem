package org.example.Discount;

import org.example.Money;
import org.example.Product.Product;
import org.example.Product.Quantity;

public class MaxXDiscount extends ProductDecorator {
    private final int max; //Max amount discount can be applied
    private final ProductDecorator discountType;

    public MaxXDiscount(ProductDecorator discountType, int max){
        super(discountType.getProduct(), discountType.getStartTime(), discountType.getEndTime(), discountType.clock);
        if(max < 1) throw new IllegalArgumentException();
        if (max == Integer.MAX_VALUE) throw new IllegalArgumentException("max must be less than Integer.MAX_VALUE");
        this.max = max;
        this.discountType = discountType;
    }

    @Override
    public Money calculatePrice(Quantity quantity) {
        if (isActive()) {
            if((int) quantity.getAmount() > max){
                int notDiscountedAmount = (int) quantity.getAmount() - max;
                long discounted = discountType.calculatePrice(new Quantity(max, quantity.getUnit())).getAmountInMinorUnits();
                long notDiscounted = discountType.getProduct().calculatePrice(new Quantity(notDiscountedAmount, quantity.getUnit())).getAmountInMinorUnits();
                return new Money(discounted+notDiscounted);
            }
            return discountType.calculatePrice(quantity);
        }
        return discountType.getProduct().calculatePrice(quantity);
    }

    @Override
    public Money calculatePriceWithVat(Quantity quantity) {
        if (isActive()) {
            if((int) quantity.getAmount() > max){
                int notDiscountedAmount = (int) quantity.getAmount() - max;
                long discounted = discountType.calculatePriceWithVat(new Quantity(max, quantity.getUnit())).getAmountInMinorUnits();
                long notDiscounted = discountType.getProduct().calculatePriceWithVat(new Quantity(notDiscountedAmount, quantity.getUnit())).getAmountInMinorUnits();
                return new Money(discounted+notDiscounted);
            }
            return discountType.calculatePriceWithVat(quantity);
        }
        return discountType.getProduct().calculatePriceWithVat(quantity);
    }

    @Override
    public ProductDecorator createFor(Product product) {
        return new MaxXDiscount(discountType.createFor(product), max);
    }
}