package org.example.Product;
import org.example.Money;

public interface PriceModel {

    Money calculatePrice(Quantity quantity);
    Unit getUnit();
}
