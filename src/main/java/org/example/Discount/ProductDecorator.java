package org.example.Discount;

import org.example.Money;
import org.example.Product.Product;
import org.example.Product.ProductGroup;
import org.example.Product.Quantity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class ProductDecorator extends Product {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Product product;

    public ProductDecorator(Product product, LocalDateTime startTime, LocalDateTime endTime){
        super(product.getName(), product.getPriceModel(), product.getProductGroup(), product.getVatRate(), product.getAgeRestriction());
        if(product == null) throw new NullPointerException();
        if (startTime == null || endTime == null) {throw new IllegalArgumentException("Start time and end time can't be null.");}
        if (endTime.isBefore(startTime)) {throw new IllegalArgumentException("Start time must be before end time.");}

        this.startTime = startTime;
        this.endTime = endTime;
        this.product = product;
    }

    public boolean isActive(){
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(endTime) && (now.isAfter(startTime) || now.isEqual(startTime));
    }

    @Override
    public String getName(){
        if(isActive()){
            return product.getName() + " got a discount.";
        }
        return product.getName();
    }

    public LocalDateTime getStartTime(){
        return startTime;
    }

    public LocalDateTime getEndTime(){
        return endTime;
    }

    public Product getProduct(){
        return product;
    }

    public abstract Money calculatePrice(Quantity quantity);

    public abstract Money calculatePriceWithVat(Quantity quantity);

    public Money getDiscountedAmount(Quantity quantity){
        return new Money(getProduct().calculatePriceWithVat(quantity).getAmountInMinorUnits() - calculatePriceWithVat(quantity).getAmountInMinorUnits());
    }
}
