package org.example.Product;
import org.example.Money;

public class Product {
    private final String name;
    private final PriceModel priceModel;
    private final ProductGroup productGroup;
    private final VatRate vatRate;

    public Product(String name, PriceModel priceModel, ProductGroup productGroup, VatRate vatRate){
        this.name = name;
        this.priceModel = priceModel;
        this.productGroup = productGroup;
        if(productGroup != null){
            productGroup.addProduct(this);
        }
        this.vatRate = vatRate;
    }

    // en produkt behöver inte ha en grupp.
    public Product(String name, PriceModel priceModel, VatRate vatRate){
        this.name = name;
        this.priceModel = priceModel;
        this.productGroup = null;
        this.vatRate = vatRate; 
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

    public VatRate getVatRate(){
        return vatRate;
    }

    public Money calculatePrice(Quantity quantity){
        return priceModel.calculatePrice(quantity);
    }

    public Money calculatePriceWithVat(Quantity quantity){
        Money price = calculatePrice(quantity);
        return new Money(Math.round(price.getAmountInMinorUnits() * (1 + vatRate.getRate()/100.0)));
    }
}
