package org.example;

import java.util.Objects;

public class Money {
    private final int amount;

    public Money(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public Money add(Money m) {
        return new Money(amount + m.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof Money){
            Money otherMoney = (Money) other;
            return amount == otherMoney.amount;
        }
        return false;
    }

    @Override
    public String toString() {
        return Integer.toString(amount);
    }
}
