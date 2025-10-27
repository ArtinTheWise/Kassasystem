package org.example.Sales;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.example.Money;
import org.example.Product.Product;
import org.example.Product.Quantity;
import org.example.Product.Unit;
import org.example.Product.UnitPriceWithPant;

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
        sb.append("\tNr: " + id);

        sb.append("\nDate: " + purchase.getDate().toString());
        String time = purchase.getTime().truncatedTo(ChronoUnit.MINUTES).toString();
        sb.append("\tTime: " + time);

        sb.append("\n---------------------------");
        
        for (Map.Entry<Product, Quantity> e : purchase.getItemsView().entrySet()) {
            Product base = e.getKey();
            Quantity q = e.getValue();

            Product priced = purchase.pricedFor(base);

            long lineGrossMinor = priced.calculatePriceWithVat(q).getAmountInMinorUnits();

                // include pant on the BASE product (the physical bottle/can)
            if (base.getPriceModel() instanceof UnitPriceWithPant upm) {
                if (q.getUnit() != Unit.PIECE) {
                    throw new IllegalArgumentException("Pant product must be counted by PIECE");
                }
                long pieces = Math.round(q.getAmount());
                lineGrossMinor += upm.getPantPerPiece().getAmountInMinorUnits() * pieces;
            }

            sb.append("\n")
                .append(base.getName()) // nicer than base.toString()
                .append(" x ")
                .append(q.getAmount())
                .append(" ")
                .append(q.getUnit());

            sb.append("\t\t\t\t").append(new Money(lineGrossMinor));
    

        }

        sb.append("\n---------------------------");

        long grossMinor = purchase.getTotalGross().getAmountInMinorUnits();
        long netMinor   = purchase.getTotalNet().getAmountInMinorUnits();
        long vatMinor   = purchase.getTotalVat().getAmountInMinorUnits();

        BigDecimal gross = BigDecimal.valueOf(grossMinor, 2);
        BigDecimal net   = BigDecimal.valueOf(netMinor, 2);
        BigDecimal vat   = BigDecimal.valueOf(vatMinor, 2);

        sb.append("\nTotal");
        sb.append("\t" + gross);
        
        sb.append("\nMoms% \tMoms \tNetto \tBrutto");
        sb.append("\n25,00\t" + vat + "\t" + net + "\t" + gross);

        return sb.toString();

    }



}
