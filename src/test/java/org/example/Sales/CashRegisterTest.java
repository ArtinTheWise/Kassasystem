package org.example.Sales;

import static org.junit.jupiter.api.Assertions.*;

import org.example.Discount.DiscountManager;
import org.example.Discount.PercentageDiscount;
import org.example.Membership.BonusCheck;
import org.example.Membership.Customer;
import org.example.Membership.Points;
import org.example.Money;
import org.example.Product.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class CashRegisterTest {

    private final String validSSN = "200001011234";
    private final String validEmailAddress = "mail@example.com";


    private Cashier cashier;
    private DiscountManager discountManager;
    private Customer customer;
    private CashRegister cashRegister;

    @BeforeEach
    void setUp() {
        cashier = new Cashier("Cashier");
        discountManager = new DiscountManager();
        customer = new Customer(validSSN, validEmailAddress);
        customer.becomeMember();
        cashRegister = new CashRegister(discountManager);
    }


    @Test
    void sellerCanLogin() {
        cashRegister.login(cashier);
        assertTrue(cashRegister.loggedIn());
    }

    @Test
    void twoSellerCannotLoginAtOnce() {
        Cashier cashier2 = new Cashier("Cashier");

        cashRegister.login(cashier);
        assertThrows(IllegalStateException.class, () -> cashRegister.login(cashier2));
    }


    @Test
    void shouldStartNewPurchaseWhenLoggedIn_nullCustomer() {
        cashRegister.login(cashier);

        cashRegister.startPurchase(null);
        assertNotNull(cashRegister.getPurchase());
    }

    @Test
    void shouldStartNewPurchaseWhenLoggedIn_nonMemberCustomer() {
        cashRegister.login(cashier);

        cashRegister.startPurchase(new Customer(validSSN, validEmailAddress));
        assertNotNull(cashRegister.getPurchase());
    }

    @Test
    void logoutShouldWorkWhenNoPurchase() {
        cashRegister.login(cashier);

        cashRegister.logout();
        assertFalse(cashRegister.loggedIn());
    }

    @Test
    void logoutDuringPurchaseShouldThrow() {
        cashRegister.login(cashier);
        cashRegister.startPurchase(null);

        assertThrows(IllegalArgumentException.class, cashRegister::logout);
    }

    @Test
    void logoutWhenNoOneIsLoggedIn() {
        assertThrows(IllegalStateException.class, cashRegister::logout);
    }


    @Test
    void endPurchaseShouldReturnReceiptAndClearPurchase() {
        cashRegister.login(cashier);
        cashRegister.startPurchase(null);

        Product milk = new Product("Milk", new UnitPrice(new Money(10)), VatRate.FOOD, false);
        cashRegister.scanProduct(milk, 1);

        String receiptStr = cashRegister.endPurchase();
        assertNotNull(receiptStr);
        assertNull(cashRegister.getPurchase());
    }

    @Test
    void scanProductWithoutPurchaseShouldThrow() {
        cashRegister.login(cashier);
        Product milk = new Product("Milk", new UnitPrice(new Money(10)), VatRate.FOOD, false);

        assertThrows(IllegalStateException.class, () -> cashRegister.scanProduct(milk, 1));
    }

    @Test
    void removeProductWithoutPurchaseShouldThrow() {
        cashRegister.login(cashier);
        Product milk = new Product("Milk", new UnitPrice(new Money(10)), VatRate.FOOD, false);

        assertThrows(IllegalStateException.class, () -> cashRegister.removeProduct(milk));
    }
    @Test
    void changeDiscountManagerShouldWorkWhenNoPurchase() {
        cashRegister.login(cashier);

        DiscountManager newDm = new DiscountManager();
        cashRegister.changeDiscountManager(newDm);
        assertEquals(newDm, cashRegister.getDiscountManager());
    }

    @Test
    void changeDiscountManagerDuringPurchaseShouldThrow() {
        cashRegister.login(cashier);
        cashRegister.startPurchase(null);

        DiscountManager newDm = new DiscountManager();
        assertThrows(IllegalStateException.class, () -> cashRegister.changeDiscountManager(newDm));
    }

    @Test
    void cashRegisterIdShouldAutoIncrement() {
        DiscountManager dm = new DiscountManager();
        CashRegister r1 = new CashRegister(dm);
        CashRegister r2 = new CashRegister(dm);
        assertTrue(r2.getId() > r1.getId());
    }

    @Test
    void shouldAddProductWithoutDiscount() {
        Product milk = new Product("Milk", new UnitPrice(new Money(10)), VatRate.FOOD, false);

        cashRegister.login(cashier);
        cashRegister.startPurchase(null);

        cashRegister.scanProduct(milk, 1);

        // Inga rabatter har applicerats
        assertEquals(new Money(10), cashRegister.getPurchase().getTotalNet());
    }

    @Test
    void shouldApplyDiscountWhenAvailable() {
        Product coffee = new Product("Coffee", new UnitPrice(new Money(50)), VatRate.FOOD, false);


        PercentageDiscount discount = new PercentageDiscount(
                coffee,
                10,
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(6)
        );
        discountManager.addDiscount(discount);


        cashRegister.login(cashier);
        cashRegister.startPurchase(null);

        cashRegister.scanProduct(coffee, 1);

        cashRegister.getPurchase().applyDiscounts();

        assertEquals(new Money(45), cashRegister.getPurchase().getTotalNet());
    }

    @Test
    void shouldAllowRemovingProductBeforeCompletion() {
        Product bread = new Product("Bread", new UnitPrice(new Money(20)), VatRate.FOOD, false);

        cashRegister.login(cashier);
        cashRegister.startPurchase(null);

        cashRegister.scanProduct(bread, 2); // 40 kr
        cashRegister.removeProduct(bread);  // ta bort

        assertEquals(new Money(0), cashRegister.getPurchase().getTotalNet());
    }

    @Test
    void shouldCalculateWeightBasedProductPriceCorrectly() {
        cashRegister.login(cashier);
        cashRegister.startPurchase(null);

        Product bananas = new Product("Bananas", new WeightPrice(new Money(30), Unit.KG), VatRate.FOOD, false);

        cashRegister.scanProduct(bananas, 1.5);

        assertEquals(new Money(45), cashRegister.getPurchase().getTotalNet());
    }

    @Test
    void shouldHandleMultipleProducts() {
        cashRegister.login(cashier);
        cashRegister.startPurchase(null);

        Product milk = new Product("Milk", new UnitPrice(new Money(10)), VatRate.FOOD, false);
        Product bread = new Product("Bread", new UnitPrice(new Money(20)), VatRate.FOOD, false);

        cashRegister.scanProduct(milk, 2); // 20 kr
        cashRegister.scanProduct(bread, 1); // 20 + 20 = 40 kr

        assertEquals(new Money(40), cashRegister.getPurchase().getTotalNet());
    }

    @Test
    void scanningSameProductMultipleTimesAddsQuantities() {
        Product milk = new Product("Milk", new UnitPrice(new Money(10)), VatRate.FOOD, false);

        cashRegister.login(cashier);
        cashRegister.startPurchase(null);

        cashRegister.scanProduct(milk, 1);
        cashRegister.scanProduct(milk, 2); // totalt 3 stycken

        assertEquals(new Money(30), cashRegister.getPurchase().getTotalNet());
    }

    @Test
    void startPurchaseShouldAddBonusCheckDiscountsFromCustomer() {
        Product coffee = new Product("Coffee", new UnitPrice(new Money(100)), VatRate.FOOD, false);

        // Bonuscheck = 50% rabatt på coffee
        customer.getMembership().addCheck(new BonusCheck("Coffee 50%",
                new PercentageDiscount(coffee, 50, LocalDateTime.now(), LocalDateTime.now().plusDays(1)),
                new Points(200)
        ));

        cashRegister.login(cashier);

        cashRegister.startPurchase(customer);

        //DiscountManager ska nu innehålla bonuscheck-rabatten
        assertTrue(discountManager.discountCheck(coffee), "DiscountManager should contain customer's bonus discount");
    }

    @Test
    void endPurchaseShouldAddPointsToCustomerMembership() {
        cashRegister.login(cashier);

        cashRegister.startPurchase(customer);

        // Lägg till produkt för 500 kr
        Product tv = new Product("TV", new UnitPrice(new Money(50000)), VatRate.OTHER, false);
        cashRegister.scanProduct(tv, 1);

        cashRegister.endPurchase();

        //500.00 kr = 500 i major units 500/100 = 5 poäng
        assertEquals(5, customer.getMembership().getPoints().getAmount(), "Customer should gain correct points from purchase");
    }

    @Test
    void bonusCheckDiscountShouldAffectFinalPrice() {
        Product coffee = new Product("Coffee", new UnitPrice(new Money(100)), VatRate.FOOD, false);

        // Bonuscheck 50% rabatt på coffee
        BonusCheck bonusCheck = new BonusCheck("Coffee 50%",
                new PercentageDiscount(coffee,50, LocalDateTime.now(), LocalDateTime.now().plusDays(1)),
                new Points(200)
        );
        customer.getMembership().addCheck(bonusCheck);

        cashRegister.login(cashier);

        cashRegister.startPurchase(customer); // ska ladda in bonuscheckens rabatt i DiscountManager
        cashRegister.scanProduct(coffee, 1);
        cashRegister.getPurchase().applyDiscounts();

        //rabatten på 50% ska ge totalpris 50 kr
        assertEquals(new Money(50), cashRegister.getPurchase().getTotalNet(),
                "Bonus check discount should reduce product price by 50%");
    }

    @Test
    void bonusCheckShouldBeRemovedIfUsedOtherwiseRemain() {
        Product coffee = new Product("Coffee", new UnitPrice(new Money(100)), VatRate.FOOD, false);
        Product tea = new Product("Tea", new UnitPrice(new Money(80)), VatRate.FOOD, false);

        // Bonuscheck = 50% rabatt på Coffee
        BonusCheck bonusCheck = new BonusCheck(
                "Coffee 50%",
                new PercentageDiscount(
                        coffee,
                        50,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(1)
                ),
                new Points(200)
        );
        customer.getMembership().addCheck(bonusCheck);

        cashRegister.login(cashier);

        // bonuscheck ska användas (Coffee köps)
        cashRegister.startPurchase(customer);
        cashRegister.scanProduct(coffee, 1);
        cashRegister.getPurchase().applyDiscounts();
        cashRegister.endPurchase();

        // Kontrollera att bonuschecken nu har tagits bort
        assertTrue(customer.getMembership().getChecks().isEmpty(),
                "BonusCheck should be removed after it has been used on a matching product");

        // bonuscheck ska INTE användas (Tea köps)
        // Lägg till ny bonuscheck
        BonusCheck bonusCheck2 = new BonusCheck(
                "Coffee 50%",
                new PercentageDiscount(
                        coffee,
                        50,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(1)
                ),
                new Points(200)
        );
        customer.getMembership().addCheck(bonusCheck2);

        cashRegister.startPurchase(customer);
        cashRegister.scanProduct(tea, 1);
        cashRegister.getPurchase().applyDiscounts();
        cashRegister.endPurchase();

        // Kontrollera att bonuschecken finns kvar eftersom den inte användes
        assertFalse(customer.getMembership().getChecks().isEmpty(),
                "BonusCheck should remain if it was not applied to any product");
    }

}




//    Kassa - taget från Bäckmans discord meddelande
//
//    1. Säljare loggar in (kassa id + säljare id)
//
//    2. Köp startas - Purchase objekt:
//
//        Kassa (för id:t)
//        Seller (för id:t)
//        List<Product> products
//        LocalDateTime
//        Money Total netto pris
//        Money Total Moms
//        Money Total rabatt
//        Money Brutto pris
//
//
//    3. Scanna Product 1:
//        Skapa Quantity:
//        Weight: Product + Weight (KAN SKAPAS PÅ VÅG SEPARAT)
//        Unit: Product + Amount (1)
//        Sök bästa rabatt:
//        Finns: lägg till reducering.
//        Finns inte: x
//        Applicera pris
//        totalPris += quantityGetPrice
//        Om rabatt: += total rabatt
//
//    4. Scanna Product 2:
//        Finns produkten i listan - sökning i O(N), låg overhead ArrayList - Array?
//        JA:
//        Weight: amount += productWeight
//        Unit: amount++
//        Nej - Skapa ny quantity ^
//                receipt.setPrice(Quantity);
//
//    4.a Ta bort produkt:
//        går att göra innan köpet är "slutfört"
//
//    5. Köpt slutfört
//        sök rabatter: discountManager.getBestDiscount(quantity.getProduct(), amount);
//        kolla medlemskap
//        sätter pris
//    6. Betalning
//
//
//    7. Skriv ut kvitto