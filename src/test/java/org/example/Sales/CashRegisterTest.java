package org.example.Sales;

import static org.junit.jupiter.api.Assertions.*;

import org.example.Discount.DiscountManager;
import org.example.Discount.PercentageDiscount;
import org.example.Money;
import org.example.Product.Product;
import org.example.Product.UnitPrice;
import org.example.Product.VatRate;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class CashRegisterTest {

    @Test
    void sellerCanLoginWithValidIds() {
        Cashier cashier = new Cashier("Cashier");
        DiscountManager dm = new DiscountManager();
        CashRegister register = new CashRegister(dm);
        register.login(register.getId(), cashier.getId());
        assertTrue(register.loggedIn());
    }

    @Test
    void shouldStartNewPurchaseWhenLoggedIn() {
        Cashier cashier = new Cashier("Cashier");
        DiscountManager dm = new DiscountManager();
        CashRegister register = new CashRegister(dm);
        register.login(register.getId(), cashier.getId());

        Purchase purchase = register.startPurchase();
        assertNotNull(purchase);
    }

    @Test
    void shouldAddProductWithoutDiscount() {
        Cashier cashier = new Cashier("Cashier");
        Product milk = new Product("Milk", new UnitPrice(new Money(10)), VatRate.FOOD, false);
        DiscountManager dm = new DiscountManager();
        CashRegister register = new CashRegister(dm);
        register.login(register.getId(), cashier.getId());
        Purchase purchase = register.startPurchase();

        // Simulerar scanning av 1 produkt
        register.scanProduct(milk, 1);

        // Inga rabatter har applicerats
        assertEquals(new Money(10), purchase.getTotalNet());
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
        register.login(register.getId(), cashier.getId());
        Purchase purchase = register.startPurchase();

        register.scanProduct(coffee, 1);

        purchase.applyDiscounts();

        assertEquals(new Money(45), purchase.getTotalNet());
    }

    @Test
    void shouldAllowRemovingProductBeforeCompletion() {
        Cashier cashier = new Cashier("Cashier");
        Product bread = new Product("Bread", new UnitPrice(new Money(20)), VatRate.FOOD, false);
        DiscountManager dm = new DiscountManager();
        CashRegister register = new CashRegister(dm);
        register.login(register.getId(), cashier.getId());
        Purchase purchase = register.startPurchase();

        register.scanProduct(bread, 2); // 40 kr
        register.removeProduct(bread);  // ta bort

        assertEquals(new Money(0), purchase.getTotalNet());
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