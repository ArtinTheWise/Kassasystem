package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    static final String PRODUCT_NAME = "Milk";
    static final Money PRODUCT_PRICE = new Money(10);

    @Test
    void toStringIsOverloadedCorrectly() {
        Product p = new Product(PRODUCT_NAME, PRODUCT_PRICE);
        assertEquals(PRODUCT_NAME, p.toString());
    }

    @Test
    void getterForNameWorks() {
        Product p = new Product(PRODUCT_NAME, PRODUCT_PRICE);
        assertEquals(PRODUCT_NAME, p.getName());
    }

    @Test
    void getterForPriceWorks() {
        Product p = new Product(PRODUCT_NAME, PRODUCT_PRICE);
        assertEquals(PRODUCT_PRICE, p.getPrice());
    }
}