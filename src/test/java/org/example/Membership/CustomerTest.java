package org.example.Membership;

import org.example.Discount.NormalDiscount;
import org.example.Money;
import org.example.Product.Product;
import org.example.Product.UnitPrice;
import org.example.Product.VatRate;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    //can använda before each

    @Test
    void constructorWithNullTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer("200404301234", null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer(null, "test@example.com");
        });
    }

    @Test
    void getSocialSecurityNumberTest() {
        Customer customer = new Customer("200404301234", "test@example.com");
        assertEquals("200404301234", customer.getSocialSecurityNumber());
    }

    @Test
    void getEmailAddressTest() {
        Customer customer = new Customer("200404301234", "test@example.com");
        assertEquals("test@example.com", customer.getEmailAddress());
    }

    //membership
    @Test
    void isMemberTest() {
        Customer customer = new Customer("200404301234", "test@example.com");
        assertThrows(IllegalStateException.class, customer::getMembership); // kollar att man inte är det från början
        customer.becomeMember();
        assertNotNull(customer.getMembership());
    }

    @Test
    void isExpiredMemberTest() {
        Customer customer = new Customer("130404301234", "test@example.com");
        customer.becomeMember();
        customer.getMembership().changeExpirationDate(LocalDate.of(1324, 4, 30));//test metod bara
        assertThrows(IllegalStateException.class, customer::getMembership);
    }

    @Test
    void renewMembershipTest() {
        Customer customer = new Customer("200404301234", "test@example.com");
        customer.becomeMember();
        customer.getMembership().changeExpirationDate(LocalDate.of(2027, 4, 30));//test metod bara

        assertEquals(LocalDate.of(2027, 4, 30), customer.getMembership().getExpirationDate());

        customer.getMembership().extendExpirationDate();

        assertEquals(LocalDate.now().plusYears(5), customer.getMembership().getExpirationDate());
    }

    @Test
    void cancelMembershipTest() {
        Customer customer = new Customer("200404301234", "test@example.com");
        customer.becomeMember();
        customer.cancelMembership();
        assertThrows(IllegalStateException.class, customer::getMembership);
    }


}