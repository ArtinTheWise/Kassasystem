package org.example.Membership;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {


    @Test
    void constructorWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer("200404301234", null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer(null, "test@example.com");
        });
    }

    @Test
    void getSocialSecurityNumber() {
        Customer customer = new Customer("200404301234", "test@example.com");
        assertEquals("200404301234", customer.getSocialSecurityNumber());
    }

    @Test
    void getEmailAddress() {
        Customer customer = new Customer("200404301234", "test@example.com");
        assertEquals("test@example.com", customer.getEmailAddress());
    }

//    @Test
//    void isMember() {
//        Customer customer = new Customer("200404301234", "test@example.com");
//        customer.becomeMember();
//        assertTrue(customer.isMember());
//    }
//
//    @Test
//    void isExpiredMember() {
//        Customer customer = new Customer("130404301234", "test@example.com");
//        customer.becomeMember();
//        customer.changeDateOfMembership(LocalDate.of(1324, 4, 30));//test metod bara
//        assertFalse(customer.isMember());
//    }
//
//    @Test
//    void renewMembership() {
//        Customer customer = new Customer("130404301234", "test@example.com");
//        customer.becomeMember();
//        customer.changeDateOfMembership(LocalDate.of(2023, 4, 30));//test metod bara
//
//        assertEquals(LocalDate.of(2023, 4, 30), customer.getDateOfMembership());
//
//        customer.renewMembership();
//
//        assertEquals(LocalDate.now(), customer.getDateOfMembership());
//    }
}