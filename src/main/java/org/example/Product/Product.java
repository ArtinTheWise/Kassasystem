package org.example.Product;
import org.example.Money;

public class Product {
    private final String name;
    private final PriceModel priceModel;
    private final ProductGroup productGroup;
    private final VatRate vatRate;
    private final Boolean ageRestriction;

    public Product(String name, PriceModel priceModel, ProductGroup productGroup, VatRate vatRate, Boolean ageRestriction){
        this.name = name;
        this.priceModel = priceModel;
        this.productGroup = productGroup;
        if(productGroup != null){
            productGroup.addProduct(this);
        }
        this.vatRate = vatRate;
        this.ageRestriction = ageRestriction;
    }

    // en produkt behöver inte ha en grupp.
    public Product(String name, PriceModel priceModel, VatRate vatRate, boolean ageRestriction){
        this.name = name;
        this.priceModel = priceModel;
        this.productGroup = null;
        this.vatRate = vatRate; 
        this.ageRestriction = ageRestriction;
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

    public Boolean getAgeRestriction(){
        return ageRestriction;
    }

    public Money calculatePrice(Quantity quantity){
        return priceModel.calculatePrice(quantity);
    }

    public Money calculatePriceWithVat(Quantity quantity){
        Money price = calculatePrice(quantity);
        return new Money(Math.round(price.getAmountInMinorUnits() * (1 + vatRate.getRate()/100.0)));
    }

    public String toString() {
        return "Name: " + name + " Price Model: " + priceModel + " Product Group: " + productGroup + " VAT Rate: " + vatRate;
    }
}