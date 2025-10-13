package org.example.Product;

import org.example.Money;

public class NormalProduct extends Product {
    public NormalProduct(String name, Money price) {
        super(name, price);
    }

    public double getFinalPrice(){
        return getPrice();
    }
}
