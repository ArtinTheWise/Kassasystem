package org.example.Product;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.example.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UnitPriceTest {

    /*
     * beräknar pris för 1 enhet korrekt
     * beräkna pris för flera enheter korrekt
     * beräkna pris för decimala enheter korrekt
     * Rundar av korrekt vid decimala resultat
     * kastar undantag om fel enhet används
     * beräkna pris för 0 enheter korrekt
     * Hanterar mycket stora kvantiteter korrekt
     * beräkna pris för negativa enheter kastar undantag
     * kastar undantag för noll pris per enhet
     * kastar undantag för negativt pris per enhet
     */

    @Test
    @DisplayName("Constructor: throws exception for null price per piece")
    void constructorThrowsExceptionForNullPricePerPiece(){
        assertThrows(Exception.class, () -> new UnitPrice(null));

    }

    @Test
    @DisplayName("Calculate price with quantity amount < 1 throws exception")
    void calculatePriceWithQuantityAmountLessThanOneThrowsException(){
        Money pricePerPiece = new Money(100); // 1.00 kr per piece
        UnitPrice unitPrice = new UnitPrice(pricePerPiece);
        try {
            Quantity quantity = new Quantity (0, Unit.PIECE);
            unitPrice.calculatePrice(quantity);
            assert(false);
        } catch (IllegalArgumentException e) {
            assert(true);
        }
    }

    @Test
    @DisplayName("Calculate price for one unit correctly")
    void calculatesCorrectPriceForOneUnit(){
        Money pricePerPiece = new Money(100); // 1.00 kr per piece
        UnitPrice unitPrice = new UnitPrice(pricePerPiece);
        Quantity quantity = new Quantity (1, Unit.PIECE);

        Money expectedPrice = new Money(100); // 1.00 kr
        Money actualPrice = unitPrice.calculatePrice(quantity);

        assert(expectedPrice.equals(actualPrice));
    }

    @Test
    @DisplayName("Calculate price for multiple units correctly")
    void calculatesCorrectPriceForMultipleUnit(){
        Money pricePerPiece = new Money(1500); // 15.00 kr per piece
        UnitPrice unitPrice = new UnitPrice(pricePerPiece);
        Quantity quantity = new Quantity (3, Unit.PIECE);
        Money expectedPrice = new Money(4500); // 45.00 kr
        Money actualPrice = unitPrice.calculatePrice(quantity);

        assert(expectedPrice.equals(actualPrice));
    }

    @Test
    @DisplayName("Rounds off correctly for decimal results")
    void roundsOffCorrectlyForDecimalResults(){
        Money pricePerPiece = new Money(333); // 3.33 kr per piece
        UnitPrice unitPrice = new UnitPrice(pricePerPiece);
        Quantity quantity = new Quantity (3, Unit.PIECE);
        Money expectedPrice = new Money(999); // 9.99 kr
        Money actualPrice = unitPrice.calculatePrice(quantity);

        assert(expectedPrice.equals(actualPrice));
    }

    @Test
    @DisplayName("Throws exception for wrong unit")
    void throwsExceptionForWrongUnit(){
        Money pricePerPiece = new Money(100); // 1.00 kr per piece
        UnitPrice unitPrice = new UnitPrice(pricePerPiece);
        Quantity quantity = new Quantity (1, Unit.KG);

        try {
            unitPrice.calculatePrice(quantity);
            assert(false); // KG should not reach here
        } catch (IllegalArgumentException e) {
            assert(true); // expected exception
        }
    }

    @Test
    @DisplayName("Handles very large quantities correctly")
    void handlesVeryLargeQuantitiesCorrectly(){
        Money pricePerPiece = new Money(100); // 1.00 kr per piece
        UnitPrice unitPrice = new UnitPrice(pricePerPiece);
        Quantity quantity = new Quantity (1_000_000, Unit.PIECE);
        Money expectedPrice = new Money(100_000_000); // 1,000,000.00 kr
        Money actualPrice = unitPrice.calculatePrice(quantity);

        assert(expectedPrice.equals(actualPrice));
    }

    @Test
    @DisplayName("Calculate price for negative units throws exception")
    void calculatesCorrectPriceForNegativeUnitsThrowsException(){
        Money pricePerPiece = new Money(100); // 1.00 kr per piece
        UnitPrice unitPrice = new UnitPrice(pricePerPiece);
        try {
            Quantity quantity = new Quantity (-1, Unit.PIECE);
            unitPrice.calculatePrice(quantity);
            assert(false); // Negative quantity should not reach here
        } catch (IllegalArgumentException e) {
            assert(true); // expected exception
        }
    }

    @Test
    @DisplayName("throw exception for zero price per piece")
    void throwsExceptionForZeroPricePerPiece(){
        try {
            Money pricePerPiece = new Money(0); // 0.00 kr per piece
            @SuppressWarnings("unused")
            UnitPrice unitPrice = new UnitPrice(pricePerPiece);
            assert(false); // Zero price should not reach here
        } catch (IllegalArgumentException e) {
            assert(true); // expected exception
        }
    }

    @Test
    @DisplayName("throw exception for negative price per piece")
    void throwsExceptionForNegativePricePerPiece(){
        try {
            Money pricePerPiece = new Money(-100); // -1.00 kr per piece
            @SuppressWarnings("unused")
            UnitPrice unitPrice = new UnitPrice(pricePerPiece);
            assert(false); // Negative price should not reach here
        } catch (IllegalArgumentException e) {
            assert(true); // expected exception
        }
    }


}
