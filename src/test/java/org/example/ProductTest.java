package org.example;

import org.example.Product.Product;
import org.example.Product.ProductGroup;
import org.example.Product.UnitPrice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.example.Product.VatRate.OTHER;
import static org.example.Product.VatRate.FOOD;

public class ProductTest {

    @Test
    void constructorsSetAllFieldsCorrectly() {
        Product product = new Product("Ahlgrens Bilar", new UnitPrice(new Money(1500)), new ProductGroup("Godis"), FOOD, false);
        Product productTwo = new Product("Snus", new UnitPrice(new Money(5000)), OTHER, true);

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
    }

    @Test
    void calculatePriceReturnsCorrectPrice() {
        Product product = new Product("Ahlgrens Bilar", new UnitPrice(new Money(1500)), new ProductGroup("Godis"), FOOD, false);


    }

    @Test
    void calculatePriceWithVatReturnsCorrectPrice() {

    }
}
