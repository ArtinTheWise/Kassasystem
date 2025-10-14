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
    public Money calculatePrice(Quantity quantity){
        if(isActive()){
            int whatToDiscount = (int) quantity.getAmount() / 3;
            return getProduct().calculatePrice(new Quantity(quantity.getAmount() - whatToDiscount, quantity.getUnit()));
        }
        return getProduct().calculatePrice(quantity);
    }
}
