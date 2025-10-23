package org.example.Discount;

import org.example.Money;
import org.example.Product.Product;
import org.example.Product.Quantity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static org.example.Product.Unit.PIECE;

public class DiscountManager {
    ArrayList<Product> products = new ArrayList<>();

    public DiscountManager(Product... product){
        for(Product p : product){
            if(!(p instanceof ProductDecorator)) throw new IllegalArgumentException();
        }
        products.addAll(Arrays.asList(product));
    }

    // Scanna alla 
    public boolean discountCheck(Product product){
        removeOldDiscounts();
        for (Product p : getActiveDiscounts()){
            ProductDecorator d = (ProductDecorator) p;
            if (d.isActive() && Objects.equals(d.getProduct(), product)) {
                return true;
            }
        }  
        return false;
    }

    public Product getBestDiscount(Product product, Quantity quantity){
        removeOldDiscounts();
        if (!discountCheck(product)) return product;

        Product cheapest = null;
        long cheapestVal = Long.MAX_VALUE;

        for (Product p : products){
            ProductDecorator d = (ProductDecorator) p;
            if (d.isActive() && Objects.equals(d.getProduct(), product)){
                Money m = p.calculatePriceWithVat(quantity);
                if (m == null) m = p.calculatePrice(quantity);
                    if (m != null) {
                        long val = m.getAmountInMinorUnits();
                        if (val < cheapestVal) { cheapestVal = val; cheapest = p; }
                    }
                }
            }
        return (cheapest != null) ? cheapest : product;
    }

    private void removeOldDiscounts(){
        for(Product p : products){
            ProductDecorator discountProduct = (ProductDecorator) p;
            if(LocalDateTime.now().isAfter(discountProduct.getEndTime())) products.remove(p);
        }
    }

    private ArrayList<Product> getActiveDiscounts(){
        ArrayList<Product> activeDiscounts = new ArrayList<>();
        for(Product p : products){
            ProductDecorator discountProduct = (ProductDecorator) p;
            if(discountProduct.isActive()) activeDiscounts.add(discountProduct);
        }
        return activeDiscounts;
    }
}
