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

    public Money calculatePrice(Map<Product, Quantity> items) {
        return calculatePriceHelper(items, false);
    }

    public Money calculatePriceWithVat(Map<Product, Quantity> items) {
        return calculatePriceHelper(items, true);
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

    @Override
    public ProductDecorator createFor(Product product) {
        return new OverXTotalDiscount(discountType.createFor(product), priceThreshold);
    }

    private Money calculatePriceHelper(Map<Product, Quantity> items, boolean withVat){
        long amount = 0;
        for (Map.Entry<Product, Quantity> entry : items.entrySet()) {
            Product p = entry.getKey();
            Quantity q = entry.getValue();
            if(withVat) {
                amount += p.calculatePriceWithVat(q).getAmountInMinorUnits();
            } else {
                amount += p.calculatePrice(q).getAmountInMinorUnits();
            }
        }
        if(amount > priceThreshold.getAmountInMinorUnits() && items.containsKey(discountType.getProduct())){
            long newAmount = 0;
            for (Map.Entry<Product, Quantity> entry : items.entrySet()) {
                Product p = entry.getKey();
                Quantity q = entry.getValue();
                if(withVat) {
                    newAmount += discountType.createFor(p).calculatePriceWithVat(q).getAmountInMinorUnits();
                } else {
                    newAmount += discountType.createFor(p).calculatePrice(q).getAmountInMinorUnits();
                }
            }
            return new Money(newAmount);
        }
        return new Money(amount);
    }
}
