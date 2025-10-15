package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class VatRateTest {

    /*
     * Testa 25% moms
     * Testa 12% moms
     * Testa 6% moms
     * kastar exception om felaktig momssats
     * testa moms på varor med 0 kr pris
     * testa moms på varor med negativt pris
     * testa moms på varor med decimalpris
     * testa moms på varor med mycket högt pris
     * testa moms på varor med mycket lågt pris
     * testa moms på varor med pris som är null
     */

    @Test
    @DisplayName("Test 25% VAT")
    void testOtherVat(){
        org.example.Product.VatRate vatRate = org.example.Product.VatRate.OTHER;
        double netPrice = 100.0;
        double expectedPriceWithVat = 125.0;
        double actualPriceWithVat = vatRate.applyVAT(netPrice);
        assert(expectedPriceWithVat == actualPriceWithVat);
    }

    @Test
    @DisplayName("Test 12% VAT")
    void testFoodVat(){
        org.example.Product.VatRate vatRate = org.example.Product.VatRate.FOOD;
        double netPrice = 100.0;
        double expectedPriceWithVat = 112.0;
        double actualPriceWithVat = vatRate.applyVAT(netPrice);
        assert(expectedPriceWithVat == actualPriceWithVat);
    }

    @Test
    @DisplayName("Test 6% VAT")
    void testBooksAndPapersVat(){
        org.example.Product.VatRate vatRate = org.example.Product.VatRate.BOOKSANDPAPERS;
        double netPrice = 100.0;
        double expectedPriceWithVat = 106.0;
        double actualPriceWithVat = vatRate.applyVAT(netPrice);
        assert(expectedPriceWithVat == actualPriceWithVat);
    }

    @SuppressWarnings("unused")
    @Test
    @DisplayName("Test invalid VAT throws exception")
    void testInvalidVat(){
        try {
            // This is not possible to create an invalid VatRate due to enum restrictions
            // So we will simulate the invalid case by trying to access a non-existing enum value
            org.example.Product.VatRate invalidVatRate = org.example.Product.VatRate.valueOf("INVALID");
            assert(false); // Should not reach here
        } catch (IllegalArgumentException e) {
            assert(true); // Expected exception
        }
    }

    @Test
    @DisplayName("Test VAT on zero price")
    void testVatOnZeroPrice(){
        org.example.Product.VatRate vatRate = org.example.Product.VatRate.OTHER;
        double netPrice = 0.0;
        double expectedPriceWithVat = 0.0;
        double actualPriceWithVat = vatRate.applyVAT(netPrice);
        assert(expectedPriceWithVat == actualPriceWithVat);
    }

    @Test
    @DisplayName("Test VAT on negative price")
    void testVatOnNegativePrice(){
        org.example.Product.VatRate vatRate = org.example.Product.VatRate.OTHER;
        double netPrice = -100.0;
        double expectedPriceWithVat = -125.0;
        double actualPriceWithVat = vatRate.applyVAT(netPrice);
        assert(expectedPriceWithVat == actualPriceWithVat);
    }
    @Test
    @DisplayName("Test VAT on decimal price")
    void testVatOnDecimalPrice(){
        org.example.Product.VatRate vatRate = org.example.Product.VatRate.OTHER;
        double netPrice = 99.99;
        double expectedPriceWithVat = 124.9875;
        double actualPriceWithVat = vatRate.applyVAT(netPrice);
        assert(Math.abs(expectedPriceWithVat - actualPriceWithVat) < 0.0001);
    }

    @Test
    @DisplayName("Test VAT on very high price")
    void testVatOnVeryHighPrice(){
        org.example.Product.VatRate vatRate = org.example.Product.VatRate.OTHER;
        double netPrice = 1_000_000.0;
        double expectedPriceWithVat = 1_250_000.0;
        double actualPriceWithVat = vatRate.applyVAT(netPrice);
        assert(expectedPriceWithVat == actualPriceWithVat);
    }

    @Test
    @DisplayName("Test VAT on very low price")
    void testVatOnVeryLowPrice(){
        org.example.Product.VatRate vatRate = org.example.Product.VatRate.OTHER;
        double netPrice = 0.01;
        double expectedPriceWithVat = 0.0125;
        double actualPriceWithVat = vatRate.applyVAT(netPrice);
        assert(Math.abs(expectedPriceWithVat - actualPriceWithVat) < 0.0001);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Test VAT on null price throws exception")
    void testVatOnNullPriceThrowsException(){
        org.example.Product.VatRate vatRate = org.example.Product.VatRate.OTHER;
        try {
            Double netPrice = null;
            @SuppressWarnings("unused")
            double actualPriceWithVat = vatRate.applyVAT(netPrice);
            assert(false); // Should not reach here
        } catch (NullPointerException e) {
            assert(true); // Expected exception
        }
    }

}
