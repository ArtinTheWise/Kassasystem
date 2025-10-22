package org.example.Purchase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.example.Money;
import org.example.Product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import Sales.Purchase;

public class PurchaseTest {

    /*
     * konstruktor 
     * kassa id - null / finns ej / fel instans
     * Seller id - null / finns ej / fel instans
     * addItem - null / finns ej / fel instance
     * 
     * 
     * createReceipt - 
     * 
     * lägga till viktvara
     * lägga till styckvara
     * lägga till viktvara igen. -e.g. första rtikel är vikt, sen massa andra, sen samma viktvara igen
     * samma som ovan för styck
     * skapa flera mockrabatter och se att den applicerar rätt rabatt på en quantity
     * räknar pris korrekt
     * räknar moms korrekt
     * räknar moms korrekt för blandade momssatser
     * räkna pant korrekt
     * kan ta bort en vara under ett köp
     * kan inte ta bort en vara under betalning
     * 
     * 
     * 
     * 
     * 
     */


    public interface SalesEmployee { String getId(); }
    public interface CashRegister  { String getId(); }

    @Mock CashRegister cashRegister;
    @Mock SalesEmployee salesEmployee; 

    @BeforeEach void setUp(){
        when(cashRegister.getId()).thenReturn("31");
        when(salesEmployee.getId()).thenReturn("44");
    }


    private Product getMockProduct(){
        Product mockProduct = mock(Product.class);
        when(mockProduct.calculatePrice(any())).thenReturn(new Money(120));
        when(mockProduct.getName()).thenReturn("Milk");
        return mockProduct;
    }


    @Test
    @DisplayName("Create Purchase with null CashRegister Object throws exception")
    void createPurchaseWithNullCashRegisterThrowsException(){
        assertThrows(IllegalArgumentException.class, () -> {
            Purchase purchase = new Purchase(null, salesEmployee);
        });

    }

    

    
}
