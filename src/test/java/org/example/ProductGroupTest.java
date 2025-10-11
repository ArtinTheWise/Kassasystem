package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductGroupTest {
    static final String PRODUCT_GROUP_NAME = "Dairy";

    @Test
    void toStringIsOverloadedCorrectly() {
        ProductGroup p = new ProductGroup(PRODUCT_GROUP_NAME);
        assertEquals(PRODUCT_GROUP_NAME, p.toString());
    }

    @Test
    void getterForNameWorks() {
        ProductGroup p = new ProductGroup(PRODUCT_GROUP_NAME);
        assertEquals(PRODUCT_GROUP_NAME, p.getName());
    }

    @Test
    void possibleToAddProducts() {
        Product milk = new Product("Milk", 10);
        ProductGroup p = new ProductGroup(PRODUCT_GROUP_NAME, milk);
        assertEquals(milk, p.getProductGroup().get(0));
    }

    @Test
    void possibleToRemoveProducts() {
        Product milk = new Product("Milk", 10);
        Product egg = new Product("Egg", 5);
        ProductGroup p = new ProductGroup(PRODUCT_GROUP_NAME, milk, egg);
        p.removeProduct(milk);
        assertFalse(p.getProductGroup().contains(milk));
        assertTrue(p.getProductGroup().contains(egg));
    }

}
