package org.example.Discount;

import java.time.*;
import java.util.HashMap;
import java.util.Map;

import org.example.Membership.Customer;
import org.example.Money;
import org.example.Product.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.example.Product.Unit.PIECE;
import static org.example.Product.VatRate.OTHER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        lenient().when(p.calculatePriceWithVat(any(Quantity.class))).thenAnswer(inv -> {
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

    private Customer getMockCustomer(String name, int age, boolean student){
        Customer c = mock(Customer.class, name);

        lenient().when(c.getAge()).thenReturn(age);
        lenient().when(c.isStudent()).thenReturn(student);

        return c;
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
        assertThrows(IllegalArgumentException.class, () -> new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, null, FIXED_DATE));
        assertThrows(IllegalArgumentException.class, () -> new PercentageDiscount(product, DISCOUNT_AMOUNT, null, DATE_IN_FUTURE, FIXED_DATE));
    }

    @Test
    @DisplayName("ProductDecorator/constructor - null product throws exception")
    void constructorDoesNotAllowNullProduct() {
        assertThrows(NullPointerException.class, () -> new PercentageDiscount(null, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE));
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
        DiscountManager manager = new DiscountManager();

        ProductGroup productGroup = new ProductGroup("Dairy", product, productTwo, productThree);
        ProductGroup discountedProductGroup = manager.discountGroup(productGroup, activeDiscount);

        assertEquals(i1, discountedProductGroup.getProductGroup().get(i2).calculatePrice(quantity(1)).getAmountInMinorUnits());
    }

    @ParameterizedTest
    @CsvSource({"120, 1", "240, 3", "360, 4"})
    @DisplayName("ThreeForTwoDiscount/calculatePriceWithVat - returns correct price")
    void ThreeForTwoDiscountCalculatesDiscountCorrectly(int i1, int i2){
        Product discountedProduct = new ThreeForTwoDiscount(product, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        assertEquals(i1, discountedProduct.calculatePrice(quantity(i2)).getAmountInMinorUnits());
    }

    @Test
    @DisplayName("ThreeForTwoDiscount/calculatePriceWithVat - returns correct price")
    void ThreeForTwoDiscountCalculatesDiscountCorrectlyForInactive(){
        Product inactiveDiscount = new ThreeForTwoDiscount(product, DATE_IN_FUTURE, DATE_IN_FUTURE, FIXED_DATE);
        assertEquals(120, inactiveDiscount.calculatePrice(quantity(1)).getAmountInMinorUnits());
        assertEquals(120, inactiveDiscount.calculatePriceWithVat(quantity(1)).getAmountInMinorUnits());
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
    @DisplayName("DiscountAtXTime/calculatePrice, calculatePriceWithVat - returns correct price")
    void discountAtXTimeGivesCorrectDiscount(){
        ProductDecorator activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        LocalTime start = LocalTime.now(FIXED_DATE);
        LocalTime startInFuture = start.plusMinutes(10);
        LocalTime end = start.plusHours(1);
        ProductDecorator specificActiveDiscount = new DiscountAtXTime(activeDiscount, start, end);
        ProductDecorator specificInactiveDiscount = new DiscountAtXTime(activeDiscount, startInFuture, end);

        assertEquals(96, specificActiveDiscount.calculatePrice(quantity(1)).getAmountInMinorUnits());
        assertEquals(120, specificInactiveDiscount.calculatePrice(quantity(1)).getAmountInMinorUnits());

        assertEquals(96, specificActiveDiscount.calculatePriceWithVat(quantity(1)).getAmountInMinorUnits());
        assertEquals(120, specificInactiveDiscount.calculatePriceWithVat(quantity(1)).getAmountInMinorUnits());
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

    @ParameterizedTest
    @CsvSource({"-1", "121"})
    @DisplayName("SeniorDiscount/constructor - 0 > age > 100 throws exception")
    void seniorDiscountDoesNotAllowUnreasonableAges(int i1){
        ProductDecorator activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        assertThrows(IllegalArgumentException.class, () -> new SeniorDiscount(activeDiscount, i1));
    }

    @Test
    @DisplayName("SeniorDiscount/calculatePrice - returns correct discounts when inactive")
    void seniorDiscountOnlyWorksIfActive(){
        Customer oldCustomer = getMockCustomer("Artin", 80, false);
        ProductDecorator inactiveDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE, FIXED_DATE);
        ProductDecorator SeniorDiscount = new SeniorDiscount(inactiveDiscount, 80);
        assertEquals(120, SeniorDiscount.calculatePrice(quantity(1), oldCustomer).getAmountInMinorUnits());
    }

    @Test
    @DisplayName("SeniorDiscount/calculatePrice - returns correct discounts")
    void seniorDiscountGivesCorrectDiscountForSeniors(){
        Customer normalCustomer = getMockCustomer("Martin", 18, false);
        Customer elderly = getMockCustomer("Artina", 70, false);

        ProductDecorator activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        ProductDecorator seniorDiscount = new SeniorDiscount(activeDiscount, 65);

        assertEquals(96, seniorDiscount.calculatePrice(quantity(1), elderly).getAmountInMinorUnits());
        assertEquals(120, seniorDiscount.calculatePrice(quantity(1), normalCustomer).getAmountInMinorUnits());
    }

    @Test
    @DisplayName("StudentDiscount/calculatePrice - returns correct discounts")
    void studentDiscountGivesCorrectDiscountForStudents(){
        Customer normalCustomer = getMockCustomer("Martin", 18, false);
        Customer student = getMockCustomer("Artina", 21, true);

        ProductDecorator activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        ProductDecorator seniorDiscount = new StudentDiscount(activeDiscount);

        assertEquals(96, seniorDiscount.calculatePrice(quantity(1), student).getAmountInMinorUnits());
        assertEquals(120, seniorDiscount.calculatePrice(quantity(1), normalCustomer).getAmountInMinorUnits());
    }

    @Test
    @DisplayName("DiscountManager/getBestDiscount - returns cheapest discount when StudentDiscount")
    void studentDiscountGetsReturnedIfBestDiscount(){
        Customer student = getMockCustomer("Artin", 21, true);
        Customer normalCustomer = getMockCustomer("Martin", 18, false);

        ProductDecorator bestActiveDiscountNotInManager = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        ProductDecorator discountForNonStudents = new NormalDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        ProductDecorator studentDiscount = new StudentDiscount(bestActiveDiscountNotInManager);

        DiscountManager manager = new DiscountManager(discountForNonStudents, studentDiscount);

        assertEquals(100, ((ProductDecorator) manager.getBestDiscount(product, quantity(1), normalCustomer)).calculatePrice(quantity(1), normalCustomer).getAmountInMinorUnits());
        assertEquals(96, ((ProductDecorator) manager.getBestDiscount(product, quantity(1), student)).calculatePrice(quantity(1), student).getAmountInMinorUnits()); //satt här i 1 timme :(
        assertEquals(100, manager.getBestDiscount(product, quantity(1)).calculatePrice(quantity(1)).getAmountInMinorUnits());
    }

    @Test
    @DisplayName("OverXTotalDiscount/calculatePrice - returns correct discounts")
    void overXTotalDiscountAppliesDiscountCorrectly(){
        ProductDecorator activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        ProductDecorator inactiveDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE, FIXED_DATE);
        ProductDecorator overXTotalDiscountOne = new OverXTotalDiscount(activeDiscount, new Money(200));
        ProductDecorator overXTotalDiscountTwo = new OverXTotalDiscount(inactiveDiscount, new Money(200));

        assertEquals(120, overXTotalDiscountOne.calculatePrice(quantity(1)).getAmountInMinorUnits());
        assertEquals(192, overXTotalDiscountOne.calculatePrice(quantity(2)).getAmountInMinorUnits());
        assertEquals(240, overXTotalDiscountTwo.calculatePrice(quantity(2)).getAmountInMinorUnits());

        assertEquals(120, overXTotalDiscountOne.calculatePriceWithVat(quantity(1)).getAmountInMinorUnits());
        assertEquals(192, overXTotalDiscountOne.calculatePriceWithVat(quantity(2)).getAmountInMinorUnits());
        assertEquals(240, overXTotalDiscountTwo.calculatePriceWithVat(quantity(2)).getAmountInMinorUnits());
    }

    @Test
    @DisplayName("OverXTotalDiscount/calculatePrice - returns correct discounts with no discounted product")
    void overXTotalDiscountDoesNotApplyDiscountIfNoDiscountedProduct(){
        Product productOne = getMockProduct("Milk", 120);
        Product productTwo = getMockProduct("Egg", 50);
        Product productThree = getMockProduct("Apple", 200);

        ProductDecorator activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        OverXTotalDiscount overXTotalDiscountOne = new OverXTotalDiscount(activeDiscount, new Money(400));

        Map<Product, Quantity> items = new HashMap<>();
        items.put(productOne, quantity(1));
        items.put(productTwo, quantity(2));
        items.put(productThree, quantity(1));

        assertEquals(420, overXTotalDiscountOne.calculatePrice(items).getAmountInMinorUnits());
    }

    @Test
    @DisplayName("OverXTotalDiscount/calculatePrice - returns correct discounts with discounted product and other products")
    void overXTotalDiscountAppliesDiscountWithMultipleProductsAndDiscountedProduct(){
        Product productOne = getMockProduct("Milk", 120);
        Product productTwo = getMockProduct("Egg", 50);
        Product productThree = getMockProduct("Apple", 200);

        ProductDecorator activeDiscount = new PercentageDiscount(productOne, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        OverXTotalDiscount overXTotalDiscountOne = new OverXTotalDiscount(activeDiscount, new Money(400));

        Map<Product, Quantity> items = new HashMap<>();
        items.put(productOne, quantity(1));
        items.put(productTwo, quantity(2));
        items.put(productThree, quantity(1));

        assertEquals(336, overXTotalDiscountOne.calculatePrice(items).getAmountInMinorUnits());

        items.remove(productThree);
        assertEquals(220, overXTotalDiscountOne.calculatePrice(items).getAmountInMinorUnits());
    }

    @Test
    @DisplayName("DiscountManager/getBestDiscount - returns correct discounts")
    void discountManagerWithOverXTotalDiscount(){
        Product productOne = getRealProduct("Milk", 120);
        Product productTwo = getRealProduct("Egg", 50);
        Product productThree = getRealProduct("Apple", 200);
        Product productFour = getRealProduct("Gold Apple", 400);

        ProductDecorator activeDiscountOne = new PercentageDiscount(productOne, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        ProductDecorator activeDiscountTwo = new NormalDiscount(productTwo, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        ProductDecorator activeDiscountThree = new PercentageDiscount(productFour, 50, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        OverXTotalDiscount overXTotalDiscount = new OverXTotalDiscount(activeDiscountThree, new Money(400));

        DiscountManager manager = new DiscountManager(activeDiscountOne, activeDiscountTwo, overXTotalDiscount);

        Map<Product, Quantity> items = new HashMap<>();
        items.put(productOne, quantity(1));
        items.put(productTwo, quantity(2));
        items.put(productThree, quantity(1));

        Map<Product, Quantity> discountedItems = manager.getBestDiscount(items, getMockCustomer("Artin", 21, true));

        long amount = 0;
        for (Map.Entry<Product, Quantity> entry : discountedItems.entrySet()) {
            Product p = entry.getKey();
            Quantity q = entry.getValue();
            amount += p.calculatePrice(q).getAmountInMinorUnits();
        }

        assertEquals(356, amount);

        items.put(productFour, quantity(1));
        discountedItems = manager.getBestDiscount(items, getMockCustomer("Artin", 21, true));

        amount = 0;
        for (Map.Entry<Product, Quantity> entry : discountedItems.entrySet()) {
            Product p = entry.getKey();
            if(p instanceof OverXTotalDiscount){
                amount = ((OverXTotalDiscount) p).calculatePrice(items).getAmountInMinorUnits();
            }
        }
        assertEquals(410, amount);
    }

    //Öka täckningsgraden

    @Test
    @DisplayName("MaxXDiscount/calculatePriceWithVat - returns correct price")
    void maxXDiscountReturnsCorrectAmount(){
        ProductDecorator activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        ProductDecorator maxActiveDiscount = new MaxXDiscount(activeDiscount, 2);
        assertEquals(96, maxActiveDiscount.calculatePriceWithVat(quantity(1)).getAmountInMinorUnits());
        assertEquals(312, maxActiveDiscount.calculatePriceWithVat(quantity(3)).getAmountInMinorUnits());

        ProductDecorator inactiveDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE, FIXED_DATE);
        ProductDecorator maxInactiveDiscount = new MaxXDiscount(inactiveDiscount, 2);
        assertEquals(120, maxInactiveDiscount.calculatePriceWithVat(quantity(1)).getAmountInMinorUnits());
    }

    @Test
    @DisplayName("MaxXDiscount/constructor - max integer throws exception")
    void maxXDiscountDoesNotAllowMaxEqualsToMAXINTEGER(){
        ProductDecorator activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        assertThrows(IllegalArgumentException.class, () -> new MaxXDiscount(activeDiscount, Integer.MAX_VALUE));
    }

    @Test
    @DisplayName("ProductDecorator/createFor - returns correct discountType")
    void productDecoratorCreateForReturnsCorrectDiscountType(){
        ProductDecorator activeDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);
        ProductDecorator maxActiveDiscount = new MaxXDiscount(activeDiscount, 2);
        ProductDecorator maxActiveDiscountTwo = maxActiveDiscount.createFor(product);

        assertEquals(maxActiveDiscount.calculatePrice(quantity(1)).getAmountInMinorUnits(), maxActiveDiscountTwo.calculatePrice(quantity(1)).getAmountInMinorUnits());

        ProductDecorator studentDiscount = new StudentDiscount(activeDiscount);
        ProductDecorator studentDiscountTwo = studentDiscount.createFor(product);

        assertEquals(studentDiscount.calculatePrice(quantity(1)).getAmountInMinorUnits(), studentDiscountTwo.calculatePrice(quantity(1)).getAmountInMinorUnits());

        ProductDecorator seniorDiscount = new SeniorDiscount(activeDiscount, 65);
        ProductDecorator seniorDiscountTwo = seniorDiscount.createFor(product);

        assertEquals(seniorDiscount.calculatePrice(quantity(1)).getAmountInMinorUnits(), seniorDiscountTwo.calculatePrice(quantity(1)).getAmountInMinorUnits());

        ProductDecorator twd = new ThreeForTwoDiscount(product, DATE_IN_PAST, DATE_IN_FUTURE);
        ProductDecorator twdTwo = twd.createFor(product);

        assertEquals(twd.calculatePrice(quantity(1)).getAmountInMinorUnits(), twdTwo.calculatePrice(quantity(1)).getAmountInMinorUnits());

        ProductDecorator oX = new OverXTotalDiscount(activeDiscount, new Money(100));
        ProductDecorator oXTwo = oX.createFor(product);

        assertEquals(oX.calculatePrice(quantity(1)).getAmountInMinorUnits(), oXTwo.calculatePrice(quantity(1)).getAmountInMinorUnits());

        ProductDecorator atX = new DiscountAtXTime(activeDiscount, LocalTime.of(8, 0), LocalTime.of(17, 0));
        ProductDecorator atXTwo = atX.createFor(product);

        assertEquals(atX.calculatePrice(quantity(1)).getAmountInMinorUnits(), atXTwo.calculatePrice(quantity(1)).getAmountInMinorUnits());
    }

    @Test
    @DisplayName("PercentageDiscount/calculatePriceWithVat - returns correct discount")
    void percentageDiscountReturnsCorrectCalculation(){
        ProductDecorator inactiveDiscount = new PercentageDiscount(product, DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE, FIXED_DATE);
        assertEquals(120, inactiveDiscount.calculatePriceWithVat(quantity(1)).getAmountInMinorUnits());
    }

    @Test
    @DisplayName("PercentageDiscount/constructor - allows endTime only")
    void percentageDiscountDoesNotThrowIfEndTimeOnly(){
        assertDoesNotThrow(() -> new PercentageDiscount(product, DISCOUNT_AMOUNT, LocalDateTime.now().plusDays(1)));
    }

    @Test
    @DisplayName("NormalDiscount/calculatePriceWithVat - returns correct discount")
    void normalDiscountReturnsCorrectCalculation(){
        ProductDecorator inactiveDiscount = new NormalDiscount(product, DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE, FIXED_DATE);
        assertEquals(120, inactiveDiscount.calculatePriceWithVat(quantity(1)).getAmountInMinorUnits());
    }

    @Test
    @DisplayName("NormalDiscount/constructor - does not throw exception")
    void normalDiscountDoesNotThrowIfEndTimeAndStartTime(){
        assertDoesNotThrow(() -> new NormalDiscount(product, DISCOUNT_AMOUNT, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1)));
    }

    @Test
    @DisplayName("StudentDiscount/calculatePriceWithVat, calculatePriceWith - returns correct discount")
    void studentDiscountReturnsCorrectPriceWhenInactive(){
        ProductDecorator inactiveDiscount = new NormalDiscount(product, DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE, FIXED_DATE);
        ProductDecorator studentDiscount = new StudentDiscount(inactiveDiscount);
        Customer c = getMockCustomer("Artin", 21,true);

        assertEquals(120, studentDiscount.calculatePriceWithVat(quantity(1), c).getAmountInMinorUnits());
        assertEquals(120, studentDiscount.calculatePrice(quantity(1), c).getAmountInMinorUnits());
    }

    @Test
    @DisplayName("SeniorDiscount/calculatePriceWithVat, calculatePriceWith - returns correct discount")
    void seniorDiscountReturnsCorrectPrice(){
        ProductDecorator inactiveDiscount = new NormalDiscount(product, DISCOUNT_AMOUNT, DATE_IN_FUTURE, DATE_IN_FUTURE, FIXED_DATE);
        ProductDecorator activeDiscount = new NormalDiscount(product, DISCOUNT_AMOUNT, DATE_IN_PAST, DATE_IN_FUTURE, FIXED_DATE);

        ProductDecorator seniorDiscount = new SeniorDiscount(activeDiscount, 65);
        Customer c = getMockCustomer("Artin", 21,true);
        Customer cOld = getMockCustomer("Artin", 65,true);

        assertEquals(120, seniorDiscount.calculatePriceWithVat(quantity(1), c).getAmountInMinorUnits());
        assertEquals(120, seniorDiscount.calculatePrice(quantity(1), c).getAmountInMinorUnits());
        assertEquals(120, seniorDiscount.calculatePriceWithVat(quantity(1)).getAmountInMinorUnits());
        assertEquals(120, seniorDiscount.calculatePrice(quantity(1)).getAmountInMinorUnits());
        assertEquals(100, seniorDiscount.calculatePriceWithVat(quantity(1), cOld).getAmountInMinorUnits());

        ProductDecorator seniorDiscountTwo = new SeniorDiscount(inactiveDiscount, 65);
        assertEquals(120, seniorDiscountTwo.calculatePriceWithVat(quantity(1), cOld).getAmountInMinorUnits());
    }


}
