package org.example;

import org.example.Product.Quantity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class QuantityTest {

    /*
    PIECE:
     * skapa quantity med heltal för piece
     * skapa quantity med 0 för piece
     * skapa quantity med stort heltal för piece
     * kastar exception för decimaltal med piece
     * kastar exception för negativt tal med piece
     * accepterar 1.0 som heltal för piece
     * exception för litet demicaltal för piece (1.00001)
    KG:
     * skapa quantity med decimaltal för kg
     * skapa quantity med miniumum 0.001 för kg
     * skapa quantity under minium - 0.0009 för kg kastar exception
     * skapa quantity med stort heltal för kg
     * skapa quantity med stort decimaltal för kg
     * kastar exception för negativt tal med kg
     * hanterar stor vikt för KG korrekt
    G:
     * skapar quantity med heltal för G
     * skapar quantity med decimaltal för G
     * skapar quantity med noll G
     * kastar exception för negativt tal med G
     * hanterar stor vikt för G korrekt
     * hanterar liten vikt för G korrekt
    HG:
     * skapar quantity med heltal för HG
     * skapar quantity med decimaltal för HG
     * skapar quantity med noll HG
     * kastar exception för negativt tal med HG
     * hanterar stor vikt för HG korrekt
     * hanterar liten vikt för HG korrekt
     */


    @Test
    @DisplayName("Create quantity with whole number for PIECE")
    void createsQuantityWithWholeNumberForPiece(){
        Quantity quantity = new Quantity(5, org.example.Product.Unit.PIECE);
        assert(quantity.getAmount() == 5);
        assert(quantity.getUnit() == org.example.Product.Unit.PIECE);
    }

    @Test
    @DisplayName("Create quantity with 0 for PIECE")
    void createsQuantityWithZeroForPiece(){
        Quantity quantity = new Quantity(0, org.example.Product.Unit.PIECE);
        assert(quantity.getAmount() == 0);
        assert(quantity.getUnit() == org.example.Product.Unit.PIECE);
    }

    @Test
    @DisplayName("Create quantity with large whole number for PIECE")
    void createsQuantityWithLargeWholeNumberForPiece(){
        Quantity quantity = new Quantity(1000000, org.example.Product.Unit.PIECE);
        assert(quantity.getAmount() == 1000000);
        assert(quantity.getUnit() == org.example.Product.Unit.PIECE);
    }

    @Test
    @DisplayName("Throws exception for decimal number with PIECE")
    void throwsExceptionForDecimalNumberWithPiece(){
        try {
            @SuppressWarnings("unused")
            Quantity quantity = new Quantity(2.5, org.example.Product.Unit.PIECE);
            assert(false); // Should not reach this point
        } catch (IllegalArgumentException e) {
            assert(e.getMessage().equals("Amount must be a whole number for PIECE unit."));
        }
    }

    @Test
    @DisplayName("Throws exception for negative number with PIECE")
    void throwsExceptionForNegativeNumberWithPiece(){
        try {
            @SuppressWarnings("unused")
            Quantity quantity = new Quantity(-3, org.example.Product.Unit.PIECE);
            assert(false); // Should not reach this point
        } catch (IllegalArgumentException e) {
            assert(e.getMessage().equals("Amount cannot be negative."));
        }
    }

    @Test
    @DisplayName("Accepts 1.0 as whole number for PIECE")
    void acceptsOnePointZeroAsWholeNumberForPiece(){
        Quantity quantity = new Quantity(1.0, org.example.Product.Unit.PIECE);
        assert(quantity.getAmount() == 1.0);
        assert(quantity.getUnit() == org.example.Product.Unit.PIECE);
    }

    @Test
    @DisplayName("Throws exception for very small decimal number for PIECE")
    void throwsExceptionForVerySmallDecimalNumberForPiece(){
        try {
            @SuppressWarnings("unused")
            Quantity quantity = new Quantity(1.00001, org.example.Product.Unit.PIECE);
            assert(false); // Should not reach this point
        } catch (IllegalArgumentException e) {
            assert(e.getMessage().equals("Amount must be a whole number for PIECE unit."));
        }
    }

    @Test
    @DisplayName("Create quantity with decimal number for KG")
    void createsQuantityWithDecimalNumberForKg(){
        Quantity quantity = new Quantity(2.5, org.example.Product.Unit.KG);
        assert(quantity.getAmount() == 2.5);
        assert(quantity.getUnit() == org.example.Product.Unit.KG);
    }

    @Test
    @DisplayName("Create quantity with minimum 0.001 for KG")
    void createsQuantityWithMinimumForKg(){
        Quantity quantity = new Quantity(0.001, org.example.Product.Unit.KG);
        assert(quantity.getAmount() == 0.001);
        assert(quantity.getUnit() == org.example.Product.Unit.KG);
    }

    @Test
    @DisplayName("Throws exception for weight under minimum for KG")
    void throwsExceptionForWeightUnderMinimumForKg(){
        try {
            @SuppressWarnings("unused")
            Quantity quantity = new Quantity(0.0009, org.example.Product.Unit.KG);
            assert(false); // Should not reach this point
        } catch (IllegalArgumentException e) {
            assert(e.getMessage().equals("Weight must be at least 0.001 kg."));
        }
    }

    @Test
    @DisplayName("Create quantity with large whole number for KG")
    void createsQuantityWithLargeWholeNumberForKg(){
        Quantity quantity = new Quantity(1000000, org.example.Product.Unit.KG);
        assert(quantity.getAmount() == 1000000);
        assert(quantity.getUnit() == org.example.Product.Unit.KG);
    }

    @Test
    @DisplayName("Create quantity with large decimal number for KG")
    void createsQuantityWithLargeDecimalNumberForKg(){
        Quantity quantity = new Quantity(123456.789, org.example.Product.Unit.KG);
        assert(quantity.getAmount() == 123456.789);
        assert(quantity.getUnit() == org.example.Product.Unit.KG);
    }

    @Test
    @DisplayName("Throws exception for negative number with KG")
    void throwsExceptionForNegativeNumberWithKg(){
        try {
            @SuppressWarnings("unused")
            Quantity quantity = new Quantity(-3.5, org.example.Product.Unit.KG);
            assert(false); // Should not reach this point
        } catch (IllegalArgumentException e) {
            assert(e.getMessage().equals("Amount cannot be negative."));
        }
    }

    @Test
    @DisplayName("Handles large weight for KG correctly")
    void handlesLargeWeightForKgCorrectly(){
        Quantity quantity = new Quantity(1_000_000, org.example.Product.Unit.KG);
        assert(quantity.getAmount() == 1_000_000);
        assert(quantity.getUnit() == org.example.Product.Unit.KG);
    }
    
    @Test
    @DisplayName("Create quantity with whole number for G")
    void createsQuantityWithWholeNumberForG(){
        Quantity quantity = new Quantity(5000, org.example.Product.Unit.G);
        assert(quantity.getAmount() == 5000);
        assert(quantity.getUnit() == org.example.Product.Unit.G);
    }

    @Test
    @DisplayName("Create quantity with decimal number for G")
    void createsQuantityWithDecimalNumberForG(){
        Quantity quantity = new Quantity(2500.5, org.example.Product.Unit.G);
        assert(quantity.getAmount() == 2500.5);
        assert(quantity.getUnit() == org.example.Product.Unit.G);
    }

    @Test
    @DisplayName("Create quantity with 0 for G")
    void createsQuantityWithZeroForG(){
        Quantity quantity = new Quantity(0, org.example.Product.Unit.G);
        assert(quantity.getAmount() == 0);
        assert(quantity.getUnit() == org.example.Product.Unit.G);
    }

    @Test
    @DisplayName("Throws exception for negative number with G")
    void throwsExceptionForNegativeNumberWithG(){
        try {
            @SuppressWarnings("unused")
            Quantity quantity = new Quantity(-100, org.example.Product.Unit.G);
            assert(false); // Should not reach this point
        } catch (IllegalArgumentException e) {
            assert(e.getMessage().equals("Amount cannot be negative."));
        }
    }

    @Test
    @DisplayName("Handles large weight for G correctly")
    void handlesLargeWeightForGCorrectly(){
        Quantity quantity = new Quantity(1_000_000, org.example.Product.Unit.G);
        assert(quantity.getAmount() == 1_000_000);
        assert(quantity.getUnit() == org.example.Product.Unit.G);
    }

    @Test
    @DisplayName("Handles small weight for G correctly")
    void handlesSmallWeightForGCorrectly(){
        Quantity quantity = new Quantity(0.001, org.example.Product.Unit.G);
        assert(quantity.getAmount() == 0.001);
        assert(quantity.getUnit() == org.example.Product.Unit.G);
    }

    @Test
    @DisplayName("Create quantity with whole number for HG")
    void createsQuantityWithWholeNumberForHg(){
        Quantity quantity = new Quantity(500, org.example.Product.Unit.HG);
        assert(quantity.getAmount() == 500);
        assert(quantity.getUnit() == org.example.Product.Unit.HG);
    }

    @Test
    @DisplayName("Create quantity with decimal number for HG")
    void createsQuantityWithDecimalNumberForHg(){
        Quantity quantity = new Quantity(250.5, org.example.Product.Unit.HG);
        assert(quantity.getAmount() == 250.5);
        assert(quantity.getUnit() == org.example.Product.Unit.HG);
    }

    @Test
    @DisplayName("Create quantity with 0 for HG")
    void createsQuantityWithZeroForHg(){
        Quantity quantity = new Quantity(0, org.example.Product.Unit.HG);
        assert(quantity.getAmount() == 0);
        assert(quantity.getUnit() == org.example.Product.Unit.HG);
    }

    @Test
    @DisplayName("Throws exception for negative number with HG")
    void throwsExceptionForNegativeNumberWithHg(){
        try {
            @SuppressWarnings("unused")
            Quantity quantity = new Quantity(-100, org.example.Product.Unit.HG);
            assert(false); // Should not reach this point
        } catch (IllegalArgumentException e) {
            assert(e.getMessage().equals("Amount cannot be negative."));
        }
    }

    @Test
    @DisplayName("Handles large weight for HG correctly")
    void handlesLargeWeightForHgCorrectly(){
        Quantity quantity = new Quantity(1_000_000, org.example.Product.Unit.HG);
        assert(quantity.getAmount() == 1_000_000);
        assert(quantity.getUnit() == org.example.Product.Unit.HG);
    }

    @Test
    @DisplayName("Handles small weight for HG correctly")
    void handlesSmallWeightForHgCorrectly(){
        Quantity quantity = new Quantity(0.1, org.example.Product.Unit.HG);
        assert(quantity.getAmount() == 0.1);
        assert(quantity.getUnit() == org.example.Product.Unit.HG);
    }

}
