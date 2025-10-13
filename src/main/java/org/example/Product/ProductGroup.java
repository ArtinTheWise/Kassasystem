package org.example.Product;

import java.util.*;

public class ProductGroup {
    private final String name;
    private final List<Product> products = new ArrayList<>();
    private VatGroup vatGroup;

    public ProductGroup(String name, VatGroup vatGroup){
        this.name = name;
    }

    public ProductGroup(String name, Product... product){
        this.name = name;
        products.addAll(Arrays.asList(product));
    }

    public VatGroup getVatGroup() {
        return vatGroup;
    }

    public void addProduct(Product p) {
        products.add(p);
    }

    public void removeProduct(Product p) {
        products.remove(p);
    }

    public List<Product> getProductGroup(){
        return products;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString(){
        return name;
    }
}
