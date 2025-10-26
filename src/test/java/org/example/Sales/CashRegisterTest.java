package org.example.Sales;

import static org.junit.jupiter.api.Assertions.*;

class CashRegisterTest {

//    Kassa - taget från Bäckmans discord meddelande
//
//    1. Säljare loggar in (kassa id + säljare id)
//
//    2. Köp startas - Purchase objekt:
//
//        Kassa (för id:t)
//        Seller (för id:t)
//        List<Product> products
//        LocalDateTime
//        Money Total netto pris
//        Money Total Moms
//        Money Total rabatt
//        Money Brutto pris
//
//
//    3. Scanna Product 1:
//        Skapa Quantity:
//        Weight: Product + Weight (KAN SKAPAS PÅ VÅG SEPARAT)
//        Unit: Product + Amount (1)
//        Sök bästa rabatt:
//        Finns: lägg till reducering.
//        Finns inte: x
//        Applicera pris
//        totalPris += quantityGetPrice
//        Om rabatt: += total rabatt
//
//    4. Scanna Product 2:
//        Finns produkten i listan - sökning i O(N), låg overheadd ArrayList - Array?
//        JA:
//        Weight: amount += productWeight
//        Unit: amount++
//        Nej - Skapa ny quantity ^
//                receipt.setPrice(Quantity);
//
//    4.a Ta bort produkt:
//        går att göra innan köpet är "slutfört"
//
//    5. Köpt slutfört
//        sök rabatter: discountManager.getBestDiscount(quantity.getProduct(), amount);
//        kolla medlemsskap
//        sätter pris
//    6. Betalning
//
//
//    7. Skriv ut kvitto

}