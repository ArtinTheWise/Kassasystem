package org.example.Discount;

import org.example.Membership.Customer;
import org.example.Money;
import org.example.Product.Product;
import org.example.Product.Quantity;
import java.time.Clock;
import java.time.LocalDateTime;

public abstract class ProductDecorator extends Product {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Product product;
    protected final Clock clock;

    public ProductDecorator(Product product, LocalDateTime startTime, LocalDateTime endTime, Clock clock){ //Clock is used for testing
        super(product.getName(), product.getPriceModel(), product.getProductGroup(), product.getVatRate(), product.getAgeRestriction());

        if (startTime == null || endTime == null) {throw new IllegalArgumentException("Start time and end time can't be null.");}
        if (endTime.isBefore(startTime)) {throw new IllegalArgumentException("Start time must be before end time.");}

        this.startTime = startTime;
        this.endTime = endTime;
        this.product = product;
        this.clock = clock;
    }

    public boolean isActive(){
        LocalDateTime now = LocalDateTime.now(clock);
        return now.isBefore(endTime) && (now.isAfter(startTime) || now.isEqual(startTime));
    }

    @Override
    public String getName(){
        if(isActive()){
            return product.getName() + " got a discount.";
        }
        return product.getName();
    }

    public LocalDateTime getStartTime(){
        return startTime;
    }

    public LocalDateTime getEndTime(){
        return endTime;
    }

    public Product getProduct(){
        return product;
    }

    public abstract Money calculatePrice(Quantity quantity);

    public abstract Money calculatePriceWithVat(Quantity quantity);

    public Money calculatePrice(Quantity quantity, Customer customer) {
        return calculatePrice(quantity); //Needed for specialDiscount
    }

    public Money calculatePriceWithVat(Quantity quantity, Customer customer) {
        return calculatePriceWithVat(quantity); //Needed for specialDiscount
    }

    public Money getDiscountedAmount(Quantity quantity){
        long originalPrice = getProduct().calculatePrice(quantity).getAmountInMinorUnits();
        long discountedPrice = calculatePrice(quantity).getAmountInMinorUnits();

        return new Money(originalPrice - discountedPrice);
    }

    public abstract ProductDecorator createFor(Product product);
}
