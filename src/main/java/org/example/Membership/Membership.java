package org.example.Membership;

import java.time.LocalDate;

public class Membership {

    private String socialSecurityNumber;
    private String email;
    private LocalDate expirationDate;

    public Membership(String socialSecurityNumber, String email) {
        this.socialSecurityNumber = socialSecurityNumber;
        this.email = email;
        expirationDate = LocalDate.now().plusYears(5);
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void extendExpirationDate() {
        this.expirationDate = LocalDate.now().plusYears(5);
    }

    //cancel,
}
