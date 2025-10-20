package org.example;

import org.example.Product.*;
import org.junit.jupiter.api.Test;

import static org.example.Product.Unit.*;
import static org.example.Product.VatRate.*;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    @Test
    void constructorsSetAllFieldsCorrectly() {
        Product product = new Product("Ahlgrens Bilar", new UnitPrice(new Money(1500)), new ProductGroup("Godis"), FOOD, false);
        Product productTwo = new Product("Snus", new UnitPrice(new Money(5000)), OTHER, true);
        Product productThree = new Product("Bok", new UnitPrice(new Money(150000)), BOOKSANDPAPERS);

        assertEquals("Ahlgrens Bilar", product.getName());
        assertInstanceOf(UnitPrice.class, product.getPriceModel());
        assertEquals("Godis", product.getProductGroup().getName());
        assertEquals(FOOD, product.getVatRate());
        assertFalse(product.getAgeRestriction());

        assertEquals("Snus", productTwo.getName());
        assertInstanceOf(UnitPrice.class, productTwo.getPriceModel());
        assertNull(productTwo.getProductGroup());
        assertEquals(OTHER, productTwo.getVatRate());
        assertTrue(productTwo.getAgeRestriction());

        assertEquals("Bok", productThree.getName());
        assertInstanceOf(UnitPrice.class, productThree.getPriceModel());
        assertNull(productThree.getProductGroup());
        assertEquals(BOOKSANDPAPERS, productThree.getVatRate());
        assertFalse(productThree.getAgeRestriction());
    }

    @Test
    void calculatePriceReturnsCorrectPrice() {
        Product product = new Product("Ahlgrens Bilar", new UnitPrice(new Money(1500)), new ProductGroup("Godis"), FOOD, false);
        Quantity quantity = new Quantity(3, Unit.PIECE);

        Money expected = new Money(4500);
        Money actual = product.calculatePrice(quantity);

        assertEquals(expected, actual);
    }

    @Test
    void calculatePriceWithVatReturnsCorrectPriceForFOOD() {
        Product product = new Product("Ahlgrens Bilar", new UnitPrice(new Money(1500)), new ProductGroup("Godis"), FOOD, false);
        Quantity quantity = new Quantity(3, Unit.PIECE);

        double expectedWithVat = 4500 * (1 + FOOD.getRate() / 100.0);
        Money expected = new Money(Math.round(expectedWithVat));
        Money actual = product.calculatePriceWithVat(quantity);

        assertEquals(expected, actual);
    }

    @Test
    void calculatePriceWithVatReturnsCorrectPriceForOTHER() {
        Product product = new Product("Snus", new UnitPrice(new Money(5000)), OTHER, true);
        Quantity quantity = new Quantity(3, Unit.PIECE);

        double expectedWithVat = 15000 * (1 + OTHER.getRate() / 100.0);
        Money expected = new Money(Math.round(expectedWithVat));
        Money actual = product.calculatePriceWithVat(quantity);

        assertEquals(expected, actual);
    }

    @Test
    void calculatePriceWithVatReturnsCorrectPriceForBOOKSANDPAPERS() {
        Product product = new Product("Bok", new UnitPrice(new Money(150000)), BOOKSANDPAPERS, false);
        Quantity quantity = new Quantity(2, Unit.PIECE);

        double expectedWithVat = 300000 * (1 + BOOKSANDPAPERS.getRate() / 100.0);
        Money expected = new Money(Math.round(expectedWithVat));
        Money actual = product.calculatePriceWithVat(quantity);

        assertEquals(expected, actual);
    }

    @Test
    void calculatePriceWithVatReturnsCorrectPriceForWeightKG() {
        Product product = new Product("Godis Lösvikt", new WeightPrice(new Money(8900), KG), new ProductGroup("Godis"), FOOD, false);
        Quantity quantity = new Quantity(0.1, Unit.KG);

        double expectedWithVat = 890 * (1 + FOOD.getRate() / 100.0);
        Money expected = new Money(Math.round(expectedWithVat));
        Money actual = product.calculatePriceWithVat(quantity);

        assertEquals(expected, actual);
    }

    @Test
    void calculatePriceWithVatReturnsCorrectPriceForWeightHG() {
        Product product = new Product("Godis Lösvikt", new WeightPrice(new Money(700), HG), new ProductGroup("Godis"), FOOD, false);
        Quantity quantity = new Quantity(3, Unit.HG);

        double expectedWithVat = 2100 * (1 + FOOD.getRate() / 100.0);
        Money expected = new Money(Math.round(expectedWithVat));
        Money actual = product.calculatePriceWithVat(quantity);

        assertEquals(expected, actual);
    }

    @Test
    void calculatePriceWithVatReturnsCorrectPriceForWeightG() {
        Product product = new Product("Godis Lösvikt", new WeightPrice(new Money(5), G), new ProductGroup("Godis"), FOOD, false);
        Quantity quantity = new Quantity(300, Unit.G);

        double expectedWithVat = 1500 * (1 + FOOD.getRate() / 100.0);
        Money expected = new Money(Math.round(expectedWithVat));
        Money actual = product.calculatePriceWithVat(quantity);

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
        Product product = new Product("Ahlgrens Bilar", new UnitPrice(new Money(1500)), new ProductGroup("Godis"), FOOD, false);

        assertTrue(product.equals(product));
    }

    @Test
    void sameProductHasTheSameHashCode() {
        Product product = new Product("Ahlgrens Bilar", new UnitPrice(new Money(1500)), new ProductGroup("Godis"), FOOD, false);
        Product productTwo = new Product("Ahlgrens Bilar", new UnitPrice(new Money(1500)), new ProductGroup("Godis"), FOOD, false);

        assertEquals(product.hashCode(), productTwo.hashCode());
    }
}