package org.example.Product;

import org.example.Money;

public class UnitPriceWithPant implements PriceModel{

    private final UnitPrice basePrice; // Locked to UnitPrice - model
    private final Money pantPerPiece;

    public UnitPriceWithPant(UnitPrice basePrice, Money pantPerPiece){
        if (basePrice == null){
            throw new IllegalArgumentException("Base price cannot be null.");
        }
        else if (pantPerPiece == null){
            throw new IllegalArgumentException("Pant per piece cannot be null.");
        }
        else if (pantPerPiece.getAmountInMinorUnits() < 0){
            throw new IllegalArgumentException("Pant per piece cannot be negative.");
        }

        this.basePrice = basePrice;
        this.pantPerPiece = pantPerPiece;
    }
    
    @Override
    public Money calculatePrice(Quantity quantity) {
        if (quantity.getUnit() != Unit.PIECE) {
            throw new IllegalArgumentException("Quantity unit does not match price model unit.");
        }
        if (quantity.getAmount() < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1 for PIECE unit.");
        }

        return new Money(Math.round(basePrice.calculatePrice(quantity).getAmountInMinorUnits() 
        + (pantPerPiece.getAmountInMinorUnits() 
        * quantity.getAmount())));
    }
    
}
