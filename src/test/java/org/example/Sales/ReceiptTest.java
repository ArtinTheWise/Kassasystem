package org.example.Sales;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.example.Money;
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
import org.mockito.MockedStatic;
import org.mockito.Mockito;

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
    
    private Product mockWeightProductGrossOnly(String name, Money grossPerKg) {
        Product p = mock(Product.class, name);
        WeightPrice pm = mock(WeightPrice.class);
        lenient().when(p.getPriceModel()).thenReturn(pm);

        lenient().when(p.calculatePriceWithVat(any(Quantity.class))).thenAnswer(inv -> {
            Quantity q = inv.getArgument(0);
            double kg = toKg(q);
            long totalMinor = Math.round(grossPerKg.getAmountInMinorUnits() * kg);
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

        System.out.println(receipt.toString());

        assertTrue(receipt.toString().contains(purchase.getTime().toString()));


    }


}



