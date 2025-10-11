package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MoneyTest {
    @Test
    void getterForAmountWorks() {
        Money m = new Money(100);
        assertEquals(100, m.getAmount());
    }

    @Test
    void moneyWorksAsValueClassAndAddsMoney() {
        Money m1 = new Money(100);
        Money m2 = m1.add(m1);

        assertEquals(100, m1.getAmount());
        assertEquals(200, m2.getAmount());
    }

    @Test
    void equalsIsOverloaded() {
        Money m1 = new Money(100);
        Money m2 = new Money(100);
        Money m3 = new Money(50);

        assertEquals(m1, m2);
        assertNotEquals(m1, m3);
        assertNotEquals(new Object(), m1);
    }

    @Test
    void hashCodeIsOverloaded() {
        Money m1 = new Money(100);
        Money m2 = new Money(100);

        assertEquals(m1.hashCode(), m2.hashCode());
    }
}
