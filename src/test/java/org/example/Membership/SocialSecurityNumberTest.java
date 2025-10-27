package org.example.Membership;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SocialSecurityNumberTest {

    private final String validEmailAddress = "mail@example.com";

    private final String invalidCheckDigit = "Invalid check digit";
    private final String invalidFormat = "Invalid format";
    private final String invalidDate = "Invalid date";

    @Test
    void T1_R2() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            Customer customerExample = new Customer("199012309999", validEmailAddress);
        });
        assertTrue(e.getMessage().contains(invalidCheckDigit ));
    }

    @Test
    void T2_R2() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            Customer customerExample = new Customer("200004300000", validEmailAddress);
        });
        assertTrue(e.getMessage().contains(invalidCheckDigit ));
    }

    @Test
    void T3_R4() {
        assertDoesNotThrow(() -> {
            Customer customerExample = new Customer("201002283343", validEmailAddress);
        });
    }

    @Test
    void T4_R4() {
        assertDoesNotThrow(() -> {
            Customer customerExample = new Customer("200402283341", validEmailAddress);
        });
    }

    @Test
    void T5_R3() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            Customer customerExample = new Customer(null, validEmailAddress);
        });
        assertTrue(e.getMessage().contains(invalidFormat));
    }
    @Test
    void T6_R3() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            Customer customerExample = new Customer("", validEmailAddress);
        });
        assertTrue(e.getMessage().contains(invalidFormat));
    }
    @Test
    void T7_R3() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            Customer customerExample = new Customer("20004300006", validEmailAddress);
        });
        assertTrue(e.getMessage().contains(invalidFormat));
    }
    @Test
    void T8_R3() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            Customer customerExample = new Customer("2000004300006", validEmailAddress);
        });
        assertTrue(e.getMessage().contains(invalidFormat));
    }
    @Test
    void T9_R3() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            Customer customerExample = new Customer("2ooo04300006", validEmailAddress);
        });
        assertTrue(e.getMessage().contains(invalidFormat));
    }
    @Test
    void T10_R3() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            Customer customerExample = new Customer("2000o4300006", validEmailAddress);
        });
        assertTrue(e.getMessage().contains(invalidFormat));
    }
    @Test
    void T11_R3() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            Customer customerExample = new Customer("2000043o0006", validEmailAddress);
        });
        assertTrue(e.getMessage().contains(invalidFormat));
    }
    @Test
    void T12_R3() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            Customer customerExample = new Customer("20000430ooo6", validEmailAddress);
        });
        assertTrue(e.getMessage().contains(invalidFormat));
    }
    @Test
    void T13_R1() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            Customer customerExample = new Customer("000204300006", validEmailAddress);
        });
        assertTrue(e.getMessage().contains(invalidDate));
    }
    @Test
    void T14_R1() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            Customer customerExample = new Customer("200000300000", validEmailAddress);
        });
        assertTrue(e.getMessage().contains(invalidDate));
    }
    @Test
    void T15_R1() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            Customer customerExample = new Customer("200014300006", validEmailAddress);
        });
        assertTrue(e.getMessage().contains(invalidDate));
    }
    @Test
    void T16_R1() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            Customer customerExample = new Customer("200004000006", validEmailAddress);
        });
        assertTrue(e.getMessage().contains(invalidDate));
    }
    @Test
    void T17_R1() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            Customer customerExample = new Customer("200003320005", validEmailAddress);
        });
        assertTrue(e.getMessage().contains(invalidDate));
    }
    @Test
    void T18_R1() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            Customer customerExample = new Customer("200004310005", validEmailAddress);
        });
        assertTrue(e.getMessage().contains(invalidDate));
    }
    @Test
    void T19_R1() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            Customer customerExample = new Customer("201002293343", validEmailAddress);
        });
        assertTrue(e.getMessage().contains(invalidDate));
    }
    @Test
    void T20_R1() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            Customer customerExample = new Customer("20040230334", validEmailAddress);
        });
        assertTrue(e.getMessage().contains(invalidDate));
    }

}