package org.example;

import jdk.jfr.Percentage;
import org.example.Discount.*;
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
import java.time.LocalTime;
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

        PercentageDiscount oldDiscount = new PercentageDiscount(getMockProduct(), DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_PAST);
        assertFalse(oldDiscount.isActive());
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

    @Test
    void discountManagerDoesNotAllowNullInConstructor(){
        Product product = null;
        assertThrows(IllegalArgumentException.class, () -> new DiscountManager(product));
    }

    @Test
    void discountManagerOnlyAllowsDiscountedProducts(){
        Product product = getMockProduct();
        assertThrows(IllegalArgumentException.class, () -> new DiscountManager(product));

        Product discountedProduct = mock(PercentageDiscount.class);
        assertDoesNotThrow(() -> new DiscountManager(discountedProduct));
    }

    @Test
    void discountManagerSaysIfProductGotDiscount(){
        PriceModel mockPriceModel = mock(PriceModel.class);
        Product productOne = new Product("Milk", mockPriceModel, OTHER);
        Product productTwo = new Product("Egg", mockPriceModel, OTHER);
        Product productThree = new Product("Apple", mockPriceModel, OTHER);
        Product productFour = new Product("Egg", mockPriceModel, OTHER);

        Product activeDiscount = new PercentageDiscount(productTwo, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE);
        Product inactiveDiscount = new PercentageDiscount(productThree, DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE);
        DiscountManager manager = new DiscountManager(activeDiscount, inactiveDiscount);

        assertFalse(manager.discountCheck(productOne));
        assertTrue(manager.discountCheck(productTwo));
        assertFalse(manager.discountCheck(productThree));
        assertTrue(manager.discountCheck(productFour));
    }

    @Test
    void discountManagerWithOldAndNewDiscount(){
        PriceModel mockPriceModel = mock(PriceModel.class);
        Product product = new Product("Milk", mockPriceModel, OTHER);

        Product oldDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_PAST);
        Product activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE);
        DiscountManager manager = new DiscountManager(oldDiscount, activeDiscount);

        assertTrue(manager.discountCheck(product));
    }

    @Test
    void discountManagerWithInactiveAndActiveDiscount(){
        PriceModel mockPriceModel = mock(PriceModel.class);
        Product product = new Product("Milk", mockPriceModel, OTHER);

        Product futureDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE);
        Product activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE);
        DiscountManager manager = new DiscountManager(futureDiscount, activeDiscount);

        assertTrue(manager.discountCheck(product));
    }

    @Test
    void discountManagerReturnsCheapestDiscount(){
        PriceModel mockPriceModel = new UnitPrice(new Money(120));
        Product product = new Product("Milk", mockPriceModel, OTHER);

        Product goodDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE);
        Product badDiscount = new NormalDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE);
        DiscountManager manager = new DiscountManager(badDiscount, goodDiscount);

        assertEquals(96, manager.getBestDiscount(product, new Quantity(1, PIECE)).calculatePrice(new Quantity(1, PIECE)).getAmountInMinorUnits());
    }

    @Test
    void productDecoratorChildrenDoesNotAllowNullProduct(){
        assertThrows(NullPointerException.class, () -> new PercentageDiscount(null, DISCOUNT_AMOUNT, DATE_IN_FUTURE));
        assertThrows(NullPointerException.class, () -> new NormalDiscount(null, DISCOUNT_AMOUNT, DATE_IN_FUTURE));
        assertThrows(NullPointerException.class, () -> new ThreeForTwoDiscount(null, DATE_IN_FUTURE));

    }

    @Test
    void discountManagerAllowsProductGroupInConstructor(){
        PriceModel mockPriceModel = new UnitPrice(new Money(120));
        Product product = new Product("Red Milk", mockPriceModel, OTHER);

        Product nonDiscountedProduct = getMockProduct();
        Product discountedProduct = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE);
        ProductGroup group = new ProductGroup("Dairy", nonDiscountedProduct, discountedProduct);
        DiscountManager manager = new DiscountManager(group);

        assertTrue(manager.discountCheck(discountedProduct));
        assertFalse(manager.discountCheck(nonDiscountedProduct));
    }

    @Test
    void discountManagerReturnsCheapestDiscountWithThreeForTwo(){
        PriceModel mockPriceModel = new UnitPrice(new Money(120));
        Product product = new Product("Milk", mockPriceModel, OTHER);

        Product discountPercentage = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE);
        Product discountThreeForTwo = new ThreeForTwoDiscount(product, DATE_IN_PAST, DATE_IN_FUTURE);
        DiscountManager manager = new DiscountManager(discountPercentage, discountThreeForTwo);

        assertEquals(96, manager.getBestDiscount(product, new Quantity(1, PIECE)).calculatePrice(new Quantity(1, PIECE)).getAmountInMinorUnits());
        assertEquals(240, manager.getBestDiscount(product, new Quantity(3, PIECE)).calculatePrice(new Quantity(3, PIECE)).getAmountInMinorUnits());
    }

    @Test
    void getDiscountedAmountReturnsCorrectDiscount(){
        PriceModel mockPriceModel = new UnitPrice(new Money(120));
        Product product = new Product("Milk", mockPriceModel, OTHER);
        ProductDecorator discountPercentage = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE);
        assertEquals(24, discountPercentage.getDiscountedAmount(new Quantity(1, PIECE)).getAmountInMinorUnits());
    }

    @Test
    void discountManagerAddMethodWorks(){
        PriceModel mockPriceModel = new UnitPrice(new Money(120));
        Product product = new Product("Blue Milk", mockPriceModel, OTHER);
        Product productTwo = new Product("Red Milk", mockPriceModel, OTHER);

        Product nonDiscountedProduct = getMockProduct();
        Product discountedProductOne = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE);
        Product discountedProductTwo = new NormalDiscount(productTwo, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE);
        ProductGroup group = new ProductGroup("Dairy", nonDiscountedProduct, discountedProductOne);
        DiscountManager manager = new DiscountManager();
        manager.addDiscount(group);
        manager.addDiscount(discountedProductTwo);

        assertTrue(manager.discountCheck(discountedProductOne));
        assertTrue(manager.discountCheck(discountedProductTwo));
        assertFalse(manager.discountCheck(nonDiscountedProduct));
    }

    @Test
    void threeForTwoDiscountWrapsProperly(){
        PriceModel mockPriceModel = new UnitPrice(new Money(120));
        Product product = new Product("Milk", mockPriceModel, OTHER);
        Product discountedProductOne = new ThreeForTwoDiscount(product, DATE_IN_PAST, DATE_IN_FUTURE);
        Product discountedProductTwo = new PercentageDiscount(discountedProductOne, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE);

        assertEquals(96, discountedProductTwo.calculatePrice(new Quantity(1, PIECE)).getAmountInMinorUnits());
        assertEquals(192, discountedProductTwo.calculatePrice(new Quantity(3, PIECE)).getAmountInMinorUnits());
        assertEquals(288, discountedProductTwo.calculatePrice(new Quantity(4, PIECE)).getAmountInMinorUnits());
    }

    @Test
    void discountAtXTimeMustHaveCorrectStartAndEndTime(){
        ProductDecorator discounted = new PercentageDiscount(getMockProduct(), DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE);
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(17, 0);
        LocalTime endTimeEqualToStart = LocalTime.of(8, 0);

        assertThrows(IllegalArgumentException.class, () -> new DiscountAtXTime(discounted, end, start));
        assertDoesNotThrow(() -> new DiscountAtXTime(discounted, start, end));
        assertThrows(IllegalArgumentException.class, () -> new DiscountAtXTime(discounted, endTimeEqualToStart, start));
    }

    @Test
    void discountAtXTimeIsActiveAtCorrectTimes(){
        ProductDecorator activeDiscount = new PercentageDiscount(getMockProduct(), DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE);
        ProductDecorator inactiveDiscount = new PercentageDiscount(getMockProduct(), DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE);
        LocalTime start = LocalTime.now();
        LocalTime startInFuture = start.plusMinutes(10);
        LocalTime end = start.plusHours(1);

        ProductDecorator specificActiveDiscount = new DiscountAtXTime(activeDiscount, start, end);
        ProductDecorator specificInactiveDiscount = new DiscountAtXTime(inactiveDiscount, start, end);
        ProductDecorator specificInactiveDiscountTwo = new DiscountAtXTime(activeDiscount, startInFuture, end);
        assertTrue(specificActiveDiscount.isActive());
        assertFalse(specificInactiveDiscount.isActive());
        assertFalse(specificInactiveDiscountTwo.isActive());
    }

    @Test
    void discountAtXTimeGivesCorrectDiscount(){
        ProductDecorator activeDiscount = new PercentageDiscount(getMockProduct(), DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE);
        LocalTime start = LocalTime.now();
        LocalTime startInFuture = start.plusMinutes(10);
        LocalTime end = start.plusHours(1);
        ProductDecorator specificActiveDiscount = new DiscountAtXTime(activeDiscount, start, end);
        ProductDecorator specificInactiveDiscount = new DiscountAtXTime(activeDiscount, startInFuture, end);

        assertEquals(96, specificActiveDiscount.calculatePrice(new Quantity(1, PIECE)).getAmountInMinorUnits());
        assertEquals(120, specificInactiveDiscount.calculatePrice(new Quantity(1, PIECE)).getAmountInMinorUnits());
    }

    @Test
    void maxXDiscountOnlyAllowsMaxMoreThan0(){
        ProductDecorator activeDiscount = new PercentageDiscount(getMockProduct(), DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE);
        assertThrows(IllegalArgumentException.class, () -> new MaxXDiscount(activeDiscount, 0));
        assertDoesNotThrow(() -> new MaxXDiscount(activeDiscount, 1));
    }

    @Test
    void maxXDiscountCalculatesPriceCorrectly(){
        PriceModel mockPriceModel = new UnitPrice(new Money(120));
        Product product = new Product("Milk", mockPriceModel, OTHER);

        ProductDecorator activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE);
        ProductDecorator inactiveDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE);
        ProductDecorator maxActiveDiscount = new MaxXDiscount(activeDiscount, 2);
        ProductDecorator maxInactiveDiscount = new MaxXDiscount(inactiveDiscount, 2);

        assertEquals(192, maxActiveDiscount.calculatePrice(new Quantity(2, PIECE)).getAmountInMinorUnits());
        assertEquals(360, maxInactiveDiscount.calculatePrice(new Quantity(3, PIECE)).getAmountInMinorUnits());
        assertEquals(312, maxActiveDiscount.calculatePrice(new Quantity(3, PIECE)).getAmountInMinorUnits());
    }

    private Product getMockProduct() {
        PriceModel mockPriceModel = mock(PriceModel.class);
        when(mockPriceModel.calculatePrice(any())).thenReturn(new Money(120));

        return new Product("Milk", mockPriceModel, OTHER);
    }
}
