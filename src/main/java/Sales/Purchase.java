package Sales;

import java.util.Objects;
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
    private DiscountManager discountManager;

    public Purchase(Object cashRegister, Object salesEmployee, DiscountManager discountManager){
        if (cashRegister == null) {
            throw new IllegalArgumentException("CashRegister cannot be null.");
        }
        if (salesEmployee == null) {
            throw new IllegalArgumentException("SalesEmployee cannot be null.");
        }
        if (discountManager == null){
            throw new IllegalArgumentException("DiscountManager cannot be null");
        }

        this.discountManager = discountManager;
    }
        public Purchase(Object cashRegister, Object salesEmployee){
        if (cashRegister == null) {
            throw new IllegalArgumentException("CashRegister cannot be null.");
        }
        if (salesEmployee == null) {
            throw new IllegalArgumentException("SalesEmployee cannot be null.");
        }
        if (discountManager == null){
            throw new IllegalArgumentException("DiscountManager cannot be null");
        }
        this.discountManager = null;

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
        if (product.getPriceModel() == null){
            throw new IllegalArgumentException("Product has no PriceModel");
        }
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
            total+= e.getKey().calculatePrice(e.getValue()).getAmountInMinorUnits();
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
            total+= e.getKey().calculatePriceWithVat(e.getValue()).getAmountInMinorUnits();
            if(e.getKey().getPriceModel() instanceof UnitPriceWithPant){
                total += ((UnitPriceWithPant) e.getKey().getPriceModel()).getPantPerPiece().getAmountInMinorUnits();
            }
        }
        return new Money(total);
    }

    public void applyDiscounts(){
        Map<Product, Quantity> updated = new LinkedHashMap<>();

        for (Map.Entry<Product, Quantity> e : items.entrySet()) {
            Product base = e.getKey();
            Quantity qty = e.getValue();

            Product priced = discountManager.getBestDiscount(base, qty);

            updated.merge(priced, qty, (q1, q2) -> {
                if (q1.getUnit() != q2.getUnit()) {
                    throw new IllegalArgumentException("Unit conflict for product");
                }
                return new Quantity (q1.getAmount() + q2.getAmount(), q1.getUnit());
            });
        }
        items = updated;
    }
    
}
