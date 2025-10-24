package org.example.Discount;

import org.example.Money;
import org.example.Product.Product;
import org.example.Product.Quantity;
import org.example.Product.WeightPrice;

import java.time.LocalDateTime;

public class ThreeForTwoDiscount extends ProductDecorator{

    public ThreeForTwoDiscount(Product product, LocalDateTime endTime){
        this(product, LocalDateTime.now(), endTime);
    }

    public ThreeForTwoDiscount(Product product, LocalDateTime startTime, LocalDateTime endTime){
        super(product, startTime, endTime);
        if(product.getPriceModel() instanceof WeightPrice) throw new IllegalArgumentException();
    }

    @Override
    public Money calculatePrice(Quantity q) {
        if (!isActive()) return getProduct().calculatePrice(q);
        return calculatePriceForQuantity(q, false);
    }

    @Override
    public Money calculatePriceWithVat(Quantity q) {
        if (!isActive()) return getProduct().calculatePriceWithVat(q);
        return calculatePriceForQuantity(q, true);
    }

    private Money calculatePriceForQuantity(Quantity q, boolean withVat) {
        double amount = q.getAmount();
        long free = (long) Math.floor(amount / 3);
        double chargedAmount = Math.max(0d, amount - free);
        Quantity charged = new Quantity(chargedAmount, q.getUnit());

        return withVat ? getProduct().calculatePriceWithVat(charged) : getProduct().calculatePrice(charged);
    }
}
