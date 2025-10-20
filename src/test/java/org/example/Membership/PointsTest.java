package org.example.Membership;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PointsTest {

    @Test
    void defaultConstructor_ShouldStartWithZeroPoints() {
        Points points = new Points();
        assertEquals(0, points.getAmount());
    }

    @Test
    void constructor_ShouldSetInitialAmount() {
        Points points = new Points(100);
        assertEquals(100, points.getAmount());
    }

    @Test
    void add_ShouldIncreasePoints() {
        Points points = new Points(50);
        points.add(30);
        assertEquals(80, points.getAmount());
    }

    @Test
    void add_WithNegativeValue_ShouldThrowException() {
        Points points = new Points(10);
        assertThrows(IllegalArgumentException.class, () -> points.add(-5));
    }

    @Test
    void add_WithAnotherPointsObject_ShouldIncreaseAmount() {
        Points p1 = new Points(20);
        Points p2 = new Points(30);
        p1.add(p2);
        assertEquals(50, p1.getAmount());
    }

    @Test
    void subtract_ShouldDecreasePoints() {
        Points points = new Points(100);
        points.subtract(40);
        assertEquals(60, points.getAmount());
    }

    @Test
    void subtract_MoreThanAvailable_ShouldThrowException() {
        Points points = new Points(30);
        assertThrows(IllegalArgumentException.class, () -> points.subtract(50));
    }

    @Test
    void subtract_WithNegativeValue_ShouldThrowException() {
        Points points = new Points(30);
        assertThrows(IllegalArgumentException.class, () -> points.subtract(-10));
    }

    @Test
    void subtract_WithAnotherPointsObject_ShouldDecreaseAmount() {
        Points p1 = new Points(100);
        Points p2 = new Points(40);
        p1.subtract(p2);
        assertEquals(60, p1.getAmount());
    }
}

