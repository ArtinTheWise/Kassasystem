package org.example.Sales;

import org.example.Discount.DiscountManager;
import org.example.Product.Product;

public class CashRegister {

    int cashRegisterId;


    public CashRegister(DiscountManager dm) {
    }

    public boolean login(String username, String password) {
        return false;
    }

    public Purchase startPurchase() {
        return null;
    }


    public void scanProduct(Product milk, int i) {
    }

    public void removeProduct(Product bread) {
    }

    public int getId(){
        return cashRegisterId;
    }
}
