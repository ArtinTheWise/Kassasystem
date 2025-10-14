package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyTest {

    @Test
    void getterForAmountInMinorWorks() {
        Money m1 = new Money(100);
        assertEquals(100, m1.getAmountInMinorUnits());
    }

    @Test
    void getterForAmountInMajorWorks() {
        Money m1 = new Money(100);
        assertEquals(1, m1.getAmountInMajorUnits());
    }

    @Test
    void addingMoneyWorksForLongAndMoney() {
        Money m1 = new Money(100);
        Money m2 = m1.add(50);
        Money m3 = m1.add(new Money(100));

        assertEquals(150, m2.getAmountInMinorUnits());
        assertEquals(200, m3.getAmountInMinorUnits());
    }

    @Test
    void subtractingMoneyWorksForLongAndMoney() {
        Money m1 = new Money(100);
        Money m2 = m1.subtract(50);
        Money m3 = m1.subtract(new Money(100));

        assertEquals(50, m2.getAmountInMinorUnits());
        assertEquals(0, m3.getAmountInMinorUnits());
    }

    @Test
    void subtractingMoneyMustBeEqualOrLessThanCurrentMoney() {
        Money m1 = new Money(100);
        assertThrows(IllegalArgumentException.class, ()->{
            m1.subtract(101);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            m1.subtract(new Money(101));
        });
    }

    @Test
    void constructorDoesNotAllowNegativeArg() {
        assertThrows(IllegalArgumentException.class, ()->{
            new Money(-1);
        });
    }

    @Test
    void addAndSubtractDoesNotAllowNegativeArg() {
        Money m1 = new Money(100);
        assertThrows(IllegalArgumentException.class, ()->{
            m1.subtract(-1);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            m1.add(-1);
        });
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

        assertEquals(m1.hashCode(), new Money(100).hashCode());
        assertNotEquals(m1.hashCode(), new Money(50).hashCode());
    }

    @ParameterizedTest
    @CsvSource({"0, 100", "1, 99", "-1, 101"})
    void compareToIsImplemented(int i1, int i2) {
        Money m1 = new Money(100);
        assertEquals(i1, m1.compareTo(new Money(i2)));
    }

    @ParameterizedTest
    @CsvSource({"65, '0,65 SEK'", "165, '1,65 SEK'", "5, '0,05 SEK'"})
    void toStringImplemented(int i, String s) {
        Money m1 = new Money(i);
        assertEquals(s, m1.toString());
    }
}
