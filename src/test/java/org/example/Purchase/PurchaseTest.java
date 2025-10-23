package org.example.Purchase;

import static org.example.Product.VatRate.OTHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.example.Money;
import org.example.Product.VatRate;
import org.example.Product.PriceModel;
import org.example.Product.Product;
import org.example.Product.Quantity;
import org.example.Product.Unit;
import org.example.Product.WeightPrice;
import org.example.Product.UnitPrice;
import org.example.Product.UnitPriceWithPant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import Sales.Purchase;

@ExtendWith(MockitoExtension.class)
public class PurchaseTest {

    /*
     * konstruktor 
     * kassa id - null / finns ej / fel instans
     * Seller id - null / finns ej / fel instans
     * addItem - null / finns ej / fel instance
     * 
     * createReceipt - toString metod
     * 
     * lägga till viktvara - klar
     * lägga till styckvara - klar
     * lägga till viktvara igen. -e.g. första artikel är vikt, sen massa andra, sen samma viktvara igen
     * samma som ovan för styck - klar
     * skapa flera mockrabatter och se att den applicerar rätt rabatt på en quantity
     * räkna pant korrekt
     * kan ta bort en vara under ett köp - klar
     * 
     * 
     * Pris:
     *      totalNet
     *      totalGross
     *      totalVAT
     *      (inkl räkna på pant)
     */


    public interface SalesEmployee { String getId(); }
    public interface CashRegister  { String getId(); }

    @Mock CashRegister cashRegister;
    @Mock SalesEmployee salesEmployee; 
    @Mock Quantity quantity;

    private Product mockUnitProduct(String name){
        Product p = mock(Product.class, name);
        PriceModel pm = mock(UnitPrice.class);
        when(p.getPriceModel()).thenReturn(pm);
        return p;
    }
    private Product mockUnitProductWithInfo(String name, Money price, VatRate vatRate){ // namn, pris i öre, moms i %. e.g banan, 500, OTHER - banan 500kr 25% moms
        Product p = mock(Product.class, name);
        PriceModel pm = mock(WeightPrice.class);

        when(p.getPriceModel()).thenReturn(pm);
        when(p.getVatRate()).thenReturn(vatRate);
        when(p.calculatePrice(any(Quantity.class)))
            .thenAnswer(inv -> {
                Quantity q = inv.getArgument(0);
                return new Money(price.getAmountInMinorUnits() * (int)q.getAmount());
            });

        return p;
    }

    private Product mockWeightProduct(String name) {
        Product p = mock(Product.class, name);
        PriceModel pm = mock(WeightPrice.class);

        when(p.getPriceModel()).thenReturn(pm);

        when(pm.calculatePrice(any(Quantity.class)))
            .thenAnswer(inv -> {
                Quantity q = inv.getArgument(0);
                return new Money(100 * (int)q.getAmount());
            });
            
        return p;
    }

    private Product mockUnitProductWithPant(String name) { // 1 kr pant
        Product p = mock(Product.class, name);
        UnitPrice basePrice = mock(UnitPrice.class);

        when(basePrice.calculatePrice(any(Quantity.class)))
            .thenAnswer(inv -> {
                Quantity q = inv.getArgument(0);
                return new Money(100 * (int)q.getAmount());

            });
        UnitPriceWithPant realPriceModel = new UnitPriceWithPant(basePrice, new Money(1L));

        when(p.getPriceModel()).thenReturn(realPriceModel);

        return p;
    }

    @Test
    @DisplayName("constructor: null salesEmployee throws exception")
    void createPurchaseWithNullCashRegisterThrowsException(){
        assertThrows(IllegalArgumentException.class, 
            () -> new Purchase(null, salesEmployee));
    }

    @Test
    @DisplayName("constructor: null cashRegister throws exception")
    void createPurchaseWithNullSalesEmployeeThrowsException(){
        assertThrows(IllegalArgumentException.class, 
            () -> new Purchase(cashRegister, null));
    }

    @Test
    @DisplayName("addPiece: throws exception for null Product")
    void addPiece_onNullProduct_throws(){
        Purchase purchase = new Purchase(cashRegister, salesEmployee);
        assertThrows(NullPointerException.class, 
            () -> purchase.addPiece(null));
    }

    @Test
    @DisplayName("addPiece: throws exception for weightPice")
    void addPiece_onWeightProduct_throws(){
        Purchase purchase = new Purchase(cashRegister, salesEmployee);
        Product cheese = mockWeightProduct("Cheese");
        
        assertThrows(IllegalArgumentException.class,
            () -> purchase.addPiece(cheese));
    }

    @Test
    @DisplayName("addWeight: throws exception for null Product")
    void addWeight_onNullProduct_throws(){
        Purchase purchase = new Purchase(cashRegister, salesEmployee);
        assertThrows(NullPointerException.class, 
            () -> purchase.addWeight(null, 0.432, Unit.G)); // unit + weight doesn't matter.
    }

    @Test
    @DisplayName("addWeight: throws exception for Piece Product")
    void addWeight_onPieceProduct_throws() {
        Product beef = mockUnitProduct("beef");

        Purchase purchase = new Purchase(cashRegister, salesEmployee);
        assertThrows(IllegalArgumentException.class,
            () -> purchase.addWeight(beef, 0.432, Unit.G)); // unit + weight doesn't matter.
    }

    @Test
    @DisplayName("addWeight: add weight for KG product")
    void addWeight_addsAmount_forKGProduct() {
        Purchase purchase = new Purchase(cashRegister, salesEmployee);
        Product potato = mockWeightProduct("Potato");

        purchase.addWeight(potato, 0.350, Unit.KG);

        Map<Product,Quantity> items = purchase.getItemsView();
        assertEquals(1, items.size());
        Quantity q = items.get(potato);
        assertNotNull(q);
        assertEquals(0.350, q.getAmount(), 0.0001);
        assertEquals(Unit.KG, q.getUnit());
    }

    @Test
    @DisplayName("addWeight: add weight for HG product")
    void addWeight_addsAmount_forHGProduct() {
        Purchase purchase = new Purchase(cashRegister, salesEmployee);
        Product potato = mockWeightProduct("Potato");

        purchase.addWeight(potato, 0.350, Unit.HG);

        Map<Product,Quantity> items = purchase.getItemsView();
        assertEquals(1, items.size());
        Quantity q = items.get(potato);
        assertNotNull(q);
        assertEquals(0.350, q.getAmount(), 0.0001);
        assertEquals(Unit.HG, q.getUnit());
    }

    @Test
    @DisplayName("addWeight: add weight for G product")
    void addWeight_addsAmount_forGProduct() {
        Purchase purchase = new Purchase(cashRegister, salesEmployee);
        Product potato = mockWeightProduct("Potato");

        purchase.addWeight(potato, 0.350, Unit.G);

        Map<Product,Quantity> items = purchase.getItemsView();
        assertEquals(1, items.size());
        Quantity q = items.get(potato);
        assertNotNull(q);
        assertEquals(0.350, q.getAmount(), 0.0001);
        assertEquals(Unit.G, q.getUnit());
    }


    @Test
    @DisplayName("addPiece - add piece for piece product ")
    void addPiece_addsOnePiece() {
        Purchase purchase = new Purchase (cashRegister, salesEmployee);
        Product banana = mockUnitProduct("Banana");

        purchase.addPiece(banana);

        Map<Product,Quantity> items = purchase.getItemsView();
        assertEquals(1, items.size());
        Quantity q = items.get(banana);
        assertNotNull(q);
        assertEquals(1.0, q.getAmount(), 0.0001);
        assertEquals(Unit.PIECE, q.getUnit());
    }

    @Test
    @DisplayName("addPiece - add several pieces correctly")
    void addPiece_addsManyPieces() {
        Purchase purchase = new Purchase(cashRegister, salesEmployee);
        Product karinsLasagne = mockUnitProduct("Karins lasagne");

        for (int i = 0; i < 3; i++){
            purchase.addPiece(karinsLasagne);
        }

        Quantity q = purchase.getItemsView().get(karinsLasagne);
        assertEquals(3.0, q.getAmount(), 0.0001);
        assertEquals(Unit.PIECE, q.getUnit());
    
    }

    @Test
    @DisplayName("addPieceWithPant - add piece for piece with pant product ")
    void addPiece_addsOnePieceWithPant() {
        Purchase purchase = new Purchase (cashRegister, salesEmployee);
        Product cola = mockUnitProductWithPant("Coca Cola 33cl");

        purchase.addPiece(cola);

        Map<Product,Quantity> items = purchase.getItemsView();
        assertEquals(1, items.size());
        Quantity q = items.get(cola);
        assertNotNull(q);
        assertEquals(1.0, q.getAmount(), 0.0001);
        assertEquals(Unit.PIECE, q.getUnit());
        assertTrue(cola.getPriceModel() instanceof UnitPriceWithPant);
    }

    @Test
    @DisplayName("addPiece + addWeight- add Piece then weight then piece correctly")
    void addPiece_addsUnitsInDifferentOrder() {
        Purchase purchase = new Purchase(cashRegister, salesEmployee);
        Product karinsLasagne = mockUnitProduct("Karins lasagne");
        Product potato = mockWeightProduct("Potato");

        purchase.addPiece(karinsLasagne);
        purchase.addWeight(potato, 0.350, Unit.G);
        purchase.addPiece(karinsLasagne); //should be added to same quantity as first one

        Quantity q = purchase.getItemsView().get(karinsLasagne);
        assertEquals(2.0, q.getAmount(), 0.0001);
        assertEquals(Unit.PIECE, q.getUnit());
    
    }

    @Test
    @DisplayName("addPiece + addWeight- add Weight then Piece then Weight correctly")
    void addPiece_addsUnitsInDifferentOrderTwo() {
        Purchase purchase = new Purchase(cashRegister, salesEmployee);
        Product karinsLasagne = mockUnitProduct("Karins lasagne");
        Product potato = mockWeightProduct("Potato");

        purchase.addWeight(potato, 0.350, Unit.G);
        purchase.addPiece(karinsLasagne);
        purchase.addWeight(potato, 0.400, Unit.G); //should be added to same quantity as first one

        Quantity q = purchase.getItemsView().get(potato);
        assertEquals(0.750, q.getAmount(), 0.0001);
        assertEquals(Unit.G, q.getUnit());
    
    }

    @Test
    @DisplayName("removeProduct - removes the whole row")
    void removeProduct_removesLine(){
        Purchase purchase = new Purchase(cashRegister, salesEmployee);
        Product cola = mockUnitProductWithPant("Coca Cola 33cl");
        purchase.addPiece(cola);

        purchase.removeProduct(cola);
        assertTrue(purchase.getItemsView().isEmpty());
    }

    @Test
    @DisplayName("getTotalNet - calculates net price correctly")
    void getTotalNet_calculateTotalNet(){
        Purchase purchase = new Purchase(cashRegister, salesEmployee);
        Product karinsLasagne = mockUnitProductWithInfo("Karins lasagne", new Money(50000), OTHER);
        Product billys = mockUnitProductWithInfo("Billys Pan Pizza", new Money(40000), OTHER);
        Product kanelbulle = mockUnitProductWithInfo("Kanelbulle", new Money(10000), OTHER);

        purchase.addPiece(karinsLasagne);
        purchase.addPiece(billys);
        purchase.addPiece(kanelbulle);

        Money netPrice = purchase.getTotalNet();


        assertEquals(100000L, netPrice.getAmountInMinorUnits());


    }

}
