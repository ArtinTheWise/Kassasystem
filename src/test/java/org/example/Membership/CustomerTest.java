package org.example.Membership;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private final String validSSN = "200001011234";
    private final String validEmailAddress = "mail@example.com";

    //can använda before each

    @Test
    void constructorWithNullTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer(validSSN, null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer(null, validEmailAddress);
        });
    }

    @Test
    void getSocialSecurityNumberTest() {
        Customer customer = new Customer(validSSN, validEmailAddress);
        assertEquals(validSSN, customer.getSocialSecurityNumber());
    }

    @Test
    void getEmailAddressTest() {
        Customer customer = new Customer(validSSN, validEmailAddress);
        assertEquals(validEmailAddress, customer.getEmailAddress());
    }

    //membership
    @Test
    void isMemberTest() {
        Customer customer = new Customer(validSSN, validEmailAddress);
        assertNull(customer.getMembership());// kollar att man inte är det från början
        customer.becomeMember();
        assertNotNull(customer.getMembership());
    }

    @Test
    void isExpiredMemberTest() {
        Customer customer = new Customer("158204301234", validEmailAddress);
        customer.becomeMember();
        customer.getMembership().changeExpirationDate(LocalDate.of(1600, 4, 30));//test metod bara
        assertNull(customer.getMembership());
    }

    @Test
    void renewMembershipTest() {
        Customer customer = new Customer(validSSN, validEmailAddress);
        customer.becomeMember();
        customer.getMembership().changeExpirationDate(LocalDate.of(2027, 4, 30));//test metod bara

        assertEquals(LocalDate.of(2027, 4, 30), customer.getMembership().getExpirationDate());

        customer.getMembership().extendExpirationDate();

        assertEquals(LocalDate.now().plusYears(5), customer.getMembership().getExpirationDate());
    }

    @Test
    void cancelMembershipTest() {
        Customer customer = new Customer(validSSN, validEmailAddress);
        customer.becomeMember();
        customer.cancelMembership();
        assertNull(customer.getMembership());
    }

    @Test
    void validateDateFor30DayMonths() {
        String invalidDate = "Invalid date";

        Exception a = assertThrows(IllegalArgumentException.class, () -> {
            new Customer("200004310006", validEmailAddress);
        });
        assertTrue(a.getMessage().contains(invalidDate));
        Exception b = assertThrows(IllegalArgumentException.class, () -> {
            new Customer("200006310006", validEmailAddress);
        });
        assertTrue(b.getMessage().contains(invalidDate));
        Exception c = assertThrows(IllegalArgumentException.class, () -> {
            new Customer("200009310006", validEmailAddress);
        });
        assertTrue(c.getMessage().contains(invalidDate));
        Exception d = assertThrows(IllegalArgumentException.class, () -> {
            new Customer("200011310006", validEmailAddress);
        });
        assertTrue(d.getMessage().contains(invalidDate));
    }

    @Test
    void validateDateYearInFebruary() { // todo not done
        try {
            new Customer("200002010006", validEmailAddress);
            new Customer("200402010006", validEmailAddress);
            new Customer("220002010006", validEmailAddress);
            new Customer("200102010006", validEmailAddress);
        } catch (Exception ignored) {}
    }

    @Test
    void constructorThrowsWhenEmailFormatIsInvalid() {
        String[] invalidEmails = {
                "", "Å@test.se", "A @test.se", "A..@test.se",
                "A.se", "@test.se", ".A@Test.se", "a@.com",
                "a@-a.com", "a@a_a.com", "a@com", "a@y.c", "a@y.c.",
                "A@@test.se", "A@"
        };

        for (String email : invalidEmails) {
            assertThrows(IllegalArgumentException.class,
                    () -> new Customer(validSSN, email),
                    "Should fail for: " + email);
        }
    }

    @Test
    void constructorThrowsWhenLocalPartIsTooLong() {
        String longLocal = "a".repeat(65) + "@test.se";
        assertThrows(IllegalArgumentException.class,
                () -> new Customer(validSSN, longLocal));
    }

    @Test
    void constructorThrowsWhenDomainPartIsTooLong() {
        String longDomain = "a@" + "a".repeat(256) + ".com";
        assertThrows(IllegalArgumentException.class,
                () -> new Customer(validSSN, longDomain));
    }

    @Test
    void constructorThrowsWhenEndAdressIsTooLong() {
        String longAdress = "a@y." + "a".repeat(64);
        assertThrows(IllegalArgumentException.class,
                () -> new Customer(validSSN, longAdress));
    }

    @Test
    void constructorAcceptsValidEmails() {
        String[] validEmails = {
                "A.HEIDARI0554@GMAIL.COM",
                "Aa\" \"1!.@gmail.com",
                "\"test\\\"user\"@gmail.com"
        };

        for (String email : validEmails) {
            assertDoesNotThrow(() -> new Customer(validSSN, email));
        }
    }
    @Test
    void constructorSetsAgeAndStudentCorrectly() {
        Customer c = new Customer(validSSN, validEmailAddress, 25, true);
        assertEquals(25, c.getAge());
        assertTrue(c.isStudent());
    }
    @Test
    void defaultConstructorSetsAgeAndStudentCorrectly() {
        Customer c = new Customer(validSSN, validEmailAddress);
        assertEquals(18, c.getAge());
        assertFalse(c.isStudent());
    }

}