package org.example.Sales;

import org.example.Discount.DiscountManager;
import org.example.Membership.BonusCheck;
import org.example.Membership.Customer;
import org.example.Product.Product;
import org.example.Product.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CashRegister {

    private static final AtomicInteger SEQ = new AtomicInteger(1);

    private final int cashRegisterId;

    private DiscountManager dm;
    private Cashier cashier;
    private Purchase purchase;
    private Customer customer;


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

    public void startPurchase(Customer customer) {
        isSomeoneLoggedIn();

        if (customer != null && customer.getMembership() != null){
            this.customer = customer;
            for (BonusCheck check : customer.getMembership().getChecks()) {
                dm.addDiscount(check.getDiscount());
            }
        }

        purchase = new Purchase(this, cashier, dm);
    }

    public String endPurchase() {
        isSomeoneLoggedIn();
        isPurchaseUnderWay();

        Receipt receipt = new Receipt(purchase);

        if (customer != null && customer.getMembership() != null){
            long pointsGained = purchase.getTotalGross().getAmountInMajorUnits() / 100;
            customer.getMembership().getPoints().add(pointsGained);

            List<BonusCheck> checksToRemove = new ArrayList<>();

            for (BonusCheck check : customer.getMembership().getChecks()) {

                Product product= check.getDiscount().getProduct();

                if (purchase.getItemsView().containsKey(product)) {
                    if (dm.getBestDiscount(product, purchase.getItemsView().get(product)) == check.getDiscount()) {
                        checksToRemove.add(check);
                    }
                }
            }
            for (BonusCheck check : checksToRemove) {
                customer.getMembership().removeCheck(check);
            }
        }

        customer = null;
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
