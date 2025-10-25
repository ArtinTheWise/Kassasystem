package org.example.Product;
import org.example.Money;

import java.util.Objects;

public class Product {
    private final String name;
    private final PriceModel priceModel;
    private final ProductGroup productGroup;
    private final VatRate vatRate;
    private final Boolean ageRestriction;

    public Product(String name, PriceModel priceModel, ProductGroup productGroup, VatRate vatRate, Boolean ageRestriction){
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or blank");
        }
        if (priceModel == null) {
            throw new IllegalArgumentException("PriceModel cannot be null");
        }
        if (vatRate == null) {
            throw new IllegalArgumentException("VatRate cannot be null");
        }
        this.name = name;
        this.priceModel = priceModel;
        this.productGroup = productGroup;
        if(productGroup != null){
            productGroup.addProduct(this);
        }
        this.vatRate = vatRate;
        this.ageRestriction = ageRestriction;
    }

    public Product(String name, PriceModel priceModel, VatRate vatRate, boolean ageRestriction){
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or blank");
        }
        if (priceModel == null) {
            throw new IllegalArgumentException("PriceModel cannot be null");
        }
        if (vatRate == null) {
            throw new IllegalArgumentException("VatRate cannot be null");
        }
        this.name = name;
        this.priceModel = priceModel;
        this.productGroup = null;
        this.vatRate = vatRate; 
        this.ageRestriction = ageRestriction;
    }

    public Product(String name, PriceModel priceModel, VatRate vatRate){
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or blank");
        }
        if (priceModel == null) {
            throw new IllegalArgumentException("PriceModel cannot be null");
        }
        if (vatRate == null) {
            throw new IllegalArgumentException("VatRate cannot be null");
        }
        this.name = name;
        this.priceModel = priceModel;
        this.productGroup = null;
        this.vatRate = vatRate;
        this.ageRestriction = false;
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

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Product)) return false;

        Product other = (Product) o;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.name);
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", priceModel=" + priceModel +
                ", productGroup=" + (productGroup != null ? productGroup.getName() : "none") +
                ", vatRate=" + vatRate +
                ", ageRestriction=" + ageRestriction +
                '}';
    }
}
