package org.example;

import org.example.Product.NormalProduct;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.example.Product.Product;
import org.junit.jupiter.api.Test;

class NormalProductTest {
    @Test
    void getNameReturnsCorrectName() {
        Money mockMoney = mock(Money.class);

        Product p = new NormalProduct("Milk", mockMoney);
        assertEquals("Milk", p.getName());
    }

    @Test
    void getFinalPriceReturnsCorrectPrice() {
        Money mockMoney = mock(Money.class);
        when(mockMoney.getAmount()).thenReturn(10.0);

        Product p = new NormalProduct("Milk", mockMoney);
        assertEquals(10, p.getFinalPrice());
    }

    @Test
    void toStringIsOverloadedProperly() {
        Money mockMoney = mock(Money.class);
        when(mockMoney.getAmount()).thenReturn(10.0);

        Product p = new NormalProduct("Milk", mockMoney);
        assertEquals("Milk 10.0", p.toString());
    }
}