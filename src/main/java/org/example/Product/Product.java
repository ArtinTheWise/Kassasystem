package org.example.Product;

import org.example.Money;

public abstract class Product {
    private final String name;
    private final Money price;

    public Product(String name, Money price){
        this.name = name;
        this.price = price;
    }
    public String getName(){
        return name;
    }

    protected double getPrice(){
        return price.getAmountInMinorUnits();
    }

    public abstract double getFinalPrice();
}
