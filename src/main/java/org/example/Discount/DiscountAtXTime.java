package org.example.Discount;

import org.example.Money;
import org.example.Product.Product;
import org.example.Product.Quantity;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class DiscountAtXTime extends ProductDecorator {
    private LocalTime startTimeInDay;
    private LocalTime endTimeInDay;
    private ProductDecorator discount;

    public DiscountAtXTime(ProductDecorator product, LocalTime startTimeInDay, LocalTime endTimeInDay){
        super(product, product.getStartTime(), product.getEndTime());
        if(startTimeInDay.isAfter(endTimeInDay) || startTimeInDay.equals(endTimeInDay)) throw new IllegalArgumentException();

        discount = product;
        this.startTimeInDay = startTimeInDay;
        this.endTimeInDay = endTimeInDay;
    }

    @Override
    public boolean isActive(){
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();

        if(discount.isActive() && !currentTime.isBefore(startTimeInDay) && !currentTime.isAfter(endTimeInDay)) return true;
        return false;
    }

    @Override
    public Money calculatePrice(Quantity quantity) {
        return getProduct().calculatePrice(quantity);
    }

    @Override
    public Money calculatePriceWithVat(Quantity quantity) {
        return getProduct().calculatePriceWithVat(quantity);
    }

}
