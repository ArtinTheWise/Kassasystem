package org.example.Sales;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import org.mockito.Mock;

public class ReceiptTest {

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

    private Product mockUnitProductNetOnly(String name, Money netPerPiece) {
        Product p = mock(Product.class, name);
        UnitPrice pm = mock(UnitPrice.class);
        lenient().when(p.getPriceModel()).thenReturn(pm);

        lenient().when(p.calculatePrice(any(Quantity.class))).thenAnswer(inv -> {
            Quantity q = inv.getArgument(0);
            long qty = (long) q.getAmount(); // PIECE quantities are whole numbers in these tests
            return new Money(netPerPiece.getAmountInMinorUnits() * qty);
        });

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

    private Product mockUnitProductGrossOnly(String name, Money grossPerPiece) {
        Product p = mock(Product.class, name);
        UnitPrice pm = mock(UnitPrice.class);
        lenient().when(p.getPriceModel()).thenReturn(pm);

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

    private Product mockWeightProduct(String name) {
        Product p = mock(Product.class, name);
        PriceModel pm = mock(WeightPrice.class);
        lenient().when(p.getPriceModel()).thenReturn(pm);

        lenient().when(pm.getUnit()).thenReturn(Unit.KG);
        lenient().when(p.getName()).thenReturn(name);
        lenient().when(p.getPriceModel()).thenReturn(pm);
        lenient().when(p.getVatRate()).thenReturn(mock(VatRate.class));

        return p;

    }

    private Product mockUnitProductWithPant(String name) {
        Product p = mock(Product.class, name);
        PriceModel pm = mock(UnitPriceWithPant.class);
        lenient().when(p.getPriceModel()).thenReturn(pm);

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
        var cashRegister = new Object();
        var cashier = new Object();

        Purchase empty = new Purchase(cashRegister, cashier);
        assertThrows(IllegalArgumentException.class, 
            () -> new Receipt(new Purchase(cashRegister, cashier))); // Empty purchase - no articles added.
    }

    @Test
    @DisplayName("Header: receipt contains cashier id")
    void receiptHeaderContainsCashierId(){
        var cashRegister = new Object();
        var cashier = new Object();
    }

    






}



