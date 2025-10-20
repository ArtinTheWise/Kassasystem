package org.example.Purchase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.example.Money;
import org.example.Product.Product;

public class PurchaseTest {

    /*
     * konstruktor 
     * Seller - null / finns ej
     * 
     * addItem - null / finns ej / fel instance
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
     */

        private Product getMockProduct(){
            Product mockProduct = mock(Product.class);
            when(mockProduct.calculatePrice(any())).thenReturn(new Money(120));
            when(mockProduct.getName()).thenReturn("Milk");
            return mockProduct;

        }


        









    
}
