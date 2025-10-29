package org.example.Product;

import java.util.*;


 // REPRESENTS CATEGORIES - E.G. FRUIT, VEGETABLES, DAIRY, MEAT, BAKERY, BOOKS, ELECTRONICS
public class ProductGroup {
    private final String name;
    private final List<Product> products = new ArrayList<>();

    public ProductGroup(String name){
        this.name = name;
    }

    public ProductGroup(String name, Product... product){
        this.name = name;
        products.addAll(Arrays.asList(product));
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


        return "name='" + name + "'\n" + "Products:" + "\n" + products;
    }
}
