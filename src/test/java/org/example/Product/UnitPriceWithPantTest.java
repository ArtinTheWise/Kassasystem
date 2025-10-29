package org.example.Product;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.example.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UnitPriceWithPantTest {
    /*
     * Beräkna totaltpris med pant för 1 enhet
     * Beräkna totalpris med pant för flera enheter
     * beräkna pris för 0 enheter korrekt
     * Hanterar mycket stora kvantiteter korrekt
     * totalpriset räknas ut korrekt
     * kastar undantag för null basePrice
     * kastar undantag för null pant
     * kastar undantag med negativ pant
     * kastar undantag om fel enhet används
     */

    @Test
    @DisplayName("calculatePrice: quantity < 1 throws exception")
    void calculatePriceWithQuantityAmountLessThanOneThrowsException(){
        Money pricePerPiece = new Money(100); // 1.00 kr per piece
        Money pantPerPiece = new Money(20); // 0.20 kr pant per piece
        UnitPrice basePrice = new UnitPrice(pricePerPiece);
        UnitPriceWithPant unitPriceWithPant = new UnitPriceWithPant(basePrice, pantPerPiece);
        try {
            Quantity quantity = new Quantity (0, org.example.Product.Unit.PIECE);
            unitPriceWithPant.calculatePrice(quantity);
            assert(false);
        } catch (IllegalArgumentException e) {
            assert(true);
        }
    }

    @Test
    @DisplayName("getPantPerPiece returns correct pant per piece")
    void getPantPerPieceReturnsCorrectPantPerPiece(){
        Money pricePerPiece = new Money(100); // 1.00 kr per piece
        Money pantPerPiece = new Money(20); // 0.20 kr pant per piece
        UnitPrice basePrice = new UnitPrice(pricePerPiece);
        UnitPriceWithPant unitPriceWithPant = new UnitPriceWithPant(basePrice, pantPerPiece);

        assert(unitPriceWithPant.getPantPerPiece().equals(pantPerPiece));
    }

    @Test
    @DisplayName("getUnitPrice returns correct base price")
    void getUnitPriceReturnsCorrectBasePrice(){
        Money pricePerPiece = new Money(100); // 1.00 kr per piece
        Money pantPerPiece = new Money(20); // 0.20 kr pant per piece
        UnitPrice basePrice = new UnitPrice(pricePerPiece);
        UnitPriceWithPant unitPriceWithPant = new UnitPriceWithPant(basePrice, pantPerPiece);

        assert(unitPriceWithPant.getUnitPrice().equals(basePrice));
    }

    @Test
    @DisplayName("getUnit returns PIECE")
    void getUnitReturnsPiece(){
        Money pricePerPiece = new Money(100); // 1.00 kr per piece
        Money pantPerPiece = new Money(20); // 0.20 kr pant per piece
        UnitPrice basePrice = new UnitPrice(pricePerPiece);
        UnitPriceWithPant unitPriceWithPant = new UnitPriceWithPant(basePrice, pantPerPiece);

        assert(unitPriceWithPant.getUnit() == org.example.Product.Unit.PIECE);
    }


    @Test
    @DisplayName("Calculate total price with pant for one unit")
    void calculatesCorrectTotalPriceWithPantForOneUnit(){
        Money pricePerPiece = new Money(100); // 1.00 kr per piece
        Money pantPerPiece = new Money(20); // 0.20 kr pant per piece
        UnitPrice basePrice = new UnitPrice(pricePerPiece);
        UnitPriceWithPant unitPriceWithPant = new UnitPriceWithPant(basePrice, pantPerPiece);
        Quantity quantity = new Quantity (1, org.example.Product.Unit.PIECE);

        Money expectedPrice = new Money(120); // 1.20 kr
        Money actualPrice = unitPriceWithPant.calculatePrice(quantity);

        assert(expectedPrice.equals(actualPrice));
    }

    @Test
    @DisplayName("Calculate total price with pant for multiple units")
    void calculatesCorrectTotalPriceWithPantForMultipleUnits(){
        Money pricePerPiece = new Money(150); // 1.50 kr per piece
        Money pantPerPiece = new Money(30); // 0.30 kr pant per piece
        UnitPrice basePrice = new UnitPrice(pricePerPiece);
        UnitPriceWithPant unitPriceWithPant = new UnitPriceWithPant(basePrice, pantPerPiece);
        Quantity quantity = new Quantity (3, org.example.Product.Unit.PIECE);

        Money expectedPrice = new Money(540); // 5.40 kr
        Money actualPrice = unitPriceWithPant.calculatePrice(quantity);

        assert(expectedPrice.equals(actualPrice));
    }

    @Test
    @DisplayName("Handles very large quantities correctly")
    void handlesVeryLargeQuantitiesCorrectly(){
        Money pricePerPiece = new Money(100); // 1.00 kr per piece
        Money pantPerPiece = new Money(20); // 0.20 kr pant per piece
        UnitPrice basePrice = new UnitPrice(pricePerPiece);
        UnitPriceWithPant unitPriceWithPant = new UnitPriceWithPant(basePrice, pantPerPiece);
        Quantity quantity = new Quantity (1_000_000, org.example.Product.Unit.PIECE);

        Money expectedPrice = new Money(120_000_000); // 1,200,000.00 kr
        Money actualPrice = unitPriceWithPant.calculatePrice(quantity);

        assert(expectedPrice.equals(actualPrice));
    }

    @Test
    @DisplayName("Calculate total price is calculated correctly")
    void calculatesCorrectTotalPriceIsCalculatedCorrectly(){
        Money pricePerPiece = new Money(200); // 2.00 kr per piece
        Money pantPerPiece = new Money(50); // 0.50 kr pant per piece
        UnitPrice basePrice = new UnitPrice(pricePerPiece);
        UnitPriceWithPant unitPriceWithPant = new UnitPriceWithPant(basePrice, pantPerPiece);
        Quantity quantity = new Quantity (4, org.example.Product.Unit.PIECE);

        Money expectedPrice = new Money(1000); // 10.00 kr
        Money actualPrice = unitPriceWithPant.calculatePrice(quantity);

        assert(expectedPrice.equals(actualPrice));
    }

    @Test
    @DisplayName("Throws exception for null basePrice")
    void throwsExceptionForNullBasePrice(){
        Money pantPerPiece = new Money(20); // 0.20 kr pant per piece

        try {
            @SuppressWarnings("unused")
            UnitPriceWithPant unitPriceWithPant = new UnitPriceWithPant(null, pantPerPiece);
            assert(false); // null basePrice should not reach here
        } catch (IllegalArgumentException e) {
            assert(true); // expected exception
        }
    }

    @Test
    @DisplayName("Throws exception for null pant")
    void throwsExceptionForNullPant(){
        Money pricePerPiece = new Money(100); // 1.00 kr per piece
        UnitPrice basePrice = new UnitPrice(pricePerPiece);

        try {
            @SuppressWarnings("unused")
            UnitPriceWithPant unitPriceWithPant = new UnitPriceWithPant(basePrice, null);
            assert(false); // null pant should not reach here
        } catch (IllegalArgumentException e) {
            assert(true); // expected exception
        }
    }

    @Test
    @DisplayName("Throws exception for wrong unit")
    void throwsExceptionForWrongUnit(){
        Money pricePerPiece = new Money(100); // 1.00 kr per piece
        Money pantPerPiece = new Money(20); // 0.20 kr pant per piece
        UnitPrice basePrice = new UnitPrice(pricePerPiece);
        UnitPriceWithPant unitPriceWithPant = new UnitPriceWithPant(basePrice, pantPerPiece);
        Quantity quantity = new Quantity (1, org.example.Product.Unit.KG);

        try {
            unitPriceWithPant.calculatePrice(quantity);
            assert(false); // KG should not reach here
        } catch (IllegalArgumentException e) {
            assert(true); // expected exception
        }
    }



    
    
}
