package org.example.Product;

import org.example.Money;

public class WeightedProduct extends Product {
    private final double weight;

    public WeightedProduct(String name, Money price, double weight){
        super(name, price);
        this.weight = weight;
    }

    public double getWeight(){
        return weight;
    }

    @Override
    public double getFinalPrice() {
        return getPrice() * weight;
    }
}
