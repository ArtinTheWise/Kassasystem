import org.example.Money;
import org.example.Product.Unit;
import org.example.Product.WeightPrice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class WeightPriceTest {

    /*
     * Kan skapa en WeightPrice med kilogram som enhet
     * Kan skapa en WeightPrice med hektogram som enhet
     * Kan skapa en WeightPrice med gram som enhet
     * Beräkna pris för 1 kilogram korrekt
     * beräkna pris för flera kilogram korrekt
     * beräkna pris för decimala kilogram korrekt
     * beräknar pris för hektogram korrekt
     * beräkna pris för flera hektogram korrekt
     * beräkna pris för decimala hektogram korrekt
     * beräkna pris för gram korrekt
     * beräkna pris för flera gram korrekt
     * beräkna pris för decimala gram korrekt
     * kasta undantag när kvantitetens enhet inte stämmer överens med prismodellens enhet
     * kastar undantag för piece enhet
     * hanterar noll kvantitet korrekt
     * hanterar mycket stora kvantiteter korrekt
     * hanterar mycket små vikter korrekt
     * beräkna pris för negativa kilogram kastar undantag
     * kastar undantag för noll pris per kilogram
     * kastar undantag för negativt pris per kilogram
     * kastar undantag för noll pris per hektogram
     * kastar undantag för negativt pris per hektogram
     * kastar undantag för noll pris per gram
     * kastar undantag för negativt pris per gram
     * Kastar undantag om enhet är null
     * Kastar undantag om pris per enhet är null
     * minimum 1 gram
     * minimum 0.1 hektogram
     * minimum 0.001 kilogram
     * 
     * REDUCERA ANTALET TESTFALL?
     */

    @Test
    @DisplayName("Constructor: PIECE throws exception")
    void constructorPieceThrowsException(){
        Money pricePerPiece = new Money(100); // 1.00 kr per piece
        try {
            @SuppressWarnings("unused")
            WeightPrice weightPrice = new WeightPrice(pricePerPiece, Unit.PIECE);
            assert(false);
        } catch (IllegalArgumentException e) {
            assert(true);
        }
    }
    @Test
    @DisplayName("calculatePrice: amount < 0.001 throws exception")
    void calculatePriceAmountLessThanMinimumThrowsException(){    
        Money pricePerKg = new Money(1000); // 10.00 kr per kg
        WeightPrice weightPrice = new WeightPrice(pricePerKg, Unit.KG);
        org.example.Product.Quantity quantity = new org.example.Product.Quantity (0.0009, Unit.KG);

        try {
            weightPrice.calculatePrice(quantity);
            assert(false); 
        } catch (IllegalArgumentException e) {
            assert(true);
        }
    }


    @Test
    @DisplayName("Can create WeightPrice with kilogram as unit")
        void canCreateWeightPriceWithKilogramAsUnit(){
            Money pricePerKg = new Money(1000); // 10.00 kr per kg
            WeightPrice weightPrice = new WeightPrice(pricePerKg, Unit.KG);
            assert(weightPrice != null);
        }
    

    @Test
    @DisplayName("Can create WeightPrice with hectogram as unit")
        void canCreateWeightPriceWithHectogramAsUnit(){
            Money pricePerHg = new Money(100); // 1.00 kr per hg
            WeightPrice weightPrice = new WeightPrice(pricePerHg, Unit.HG);
            assert(weightPrice != null);
        }

    @Test
    @DisplayName("Can create WeightPrice with gram as unit")
        void canCreateWeightPriceWithGramAsUnit(){
            Money pricePerG = new Money(10); // 0.10 kr per g
            WeightPrice weightPrice = new WeightPrice(pricePerG, Unit.G);
            assert(weightPrice != null);
        }

    @Test
    @DisplayName("Calculate price for one kilogram correctly")
        void calculatesCorrectPriceForOneKilogram(){
            Money pricePerKg = new Money(2000); // 20.00 kr per kg
            WeightPrice weightPrice = new WeightPrice(pricePerKg, Unit.KG);
            org.example.Product.Quantity quantity = new org.example.Product.Quantity (1, Unit.KG);

            Money expectedPrice = new Money(2000); // 20.00 kr
            Money actualPrice = weightPrice.calculatePrice(quantity);

            assert(expectedPrice.equals(actualPrice));
        }
        
    @Test
    @DisplayName("Calculate price for multiple kilograms correctly")
        void calculatesCorrectPriceForMultipleKilograms(){
            Money pricePerKg = new Money(1500); // 15.00 kr per kg
            WeightPrice weightPrice = new WeightPrice(pricePerKg, Unit.KG);
            org.example.Product.Quantity quantity = new org.example.Product.Quantity (3, Unit.KG);

            Money expectedPrice = new Money(4500); // 45.00 kr
            Money actualPrice = weightPrice.calculatePrice(quantity);

            assert(expectedPrice.equals(actualPrice));
        }

    @Test
    @DisplayName("Calculate price correct for decimal kilograms")
        void calculatesCorrectPriceForDecimalKilograms(){
            Money pricePerKg = new Money(1999); // 19.99 kr per kg
            WeightPrice weightPrice = new WeightPrice(pricePerKg, Unit.KG);
            org.example.Product.Quantity quantity = new org.example.Product.Quantity (2.5, Unit.KG);

            Money expectedPrice = new Money(4998); // 49.98 kr
            Money actualPrice = weightPrice.calculatePrice(quantity);

            assert(expectedPrice.equals(actualPrice));
        }

    @Test
    @DisplayName("Calculate price for one hectogram correctly")
        void calculatesCorrectPriceForOneHectogram(){
            Money pricePerHg = new Money(100); // 1.00 kr per hg
            WeightPrice weightPrice = new WeightPrice(pricePerHg, Unit.HG);
            org.example.Product.Quantity quantity = new org.example.Product.Quantity (1, Unit.HG);

            Money expectedPrice = new Money(100); // 1.00 kr
            Money actualPrice = weightPrice.calculatePrice(quantity);

            assert(expectedPrice.equals(actualPrice));
        }

    @Test
    @DisplayName("Calculate price for multiple hectograms correctly")
        void calculatesCorrectPriceForMultipleHectograms(){
            Money pricePerHg = new Money(150); // 1.50 kr per hg
            WeightPrice weightPrice = new WeightPrice(pricePerHg, Unit.HG);
            org.example.Product.Quantity quantity = new org.example.Product.Quantity (3, Unit.HG);

            Money expectedPrice = new Money(450); // 4.50 kr
            Money actualPrice = weightPrice.calculatePrice(quantity);

            assert(expectedPrice.equals(actualPrice));
        }

    @Test
    @DisplayName("Calculate price correct for decimal hectograms")
        void calculatesCorrectPriceForDecimalHectograms(){
            Money pricePerHg = new Money(199); // 1.99 kr per hg
            WeightPrice weightPrice = new WeightPrice(pricePerHg, Unit.HG);
            org.example.Product.Quantity quantity = new org.example.Product.Quantity (2.5, Unit.HG);

            Money expectedPrice = new Money(498); // 4.98 kr
            Money actualPrice = weightPrice.calculatePrice(quantity);

            assert(expectedPrice.equals(actualPrice));
        }

    @Test
    @DisplayName("Calculate price for one gram correctly")
        void calculatesCorrectPriceForOneGram(){
            Money pricePerG = new Money(10); // 0.10 kr per g
            WeightPrice weightPrice = new WeightPrice(pricePerG, Unit.G);
            org.example.Product.Quantity quantity = new org.example.Product.Quantity (1, Unit.G);

            Money expectedPrice = new Money(10); // 0.10 kr
            Money actualPrice = weightPrice.calculatePrice(quantity);

            assert(expectedPrice.equals(actualPrice));
        }

    @Test
    @DisplayName("Calculate price for multiple grams correctly")
        void calculatesCorrectPriceForMultipleGrams(){
            Money pricePerG = new Money(15); // 0.15 kr per g
            WeightPrice weightPrice = new WeightPrice(pricePerG, Unit.G);
            org.example.Product.Quantity quantity = new org.example.Product.Quantity (3, Unit.G);

            Money expectedPrice = new Money(45); // 0.45 kr
            Money actualPrice = weightPrice.calculatePrice(quantity);

            assert(expectedPrice.equals(actualPrice));
        }

    @Test
    @DisplayName("Calculate price correct for decimal grams")
        void calculatesCorrectPriceForDecimalGrams(){
            Money pricePerG = new Money(199); // 1.99 kr per g
            WeightPrice weightPrice = new WeightPrice(pricePerG, Unit.G);
            org.example.Product.Quantity quantity = new org.example.Product.Quantity (2.5, Unit.G);

            Money expectedPrice = new Money(498); // 4.98 kr
            Money actualPrice = weightPrice.calculatePrice(quantity);

            assert(expectedPrice.equals(actualPrice));
        }

    @Test
    @DisplayName("Throws exception when quantity unit does not match price model unit")
        void throwsExceptionWhenQuantityUnitDoesNotMatchPriceModelUnit(){
            Money pricePerKg = new Money(1000); // 10.00 kr per kg
            WeightPrice weightPrice = new WeightPrice(pricePerKg, Unit.KG);
            org.example.Product.Quantity quantity = new org.example.Product.Quantity (1, Unit.HG);

            try {
                weightPrice.calculatePrice(quantity);
                assert(false); // Fail the test if no exception is thrown
            } catch (IllegalArgumentException e) {
                assert(true); // Test passes if exception is thrown
            }
        }

    @Test
    @DisplayName("Throws exception for piece unit")
        void throwsExceptionForPieceUnit(){
            Money pricePerKg = new Money(1000); // 10.00 kr per kg
            WeightPrice weightPrice = new WeightPrice(pricePerKg, Unit.KG);
            org.example.Product.Quantity quantity = new org.example.Product.Quantity (1, Unit.PIECE);

            try {
                weightPrice.calculatePrice(quantity);
                assert(false); // PIECE should not reach here
            } catch (IllegalArgumentException e) {
                assert(true); // expected exception
            }
        }


    @Test
    @DisplayName("Handles very large quantities correctly")
        void handlesVeryLargeQuantitiesCorrectly(){
            Money pricePerKg = new Money(1000); // 10.00 kr per kg
            WeightPrice weightPrice = new WeightPrice(pricePerKg, Unit.KG);
            org.example.Product.Quantity quantity = new org.example.Product.Quantity (1_000_000, Unit.KG);

            Money expectedPrice = new Money(1_000_000_000); // 10,000,000.00 kr
            Money actualPrice = weightPrice.calculatePrice(quantity);

            assert(expectedPrice.equals(actualPrice));
        }

    @Test
    @DisplayName("Handles very small weights correctly")
        void handlesVerySmallWeightsCorrectly(){
            Money pricePerG = new Money(1); // 0.01 kr per g
            WeightPrice weightPrice = new WeightPrice(pricePerG, Unit.G);
            org.example.Product.Quantity quantity = new org.example.Product.Quantity (2, Unit.G);

            Money expectedPrice = new Money(2); // 0.02 kr
            Money actualPrice = weightPrice.calculatePrice(quantity);

            assert(expectedPrice.equals(actualPrice));
        }

    @Test
    @DisplayName("Calculate price for negative kilograms throws exception")
        void calculatesCorrectPriceForNegativeKilogramsThrowsException(){
            Money pricePerKg = new Money(1000); // 10.00 kr per kg
            WeightPrice weightPrice = new WeightPrice(pricePerKg, Unit.KG);
            try {
                org.example.Product.Quantity quantity = new org.example.Product.Quantity (-1, Unit.KG);
                weightPrice.calculatePrice(quantity);
                assert(false); // Negative quantity should not reach here
            } catch (IllegalArgumentException e) {
                assert(true); // expected exception
            }
        }

    @Test
    @DisplayName("Throws exception for zero price per kilogram")
        void throwsExceptionForZeroPricePerKilogram(){
            try {
                Money pricePerKg = new Money(0); // 0.00 kr per kg
                @SuppressWarnings("unused")
                WeightPrice weightPrice = new WeightPrice(pricePerKg, Unit.KG);
                assert(false); // Zero price should not reach here
            } catch (IllegalArgumentException e) {
                assert(true); // expected exception
            }
        }

    @Test
    @DisplayName("Throws exception for negative price per kilogram")
        void throwsExceptionForNegativePricePerKilogram(){
            try {
                Money pricePerKg = new Money(-100); // -1.00 kr per kg
                @SuppressWarnings("unused")
                WeightPrice weightPrice = new WeightPrice(pricePerKg, Unit.KG);
                assert(false); // Negative price should not reach here
            } catch (IllegalArgumentException e) {
                assert(true); // expected exception
            }
        }
        
    @Test
    @DisplayName("Throws exception for zero price per hectogram")
        void throwsExceptionForZeroPricePerHectogram(){
            try {
                Money pricePerHg = new Money(0); // 0.00 kr per hg
                @SuppressWarnings("unused")
                WeightPrice weightPrice = new WeightPrice(pricePerHg, Unit.HG);
                assert(false); // Zero price should not reach here
            } catch (IllegalArgumentException e) {
                assert(true); // expected exception
            }
        }

    @Test
    @DisplayName("Throws exception for negative price per hectogram")
        void throwsExceptionForNegativePricePerHectogram(){
            try {
                Money pricePerHg = new Money(-100); // -1.00 kr per hg
                @SuppressWarnings("unused")
                WeightPrice weightPrice = new WeightPrice(pricePerHg, Unit.HG);
                assert(false); // Negative price should not reach here
            } catch (IllegalArgumentException e) {
                assert(true); // expected exception
            }
        }

    @Test
    @DisplayName("Throws exception for zero price per gram")
        void throwsExceptionForZeroPricePerGram(){
            try {
                Money pricePerG = new Money(0); // 0.00 kr per g
                @SuppressWarnings("unused")
                WeightPrice weightPrice = new WeightPrice(pricePerG, Unit.G);
                assert(false); // Zero price should not reach here
            } catch (IllegalArgumentException e) {
                assert(true); // expected exception
            }
        }

    @Test
    @DisplayName("Throws exception for negative price per gram")
        void throwsExceptionForNegativePricePerGram(){
            try {
                Money pricePerG = new Money(-10); // -0.10 kr per g
                @SuppressWarnings("unused")
                WeightPrice weightPrice = new WeightPrice(pricePerG, Unit.G);
                assert(false); // Negative price should not reach here
            } catch (IllegalArgumentException e) {
                assert(true); // expected exception
            }
        }

    @Test
    @DisplayName("Throws exception if unit is null")
        void throwsExceptionIfUnitIsNull(){
            try {
                Money pricePerKg = new Money(1000); // 10.00 kr per kg
                @SuppressWarnings("unused")
                WeightPrice weightPrice = new WeightPrice(pricePerKg, null);
                assert(false); // Null unit should not reach here
            } catch (IllegalArgumentException e) {
                assert(true); // expected exception
            }
        }

    @Test
    @DisplayName("Throws exception if price per unit is null")
        void throwsExceptionIfPricePerUnitIsNull(){
            try {
                @SuppressWarnings("unused")
                WeightPrice weightPrice = new WeightPrice(null, Unit.KG);
                assert(false); // Null price should not reach here
            } catch (IllegalArgumentException e) {
                assert(true); // expected exception
            }
        }

    
}
