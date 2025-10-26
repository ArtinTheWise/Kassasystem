package org.example.Sales;

import java.util.Objects;

public class Receipt {

    private final Purchase purchase;

    public Receipt(Purchase purchase){
        if (purchase.getItemsView().isEmpty()){
            throw new IllegalArgumentException("the purchase has no scanned articles yet");
        }
        this.purchase = Objects.requireNonNull(purchase, "purchase musn't be null");
    }

    public Purchase getPurchase(){
        return purchase;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }



}
