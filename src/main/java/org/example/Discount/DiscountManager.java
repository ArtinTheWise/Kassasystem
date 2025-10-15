package org.example.Discount;

import org.example.Product.Product;
import org.example.Product.Quantity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.example.Product.Unit.PIECE;

public class DiscountManager {
    ArrayList<Product> products = new ArrayList<>();

    public DiscountManager(Product... product){
        for(Product p : product){
            if(!(p instanceof ProductDecorator)) throw new IllegalArgumentException();
        }
        products.addAll(Arrays.asList(product));
    }

    public boolean discountCheck(Product product){
        removeOldDiscounts();
        for(Product p : getActiveDiscounts()){
            ProductDecorator discountProduct = (ProductDecorator) p;
            return discountProduct.getProduct().equals(product) && discountProduct.isActive();
        }
        return false;
    }

    public Product getBestDiscount(Product product, Quantity quantity){
        if(!discountCheck(product)) return product;
        Product cheapest = null;
        for(Product p : products){
            ProductDecorator discountProduct = (ProductDecorator) p;
            if(discountProduct.equals(product) && discountProduct.isActive()){
                if(cheapest == null){
                    cheapest = p;
                } else if (cheapest.calculatePrice(quantity).getAmountInMinorUnits() > p.calculatePrice(quantity).getAmountInMinorUnits()) {
                    cheapest = p;
                }
            }
        }
        return cheapest;
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
