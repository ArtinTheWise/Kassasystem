package org.example.Sales;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.example.Money;
import org.example.Discount.DiscountManager;
import org.example.Discount.NormalDiscount;
import org.example.Discount.ThreeForTwoDiscount;
import org.example.Product.PriceModel;
import org.example.Product.Product;
import org.example.Product.Quantity;
import org.example.Product.Unit;
import org.example.Product.UnitPrice;
import org.example.Product.UnitPriceWithPant;
import org.example.Product.VatRate;
import org.example.Product.WeightPrice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ReceiptTest {

    private Cashier mockCashier(){
        Cashier c = mock(Cashier.class);
        return c;
    }

    private CashRegister mockCashRegister(){
        CashRegister cr = mock(CashRegister.class);
        return cr;
    }

    private Purchase mockPurchase() {
        Cashier cashier = mock(Cashier.class);
        when(cashier.getId()).thenReturn(1);

        CashRegister register = mock(CashRegister.class);
        when(register.getId()).thenReturn(1);

        // real Purchase so addPiece/addWeight/use of internal maps all work
        Purchase real = new Purchase(register, cashier);
        return spy(real); // spy allows override if needed later
    }

    // receipt with null purchase
    // receipt with no products in purchase

    private double toKg(Quantity q) {
        switch (q.getUnit()) {
            case KG: return q.getAmount();
            case HG: return q.getAmount() / 10.0;
            case G:  return q.getAmount() / 1000.0;
            default: throw new IllegalArgumentException("Unsupported unit for weight product: " + q.getUnit());
        }
    }
    
    private Product mockWeightProductGrossOnly(String name, Money netPerKg, Money grossPerKg) {
        Product p = mock(Product.class, name);
        WeightPrice pm = mock(WeightPrice.class);
        lenient().when(p.getPriceModel()).thenReturn(pm);

        lenient().when(p.calculatePriceWithVat(any(Quantity.class))).thenAnswer(inv -> {
            Quantity q = inv.getArgument(0);
            double kg = toKg(q);
            long totalMinor = Math.round(grossPerKg.getAmountInMinorUnits() * kg);
            return new Money(totalMinor);
        });

        lenient().when(p.calculatePrice(any(Quantity.class))).thenAnswer(inv -> {
            Quantity q = inv.getArgument(0);
            double kg = toKg(q);
            long totalMinor = Math.round(netPerKg.getAmountInMinorUnits() * kg);
            return new Money(totalMinor);
        });

        lenient().when(pm.getUnit()).thenReturn(Unit.KG);
        lenient().when(p.getName()).thenReturn(name);
        lenient().when(p.getPriceModel()).thenReturn(pm);
        lenient().when(p.getVatRate()).thenReturn(mock(VatRate.class));

        return p;


    }

    private Product mockUnitProduct(String name){
        Product p = mock(Product.class, name);
        PriceModel pm = mock(UnitPrice.class);
        lenient().when(p.getPriceModel()).thenReturn(pm);

        lenient().when(pm.getUnit()).thenReturn(Unit.PIECE);
        lenient().when(p.getName()).thenReturn(name);
        lenient().when(p.getPriceModel()).thenReturn(pm);
        lenient().when(p.getVatRate()).thenReturn(mock(VatRate.class));

        return p;


    }

    private Product mockUnitProductWithGross(String name, Money netPerPiece, Money grossPerPiece) {
        Product p = mock(Product.class, name);
        UnitPrice pm = mock(UnitPrice.class);
        lenient().when(p.getPriceModel()).thenReturn(pm);

        lenient().when(p.calculatePrice(any(Quantity.class))).thenAnswer(inv -> {
            Quantity q = inv.getArgument(0);
            long qty = (long) q.getAmount();
            return new Money(netPerPiece.getAmountInMinorUnits() * qty);
        });

        lenient().when(p.calculatePriceWithVat(any(Quantity.class))).thenAnswer(inv -> {
            Quantity q = inv.getArgument(0);
            long qty = (long) q.getAmount();
            return new Money(grossPerPiece.getAmountInMinorUnits() * qty);
        });

        lenient().when(pm.getUnit()).thenReturn(Unit.PIECE);
        lenient().when(p.getName()).thenReturn(name);
        lenient().when(p.getPriceModel()).thenReturn(pm);
        lenient().when(p.getVatRate()).thenReturn(mock(VatRate.class));

        return p;
    }

    private Product mockUnitProductWithPantGrossOnly(String name, Money grossPerPiece) {
        Product p = mock(Product.class, name);
        UnitPriceWithPant pm = mock(UnitPriceWithPant.class);
        lenient().when(p.getPriceModel()).thenReturn(pm);
        lenient().when(pm.getPantPerPiece()).thenReturn(new Money(100));

        lenient().when(p.calculatePriceWithVat(any(Quantity.class))).thenAnswer(inv -> {
            Quantity q = inv.getArgument(0);
            long qty = (long) q.getAmount();
            return new Money(grossPerPiece.getAmountInMinorUnits() * qty);
        });

        lenient().when(p.calculatePrice(any(Quantity.class))).thenAnswer(inv -> {
            Quantity q = inv.getArgument(0);
            long qty = (long) q.getAmount();

            long gross = grossPerPiece.getAmountInMinorUnits() * qty;
            return new Money(gross);
        });

        lenient().when(pm.getUnit()).thenReturn(Unit.PIECE);
        lenient().when(p.getName()).thenReturn(name);
        lenient().when(p.getPriceModel()).thenReturn(pm);
        lenient().when(p.getVatRate()).thenReturn(mock(VatRate.class));

        return p;
    }


    @Test
    @DisplayName("constructor: null purchase throws exception")
    void createReceiptWithNullPurchaseThrowsException(){
        assertThrows(NullPointerException.class, 
            () -> new Receipt(null));
    }

    @SuppressWarnings("unused")
    @Test
    @DisplayName("constructor: purchase with no articles throws exception")
    void createReceiptWithPurchaseWithoutArticlesThrowsException(){
        CashRegister cashRegister = mockCashRegister();
        Cashier cashier = mockCashier();

        Purchase empty = new Purchase(cashRegister, cashier);
        assertThrows(IllegalArgumentException.class, 
            () -> new Receipt(new Purchase(cashRegister, cashier))); // Empty purchase - no articles added.
    }

    @Test
    @DisplayName("Constructor: receipts generate unique ids")
    void receiptHeaderContainsReceiptId(){
        Purchase purchase = mockPurchase();
        Product banana = mockUnitProduct("banana");
        purchase.addPiece(banana);

        Receipt receiptOne = new Receipt(purchase);
        Receipt receiptTwo = new Receipt(purchase);

        assertNotEquals(receiptOne.getId(), receiptTwo.getId());
    }

    @Test
    @DisplayName("Header: receipt contains cashier id")
    void receiptHeaderContainsCashierId(){

        Purchase purchase = mockPurchase();
        Product banana = mockUnitProductWithGross("banana", new Money(1000),new Money(1250));
        purchase.addPiece(banana);
        
        Receipt receipt = new Receipt(purchase);

        assertTrue(receipt.toString().contains("cashier: 1"));
    }

    @Test
    @DisplayName("Header: receipt contains cashRegister id")
    void receiptHeaderContainsCashRegisterId(){

        Purchase purchase = mockPurchase();
        Product banana = mockUnitProductWithGross("banana", new Money(1000),new Money(1250));
        purchase.addPiece(banana);
        
        Receipt receipt = new Receipt(purchase);

        assertTrue(receipt.toString().contains("cashRegister: 1"));
    }

    @Test
    @DisplayName("Header: receipt contains date")
    void receiptHeaderContainsDate(){

        Purchase purchase = mockPurchase();
        Product banana = mockUnitProductWithGross("banana", new Money(1000),new Money(1250));
        purchase.addPiece(banana);

        Receipt receipt = new Receipt(purchase);

        assertTrue(receipt.toString().contains(purchase.getDate().toString()));
        
    }

    @Test
    @DisplayName("Header: receipt contains time")
    void receiptHeaderContainsTime(){

        Purchase purchase = mockPurchase();
        Product banana = mockUnitProductWithGross("banana", new Money(1000),new Money(1250));
        purchase.addPiece(banana);

        Receipt receipt = new Receipt(purchase);

        String time = purchase.getTime().truncatedTo(ChronoUnit.MINUTES).toString();

        assertTrue(receipt.toString().contains(time));
    }

    @Test
    @DisplayName("large receipt works correctly") // just for reading purposes
    void receiptManyArticles() {
        LocalDateTime ends = LocalDateTime.of(2099, 1, 1, 0, 0);

        Product banana = mockUnitProductWithGross("Banana", new Money(1000), new Money(1250));
        Product twix = mockUnitProductWithGross("Twix", new Money(700), new Money(875));
        Product snickers = mockUnitProductWithGross("Snickers", new Money(760), new Money(950));
        Product olwDill = mockUnitProductWithGross("OLW Dill & Gräslök", new Money(2000), new Money(2500));
        Product potato = mockWeightProductGrossOnly("Potato", new Money(2000), new Money(2500));
        Product cocaCola33Cl = mockUnitProductWithPantGrossOnly("Coca Cola 33Cl", new Money(800));

        DiscountManager discountManager = new DiscountManager();
        CashRegister cashRegister = new CashRegister(discountManager);
        Cashier cashier = new Cashier("TestCashier");
        Purchase purchase = new Purchase(cashRegister, cashier, discountManager);

        // discounts: 500 off OLW and 3-for-2 on bananas
        NormalDiscount nd = new NormalDiscount(olwDill, 500, ends);
        ThreeForTwoDiscount tf2 = new ThreeForTwoDiscount(banana, ends);
        discountManager.addDiscount(nd, tf2);

        // scan items
        purchase.addPiece(banana);
        purchase.addPiece(twix);
        purchase.addPiece(snickers);
        purchase.addPiece(olwDill);
        purchase.addWeight(potato, 1, Unit.KG);
        purchase.addPiece(cocaCola33Cl);
        purchase.addPiece(banana);
        purchase.addPiece(banana);

        // apply discounts and build receipt
        purchase.applyDiscounts();
        Receipt receipt = new Receipt(purchase);

        String out = receipt.toString();

        // --- structural/header assertions ---
        assertTrue(out.contains("StoreName"), "Store name missing");
        assertTrue(out.contains("StoreLocation"), "Store location missing");

        assertTrue(out.contains("cashier: " + cashier.getId()), "Cashier id missing");
        assertTrue(out.contains("cashRegister: " + cashRegister.getId()), "Cash register id missing");
        assertTrue(out.contains("Nr: " + receipt.getId()), "Receipt id missing");

        assertTrue(out.contains("Date:"), "Date missing");
        assertTrue(out.contains("Time:"), "Time missing");

        // separators
        assertTrue(out.contains("\n---------------------------"), "Missing line separator");
        assertEquals(2, out.split("\n---------------------------", -1).length - 1, "Should have two separators");

        // --- line-items present (don’t assert exact numeric formatting) ---
        assertTrue(out.contains("Banana"), "Banana line missing (1st)");
        assertTrue(out.contains("Twix"), "Twix line missing");
        assertTrue(out.contains("Snickers"), "Snickers line missing");
        assertTrue(out.contains("OLW"), "OLW line missing");
        assertTrue(out.contains("Potato"), "Potato (weighted) line missing");
        assertTrue(out.contains("Coca Cola"), "Coca Cola line missing");
        // bananas added two more times (to trigger 3-for-2)
        // we only assert that at least one banana line exists; quantity is printed per entry by design
        // If your purchase collapses identical products, update this to match that behavior:
        assertTrue(out.contains("Banana"), "Banana line(s) missing (additional)");

        // --- totals must match purchase figures exactly as rendered by Receipt ---
        BigDecimal expectedGross = BigDecimal.valueOf(purchase.getTotalGross().getAmountInMinorUnits(), 2);
        BigDecimal expectedNet   = BigDecimal.valueOf(purchase.getTotalNet().getAmountInMinorUnits(), 2);
        BigDecimal expectedVat   = BigDecimal.valueOf(purchase.getTotalVat().getAmountInMinorUnits(), 2);

        assertTrue(out.contains("\nTotal\t" + expectedGross), "Total gross does not match purchase");

        String summaryRow = "\n25,00\t" + expectedVat + "\t" + expectedNet + "\t" + expectedGross;
        assertTrue(out.contains("Moms% \tMoms \tNetto \tBrutto"), "VAT header missing");
        assertTrue(out.contains(summaryRow), "VAT/Net/Gross summary row mismatch");

        // --- sanity: no accidental 'null' text anywhere ---
        assertFalse(out.contains("null"), "Receipt contains 'null' text");

        // --- optional: ensure discounts had some effect ---
        // We can at least assert that discounted totals are <= the sum without discounts.
        // (This doesn’t rely on specific discount math.)
        // If you have an API to compute totals before discounts, compare explicitly here.
    }

    @Test
    @DisplayName("GetPurchase: returns correct purchase")
    void getPurchaseReturnsCorrectPurchase(){
        Purchase purchase = mockPurchase();
        Product banana = mockUnitProductWithGross("banana", new Money(1000),new Money(1250));
        purchase.addPiece(banana);

        Receipt receipt = new Receipt(purchase);

        assert(receipt.getPurchase().equals(purchase));
    }

}



