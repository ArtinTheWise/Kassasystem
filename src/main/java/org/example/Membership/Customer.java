package org.example.Membership;

import java.time.LocalDate;

public class Customer {

    private final String socialSecurityNumber;
    private final String emailAddress;
    private boolean member;
    private LocalDate dateOfMembership;

    // ev lista av köphistorik

    public Customer(String socialSecurityNumber, String emailAddress) {
        if (emailAddress == null || socialSecurityNumber == null) {
            throw new IllegalArgumentException("Cannot be null.");
        }
        // validate SSN & email
        this.socialSecurityNumber = socialSecurityNumber;
        this.emailAddress = emailAddress;
        this.member = false;
    }



    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public boolean isMember() {
        if (member && dateOfMembership.plusYears(5).isAfter(LocalDate.now())){
            return true;
        }
        member = false;
        return false;
    }

    public void becomeMember() {
        if (!member) {
            LocalDate age = LocalDate.of(
                    Integer.parseInt(socialSecurityNumber.substring(0,4))+18,
                    Integer.parseInt(socialSecurityNumber.substring(4,6)),
                    Integer.parseInt(socialSecurityNumber.substring(6,8))
            );
            if (age.isEqual(LocalDate.now())||age.isBefore(LocalDate.now())) {
                member = true;
                dateOfMembership = LocalDate.now();
            }
        }
    }

    public void renewMember() {
        if (member) {
            dateOfMembership = LocalDate.now();
        }
    }

    public void cancelMembership() {
        member = false;
    }

    // Endast för test av .....   u get it
    public void changeDateOfMembership(LocalDate newDate) {
        dateOfMembership = newDate;
    }
    // endast för test, för nu
    public LocalDate getDateOfMembership() {
        return dateOfMembership;
    }


}
