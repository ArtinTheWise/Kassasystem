package org.example.Product;

import org.example.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.example.Product.Unit.*;
import static org.example.Product.VatRate.*;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    private Product candyProduct;
    private Product snusProduct;
    private Product bookProduct;
    private Product bulkCandyKG;
    private Product bulkCandyHG;
    private Product bulkCandyG;

    @BeforeEach
    void setUp() {
        candyProduct = new Product("Ahlgrens Bilar", new UnitPrice(new Money(1500)), new ProductGroup("Godis"), FOOD, false);
        snusProduct = new Product("Snus", new UnitPrice(new Money(5000)), OTHER, true);
        bookProduct = new Product("Bok", new UnitPrice(new Money(150000)), BOOKSANDPAPERS);
        bulkCandyKG = new Product("Godis Lösvikt", new WeightPrice(new Money(8900), KG), new ProductGroup("Godis"), FOOD, false);
        bulkCandyHG = new Product("Godis Lösvikt", new WeightPrice(new Money(700), HG), new ProductGroup("Godis"), FOOD, false);
        bulkCandyG = new Product("Godis Lösvikt", new WeightPrice(new Money(5), G), new ProductGroup("Godis"), FOOD, false);
    }

    @Test
    void constructorsSetAllFieldsCorrectly() {
        assertEquals("Ahlgrens Bilar", candyProduct.getName());
        assertInstanceOf(UnitPrice.class, candyProduct.getPriceModel());
        assertEquals("Godis", candyProduct.getProductGroup().getName());
        assertEquals(FOOD, candyProduct.getVatRate());
        assertFalse(candyProduct.getAgeRestriction());

        assertEquals("Snus", snusProduct.getName());
        assertInstanceOf(UnitPrice.class, snusProduct.getPriceModel());
        assertNull(snusProduct.getProductGroup());
        assertEquals(OTHER, snusProduct.getVatRate());
        assertTrue(snusProduct.getAgeRestriction());

        assertEquals("Bok", bookProduct.getName());
        assertInstanceOf(UnitPrice.class, bookProduct.getPriceModel());
        assertNull(bookProduct.getProductGroup());
        assertEquals(BOOKSANDPAPERS, bookProduct.getVatRate());
        assertFalse(bookProduct.getAgeRestriction());
    }

    @Test
    void constructorsThrowExceptionForNullName() {
        assertThrows(IllegalArgumentException.class, () ->
                new Product(null, new UnitPrice(new Money(1500)), new ProductGroup("Godis"), FOOD, false)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Product(null, new UnitPrice(new Money(1500)), FOOD, false)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Product(null, new UnitPrice(new Money(1500)), FOOD)
        );
    }

    @Test
    void constructorsThrowExceptionForNullPriceModel() {
        assertThrows(IllegalArgumentException.class, () ->
                new Product("Ahlgrens Bilar", null, new ProductGroup("Godis"), FOOD, false)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Product("Ahlgrens Bilar", null, FOOD, false)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Product("Ahlgrens Bilar", null, FOOD)
        );
    }

    @Test
    void constructorsThrowExceptionForNullVatRate() {
        assertThrows(IllegalArgumentException.class, () ->
                new Product("Ahlgrens Bilar", new UnitPrice(new Money(1500)), new ProductGroup("Godis"), null, false)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Product("Ahlgrens Bilar", new UnitPrice(new Money(1500)), null, false)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Product("Ahlgrens Bilar", new UnitPrice(new Money(1500)), null)
        );
    }


    @Test
    void calculatePriceReturnsCorrectPrice() {
        Quantity quantity = new Quantity(3, Unit.PIECE);

        Money expected = new Money(4500);
        Money actual = candyProduct.calculatePrice(quantity);

        assertEquals(expected, actual);
    }

    @Test
    void calculatePriceWithVatReturnsCorrectPriceForFOOD() {
        Quantity quantity = new Quantity(3, Unit.PIECE);

        double expectedWithVat = 4500 * (1 + FOOD.getRate() / 100.0);
        Money expected = new Money(Math.round(expectedWithVat));
        Money actual = candyProduct.calculatePriceWithVat(quantity);

        assertEquals(expected, actual);
    }

    @Test
    void calculatePriceWithVatReturnsCorrectPriceForOTHER() {
        Quantity quantity = new Quantity(3, Unit.PIECE);

        double expectedWithVat = 15000 * (1 + OTHER.getRate() / 100.0);
        Money expected = new Money(Math.round(expectedWithVat));
        Money actual = snusProduct.calculatePriceWithVat(quantity);

        assertEquals(expected, actual);
    }

    @Test
    void calculatePriceWithVatReturnsCorrectPriceForBOOKSANDPAPERS() {
        Quantity quantity = new Quantity(2, Unit.PIECE);

        double expectedWithVat = 300000 * (1 + BOOKSANDPAPERS.getRate() / 100.0);
        Money expected = new Money(Math.round(expectedWithVat));
        Money actual = bookProduct.calculatePriceWithVat(quantity);

        assertEquals(expected, actual);
    }

    @Test
    void calculatePriceWithVatReturnsCorrectPriceForWeightKG() {
        Quantity quantity = new Quantity(0.1, Unit.KG);

        double expectedWithVat = 890 * (1 + FOOD.getRate() / 100.0);
        Money expected = new Money(Math.round(expectedWithVat));
        Money actual = bulkCandyKG.calculatePriceWithVat(quantity);

        assertEquals(expected, actual);
    }

    @Test
    void calculatePriceWithVatReturnsCorrectPriceForWeightHG() {
        Quantity quantity = new Quantity(3, Unit.HG);

        double expectedWithVat = 2100 * (1 + FOOD.getRate() / 100.0);
        Money expected = new Money(Math.round(expectedWithVat));
        Money actual = bulkCandyHG.calculatePriceWithVat(quantity);

        assertEquals(expected, actual);
    }

    @Test
    void calculatePriceWithVatReturnsCorrectPriceForWeightG() {
        Quantity quantity = new Quantity(300, Unit.G);

        double expectedWithVat = 1500 * (1 + FOOD.getRate() / 100.0);
        Money expected = new Money(Math.round(expectedWithVat));
        Money actual = bulkCandyG.calculatePriceWithVat(quantity);

        assertEquals(expected, actual);
    }

    @Test
    void equalsReturnsTrueForObjectsWithTheSameName() {
        Product product = new Product("Test", new UnitPrice(new Money(1500)), new ProductGroup("Godis"), FOOD, false);
        Product productTwo = new Product("Test", new UnitPrice(new Money(5000)), OTHER, true);

        assertEquals(product, productTwo);
    }

    @Test
    void equalsReturnsFalseForObjectsWithDifferentNames() {
        Product product = new Product("Test1", new UnitPrice(new Money(1500)), new ProductGroup("Godis"), FOOD, false);
        Product productTwo = new Product("Test2", new UnitPrice(new Money(1500)), new ProductGroup("Godis"), FOOD, false);

        assertNotEquals(product, productTwo);
    }

    @Test
    void equalsReturnsTrueForTheSameObject() {
        assertTrue(candyProduct.equals(candyProduct));
    }

    @Test
    void equalsReturnsFalseForNull() {
        assertFalse(candyProduct.equals(null));
    }

    @Test
    void equalsReturnsFalseForDifferentType() {
        assertFalse(candyProduct.equals("Not a Product"));
    }

    @Test
    void sameProductHasTheSameHashCode() {
        Product productTwo = new Product("Ahlgrens Bilar", new UnitPrice(new Money(1500)), new ProductGroup("Godis"), FOOD, false);

        assertEquals(candyProduct.hashCode(), productTwo.hashCode());
    }

    @Test
    void toStringReturnsCorrectFormat() {
        String productString = candyProduct.toString();
        assertTrue(productString.contains("name='Ahlgrens Bilar'"));
        assertTrue(productString.contains("priceModel="));
        assertTrue(productString.contains("productGroup=Godis"));
        assertTrue(productString.contains("vatRate=FOOD"));
        assertTrue(productString.contains("ageRestriction=false"));

        String productTwoString = snusProduct.toString();
        assertTrue(productTwoString.contains("name='Snus'"));
        assertTrue(productTwoString.contains("productGroup=none"));
        assertTrue(productTwoString.contains("vatRate=OTHER"));
        assertTrue(productTwoString.contains("ageRestriction=true"));

        String productThreeString = bookProduct.toString();
        assertTrue(productThreeString.contains("name='Bok'"));
        assertTrue(productThreeString.contains("productGroup=none"));
        assertTrue(productThreeString.contains("vatRate=BOOKSANDPAPERS"));
        assertTrue(productThreeString.contains("ageRestriction=false"));
    }
}