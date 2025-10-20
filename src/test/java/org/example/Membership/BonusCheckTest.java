package org.example.Membership;

import org.example.Discount.ProductDecorator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BonusCheckTest {

    @Test
    void constructor_ShouldSetAllFieldsCorrectly() {

        String expectedName = "10% Off Coffee";
        ProductDecorator mockDiscount = mock(ProductDecorator.class);
        Points expectedPoints = new Points(150);

        BonusCheck bonusCheck = new BonusCheck(expectedName, mockDiscount, expectedPoints);

        assertEquals(expectedName, bonusCheck.getName());
        assertEquals(mockDiscount, bonusCheck.getDiscount());
        assertEquals(expectedPoints, bonusCheck.getPointsToBuy());
    }

}

