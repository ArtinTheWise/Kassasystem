package Sales;

public class Purchase {

    public Purchase(Object cashRegister, Object salesEmployee){
        if (cashRegister == null) {
            throw new IllegalArgumentException("CashRegister cannot be null.");
        }
        if (salesEmployee == null) {
            throw new IllegalArgumentException("SalesEmployee cannot be null.");
        }

    }


    
}
