package org.example.Product;

public class VatGroup {

    private String name;
    private double vatRate;
    

    public VatGroup(String name, double vatRate) {
        this.name = name;
        this.vatRate = vatRate;
    }

    public String getName() {
        return name;
    }

    public double getVatRate() {
        return vatRate;
    }
    
}
