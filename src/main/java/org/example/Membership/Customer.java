package org.example.Membership;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Customer {

    private final String socialSecurityNumber;
    private final String emailAddress;
    // samling av checkar


    // gör member till egen grej
    private Membership membership;

    // ev? lista av köphistorik

    public Customer(String socialSecurityNumber, String emailAddress) {
        if (emailAddress == null || socialSecurityNumber == null) {
            throw new IllegalArgumentException("Cannot be null.");
        }
        // validate SSN & email
        this.socialSecurityNumber = socialSecurityNumber;
        this.emailAddress = emailAddress;
        membership = null;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    //membership
    public Membership getMembership() {
        if (membership == null || membership.getExpirationDate().isBefore(LocalDate.now())) {
            membership = null;                              // tas den bort om du glömt att renew:a den, tappar då alla poäng
            throw new IllegalStateException("Non-member");
        }
        return membership;
    }

    public void becomeMember() {
        if (membership == null) {
            LocalDate age = LocalDate.of(
                    Integer.parseInt(socialSecurityNumber.substring(0,4))+18, //flyttal
                    Integer.parseInt(socialSecurityNumber.substring(4,6)),
                    Integer.parseInt(socialSecurityNumber.substring(6,8))
            );
            if (age.isEqual(LocalDate.now())||age.isBefore(LocalDate.now())) {
                membership = new Membership(this);
            }
        }
    }

    protected void cancelMembership() {
        membership = null;
    }
}
