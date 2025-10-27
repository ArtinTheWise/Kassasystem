package org.example.Membership;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SocialSecurityNumberTest {

    private final String validEmailAddress = "mail@example.com";

    @Test
    void T1_R2() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            Customer customerExample = new Customer("199012309999", validEmailAddress);
        });
        assertTrue(e.getMessage().contains("Invalid check digit"));
    }

    @Test
    void T2_R2() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            Customer customerExample = new Customer("200004300000", validEmailAddress);
        });
        assertTrue(e.getMessage().contains("Invalid check digit"));
    }

    @Test
    void T3_R5() {
        assertDoesNotThrow(() -> {
            Customer customerExample = new Customer("201002283343", validEmailAddress);
        });
    }

    @Test
    void T4_R5() {
        assertDoesNotThrow(() -> {
            Customer customerExample = new Customer("200402283341", validEmailAddress);
        });
    }




}