package org.example;

import org.example.Discount.*;
import org.example.Product.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.example.Product.Unit.KG;
import static org.example.Product.Unit.PIECE;
import static org.example.Product.VatRate.OTHER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.*;

@ExtendWith(MockitoExtension.class)
public class DiscountTest {
    private static final Clock FIXED_DATE = Clock.fixed(Instant.parse("2025-01-01T12:00:00Z"), ZoneId.systemDefault());
    private static final LocalDateTime DATE_IN_FUTURE = LocalDateTime.now(FIXED_DATE).plusDays(1);
    private static final LocalDateTime DATE_IN_PAST = LocalDateTime.now(FIXED_DATE).minusDays(1);
    private static final int DISCOUNT_AMOUNT = 20;
    private Product product;

    private Product getMockProduct(String name, long price, Unit unit) {
        Product p = mock(Product.class, name);
        PriceModel pm = mock(PriceModel.class);

        lenient().when(pm.getUnit()).thenReturn(unit);
        lenient().when(p.getName()).thenReturn(name);
        lenient().when(p.getPriceModel()).thenReturn(pm);
        lenient().when(p.getVatRate()).thenReturn(mock(VatRate.class));

        lenient().when(p.calculatePrice(any(Quantity.class))).thenAnswer(inv -> {
            Quantity q = inv.getArgument(0);
            return new Money(Math.round(q.getAmount() * price));
        });

        return p;
    }

    private Product getMockProduct(String name, long price){
        return getMockProduct(name, price, PIECE);
    }

    private Product getRealProduct(String name, long price){ //needed for equals and hashcode
        PriceModel pm = mock(PriceModel.class);
        Product p = new Product(name, pm, OTHER);

        lenient().when(pm.getUnit()).thenReturn(PIECE);
        lenient().when(pm.calculatePrice(any(Quantity.class))).thenAnswer(inv -> {
            Quantity q = inv.getArgument(0);
            return new Money(Math.round(q.getAmount() * price));
        });

        return p;
    }

    private Quantity quantity(int number){
        return new Quantity(number, PIECE);
    }

    @BeforeEach
    void setUp() {
        product = getMockProduct("Milk", 120);
    }

    @Test
    @DisplayName("ProductDecorator/isActive - correct boolean result")
    void isActiveWorksForValidDates(){
        PercentageDiscount activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        assertTrue(activeDiscount.isActive());

        PercentageDiscount inactiveDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE, FIXED_DATE);
        assertFalse(inactiveDiscount.isActive());

        PercentageDiscount oldDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_PAST, FIXED_DATE);
        assertFalse(oldDiscount.isActive());
    }

    @Test
    @DisplayName("ProductDecorator/constructor - null and impossible date throws exception")
    void constructorDoesNotAllowImpossibleDates(){
        assertThrows(IllegalArgumentException.class, () -> new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_PAST, FIXED_DATE));
        assertThrows(IllegalArgumentException.class, () -> new PercentageDiscount(product, DISCOUNT_AMOUNT, null, null, FIXED_DATE));
    }

    @Test
    @DisplayName("ProductDecorator/constructor - null product throws exception")
    void constructorDoesNotAllowNullProduct() {
        assertThrows(NullPointerException.class, () -> new PercentageDiscount(null, DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_PAST, FIXED_DATE));
    }

    @ParameterizedTest
    @CsvSource({"0, 0", "96, 1", "192, 2"})
    @DisplayName("PercentageDiscount/calculatePrice - discount returns correct amount of money")
    void percentDiscountGivesCorrectDiscount(int i1, int i2){
        Product activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        assertEquals(i1, activeDiscount.calculatePrice(quantity(i2)).getAmountInMinorUnits());

        Product inactiveDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE, FIXED_DATE);
        assertEquals(120, inactiveDiscount.calculatePrice(quantity(1)).getAmountInMinorUnits());
    }

    @Test
    @DisplayName("PercentageDiscount/constructor - 0 > discount > 100 throws exception")
    void percentDiscountIsValid(){
        assertDoesNotThrow(() -> new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE));
        assertThrows(IllegalArgumentException.class, () -> new PercentageDiscount(product, 101, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE));
        assertThrows(IllegalArgumentException.class, () -> new PercentageDiscount(product, -1, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE));
    }

    @Test
    @DisplayName("ProductDecorator/getName - returns correct string")
    void getNameSaysIfThereIsADiscount(){
        Product activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        assertEquals(product.getName() + " got a discount.", activeDiscount.getName());

        Product inactiveDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE, FIXED_DATE);
        assertEquals(product.getName(), inactiveDiscount.getName());
    }

    @ParameterizedTest
    @CsvSource({"0, 0", "100, 1", "200, 2"})
    @DisplayName("NormalDiscount/calculatePrice - discount returns correct amount of money")
    void normalDiscountGivesCorrectDiscount(int i1, int i2){
        Product activeDiscount = new NormalDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        assertEquals(i1, activeDiscount.calculatePrice(quantity(i2)).getAmountInMinorUnits());

        Product inactiveDiscount = new NormalDiscount(product, DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE, FIXED_DATE);
        assertEquals(120, inactiveDiscount.calculatePrice(quantity(1)).getAmountInMinorUnits());
    }

    @Test
    @DisplayName("NormalDiscount/constructor - 0 > discount > ProductPrice throws exception")
    void normalDiscountIsValid(){
        assertDoesNotThrow(() -> new NormalDiscount(product, DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE, FIXED_DATE));
        assertThrows(IllegalArgumentException.class, () -> new NormalDiscount(product, -1, DATE_IN_FUTURE, DATE_IN_FUTURE, FIXED_DATE));
        assertThrows(IllegalArgumentException.class, () -> new NormalDiscount(product, 121, DATE_IN_FUTURE, DATE_IN_FUTURE, FIXED_DATE));
    }

    @ParameterizedTest
    @CsvSource({"100, 0", "120, 1", "140, 2"})
    @DisplayName("NormalDiscount/discountGroup - every product in the group is discounted")
    void discountProductGroupDiscountsEveryProductInGroup(int i1, int i2){
        Product productTwo = getMockProduct("Green Milk", 140);
        Product productThree = getMockProduct("Red Milk", 160);
        NormalDiscount activeDiscount = new NormalDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);

        ProductGroup productGroup = new ProductGroup("Dairy", product, productTwo, productThree);
        ProductGroup discountedProductGroup = activeDiscount.discountGroup(productGroup, DATE_IN_PAST, DATE_IN_FUTURE);
        assertEquals(i1, discountedProductGroup.getProductGroup().get(i2).calculatePrice(quantity(1)).getAmountInMinorUnits());
    }

    @ParameterizedTest
    @CsvSource({"120, 1", "240, 3", "360, 4"})
    @DisplayName("ThreeForTwoDiscount/calculatePrice - returns correct price")
    void ThreeForTwoDiscountCalculatesDiscountCorrectly(int i1, int i2){
        Product discountedProduct = new ThreeForTwoDiscount(product, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        assertEquals(i1, discountedProduct.calculatePrice(quantity(i2)).getAmountInMinorUnits());
    }

    @ParameterizedTest
    @CsvSource({"KG", "HG", "G"})
    @DisplayName("ThreeForTwoDiscount/constructor - only PIECE allowed")
    void ThreeForTwoDiscountOnlyAllowsPIECE(Unit i1){
        Product product = getMockProduct("Milk", 120, i1);

        assertThrows(IllegalArgumentException.class, () -> new ThreeForTwoDiscount(product, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE));
    }

    @Test
    @DisplayName("DiscountManager/constructor - null is not allowed")
    void discountManagerDoesNotAllowNullInConstructor(){
        Product product = null;
        assertThrows(IllegalArgumentException.class, () -> new DiscountManager(product));
    }

    @Test
    @DisplayName("DiscountManager/constructor - only objects from ProductDecorator allowed")
    void discountManagerOnlyAllowsDiscountedProducts(){
        assertThrows(IllegalArgumentException.class, () -> new DiscountManager(product));

        Product discountedProduct = new NormalDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        assertDoesNotThrow(() -> new DiscountManager(discountedProduct));
    }

    @Test
    @DisplayName("DiscountManager/discountCheck - products with active discounts returns correct boolean")
    void discountManagerSaysIfProductGotDiscount(){
        Product productOne = getRealProduct("Milk", 120);
        Product productTwo = getRealProduct("Egg", 50);
        Product productThree = getRealProduct("Apple", 200);
        Product productFour = getRealProduct("Egg", 50);

        Product activeDiscount = new PercentageDiscount(productTwo, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        Product inactiveDiscount = new PercentageDiscount(productThree, DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE, FIXED_DATE);
        DiscountManager manager = new DiscountManager(activeDiscount, inactiveDiscount);

        assertFalse(manager.discountCheck(productOne));
        assertTrue(manager.discountCheck(productTwo));
        assertFalse(manager.discountCheck(productThree));
        assertTrue(manager.discountCheck(productFour));
    }

    @Test
    @DisplayName("DiscountManager/discountCheck - returns true with active and old discount")
    void discountManagerWithOldAndNewDiscount(){
        Product oldDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_PAST, FIXED_DATE);
        Product activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        DiscountManager manager = new DiscountManager(oldDiscount, activeDiscount);

        assertTrue(manager.discountCheck(product));
    }

    @Test
    @DisplayName("DiscountManager/discountCheck - returns true with active and upcoming discount")
    void discountManagerWithInactiveAndActiveDiscount(){
        Product futureDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE, FIXED_DATE);
        Product activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        DiscountManager manager = new DiscountManager(futureDiscount, activeDiscount);

        assertTrue(manager.discountCheck(product));
    }

    @Test
    @DisplayName("DiscountManager/getBestDiscount - returns cheapest discount")
    void discountManagerReturnsCheapestDiscount(){
        Product goodDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        Product badDiscount = new NormalDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        DiscountManager manager = new DiscountManager(badDiscount, goodDiscount);

        assertEquals(96, manager.getBestDiscount(product, quantity(1)).calculatePrice(quantity(1)).getAmountInMinorUnits());
    }

    @Test
    @DisplayName("DiscountManager/discountCheck - works with ProductGroup")
    void discountManagerAllowsProductGroupInConstructor(){
        Product nonDiscountedProduct = new PercentageDiscount(getRealProduct("Milk", 120), DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE, FIXED_DATE);
        Product discountedProduct = new PercentageDiscount(getRealProduct("Red Milk", 160), DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        ProductGroup group = new ProductGroup("Dairy", nonDiscountedProduct, discountedProduct);
        DiscountManager manager = new DiscountManager(group);

        assertTrue(manager.discountCheck(discountedProduct));
        assertFalse(manager.discountCheck(nonDiscountedProduct));
    }

    @Test
    @DisplayName("DiscountManager/getBestDiscount - quantity changes which discount is chosen")
    void discountManagerReturnsCheapestDiscountWithThreeForTwo(){
        Product discountPercentage = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        Product discountThreeForTwo = new ThreeForTwoDiscount(product, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        DiscountManager manager = new DiscountManager(discountPercentage, discountThreeForTwo);

        assertEquals(96, manager.getBestDiscount(product, quantity(1)).calculatePrice(quantity(1)).getAmountInMinorUnits());
        assertEquals(240, manager.getBestDiscount(product, quantity(3)).calculatePrice(quantity(3)).getAmountInMinorUnits());
    }

    @Test
    @DisplayName("ProductDecorator/getDiscountedAmount - returns how much got subtracted")
    void getDiscountedAmountReturnsCorrectDiscount(){
        ProductDecorator discountPercentage = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        assertEquals(24, discountPercentage.getDiscountedAmount(quantity(1)).getAmountInMinorUnits());
    }

    @Test
    @DisplayName("DiscountManager/addDiscount - addDiscount adds correct discounts")
    void discountManagerAddMethodWorks(){
        Product product = getRealProduct("Milk", 120);
        Product productTwo = getRealProduct("Green Milk", 140);
        Product nonDiscountedProduct = getRealProduct("Red Milk", 160);

        Product discountedProductOne = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        Product discountedProductTwo = new NormalDiscount(productTwo, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        ProductGroup group = new ProductGroup("Dairy", nonDiscountedProduct, discountedProductOne);
        DiscountManager manager = new DiscountManager();
        manager.addDiscount(group);
        manager.addDiscount(discountedProductTwo);

        assertTrue(manager.discountCheck(discountedProductOne));
        assertTrue(manager.discountCheck(discountedProductTwo));
        assertFalse(manager.discountCheck(nonDiscountedProduct));
    }

    @Test
    @DisplayName("ThreeForTwo/constructor - discounts wrap properly")
    void threeForTwoDiscountWrapsProperly(){
        Product discountedProductOne = new ThreeForTwoDiscount(product, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        Product discountedProductTwo = new PercentageDiscount(discountedProductOne, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);

        assertEquals(96, discountedProductTwo.calculatePrice(quantity(1)).getAmountInMinorUnits());
        assertEquals(192, discountedProductTwo.calculatePrice(quantity(3)).getAmountInMinorUnits());
        assertEquals(288, discountedProductTwo.calculatePrice(quantity(4)).getAmountInMinorUnits());
    }

    @Test
    @DisplayName("DiscountAtXTime/constructor - wrong end and start time throws exception")
    void discountAtXTimeMustHaveCorrectStartAndEndTime(){
        ProductDecorator discounted = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(17, 0);
        LocalTime endTimeEqualToStart = LocalTime.of(8, 0);

        assertThrows(IllegalArgumentException.class, () -> new DiscountAtXTime(discounted, end, start));
        assertDoesNotThrow(() -> new DiscountAtXTime(discounted, start, end));
        assertThrows(IllegalArgumentException.class, () -> new DiscountAtXTime(discounted, endTimeEqualToStart, start));
    }

    //LocalTime start = LocalTime.now();
    //LocalTime startInFuture = start.plusMinutes(10);
    //LocalTime end = start.plusHours(1); gjorde så att det blev fel i testet vid 23:30
    @Test
    @DisplayName("DiscountAtXTime/isActive - isActive returns true if both discounts are active")
    void discountAtXTimeIsActiveAtCorrectTimes(){
        ProductDecorator activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        ProductDecorator inactiveDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE, FIXED_DATE);
        LocalTime start = LocalTime.now(FIXED_DATE);
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
    @DisplayName("DiscountAtXTime/calculatePrice - returns correct price")
    void discountAtXTimeGivesCorrectDiscount(){
        ProductDecorator activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        LocalTime start = LocalTime.now(FIXED_DATE);
        LocalTime startInFuture = start.plusMinutes(10);
        LocalTime end = start.plusHours(1);
        ProductDecorator specificActiveDiscount = new DiscountAtXTime(activeDiscount, start, end);
        ProductDecorator specificInactiveDiscount = new DiscountAtXTime(activeDiscount, startInFuture, end);

        assertEquals(96, specificActiveDiscount.calculatePrice(quantity(1)).getAmountInMinorUnits());
        assertEquals(120, specificInactiveDiscount.calculatePrice(quantity(1)).getAmountInMinorUnits());
    }

    @Test
    @DisplayName("MaxXDiscount/constructor - less than 1 max throws exception")
    void maxXDiscountOnlyAllowsMaxMoreThan0(){
        ProductDecorator activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        assertThrows(IllegalArgumentException.class, () -> new MaxXDiscount(activeDiscount, 0));
        assertDoesNotThrow(() -> new MaxXDiscount(activeDiscount, 1));
    }

    @Test
    @DisplayName("MaxXDiscount/calculatePrice - only x amount of products gets discounted")
    void maxXDiscountCalculatesPriceCorrectly(){
        ProductDecorator activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        ProductDecorator inactiveDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE, FIXED_DATE);
        ProductDecorator maxActiveDiscount = new MaxXDiscount(activeDiscount, 2);
        ProductDecorator maxInactiveDiscount = new MaxXDiscount(inactiveDiscount, 2);

        assertEquals(192, maxActiveDiscount.calculatePrice(quantity(2)).getAmountInMinorUnits());
        assertEquals(360, maxInactiveDiscount.calculatePrice(quantity(3)).getAmountInMinorUnits());
        assertEquals(312, maxActiveDiscount.calculatePrice(quantity(3)).getAmountInMinorUnits());
    }

    @Test
    @DisplayName("SpecialDiscount/calculatePrice - only x amount of products gets discounted")
    void specialDiscountDoesNotAllowUnreasonableAges(){
        ProductDecorator activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);

        assertThrows(IllegalArgumentException.class, () -> new SpecialDiscount(activeDiscount, 0, false));
    }
}
