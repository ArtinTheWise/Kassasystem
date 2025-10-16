package org.example.Membership;


import org.example.Discount.ProductDecorator;

public class BonusCheck {

    private final String name;
    private ProductDecorator discount;
    private Points pointsToBuy; // själva kostnaden för kunden att köpa den

    // specifikt för en kund, oändligt för tid för discount
    public BonusCheck(String name, ProductDecorator discount, Points pointsToBuy) {// (discount)vilket typ av discount och på vad
        this.name = name;
        this.discount = discount;
        this.pointsToBuy = pointsToBuy;
    }

    public String getName() {
        return name;
    }

    public ProductDecorator getDiscount() {
        return discount;
    }

    public Points getPointsToBuy() {
        return pointsToBuy;
    }
}
