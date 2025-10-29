package org.example.Discount;

import org.example.Money;
import org.example.Product.Product;
import org.example.Product.Quantity;
import java.time.Clock;
import java.time.LocalDateTime;

import static org.example.Product.Unit.PIECE;

public class ThreeForTwoDiscount extends ProductDecorator {

    public ThreeForTwoDiscount(Product product, LocalDateTime endTime){
        this(product, LocalDateTime.now(), endTime, Clock.systemDefaultZone());
    }

    public ThreeForTwoDiscount(Product product, LocalDateTime startTime, LocalDateTime endTime){
        this(product, startTime, endTime, Clock.systemDefaultZone());
    }

    public ThreeForTwoDiscount(Product product, LocalDateTime startTime, LocalDateTime endTime, Clock clock){
        super(product, startTime, endTime, clock);
        if(!(product.getPriceModel().getUnit() == PIECE)) throw new IllegalArgumentException();
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
        long freeQuantity = (long) Math.floor(amount / 3);
        double chargedAmount = Math.max(0d, amount - freeQuantity);
        Quantity charged = new Quantity(chargedAmount, q.getUnit());

        return withVat ? getProduct().calculatePriceWithVat(charged) : getProduct().calculatePrice(charged);
    }

    @Override
    public ProductDecorator createFor(Product product) {
        return new ThreeForTwoDiscount(product, getStartTime(), getEndTime(), clock);
    }
}
