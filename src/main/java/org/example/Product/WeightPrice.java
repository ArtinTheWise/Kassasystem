package org.example.Product;

import static org.example.Product.Unit.PIECE;

import org.example.Money;

public class WeightPrice implements PriceModel {

    private final Money pricePerUnit;
    private final Unit unit;

    public WeightPrice(Money pricePerUnit, Unit unit){
        if (pricePerUnit == null) {
            throw new IllegalArgumentException("Price per unit can't be null.");
        }
        if (pricePerUnit.getAmountInMinorUnits() <= 0) {
            throw new IllegalArgumentException("Price per unit must be greater than zero.");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }
        if (unit == PIECE) {
            throw new IllegalArgumentException("Unit cannot be PIECE for WeightPrice.");
        }

        this.pricePerUnit = pricePerUnit;
        this.unit = unit;
    }

    @Override
    public Money calculatePrice(Quantity quantity) {
        if (quantity.getUnit() != unit) {
            throw new IllegalArgumentException("Quantity unit does not match price model unit.");
        }
        if (quantity.getAmount() < 0.001) {
            throw new IllegalArgumentException("Quantity must be at least 0.001 for weight units.");
        }
        
        return new Money(Math.round(pricePerUnit.getAmountInMinorUnits() 
        * quantity.getAmount()));
        
    }
}
