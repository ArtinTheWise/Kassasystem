package org.example.Product;

import org.example.Money;

public class UnitPrice implements PriceModel{

    private final Money pricePerPiece;

    public UnitPrice(Money pricePerPiece){
        this.pricePerPiece = pricePerPiece;

    }

    @Override
    public Money calculatePrice(Quantity quantity) {
        if (quantity.getUnit() != Unit.PIECE) {
            throw new IllegalArgumentException("Quantity unit does not match price model unit.");
        }

        return pricePerPiece; // implementera pris uträkning i money klassen
    }


    
}
