package org.example;

import org.example.Product.NormalProduct;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.example.Product.Product;
import org.example.Product.WeightedProduct;
import org.junit.jupiter.api.Test;

public class WeightedProductTest {
    @Test
    void getNameReturnsCorrectName() {
        Money mockMoney = mock(Money.class);

        Product p = new WeightedProduct("Lemon", mockMoney, 10);
        assertEquals("Lemon", p.getName());
    }

    @Test
    void getFinalPriceReturnsCorrectPrice() {
        Money mockMoney = mock(Money.class);
        when(mockMoney.getAmount()).thenReturn(10.0);

        Product p = new WeightedProduct("Lemon", mockMoney, 10);
        assertEquals(100, p.getFinalPrice());
    }

    @Test
    void getWeightReturnsCorrectWeight() {
        Money mockMoney = mock(Money.class);

        WeightedProduct p = new WeightedProduct("Lemon", mockMoney, 10);
        assertEquals(10, p.getWeight());
    }
}
