package org.example.Product;
import org.example.Money;

public abstract class Product {
    private final String name;
    private final PriceModel priceModel;
    private final ProductGroup productGroup;

    public Product(String name, PriceModel priceModel, ProductGroup productGroup){
        this.name = name;
        this.priceModel = priceModel;
        this.productGroup = productGroup;
        if(productGroup != null){
            productGroup.addProduct(this);
        }
    }
    public String getName(){
        return name;
    }

    public ProductGroup getProductGroup(){
        return productGroup;
    }

    public PriceModel getPriceModel(){
        return priceModel;
    }

    public Money calculatePrice(Quantity quantity){
        return priceModel.calculatePrice(quantity);
    }
}
