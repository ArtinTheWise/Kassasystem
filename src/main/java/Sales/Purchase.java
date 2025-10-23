package Sales;


import org.example.Product.Product;
import org.example.Product.Quantity;

public class Purchase {
    public Purchase(Object cashRegister, Object salesEmployee){
        if (cashRegister == null) {
            throw new IllegalArgumentException("CashRegister cannot be null.");
        }
        if (salesEmployee == null) {
            throw new IllegalArgumentException("SalesEmployee cannot be null.");
        }

    }

    public void addProduct(Product product, Quantity quantity){
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity cannot be null.");
        }
    

    }


    
}
