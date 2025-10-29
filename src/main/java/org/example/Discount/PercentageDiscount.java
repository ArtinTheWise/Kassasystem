package org.example.Discount;

import org.example.Money;
import org.example.Product.Product;
import org.example.Product.Quantity;

import java.time.Clock;
import java.time.LocalDateTime;

public class PercentageDiscount extends ProductDecorator {
    private final int percent;

    public PercentageDiscount(Product product, int percent, LocalDateTime endTime){
        this(product, percent, LocalDateTime.now(), endTime, Clock.systemDefaultZone());
    }

    public PercentageDiscount(Product product, int percent, LocalDateTime startTime, LocalDateTime endTime){
        this(product, percent, startTime, endTime, Clock.systemDefaultZone());
    }

    public PercentageDiscount(Product product, int percent, LocalDateTime startTime, LocalDateTime endTime, Clock clock){
        super(product, startTime, endTime, clock);
        if(percent < 0 || percent > 100) throw new IllegalArgumentException();
        this.percent = percent;
    }

    @Override
    public Money calculatePrice(Quantity q) {
        if (!isActive()) return getProduct().calculatePrice(q);

        long discounted = Math.round(getProduct().calculatePrice(q).getAmountInMinorUnits() * (1 - (percent / 100.0)));
        return new Money(discounted);
    }

    @Override
    public Money calculatePriceWithVat(Quantity q) {
        if (!isActive()) return getProduct().calculatePriceWithVat(q);

        long discounted = Math.round(getProduct().calculatePriceWithVat(q).getAmountInMinorUnits() * (1 - (percent / 100.0)));
        return new Money(discounted);
    }

    @Override
    public ProductDecorator createFor(Product product) {
        return new PercentageDiscount(product, percent, getStartTime(), getEndTime(), clock);
    }
}
