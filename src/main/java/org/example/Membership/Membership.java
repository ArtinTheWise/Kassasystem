package org.example.Membership;

import java.time.LocalDate;

public class Membership {

    private final Customer customer;
    private final Points points;
    private LocalDate expirationDate;


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


    // bara för tester
    public void changeExpirationDate(LocalDate newExpirationDate) {
        this.expirationDate = newExpirationDate;
    }




}
