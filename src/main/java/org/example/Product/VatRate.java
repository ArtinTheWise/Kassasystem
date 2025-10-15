package org.example.Product;

public enum VatRate {
    OTHER(0.25),
    FOOD(0.12),
    BOOKSANDPAPERS(0.06);

    private final double rate;

    VatRate(double rate) {
        this.rate = rate;
    }

    public double applyVAT(double netPrice) {
        return netPrice * (1 + rate);
    }

    public double getRate() {
        return rate;
    }



}
