package org.example.Sales;

import java.util.Objects;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.example.Money;
import org.example.Discount.DiscountManager;
import org.example.Product.Product;
import org.example.Product.Quantity;
import org.example.Product.Unit;
import org.example.Product.UnitPrice;
import org.example.Product.UnitPriceWithPant;
import org.example.Product.WeightPrice;


public class Purchase {

    private Map<Product, Quantity> items = new LinkedHashMap<>();// Hashmap + LinkedList - sorterad ordning men med konstant get
    private Map<Product, Product> pricedByBase = new LinkedHashMap<>(); // för att hålla baspriset

    private DiscountManager discountManager;
    private CashRegister cashRegister;
    private Cashier cashier;
    private final Clock clock;
    private LocalDateTime dateTime;

    public Purchase(CashRegister cashRegister, Cashier cashier) {
        this(cashRegister, cashier, null, Clock.systemDefaultZone());
    }
    public Purchase(CashRegister cashRegister, Cashier cashier, DiscountManager discountManager) {
        this(cashRegister, cashier, discountManager, Clock.systemDefaultZone());
    }

    public Purchase(CashRegister cashRegister, Cashier cashier,
                    DiscountManager discountManager, Clock clock) {
        if (cashRegister == null) throw new IllegalArgumentException("CashRegister cannot be null.");
        if (cashier == null) throw new IllegalArgumentException("cashier cannot be null.");
        if (discountManager == null && clock == null) { /* no-op; just clarifies flow */ }
        this.cashRegister = cashRegister;
        this.cashier = cashier;
        this.discountManager = discountManager; 
        this.clock = Objects.requireNonNull(clock, "clock cannot be null");
        this.dateTime = LocalDateTime.now(this.clock); 
    }

    public Product pricedFor(Product base){
        return pricedByBase.getOrDefault(base, base);
    }

    public void addPiece(Product product){
        requireNonNullProduct(product);

        if (!(product.getPriceModel() instanceof UnitPrice || product.getPriceModel() instanceof UnitPriceWithPant)){
            throw new IllegalArgumentException("The product does not have PIECE price-model");
        }

        merge(product, new Quantity(1, Unit.PIECE));
    
    }

    public void addWeight(Product product, double amount, Unit unit){
        requireNonNullProduct(product);

        if (!(product.getPriceModel() instanceof WeightPrice)){
            throw new IllegalArgumentException("The product does not have WEIGHT price-model");
        }
        merge(product, new Quantity(amount,unit));
        
    }

    private void merge(Product product, Quantity quantity){
        items.merge(product, quantity, (oldQ, addQ) -> {
            if (oldQ.getUnit() != addQ.getUnit()) {
                throw new IllegalArgumentException("Unit conflict for product");
            }
            return new Quantity (oldQ.getAmount() + addQ.getAmount(), oldQ.getUnit());
        });
    }

    private static void requireNonNullProduct(Product product) {
        Objects.requireNonNull(product, "product cannot be null");
        
    }

    public Map<Product, Quantity> getItemsView() {
        return Collections.unmodifiableMap(items);
        
    }

    public void removeProduct(Product product){
        items.remove(product);

    }

    public Money getTotalNet(){
        long total = 0;
        for (Map.Entry<Product, Quantity> e : items.entrySet()) {
            Product base = e.getKey();
            Quantity q = e.getValue();
            total += pricedFor(base).calculatePrice(q).getAmountInMinorUnits();
            
        }
        return new Money(total);
    }

    public Money getTotalVat(){
        long vat = getTotalGross().getAmountInMinorUnits() - getTotalNet().getAmountInMinorUnits();
        return new Money(vat);

    }

    public Money getTotalGross(){
        long total = 0;
        for (Map.Entry<Product, Quantity> e : items.entrySet()) {
            Product p = e.getKey();
            Quantity q = e.getValue();

            total += pricedFor(p).calculatePriceWithVat(q).getAmountInMinorUnits();

            if (p.getPriceModel() instanceof UnitPriceWithPant upm) {
            
                long pieces = Math.round(q.getAmount());
                total += upm.getPantPerPiece().getAmountInMinorUnits() * pieces;
            }
        }
        return new Money(total);

    }

public void applyDiscounts() {
    if (discountManager == null) {
        throw new IllegalStateException("DiscountManager is required for discounts");
    }

    Map<Product, Quantity> updated = new LinkedHashMap<>();
    Map<Product, Product> chosen  = new LinkedHashMap<>();

    for (var e : items.entrySet()) {
        Product base = e.getKey();
        Quantity qty = e.getValue();
        Product priced = discountManager.getBestDiscount(base, qty);

        updated.put(base, qty);        // simple copy
        chosen.put(base, priced);      // record mapping base -> priced
    }

    items = updated;
    pricedByBase = chosen;
}

    public Cashier getCashier(){ return cashier; }

    public CashRegister getCashRegister(){ return cashRegister; }

    public LocalDateTime getLocalDateTimeObject(){ return dateTime; }

    public LocalDate getDate(){ return dateTime.toLocalDate(); }

    public LocalTime getTime(){ return dateTime.toLocalTime(); }
    
}
