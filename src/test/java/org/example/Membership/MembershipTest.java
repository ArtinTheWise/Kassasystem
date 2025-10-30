package org.example.Membership;

import org.example.Discount.NormalDiscount;
import org.example.Money;
import org.example.Product.Product;
import org.example.Product.UnitPrice;
import org.example.Product.VatRate;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

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
                        VatRate.FOOD, false), 20, LocalDateTime.now().plusMonths(6)),
                new Points(200)));
        membership.addCheck(new BonusCheck("snackSale",
                new NormalDiscount(new Product("chips", new UnitPrice(new Money(30)),
                        VatRate.FOOD, false), 20, LocalDateTime.now().plusMonths(6)),
                new Points(200)));                                                                   //dubblett ???
        membership.addCheck(new BonusCheck("santaSale",
                new NormalDiscount(new Product("skumtomtar", new UnitPrice(new Money(50)),
                        VatRate.FOOD, false), 20, LocalDateTime.now().plusMonths(3)),
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

    @Test
    void addCheckThrowsForInactiveDiscount() {
        Customer customer = new Customer(validSSN, validEmailAddress);
        Membership membership = new Membership(customer);

        Product product = new Product("soda", new UnitPrice(new Money(20)), VatRate.FOOD, false);

        LocalDateTime startTime = LocalDateTime.now().minusDays(10);
        LocalDateTime endTime = LocalDateTime.now().minusDays(1); // slutade igår

        NormalDiscount expiredDiscount = new NormalDiscount(product, 10, startTime, endTime);
        BonusCheck expiredCheck = new BonusCheck("expired", expiredDiscount, new Points(50));

        assertThrows(IllegalArgumentException.class, () -> membership.addCheck(expiredCheck));
    }

    @Test
    void removeCheckThatDoesNotExist() {
        Customer customer = new Customer(validSSN, validEmailAddress);
        Membership membership = new Membership(customer);

        membership.addCheck(new BonusCheck("snackSale",
                new NormalDiscount(new Product("chips", new UnitPrice(new Money(30)),
                        VatRate.FOOD, false), 20, LocalDateTime.now().plusMonths(6)),
                new Points(200)));


        assertThrows(IllegalArgumentException.class, () ->
                membership.removeCheck(new BonusCheck("santaSale",
                        new NormalDiscount(new Product("skumtomtar", new UnitPrice(new Money(50)),
                                VatRate.FOOD, false), 20, LocalDateTime.now().plusMonths(3)),
                        new Points(150)))


        );
    }

    @Test
    void getChecks_removesExpiredCheck() {
        Customer customer = new Customer(validSSN, validEmailAddress);
        customer.becomeMember();
        Membership membership = customer.getMembership();

        LocalDateTime discountStart = LocalDateTime.now().minusDays(10);
        LocalDateTime discountEnd = LocalDateTime.now().minusDays(5);

        Product product = new Product("OldProduct", new UnitPrice(new Money(100)), null, VatRate.FOOD, false) {};
        NormalDiscount expiredDiscount = new NormalDiscount(product, 20, discountStart, discountEnd);

        BonusCheck expiredCheck = new BonusCheck("expiredCheck", expiredDiscount, new Points(50));

        membership.forceAddExpiredCheck(expiredCheck); // metoden används bara för tester

        var checks = membership.getChecks();

        assertTrue(checks.isEmpty());
    }


}
