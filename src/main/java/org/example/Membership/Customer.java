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
        validateSSN(socialSecurityNumber);


        // validate SSN & email
        this.socialSecurityNumber = socialSecurityNumber;
        this.emailAddress = emailAddress;
        membership = null;
    }

    private void validateSSN(String ssn) {
        if (ssn == null || !ssn.matches("^[0-9]{12}$")) {
            throw new IllegalArgumentException("Invalid format");
        }
        if (!validateDate(ssn.substring(0,8))) {
            throw new IllegalArgumentException("Invalid date");
        }
        if (!validateCheckDigit(ssn)) {
            throw new IllegalArgumentException("Invalid check digit");
        }
    }

    private boolean validateDate(String date) {
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6, 8));

        if (year < 1582 || month < 1 || month > 12 || day < 1 || day > 31) {
            return false;
        }

        if ((month == 4 || month == 6 || month == 9 || month == 11) && day > 30){
            return false;
        }

        if (month == 2 && day > 29){
            return false;
        }

        if (month == 2 && day > 28 && !(year%4 == 0 && year%100 != 0 && year%400 == 0)){
            return false;
        }

        return true;
    }

    private boolean validateCheckDigit(String checkDigit) {

        int nSum = 0;
        boolean isSecond = false;
        for (int i = checkDigit.length() - 1; i >= 0; i--){

            int d = checkDigit.charAt(i) - '0';

            if (isSecond)
                d = d * 2;

            nSum += d / 10;
            nSum += d % 10;

            isSecond = !isSecond;
        }
        return (nSum % 10 == 0);
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
