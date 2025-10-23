package Sales;


import org.example.Product.Product;
import org.example.Product.UnitPrice;
import org.example.Product.UnitPriceWithPant;
import org.example.Product.WeightPrice;

public class Purchase {
    public Purchase(Object cashRegister, Object salesEmployee){
        if (cashRegister == null) {
            throw new IllegalArgumentException("CashRegister cannot be null.");
        }
        if (salesEmployee == null) {
            throw new IllegalArgumentException("SalesEmployee cannot be null.");
        }

    }

    public void addPiece(Product product){
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        if (!(product.getPriceModel() instanceof UnitPrice || product.getPriceModel() instanceof UnitPriceWithPant)){
            throw new IllegalArgumentException("The product does not have PIECE price-model");
        }
    
    }

    public void addWeight(Product product){
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        if (!(product.getPriceModel() instanceof WeightPrice)){
            throw new IllegalArgumentException("The product does not have WEIGHT price-model");
        }
        
    }


    
}
