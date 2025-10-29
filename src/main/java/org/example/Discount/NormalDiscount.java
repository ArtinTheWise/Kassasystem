package org.example.Discount;

import org.example.Money;
import org.example.Product.Product;
import org.example.Product.Quantity;

import java.time.Clock;
import java.time.LocalDateTime;

public class NormalDiscount extends ProductDecorator {
    private final int discount;

    public NormalDiscount(Product product, int discount, LocalDateTime endTime){
        this(product, discount, LocalDateTime.now(), endTime, Clock.systemDefaultZone());
    }

    public NormalDiscount(Product product, int discount, LocalDateTime startTime, LocalDateTime endTime){
        this(product, discount, startTime, endTime, Clock.systemDefaultZone());
    }

    public NormalDiscount(Product product, int discount, LocalDateTime startTime, LocalDateTime endTime, Clock clock){
        super(product, startTime, endTime, clock);
        if(discount < 0 || discount > product.calculatePrice(new Quantity(1, product.getPriceModel().getUnit())).getAmountInMinorUnits()) throw new IllegalArgumentException();
        this.discount = discount;
    }

    @Override
    public Money calculatePrice(Quantity q) {
        if (!isActive()) return getProduct().calculatePrice(q);

        long discounted = Math.max(0L, getProduct().calculatePrice(q).getAmountInMinorUnits() - discount * (long) q.getAmount());
        return new Money(discounted);
    }

    @Override
    public Money calculatePriceWithVat(Quantity q) {
        if (!isActive()) return getProduct().calculatePriceWithVat(q);

        long discounted = Math.max(0L, getProduct().calculatePriceWithVat(q).getAmountInMinorUnits() - discount * (long) q.getAmount());
        return new Money(discounted);
    }

    @Override
    public ProductDecorator createFor(Product product) {
        return new NormalDiscount(product, discount, getStartTime(), getEndTime(), clock);
    }
}