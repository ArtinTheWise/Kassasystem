package org.example;

import org.example.Discount.NormalDiscount;
import org.example.Discount.PercentageDiscount;
import org.example.Discount.ProductDecorator;
import org.example.Discount.ThreeForTwoDiscount;
import org.example.Product.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.example.Product.Unit.KG;
import static org.example.Product.Unit.PIECE;
import static org.example.Product.VatRate.OTHER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiscountTest {
    private static final int DISCOUNT_AMOUNT = 20;
    private static final LocalDateTime DATE_IN_FUTURE = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime DATE_IN_PAST = LocalDateTime.now().minusDays(1);

    @Test
    void isActiveWorksForValidDates(){
        PercentageDiscount activeDiscount = new PercentageDiscount(getMockProduct(), DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE);
        assertTrue(activeDiscount.isActive());

        PercentageDiscount inactiveDiscount = new PercentageDiscount(getMockProduct(), DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE);
        assertFalse(inactiveDiscount.isActive());
    }

    @Test
    void constructorDoesNotAllowImpossibleDate(){
        assertThrows(IllegalArgumentException.class, () -> new PercentageDiscount(getMockProduct(), DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_PAST));
        assertThrows(IllegalArgumentException.class, () -> new PercentageDiscount(getMockProduct(), DISCOUNT_AMOUNT, null, null));
    }

    @Test
    void percentDiscountGivesCorrectDiscount(){

        Product activeDiscount = new PercentageDiscount(getMockProduct(), DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE);
        assertEquals(96, activeDiscount.calculatePrice(new Quantity(1, PIECE)).getAmountInMinorUnits());

        Product inactiveDiscount = new PercentageDiscount(getMockProduct(), DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE);
        assertEquals(120, inactiveDiscount.calculatePrice(new Quantity(1, PIECE)).getAmountInMinorUnits());
    }

    @Test
    void percentDiscountIsValid(){
        assertDoesNotThrow(() -> new PercentageDiscount(getMockProduct(), DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE));
        assertThrows(IllegalArgumentException.class, () -> new PercentageDiscount(getMockProduct(), 101, DATE_IN_PAST, DATE_IN_FUTURE));
        assertThrows(IllegalArgumentException.class, () -> new PercentageDiscount(getMockProduct(), -1, DATE_IN_PAST, DATE_IN_FUTURE));
    }

    @Test
    void getNameSaysIfThereIsADiscount(){
        Product activeDiscount = new PercentageDiscount(getMockProduct(), DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE);
        assertEquals("Milk got a discount.", activeDiscount.getName());

        Product inactiveDiscount = new PercentageDiscount(getMockProduct(), DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE);
        assertEquals("Milk", inactiveDiscount.getName());
    }

    @Test
    void normalDiscountGivesCorrectDiscount(){
        Product activeDiscount = new NormalDiscount(getMockProduct(), DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE);
        assertEquals(100, activeDiscount.calculatePrice(new Quantity(1, PIECE)).getAmountInMinorUnits());

        Product inactiveDiscount = new NormalDiscount(getMockProduct(), DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE);
        assertEquals(120, inactiveDiscount.calculatePrice(new Quantity(1, PIECE)).getAmountInMinorUnits());
    }

    @Test
    void normalDiscountIsValid(){
        assertDoesNotThrow(() -> new NormalDiscount(getMockProduct(), DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE));
        assertThrows(IllegalArgumentException.class, () -> new NormalDiscount(getMockProduct(), -1, DATE_IN_FUTURE, DATE_IN_FUTURE));
        //assertThrows(IllegalArgumentException.class, () -> new NormalDiscount(product, 101, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2))); problem med att få money från en produkt.
    }

    @ParameterizedTest
    @CsvSource({"100, 0", "200, 1", "300, 2"})
    void discountProductGroupDiscountsEveryProductInGroup(int i1, int i2){
        Product productOne = getMockProduct();
        Product productTwo = getMockProduct();
        Product productThree = getMockProduct();
        when(productTwo.calculatePrice(any())).thenReturn(new Money(220));
        when(productThree.calculatePrice(any())).thenReturn(new Money(320));

        ProductGroup productGroup = new ProductGroup("Milk", productOne, productTwo, productThree);
        ProductGroup discountedProductGroup = NormalDiscount.discountGroup(productGroup, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE);
        assertEquals(i1, discountedProductGroup.getProductGroup().get(i2).calculatePrice(new Quantity(1, PIECE)).getAmountInMinorUnits());

    }

    @ParameterizedTest
    @CsvSource({"120, 1", "240, 3", "360, 4"})
    void ThreeForTwoDiscountCalculatesDiscountCorrectly(int i1, int i2){
        PriceModel mockPriceModel = new UnitPrice(new Money(120));

        Product product = new Product("Milk", mockPriceModel, OTHER);

        Product discountedProduct = new ThreeForTwoDiscount(product, DATE_IN_PAST, DATE_IN_FUTURE);
        assertEquals(i1, discountedProduct.calculatePrice(new Quantity(i2, PIECE)).getAmountInMinorUnits());
    }

    @Test
    void ThreeForTwoDiscountDoesNotAllowWeightPrice(){
        PriceModel mockPriceModel = new WeightPrice(new Money(120), KG);
        Product product = new Product("Milk", mockPriceModel, OTHER);

        assertThrows(IllegalArgumentException.class, () -> new ThreeForTwoDiscount(product, DATE_IN_PAST, DATE_IN_FUTURE));
    }

    private Product getMockProduct(){
        Product mockProduct = mock(Product.class);
        when(mockProduct.calculatePrice(any())).thenReturn(new Money(120));
        when(mockProduct.getName()).thenReturn("Milk");
        return mockProduct;
    }

}
