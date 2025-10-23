package org.example.Purchase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

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
     * lägga till viktvara
     * lägga till styckvara
     * lägga till viktvara igen. -e.g. första artikel är vikt, sen massa andra, sen samma viktvara igen
     * samma som ovan för styck
     * skapa flera mockrabatter och se att den applicerar rätt rabatt på en quantity
     * räknar pris korrekt
     * räknar moms korrekt
     * räknar moms korrekt för blandade momssatser
     * räkna pant korrekt
     * kan ta bort en vara under ett köp
     * kan inte ta bort en vara under betalning
     */


    public interface SalesEmployee { String getId(); }
    public interface CashRegister  { String getId(); }

    @Mock CashRegister cashRegister;
    @Mock SalesEmployee salesEmployee; 
    @Mock Quantity quantity;

    @Mock Product product;
        private Product mockUnitProduct(String name) {
        Product p = mock(Product.class, name);
        PriceModel pm = mock(UnitPrice.class);
        when(p.getPriceModel()).thenReturn(pm);
        return p;
    }

    private Product mockWeightProduct(String name) {
        Product p = mock(Product.class, name);
        PriceModel pm = mock(WeightPrice.class);
        when(p.getPriceModel()).thenReturn(pm);
        return p;
    }

    private Product mockUnitProductWithPant(String name) {
        Product p = mock(Product.class, name);
        PriceModel pm = mock(UnitPriceWithPant.class);
        when(p.getPriceModel()).thenReturn(pm);
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
    @DisplayName("addWeight: add weight for weight product")
    void addWeight_addsAmount_forWeightProduct() {
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






 
    
}
