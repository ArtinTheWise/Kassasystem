package org.example;

import org.example.Discount.NormalDiscount;
import org.example.Discount.PercentageDiscount;
import org.example.Discount.ProductDecorator;
import org.example.Product.*;
import org.junit.jupiter.api.Test;

import static org.example.Product.Unit.PIECE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiscountTest {

    @Test
    void isActiveWorksForValidDates(){
        Product mockProduct = mock(Product.class);
        PercentageDiscount activeDiscount = new PercentageDiscount(mockProduct, 20, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

        assertTrue(activeDiscount.isActive());

        PercentageDiscount inactiveDiscount = new PercentageDiscount(mockProduct, 20, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        assertFalse(inactiveDiscount.isActive());
    }

    @Test
    void constructorDoesNotAllowImpossibleDate(){
        Product mockProduct = mock(Product.class);
        assertInvalidDate(mockProduct, 20, LocalDateTime.now().plusDays(1), LocalDateTime.now().minusDays(1));
        assertInvalidDate(mockProduct, 20, null, null);
    }

    @Test
    void percentDiscountGivesCorrectDiscount(){
        PriceModel mockPriceModel = mock(PriceModel.class);
        when(mockPriceModel.calculatePrice(any())).thenReturn(new Money(100));
        Product product = new Product("Milk", mockPriceModel);

        Product activeDiscount = new PercentageDiscount(product, 20, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        assertEquals(80, activeDiscount.calculatePrice(new Quantity(1, PIECE)).getAmountInMinorUnits());

        Product inactiveDiscount = new PercentageDiscount(product, 20, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        assertEquals(100, inactiveDiscount.calculatePrice(new Quantity(1, PIECE)).getAmountInMinorUnits());
    }

    @Test
    void percentDiscountIsValid(){
        Product mockProduct = mock(Product.class);
        assertDoesNotThrow(() -> new PercentageDiscount(mockProduct, 20, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));
        assertThrows(IllegalArgumentException.class, () -> new PercentageDiscount(mockProduct, 101, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));
        assertThrows(IllegalArgumentException.class, () -> new PercentageDiscount(mockProduct, -1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));
    }

    @Test
    void getNameSaysIfThereIsADiscount(){
        PriceModel mockPriceModel = mock(PriceModel.class);
        Product product = new Product("Milk", mockPriceModel);
        Product activeDiscount = new PercentageDiscount(product, 20, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        assertEquals("Milk got a discount.", activeDiscount.getName());

        Product inactiveDiscount = new PercentageDiscount(product, 20, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        assertEquals("Milk", inactiveDiscount.getName());
    }

    @Test
    void normalDiscountGivesCorrectDiscount(){
        PriceModel mockPriceModel = mock(PriceModel.class);
        when(mockPriceModel.calculatePrice(any())).thenReturn(new Money(100));
        Product product = new Product("Milk", mockPriceModel);

        Product activeDiscount = new NormalDiscount(product, 10, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        assertEquals(90, activeDiscount.calculatePrice(new Quantity(1, PIECE)).getAmountInMinorUnits());

        Product inactiveDiscount = new NormalDiscount(product, 10, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        assertEquals(100, inactiveDiscount.calculatePrice(new Quantity(1, PIECE)).getAmountInMinorUnits());
    }

    @Test
    void normalDiscountIsValid(){
        PriceModel mockPriceModel = mock(PriceModel.class);
        when(mockPriceModel.calculatePrice(any())).thenReturn(new Money(100));
        Product product = new Product("Milk", mockPriceModel);
        assertDoesNotThrow(() -> new NormalDiscount(product, 20, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));
        assertThrows(IllegalArgumentException.class, () -> new NormalDiscount(product, -1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));
        //assertThrows(IllegalArgumentException.class, () -> new NormalDiscount(product, 101, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2))); problem med att få money från en produkt.
    }

    @Test
    void discountProductGroupDiscountsEveryProductInGroup(){
        PriceModel mockPriceModel1 = mock(PriceModel.class);
        when(mockPriceModel1.calculatePrice(any())).thenReturn(new Money(100));
        Product product1 = new Product("Green Apple", mockPriceModel1);

        PriceModel mockPriceModel2 = mock(PriceModel.class);
        when(mockPriceModel2.calculatePrice(any())).thenReturn(new Money(200));
        Product product2 = new Product("Red Apple", mockPriceModel2);

        PriceModel mockPriceModel3 = mock(PriceModel.class);
        when(mockPriceModel3.calculatePrice(any())).thenReturn(new Money(300));
        Product product3 = new Product("Yellow Apple", mockPriceModel3);

        VatGroup vatMock = mock(VatGroup.class);

        ProductGroup productGroup = new ProductGroup("Apples", vatMock, product1, product2, product3);

        ProductGroup discountedProductGroup = NormalDiscount.discountGroup(productGroup, 10, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        assertEquals(90, discountedProductGroup.getProductGroup().get(0).calculatePrice(new Quantity(1, PIECE)).getAmountInMinorUnits());
        assertEquals(190, discountedProductGroup.getProductGroup().get(1).calculatePrice(new Quantity(1, PIECE)).getAmountInMinorUnits());
        assertEquals(290, discountedProductGroup.getProductGroup().get(2).calculatePrice(new Quantity(1, PIECE)).getAmountInMinorUnits());

    }

    private void assertInvalidDate(Product product, int percent, LocalDateTime start, LocalDateTime end) {
        assertThrows(IllegalArgumentException.class, () -> new PercentageDiscount(product, percent, start, end));
    }
}
