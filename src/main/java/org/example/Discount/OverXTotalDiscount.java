package org.example.Discount;

import org.example.Money;
import org.example.Product.Product;
import org.example.Product.Quantity;

import java.util.Map;

public class OverXTotalDiscount extends ProductDecorator {
    private final ProductDecorator discountType;
    private final Money priceThreshold;

    public OverXTotalDiscount(ProductDecorator discountType, Money priceThreshold){
        super(discountType.getProduct(), discountType.getStartTime(), discountType.getEndTime(), discountType.clock);
        if(!(discountType instanceof NormalDiscount) && !(discountType instanceof PercentageDiscount)) throw new IllegalArgumentException();
        this.discountType = discountType;
        this.priceThreshold = priceThreshold;
    }

    @Override
    public Money calculatePrice(Quantity q) {
        if(!isActive()) return discountType.getProduct().calculatePrice(q);
        if(discountType.getProduct().calculatePriceWithVat(q).compareTo(priceThreshold) > 0) return discountType.calculatePrice(q);
        return discountType.getProduct().calculatePrice(q);
    }

    @Override
    public Money calculatePriceWithVat(Quantity q) {
        if(!isActive()) return discountType.getProduct().calculatePriceWithVat(q);
        if(discountType.getProduct().calculatePriceWithVat(q).compareTo(priceThreshold) > 0) return discountType.calculatePriceWithVat(q);
        return discountType.getProduct().calculatePriceWithVat(q);
    }

    public Money calculatePrice(Map<Product, Quantity> items) {
        long amount = 0;
        for (Map.Entry<Product, Quantity> entry : items.entrySet()) {
            Product p = entry.getKey();
            Quantity q = entry.getValue();
            amount += p.calculatePrice(q).getAmountInMinorUnits();
        }
        if(amount > priceThreshold.getAmountInMinorUnits() && items.containsKey(discountType.getProduct())){
            long newAmount = 0;
            for (Map.Entry<Product, Quantity> entry : items.entrySet()) {
                Product p = entry.getKey();
                Quantity q = entry.getValue();
                newAmount += discountType.createFor(p).calculatePrice(q).getAmountInMinorUnits();
            }
            return new Money(newAmount);
        }
        return new Money(amount);
    }

    public Money calculatePriceWithVat(Map<Product, Quantity> items) {
        long amount = 0;
        for (Map.Entry<Product, Quantity> entry : items.entrySet()) {
            Product p = entry.getKey();
            Quantity q = entry.getValue();
            amount += p.calculatePriceWithVat(q).getAmountInMinorUnits();
        }
        if(amount > priceThreshold.getAmountInMinorUnits() && items.containsKey(discountType.getProduct())){
            long newAmount = 0;
            for (Map.Entry<Product, Quantity> entry : items.entrySet()) {
                Product p = entry.getKey();
                Quantity q = entry.getValue();
                newAmount += discountType.createFor(p).calculatePriceWithVat(q).getAmountInMinorUnits();
            }
            return new Money(newAmount);
        }
        return new Money(amount);
    }

    @Override
    public ProductDecorator createFor(Product product) {
        return new OverXTotalDiscount(discountType.createFor(product), priceThreshold);
    }
}
