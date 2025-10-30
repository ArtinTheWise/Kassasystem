package org.example.Discount;

import org.example.Membership.Customer;
import org.example.Money;
import org.example.Product.Product;
import org.example.Product.ProductGroup;
import org.example.Product.Quantity;

import java.time.LocalDateTime;
import java.util.*;

public class DiscountManager {
    private final ArrayList<ProductDecorator> discounts = new ArrayList<>();

    public DiscountManager(Product... product){
        for(Product p : product){
            if(!(p instanceof ProductDecorator)) throw new IllegalArgumentException();
        }

        for(Product p : product){
            ProductDecorator d = (ProductDecorator) p;
            discounts.add(d);
        }
    }

    public DiscountManager(ProductGroup group){
        for(Product p : group.getProductGroup()){
            if(p instanceof ProductDecorator d){
                discounts.add(d);
            }
        }
    }

    public boolean discountCheck(Product product){
        removeOldDiscounts();
        for (ProductDecorator d : getActiveDiscounts()){
            if (Objects.equals(d.getProduct(), product)) {
                return true;
            }
        }
        return false;
    }

    public Product getBestDiscount(Product product, Quantity quantity){
        return getBestDiscountHelper(product, quantity, null);
    }

    public Product getBestDiscount(Product product, Quantity quantity, Customer customer){ //includes SpecialDiscount
        return getBestDiscountHelper(product, quantity, customer);
    }

    private Product getBestDiscountHelper(Product product, Quantity quantity, Customer customer){
        removeOldDiscounts();
        if (!discountCheck(product)) return product;

        Product cheapest = product;
        long cheapestValue = product.calculatePrice(quantity).getAmountInMinorUnits();

        for (ProductDecorator d : discounts){
            if (Objects.equals(d.getProduct(), product)){
                Money m;
                if(customer != null) {
                    m = d.calculatePriceWithVat(quantity, customer);
                } else {
                    m = d.calculatePriceWithVat(quantity);
                }
                long val = m.getAmountInMinorUnits();
                if (val < cheapestValue) {
                    cheapestValue = val;
                    cheapest = d;
                }
            }
        }
        return cheapest;
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
            if(p instanceof ProductDecorator d){
                bestDiscountsWithoutOverXAmount += d.calculatePrice(q, customer).getAmountInMinorUnits();
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
            Product p = entry.getKey();
            Quantity q = entry.getValue();
            Product cheapest = p;
            long cheapestValue = p.calculatePrice(q).getAmountInMinorUnits();
            for (ProductDecorator d : discounts){
                if(!(d instanceof OverXTotalDiscount)) {
                    if (Objects.equals(d.getProduct(), p)){
                        Money m = d.calculatePriceWithVat(q, customer);
                        long val = m.getAmountInMinorUnits();
                        if (val < cheapestValue) {
                            cheapestValue = val;
                            cheapest = d;
                        }
                    }
                }
            }
            if(cheapest != p){
                discountedItems.put(cheapest, q);
            } else {
                discountedItems.put(p, q);
            }
        }
        return discountedItems;
    }

    private OverXTotalDiscount bestDiscountWithOnlyOverXTotalDiscount(Map<Product, Quantity> items) {
        OverXTotalDiscount bestOverX = null;
        long cheapestValue = Long.MAX_VALUE;

        for (ProductDecorator d : discounts) {
            if (d instanceof OverXTotalDiscount dT) {
                Money m = dT.calculatePriceWithVat(items);
                long val = m.getAmountInMinorUnits();
                if (val < cheapestValue) {
                    cheapestValue = val;
                    bestOverX = dT;
                }
            }
        }
        return bestOverX;
    }

    public void addDiscount(Product... product){
        for(Product p : product) {
            if (p instanceof ProductDecorator d) {
                if (!d.getEndTime().isBefore(LocalDateTime.now(d.clock))) {
                    discounts.add(d);
                }
            }
        }
    }

    public void addDiscount(ProductGroup group){
        addDiscount(group.getProductGroup().toArray(new Product[0]));
    }

    public ProductGroup discountGroup(ProductGroup productGroup, ProductDecorator discount) {
        ProductGroup discountedProductGroup = new ProductGroup(productGroup.getName());

        for(Product p : productGroup.getProductGroup()){
            discountedProductGroup.addProduct(discount.createFor(p));
        }

        return discountedProductGroup;
    }

    private void removeOldDiscounts(){
        discounts.removeIf(d -> {
            return LocalDateTime.now(d.clock).isAfter(d.getEndTime());
        });
    }

    private ArrayList<ProductDecorator> getActiveDiscounts(){
        ArrayList<ProductDecorator> activeDiscounts = new ArrayList<>();
        for(ProductDecorator d : discounts){
            if(d.isActive()) activeDiscounts.add(d);
        }
        return activeDiscounts;
    }
}
