package org.example.Membership;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Membership {

    private final Customer customer;
    private final Points points;
    private LocalDate expirationDate;

    private final List<BonusCheck> checks = new ArrayList<>(); // tas bort i kassan när de används eller tiden got ut

    public Membership(Customer costumer) {
        this.customer = costumer;
        expirationDate = LocalDate.now().plusYears(5); //flyttal
        points = new Points();
    }

    //nytt
    public Customer getCustomer() {
        return customer;
    }

    //nytt
    public Points getPoints() {
        return points;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void extendExpirationDate() {
        this.expirationDate = LocalDate.now().plusYears(5); //flyttal
    }

        //nytt
        public void cancelMembership() {
            customer.cancelMembership();
        }


    //bonuscheckar
    public List<BonusCheck> getChecks() {
        List<BonusCheck> checksToRemove = new ArrayList<>();
        for (BonusCheck check : checks) {
            if (!check.getDiscount().isActive()){
                checksToRemove.add(check);
            }
        }
        for (BonusCheck check : checksToRemove) {
            customer.getMembership().removeCheck(check);
        }

        return Collections.unmodifiableList(checks);
    }

    public void addCheck(BonusCheck check) {
        if (!check.getDiscount().isActive())
            throw new IllegalArgumentException("Discount is not active.");
        checks.add(check);
    }

    public void removeCheck(BonusCheck check) {
        if (checks.contains(check)) {
            checks.remove(check);
        } else {
            throw new IllegalArgumentException("Check does not exist.");
        }
    }



    // bara för tester
    public void changeExpirationDate(LocalDate newExpirationDate) {
        this.expirationDate = newExpirationDate;
    }


}
