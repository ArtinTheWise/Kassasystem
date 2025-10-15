package org.example.Product;


public class Quantity {

    private final double amount; // Amount of units for piece or kg for weight
    private final Unit unit;
    
    public Quantity(double amount, Unit unit){
        if (unit == Unit.PIECE && amount != Math.floor(amount)) {
            throw new IllegalArgumentException("Amount must be a whole number for PIECE unit.");
        }
        if (unit == Unit.PIECE && amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }
        if (unit == Unit.KG && amount < 0.001) {
            throw new IllegalArgumentException("Weight must be at least 0.001 kg.");
        }
        if (unit == Unit.HG && amount < 0.1) {
            throw new IllegalArgumentException("Weight must be at least 0.1 hg.");
        }
        if (unit == Unit.G && amount < 1) {
            throw new IllegalArgumentException("Weight must be at least 1 g.");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Unit cannot be null.");
        }
        
        this.amount = amount;
        this.unit = unit;

    }

    public double getAmount(){
        return amount;
    }

    public Unit getUnit(){
        return unit;
    }


}
