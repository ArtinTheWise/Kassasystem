package org.example.Product;

import org.example.Money;

public class UnitPrice implements PriceModel {

    private final Money pricePerPiece;

    public UnitPrice(Money pricePerPiece){
        if (pricePerPiece == null) {
            throw new IllegalArgumentException("Price per piece can't be null.");
        }

        if (pricePerPiece.getAmountInMinorUnits() <= 0) {
            throw new IllegalArgumentException("Price per piece must be greater than zero.");
        }

        this.pricePerPiece = pricePerPiece;

    }

    @Override
    public Money calculatePrice(Quantity quantity) {
        if (quantity.getUnit() != Unit.PIECE) {
            throw new IllegalArgumentException("Quantity unit does not match price model unit.");
        }

        if (quantity.getAmount() < 1.0) {
            throw new IllegalArgumentException("Quantity must be at least 1 for PIECE unit.");
        }

        return new Money(Math.round(pricePerPiece.getAmountInMinorUnits() 
        * quantity.getAmount())); 
    }

    public Unit getUnit(){return Unit.PIECE;}
}
