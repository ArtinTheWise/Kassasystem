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

        double amount = q.getAmount();
        long free = (long) Math.floor(amount) / 3;
        double chargedAmount = Math.max(0d, amount - free);
        Quantity charged = new Quantity(chargedAmount, q.getUnit());

        Money net = getProduct().calculatePrice(charged);
        if (net == null) return null; // undvik NPE
        return net;
}

    @Override
    public Money calculatePriceWithVat(Quantity q) {
        if (!isActive()) return getProduct().calculatePriceWithVat(q);

        double amount = q.getAmount();
        long wholeItems = (long) Math.floor(amount); 
        long free = wholeItems / 3;                  
        double chargedAmount = Math.max(0d, amount - free);

        Quantity charged = new Quantity(chargedAmount, q.getUnit());

        Money gross = getProduct().calculatePriceWithVat(charged);
        if (gross == null) return null; // undvik NPE

        return gross;
}
}
