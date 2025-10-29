package org.example.Discount;

import org.example.Membership.Customer;
import org.example.Money;
import org.example.Product.Product;
import org.example.Product.ProductGroup;
import org.example.Product.Quantity;
import org.example.Product.Unit;

import java.time.LocalDateTime;
import java.util.*;

public class DiscountManager {
    ArrayList<Product> products = new ArrayList<>();

    public DiscountManager(Product... product){
        for(Product p : product){
            if(!(p instanceof ProductDecorator)) throw new IllegalArgumentException();
        }
        products.addAll(Arrays.asList(product));
    }

    public DiscountManager(ProductGroup group){
        for(Product p : group.getProductGroup()){
            if(p instanceof ProductDecorator){
                products.add(p);
            }
        }
    }

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
                Money m = d.calculatePriceWithVat(quantity);
                if (m == null) m = d.calculatePrice(quantity);
                if (m != null) {
                    long val = m.getAmountInMinorUnits();
                    if (val < cheapestVal) {
                        cheapestVal = val;
                        cheapest = d;
                    }
                }
            }
        }
        return (cheapest != null) ? cheapest : product;
    }

    public Product getBestDiscount(Product product, Quantity quantity, Customer customer){ //includes SpecialDiscount
        removeOldDiscounts();
        if (!discountCheck(product)) return product;

        Product cheapest = null;
        long cheapestVal = Long.MAX_VALUE;

        for (Product p : products){
            ProductDecorator d = (ProductDecorator) p;
            if (d.isActive() && Objects.equals(d.getProduct(), product)){
                Money m = d.calculatePriceWithVat(quantity, customer);
                if (m == null) m = d.calculatePrice(quantity, customer);
                if (m != null) {
                    long val = m.getAmountInMinorUnits();
                    if (val < cheapestVal) {
                        cheapestVal = val;
                        cheapest = d;
                    }
                }
            }
        }
        return (cheapest != null) ? cheapest : product;
    }

    public Map<Product, Quantity> getBestDiscount(Map<Product, Quantity> items, Customer customer){ //best
        removeOldDiscounts();
        long bestDiscountsWithoutOverXAmount = 0;
        Map<Product, Quantity> bestDiscountsWithoutOverX = bestDiscountWithoutOverXTotalDiscount(items, customer);
        long bestDiscountsWithOnlyOverXAmount = 0;
        OverXTotalDiscount bestDiscountsWithOnlyOverX = bestDiscountWithOnlyOverXTotalDiscount(items);

        for (Map.Entry<Product, Quantity> entry : bestDiscountsWithoutOverX.entrySet()) {
            Product p = entry.getKey();
            Quantity q = entry.getValue();
            if(p instanceof ProductDecorator){
                ProductDecorator discountP = (ProductDecorator) p;
                bestDiscountsWithoutOverXAmount += discountP.calculatePrice(q, customer).getAmountInMinorUnits();
            } else {
                bestDiscountsWithoutOverXAmount += p.calculatePrice(q).getAmountInMinorUnits();
            }
        }

        if(bestDiscountsWithOnlyOverX != null){
            bestDiscountsWithOnlyOverXAmount = bestDiscountsWithOnlyOverX.calculatePrice(items).getAmountInMinorUnits();
        }

        if(bestDiscountsWithOnlyOverX == null) return bestDiscountsWithoutOverX;
        if(bestDiscountsWithoutOverXAmount < bestDiscountsWithOnlyOverXAmount) return bestDiscountsWithoutOverX;

        Map<Product, Quantity> mapWithOnlyDiscount = new HashMap<>();
        mapWithOnlyDiscount.put(bestDiscountsWithOnlyOverX, null);
        return mapWithOnlyDiscount;
    }

    private Map<Product, Quantity> bestDiscountWithoutOverXTotalDiscount(Map<Product, Quantity> items, Customer customer){
        Map<Product, Quantity> discountedItems = new HashMap<>();

        for (Map.Entry<Product, Quantity> entry : items.entrySet()) {
            Product pInItems = entry.getKey();
            Quantity q = entry.getValue();
            Product cheapest = null;
            long cheapestVal = Long.MAX_VALUE;
            for (Product p : products){
                if(!(p instanceof OverXTotalDiscount)) {
                    ProductDecorator d = (ProductDecorator) p;
                    if (d.isActive() && Objects.equals(d.getProduct(), pInItems)){
                        Money m = d.calculatePriceWithVat(q, customer);
                        if (m == null) m = d.calculatePrice(q, customer);
                        if (m != null) {
                            long val = m.getAmountInMinorUnits();
                            if (val < cheapestVal) {
                                cheapestVal = val;
                                cheapest = d;
                            }
                        }
                    }
                }
            }
            if(cheapest != null){
                discountedItems.put(cheapest, q);
            } else {
                discountedItems.put(pInItems, q);
            }
        }
        return discountedItems;
    }

    private OverXTotalDiscount bestDiscountWithOnlyOverXTotalDiscount(Map<Product, Quantity> items) {
        OverXTotalDiscount bestOverX = null;
        long cheapestVal = Long.MAX_VALUE;

        for (Product p : products) {
            if (p instanceof OverXTotalDiscount) {
                OverXTotalDiscount d = (OverXTotalDiscount) p;
                if (d.isActive()) {
                    Money m = d.calculatePriceWithVat(items);
                    if (m == null) m = d.calculatePrice(items);
                    if (m != null) {
                        long val = m.getAmountInMinorUnits();
                        if (val < cheapestVal) {
                            cheapestVal = val;
                            bestOverX = d;
                        }
                    }
                }
            }
        }
        return bestOverX;
    }

    public void addDiscount(Product... product){
        for(Product p : product) {
            if (p instanceof ProductDecorator) {
                ProductDecorator d = (ProductDecorator) p;
                if (!d.getEndTime().isBefore(LocalDateTime.now(d.clock))) {
                    products.add(p);
                }
            }
        }
    }

    public void addDiscount(ProductGroup group){
        addDiscount(group.getProductGroup().toArray(new Product[0]));
    }

    private void removeOldDiscounts(){
        products.removeIf(p -> {
            ProductDecorator d = (ProductDecorator) p;
            return LocalDateTime.now(d.clock).isAfter(d.getEndTime());
        });
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
