package org.example;

public class Product {
    private final String name;
    private final Money price;

    public Product(String name, Money price){
        this.name = name;
        this.price = price;
    }

    public Product(String name, int price){
        this(name, new Money(price));
    }

    public String getName(){
        return name;
    }

    public Money getPrice(){
        return price;
    }

    @Override
    public String toString(){
        return name;
    }
}
