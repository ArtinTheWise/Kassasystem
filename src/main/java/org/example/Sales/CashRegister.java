package org.example.Sales;

import org.example.Discount.DiscountManager;
import org.example.Product.Product;
import org.example.Product.Unit;

import java.util.concurrent.atomic.AtomicInteger;

public class CashRegister {

    private static final AtomicInteger SEQ = new AtomicInteger(1);

    private final int cashRegisterId;

    private DiscountManager dm;
    private Cashier cashier;
    private Purchase purchase;


    public CashRegister(DiscountManager dm) {
        this.dm = dm;
        this.cashRegisterId = SEQ.getAndIncrement();
        this.cashier = null;
    }

    public void login(Cashier cashier) {
        if (this.cashier == null) {
            this.cashier = cashier;
        } else {
            throw new IllegalArgumentException("Someone else logged in");
        }
    }

    public void logout() {
        isSomeoneLoggedIn();
        if (purchase != null)
            throw new IllegalArgumentException("Cant logout during purchase");

        cashier = null;
    }

    public void startPurchase() {
        isSomeoneLoggedIn();

        purchase = new Purchase(this, cashier, dm);
    }

    public String endPurchase() {
        isSomeoneLoggedIn();
        isPurchaseUnderWay();

        Receipt receipt = new Receipt(purchase);
        purchase = null;
        return receipt.toString();
    }

    public void scanProduct(Product product, double quantity) {
        isSomeoneLoggedIn();
        isPurchaseUnderWay();

        Unit unit = product.getPriceModel().getUnit();
        if (unit == Unit.PIECE) {
            for (int i = 0; i < quantity; i++) {
                purchase.addPiece(product);
            }
        } else {
            purchase.addWeight(product, quantity, unit);
        }
    }

    public void removeProduct(Product product) {
        isSomeoneLoggedIn();
        isPurchaseUnderWay();

        purchase.removeProduct(product);
    }

    public void changeDiscountManager(DiscountManager discountManager) {
        isSomeoneLoggedIn();
        if (purchase != null)
            throw new IllegalStateException("Purchase under way");
        dm = discountManager;
    }

    public int getId(){
        return cashRegisterId;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public DiscountManager getDiscountManager() {
        return dm;
    }

    public boolean loggedIn() {
        return cashier != null;
    }

    private void isSomeoneLoggedIn() {
        if (cashier == null) {
            throw new IllegalStateException("No one logged in");
        }
    }

    private void isPurchaseUnderWay() {
        if (purchase == null) {
            throw new IllegalStateException("No purchase under way");
        }
    }
}
