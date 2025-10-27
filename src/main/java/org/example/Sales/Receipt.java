package org.example.Sales;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.example.Product.Product;
import org.example.Product.Quantity;

public class Receipt {
    private static final AtomicInteger SEQ = new AtomicInteger(1);

    private final Purchase purchase;
    private int id;

    public Receipt(Purchase purchase){
        this.purchase = Objects.requireNonNull(purchase, "purchase musn't be null");

        if (this.purchase.getItemsView().isEmpty()){
            throw new IllegalArgumentException("the purchase has no scanned articles yet");
        }
        this.id = SEQ.getAndIncrement();

    }

    public Purchase getPurchase(){ return purchase; }
    public int getId() {return id; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("StoreName");
        sb.append("\nStoreLocation");

        sb.append("\ncashier: " + purchase.getCashier().getId()); // säljare
        sb.append("\tcashRegister: " + purchase.getCashRegister().getId()); // kassa
        sb.append("Nr: " + id);

        sb.append("\nDate: " + purchase.getDate().toString());
        sb.append("\tTime: " + purchase.getTime().toString());

        sb.append("\n---------------------------");
        
        for (Map.Entry<Product, Quantity> e : purchase.getItemsView().entrySet()) {
            Product p = e.getKey();
            Quantity q = e.getValue();

            sb.append("\n" + p);
            sb.append("\t" + p.calculatePriceWithVat(q));

        }

        sb.append("\nTotal");
        sb.append("\t" + purchase.getTotalGross().getAmountInMinorUnits());
        
        sb.append("\n Moms% \t Moms \t Netto \t Brutto");
        sb.append("\n25,00 \t" + purchase.getTotalVat().getAmountInMinorUnits()
         + "\t" + purchase.getTotalNet().getAmountInMinorUnits() + "\t"
          + purchase.getTotalGross().getAmountInMinorUnits());

        return sb.toString();

    }



}
