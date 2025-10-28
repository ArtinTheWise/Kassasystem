package org.example.Discount;

import org.example.Money;
import org.example.Product.Product;
import org.example.Product.Quantity;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DiscountAtXTime extends ProductDecorator {
    private final LocalTime startTimeInDay;
    private final LocalTime endTimeInDay;
    private final ProductDecorator discount;

    public DiscountAtXTime(ProductDecorator discount, LocalTime startTimeInDay, LocalTime endTimeInDay){
        super(discount.getProduct(), discount.getStartTime(), discount.getEndTime(), discount.clock);
        if(startTimeInDay.isAfter(endTimeInDay) || startTimeInDay.equals(endTimeInDay)) throw new IllegalArgumentException();

        this.discount = discount;
        this.startTimeInDay = startTimeInDay;
        this.endTimeInDay = endTimeInDay;
    }

    @Override
    public boolean isActive(){
        LocalDateTime now = LocalDateTime.now(clock);
        LocalTime currentTime = now.toLocalTime();

        if(discount.isActive() && !currentTime.isBefore(startTimeInDay) && !currentTime.isAfter(endTimeInDay)) return true;
        return false;
    }

    @Override
    public Money calculatePrice(Quantity quantity) {
        if (isActive()) return discount.calculatePrice(quantity);
        return discount.getProduct().calculatePrice(quantity);
    }

    @Override
    public Money calculatePriceWithVat(Quantity quantity) {
        if (isActive()) return discount.calculatePriceWithVat(quantity);
        return discount.getProduct().calculatePriceWithVat(quantity);
    }

    @Override
    public ProductDecorator createFor(Product product) {
        return new DiscountAtXTime(discount.createFor(product), startTimeInDay, endTimeInDay);
    }
}