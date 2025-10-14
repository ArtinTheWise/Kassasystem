package org.example.Product;

import org.example.Money;

public class WeightPrice implements PriceModel {

    private final Money pricePerUnit;
    private final Unit unit;

    public WeightPrice(Money pricePerUnit, Unit unit){
        this.pricePerUnit = pricePerUnit;
        this.unit = unit;
    }

    @Override
    public Money calculatePrice(Quantity quantity) {
        if (quantity.getUnit() != unit) {
            throw new IllegalArgumentException("Quantity unit does not match price model unit.");
        }
        
        return new Money(Math.round(pricePerUnit.getAmountInMinorUnits() * quantity.getAmount())); // implementera pris uträkning i money klassen
        
    }
}
