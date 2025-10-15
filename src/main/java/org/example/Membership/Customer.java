package org.example.Membership;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Customer {

    private final String socialSecurityNumber;
    private final String emailAddress;

    // gör member till egen grej

    private Membership membership;

    private Points points = null;

    // samling av checkar
    private List<BonusCheck> checks = new ArrayList<>(); // tas bort i kassan när de används eller tiden got ut

    // antal points

    // ev lista av köphistorik

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
    public void becomeMember() {
        if (membership == null) {
            LocalDate age = LocalDate.of(
                    Integer.parseInt(socialSecurityNumber.substring(0,4))+18,
                    Integer.parseInt(socialSecurityNumber.substring(4,6)),
                    Integer.parseInt(socialSecurityNumber.substring(6,8))
            );
            if (age.isEqual(LocalDate.now())||age.isBefore(LocalDate.now())) {
                membership = new Membership(socialSecurityNumber, emailAddress);
                points = new Points();
            }
        }
    }

    public Membership getMembership() {
        if (membership == null || membership.getExpirationDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Non-member");
        }
        return membership;
    }




    //points
    public Points getPoints() {
        if (points == null) {
            throw new IllegalStateException("Not a member, no points.");
        }
        return points;
    }

    //bonuscheckar
    public List<BonusCheck> getChecks() {
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


}
