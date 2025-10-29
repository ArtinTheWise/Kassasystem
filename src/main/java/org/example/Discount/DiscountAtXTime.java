package org.example.Discount;

import org.example.Money;
import org.example.Product.Product;
import org.example.Product.Quantity;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DiscountAtXTime extends ProductDecorator {
    private final LocalTime startTimeInDay;
    private final LocalTime endTimeInDay;
    private final ProductDecorator discountType;

    public DiscountAtXTime(ProductDecorator discountType, LocalTime startTimeInDay, LocalTime endTimeInDay){
        super(discountType.getProduct(), discountType.getStartTime(), discountType.getEndTime(), discountType.clock);
        if(startTimeInDay.isAfter(endTimeInDay) || startTimeInDay.equals(endTimeInDay)) throw new IllegalArgumentException();

        this.discountType = discountType;
        this.startTimeInDay = startTimeInDay;
        this.endTimeInDay = endTimeInDay;
    }

    @Override
    public boolean isActive(){
        LocalDateTime now = LocalDateTime.now(clock);
        LocalTime currentTime = now.toLocalTime();

        if(discountType.isActive() && !currentTime.isBefore(startTimeInDay) && !currentTime.isAfter(endTimeInDay)) return true;
        return false;
    }

    @Override
    public Money calculatePrice(Quantity quantity) {
        if (isActive()) return discountType.calculatePrice(quantity);
        return discountType.getProduct().calculatePrice(quantity);
    }

    @Override
    public Money calculatePriceWithVat(Quantity quantity) {
        if (isActive()) return discountType.calculatePriceWithVat(quantity);
        return discountType.getProduct().calculatePriceWithVat(quantity);
    }

    @Override
    public ProductDecorator createFor(Product product) {
        return new DiscountAtXTime(discountType.createFor(product), startTimeInDay, endTimeInDay);
    }
}