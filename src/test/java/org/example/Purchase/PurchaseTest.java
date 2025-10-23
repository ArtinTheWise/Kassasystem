package org.example.Purchase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.example.Product.PriceModel;
import org.example.Product.Product;
import org.example.Product.Quantity;
import org.example.Product.WeightPrice;
import org.example.Product.UnitPrice;
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
        assertThrows(IllegalArgumentException.class, 
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
        assertThrows(IllegalArgumentException.class, 
            () -> purchase.addWeight(null));
    }

    @Test
    @DisplayName("addWeight: throws exception for Piece Product")
    void addWeight_onPieceProduct_throws() {
        Product beef = mockUnitProduct("beef");

        Purchase purchase = new Purchase(cashRegister, salesEmployee);
        assertThrows(IllegalArgumentException.class,
            () -> purchase.addWeight(beef));
    }


 
    
}
