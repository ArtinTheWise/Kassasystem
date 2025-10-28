package org.example.Sales;

import static org.junit.jupiter.api.Assertions.*;

import org.example.Discount.DiscountManager;
import org.example.Discount.PercentageDiscount;
import org.example.Money;
import org.example.Product.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class CashRegisterTest {

    @Test
    void sellerCanLoginWithValidIds() {
        Cashier cashier = new Cashier("Cashier");
        DiscountManager dm = new DiscountManager();
        CashRegister register = new CashRegister(dm);
        register.login(cashier);
        assertTrue(register.loggedIn());
    }

    @Test
    void shouldStartNewPurchaseWhenLoggedIn() {
        Cashier cashier = new Cashier("Cashier");
        DiscountManager dm = new DiscountManager();
        CashRegister register = new CashRegister(dm);
        register.login(cashier);

        register.startPurchase();
        assertNotNull(register.getPurchase());
    }

    @Test
    void logoutShouldWorkWhenNoPurchase() {
        Cashier cashier = new Cashier("Cashier");
        CashRegister register = new CashRegister(new DiscountManager());
        register.login(cashier);

        register.logout();
        assertFalse(register.loggedIn());
    }

    @Test
    void logoutDuringPurchaseShouldThrow() {
        Cashier cashier = new Cashier("Cashier");
        CashRegister register = new CashRegister(new DiscountManager());
        register.login(cashier);
        register.startPurchase();

        assertThrows(IllegalArgumentException.class, register::logout);
    }
    @Test
    void endPurchaseShouldReturnReceiptAndClearPurchase() {
        Cashier cashier = new Cashier("Cashier");
        CashRegister register = new CashRegister(new DiscountManager());
        register.login(cashier);
        register.startPurchase();

        Product milk = new Product("Milk", new UnitPrice(new Money(10)), VatRate.FOOD, false);
        register.scanProduct(milk, 1);

        String receiptStr = register.endPurchase();
        assertNotNull(receiptStr);
        assertNull(register.getPurchase());
    }

    @Test
    void scanProductWithoutPurchaseShouldThrow() {
        Cashier cashier = new Cashier("Cashier");
        CashRegister register = new CashRegister(new DiscountManager());
        register.login(cashier);
        Product milk = new Product("Milk", new UnitPrice(new Money(10)), VatRate.FOOD, false);

        assertThrows(IllegalStateException.class, () -> register.scanProduct(milk, 1));
    }

    @Test
    void removeProductWithoutPurchaseShouldThrow() {
        Cashier cashier = new Cashier("Cashier");
        CashRegister register = new CashRegister(new DiscountManager());
        register.login(cashier);
        Product milk = new Product("Milk", new UnitPrice(new Money(10)), VatRate.FOOD, false);

        assertThrows(IllegalStateException.class, () -> register.removeProduct(milk));
    }
    @Test
    void changeDiscountManagerShouldWorkWhenNoPurchase() {
        Cashier cashier = new Cashier("Cashier");
        CashRegister register = new CashRegister(new DiscountManager());
        register.login(cashier);

        DiscountManager newDm = new DiscountManager();
        register.changeDiscountManager(newDm);
        assertEquals(newDm, register.getDiscountManager());
    }

    @Test
    void changeDiscountManagerDuringPurchaseShouldThrow() {
        Cashier cashier = new Cashier("Cashier");
        DiscountManager dm = new DiscountManager();
        CashRegister register = new CashRegister(dm);
        register.login(cashier);
        register.startPurchase();

        DiscountManager newDm = new DiscountManager();
        assertThrows(IllegalStateException.class, () -> register.changeDiscountManager(newDm));
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
        Cashier cashier = new Cashier("Cashier");
        Product milk = new Product("Milk", new UnitPrice(new Money(10)), VatRate.FOOD, false);
        DiscountManager dm = new DiscountManager();
        CashRegister register = new CashRegister(dm);
        register.login(cashier);
        register.startPurchase();

        // Simulerar scanning av 1 produkt
        register.scanProduct(milk, 1);

        // Inga rabatter har applicerats
        assertEquals(new Money(10), register.getPurchase().getTotalNet());
    }

    @Test
    void shouldApplyDiscountWhenAvailable() {
        Cashier cashier = new Cashier("Cashier");
        // Produkt
        Product coffee = new Product("Coffee", new UnitPrice(new Money(50)), VatRate.FOOD, false);

        // Rabatt
        DiscountManager dm = new DiscountManager();
        PercentageDiscount discount = new PercentageDiscount(
                coffee,
                10,
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(6)
        );
        dm.addDiscount(discount);

        CashRegister register = new CashRegister(dm);
        register.login(cashier);
        register.startPurchase();

        register.scanProduct(coffee, 1);

        register.getPurchase().applyDiscounts();

        assertEquals(new Money(45), register.getPurchase().getTotalNet());
    }

    @Test
    void shouldAllowRemovingProductBeforeCompletion() {
        Cashier cashier = new Cashier("Cashier");
        Product bread = new Product("Bread", new UnitPrice(new Money(20)), VatRate.FOOD, false);
        DiscountManager dm = new DiscountManager();
        CashRegister register = new CashRegister(dm);
        register.login(cashier);
        register.startPurchase();

        register.scanProduct(bread, 2); // 40 kr
        register.removeProduct(bread);  // ta bort

        assertEquals(new Money(0), register.getPurchase().getTotalNet());
    }

    @Test
    void shouldCalculateWeightBasedProductPriceCorrectly() {
        Cashier cashier = new Cashier("Cashier");
        DiscountManager dm = new DiscountManager();
        CashRegister register = new CashRegister(dm);
        register.login(cashier);
        register.startPurchase();

        // 1️⃣ Skapa produkt med viktpris: 30 kr/kg
        Product bananas = new Product("Bananas", new WeightPrice(new Money(30), Unit.KG), VatRate.FOOD, false);

        // 2️⃣ Lägg till 1.5 kg bananer
        register.scanProduct(bananas, 1.5);

        // 3️⃣ Kontrollera totalpris (30 kr/kg * 1.5 kg = 45 kr)
        assertEquals(new Money(45), register.getPurchase().getTotalNet());
    }

    @Test
    void shouldHandleMultipleProducts() {
        Cashier cashier = new Cashier("Cashier");
        DiscountManager dm = new DiscountManager();
        CashRegister register = new CashRegister(dm);
        register.login(cashier);
        register.startPurchase();

        Product milk = new Product("Milk", new UnitPrice(new Money(10)), VatRate.FOOD, false);
        Product bread = new Product("Bread", new UnitPrice(new Money(20)), VatRate.FOOD, false);

        register.scanProduct(milk, 2); // 20 kr
        register.scanProduct(bread, 1); // 20 + 20 = 40 kr

        assertEquals(new Money(40), register.getPurchase().getTotalNet());
    }

    @Test
    void scanningSameProductMultipleTimesAddsQuantities() {
        Cashier cashier = new Cashier("Cashier");
        DiscountManager dm = new DiscountManager();
        Product milk = new Product("Milk", new UnitPrice(new Money(10)), VatRate.FOOD, false);

        CashRegister register = new CashRegister(dm);
        register.login(cashier);
        register.startPurchase();

        register.scanProduct(milk, 1);
        register.scanProduct(milk, 2); // totalt 3 stycken

        assertEquals(new Money(30), register.getPurchase().getTotalNet());
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