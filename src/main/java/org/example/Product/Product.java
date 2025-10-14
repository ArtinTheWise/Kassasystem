package org.example.Product;
import org.example.Money;

public class Product {
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

    public Product(String name, PriceModel priceModel){
        this(name, priceModel, null); //ändring från artin, antar man behöver inte vara i en grupp.
    }

    public String getName(){
        return name;
    }

    public PriceModel getPriceModel(){
        return priceModel;
    }

    public ProductGroup getProductGroup(){
        return productGroup;
    }

    //protected double getPrice(){
        //return 0.0; // VAD VAR HÄR
    //}

    public Money calculatePrice(Quantity quantity){
        return priceModel.calculatePrice(quantity);
    }
}
