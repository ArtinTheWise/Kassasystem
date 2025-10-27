package org.example.Sales;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class CashierTest {


    @Test
    @DisplayName("Constructor: null name throws exception")
    void createCashierNullNameThrowsException(){
        assertThrows(NullPointerException.class, () -> new Cashier(null));
        
    }

    @Test
    @DisplayName("Constructor: empty name throws exception")
    void createCashierEmptyNameThrowsException(){
        assertThrows(NullPointerException.class, () -> new Cashier(""));
        
    }

    @Test
    @DisplayName("Constructor: sets unique Ids to cashiers")
    void createCashierGivesUniqueIds(){
        Cashier cashierOne = new Cashier("name");
        Cashier cashierTwo = new Cashier("name");

        assertNotEquals(cashierOne.getId(), cashierTwo.getId());

    }



    
}
