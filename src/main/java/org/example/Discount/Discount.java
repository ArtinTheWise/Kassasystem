package org.example.Discount;

import org.example.Money;
import org.example.Product.ProductGroup;
import org.example.Product.Quantity;

public interface Discount {
    boolean isActive();
    Money calculatePrice(Quantity quantity);
}
//behövs inte