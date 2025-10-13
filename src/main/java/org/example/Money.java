package org.example;

import java.util.Objects;

public class Money implements Comparable<Money> {
    private final long amountInMinorUnits;

    public Money(long amount) {
        if(amount < 0) throw new IllegalArgumentException();
        this.amountInMinorUnits = amount;
    }

    public long getAmountInMinorUnits(){
        return amountInMinorUnits;
    }

    public long getAmountInMajorUnits(){
        return amountInMinorUnits/100;
    }

    public Money add(long amountInMinorUnits){
        if(amountInMinorUnits < 0) throw new IllegalArgumentException();
        return new Money(amountInMinorUnits + amountInMinorUnits);
    }

    public Money add(Money m){
        return add(m.getAmountInMinorUnits());
    }

    public Money subtract(long amountInMinorUnits){
        if(amountInMinorUnits > this.amountInMinorUnits || amountInMinorUnits < 0) throw new IllegalArgumentException();
        return new Money(this.amountInMinorUnits - amountInMinorUnits);
    }

    public Money subtract(Money m){
        return subtract(m.getAmountInMinorUnits());
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Money m){
            return m.getAmountInMinorUnits() == amountInMinorUnits;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return Objects.hash(amountInMinorUnits);
    }

    @Override
    public int compareTo(Money m){
        return Long.compare(this.amountInMinorUnits, m.getAmountInMinorUnits());
    }

    @Override
    public String toString(){
        return String.format("%d,%02d SEK", getAmountInMajorUnits(), getAmountInMinorUnits() % 100);
    }
}
