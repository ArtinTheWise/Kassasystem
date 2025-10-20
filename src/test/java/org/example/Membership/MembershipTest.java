package org.example.Membership;

import org.example.Discount.NormalDiscount;
import org.example.Money;
import org.example.Product.Product;
import org.example.Product.UnitPrice;
import org.example.Product.VatRate;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MembershipTest {

    @Test
    public void getCustomerTest() {
        Customer customer = new Customer("200404301234", "test@example.com");
        Membership membership = new Membership(customer);
        assertEquals(customer, membership.getCustomer());
    }

    @Test
    public void getPointsTest() {}






    //bonusCheckar
    @Test
    void getAddedChecksTest() {
        Customer customer = new Customer("200404301234", "test@example.com");
        Membership membership = new Membership(customer);

        membership.addCheck(new BonusCheck("snackSale",
                new NormalDiscount(new Product("chips", new UnitPrice(new Money(30)),
                        VatRate.FOOD, false),50, LocalDateTime.now().plusMonths(6)),
                new Points(200)));
        membership.addCheck(new BonusCheck("snackSale",
                new NormalDiscount(new Product("chips", new UnitPrice(new Money(30)),
                        VatRate.FOOD, false),50, LocalDateTime.now().plusMonths(6)),
                new Points(200)));                                                                   //dubblett ???
        membership.addCheck(new BonusCheck("santaSale",
                new NormalDiscount(new Product("skumtomtar", new UnitPrice(new Money(50)),
                        VatRate.FOOD, false),20, LocalDateTime.now().plusMonths(3)),
                new Points(150)));

        assertEquals(3, membership.getChecks().size());
    }


}
