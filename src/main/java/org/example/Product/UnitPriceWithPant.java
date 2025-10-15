package org.example.Product;

import org.example.Money;

public class UnitPriceWithPant implements PriceModel{

    private final UnitPrice basePrice; // Locked to UnitPrice - model
    private final Money pantPerPiece;

    public UnitPriceWithPant(UnitPrice basePrice, Money pantPerPiece){
        this.basePrice = basePrice;
        this.pantPerPiece = pantPerPiece;
    }
    
    @Override
    public Money calculatePrice(Quantity quantity) {
        if (quantity.getUnit() != Unit.PIECE) {
            throw new IllegalArgumentException("Quantity unit does not match price model unit.");
        }

        return new Money(Math.round(basePrice.calculatePrice(quantity).getAmountInMinorUnits() 
        + (pantPerPiece.getAmountInMinorUnits() 
        * quantity.getAmount())));
    }
    
}
