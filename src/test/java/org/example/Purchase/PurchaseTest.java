package org.example.Purchase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.example.Money;
import org.example.Product.Product;
import org.example.Product.Quantity;
import org.example.Product.WeightPrice;
import org.example.Product.Unit;
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


    @Test
    @DisplayName("Create Purchase with null salesEmployee Object throws exception")
    void createPurchaseWithNullCashRegisterThrowsException(){
        assertThrows(IllegalArgumentException.class, 
            () -> new Purchase(null, salesEmployee));
    }

    @Test
    @DisplayName("Create Purchase with null cashRegister Object throws exception")
    void createPurchaseWithNullSalesEmployeeThrowsException(){
        assertThrows(IllegalArgumentException.class, 
            () -> new Purchase(cashRegister, null));
    }

    @Test
    @DisplayName("Create Purchase with valid parameters does not throw exception")
    void createPurchaseWithValidParametersDoesNotThrowException(){
        new Purchase(cashRegister, salesEmployee);
    }

    @Test
    @DisplayName("Add null piece-product to Purchase throws exception")
    void addNullItemToPurchaseThrowsException(){
        Purchase purchase = new Purchase(cashRegister, salesEmployee);
        assertThrows(IllegalArgumentException.class, 
            () -> purchase.addPiece(null));
    }

    @Test
    @DisplayName("Add piece with weight pricemodel throws exception ")
    void addPieceWithWeightPricemodelThrowsException() {
        WeightPrice weightPrice = new WeightPrice(new Money(5000), Unit.KG);
        Product mockProduct = mock(Product.class);

        when(mockProduct.getPriceModel()).thenReturn(weightPrice);

        Purchase purchase = new Purchase(cashRegister, salesEmployee);
        assertThrows(IllegalArgumentException.class,
            () -> purchase.addPiece(mockProduct));
    }


    

    
}
