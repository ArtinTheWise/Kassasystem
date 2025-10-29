package org.example.Membership;

import org.example.Discount.NormalDiscount;
import org.example.Money;
import org.example.Product.Product;
import org.example.Product.UnitPrice;
import org.example.Product.VatRate;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MembershipTest {

    private final String validSSN = "200001011234";
    private final String validEmailAddress = "mail@example.com";

    @Test
    public void getCustomerTest() {
        Customer customer = new Customer(validSSN, validEmailAddress);
        Membership membership = new Membership(customer);
        assertEquals(customer, membership.getCustomer());
    }

    @Test
    public void getPointsTest() {
        Membership membership = new Membership(new Customer(validSSN, validEmailAddress));

        assertEquals(0, membership.getPoints().getAmount());

        membership.getPoints().add(100);

        assertEquals(100, membership.getPoints().getAmount());

        membership.getPoints().subtract(50);

        assertEquals(50, membership.getPoints().getAmount());
    }

    //bonusCheckar
    @Test
    void getAddedChecksTest() {
        Customer customer = new Customer(validSSN, validEmailAddress);
        Membership membership = new Membership(customer);

        membership.addCheck(new BonusCheck("snackSale",
                new NormalDiscount(new Product("chips", new UnitPrice(new Money(30)),
                        VatRate.FOOD, false),20, LocalDateTime.now().plusMonths(6)),
                new Points(200)));
        membership.addCheck(new BonusCheck("snackSale",
                new NormalDiscount(new Product("chips", new UnitPrice(new Money(30)),
                        VatRate.FOOD, false),20, LocalDateTime.now().plusMonths(6)),
                new Points(200)));                                                                   //dubblett ???
        membership.addCheck(new BonusCheck("santaSale",
                new NormalDiscount(new Product("skumtomtar", new UnitPrice(new Money(50)),
                        VatRate.FOOD, false),20, LocalDateTime.now().plusMonths(3)),
                new Points(150)));

        assertEquals(3, membership.getChecks().size());
    }
    @Test
    void cancelMembershipCallsCustomerCancelMembership() {
        Customer customer = new Customer(validSSN, validEmailAddress);
        customer.becomeMember();
        Membership membership = customer.getMembership();

        membership.cancelMembership();

        assertNull(customer.getMembership());
    }


}
