package org.example.Sales;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.util.Map;

import org.example.Money;
import org.example.Discount.DiscountManager;
import org.example.Discount.PercentageDiscount;
import org.example.Discount.NormalDiscount;
import org.example.Discount.ThreeForTwoDiscount;
import org.example.Product.PriceModel;
import org.example.Product.Product;
import org.example.Product.Quantity;
import org.example.Product.Unit;
import org.example.Product.WeightPrice;
import org.example.Product.UnitPrice;
import org.example.Product.UnitPriceWithPant;
import org.example.Product.VatRate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PurchaseTest {

    /*
     * konstruktor 
     * kassa id - null / finns ej / fel instans
     * Seller id - null / finns ej / fel instans
     * addItem - null / finns ej / fel instance
     * 
     * createReceipt - toString metod
     * 
     * lägga till viktvara - klar
     * lägga till styckvara - klar
     * lägga till viktvara igen. -e.g. första artikel är vikt, sen massa andra, sen samma viktvara igen - klar
     * samma som ovan för styck - klar
     * kan ta bort en vara under ett köp - klar
     * 
     * 
     * Pris:
     *      totalNet - klar
     *      totalGross - klar
     *      totalVAT - klar
     *      (inkl räkna på pant) - klar
     * Rabatt:
     *      Hämta en rabatt
     *      Välja den bästa rabatt om flera är tillgängliga
     */


    private Cashier mockCashier(){
        Cashier c = mock(Cashier.class);
        return c;
    }

    private CashRegister mockCashRegister(){
        CashRegister cr = mock(CashRegister.class);
        return cr;
    }

    private double toKg(Quantity q) {
        switch (q.getUnit()) {
            case KG: return q.getAmount();
            case HG: return q.getAmount() / 10.0;
            case G:  return q.getAmount() / 1000.0;
            default: throw new IllegalArgumentException("Unsupported unit for weight product: " + q.getUnit());
        }
    }

    
    private Product mockWeightProductGrossOnly(String name, Money grossPerKg) {
        Product p = mock(Product.class, name);
        WeightPrice pm = mock(WeightPrice.class);
        lenient().when(p.getPriceModel()).thenReturn(pm);

        lenient().when(p.calculatePriceWithVat(any(Quantity.class))).thenAnswer(inv -> {
            Quantity q = inv.getArgument(0);
            double kg = toKg(q);
            long totalMinor = Math.round(grossPerKg.getAmountInMinorUnits() * kg);
            return new Money(totalMinor);
        });

        lenient().when(pm.getUnit()).thenReturn(Unit.KG);
        lenient().when(p.getName()).thenReturn(name);
        lenient().when(p.getPriceModel()).thenReturn(pm);
        lenient().when(p.getVatRate()).thenReturn(mock(VatRate.class));

        return p;


    }

    private Product mockUnitProduct(String name){
        Product p = mock(Product.class, name);
        PriceModel pm = mock(UnitPrice.class);
        lenient().when(p.getPriceModel()).thenReturn(pm);

        lenient().when(pm.getUnit()).thenReturn(Unit.PIECE);
        lenient().when(p.getName()).thenReturn(name);
        lenient().when(p.getPriceModel()).thenReturn(pm);
        lenient().when(p.getVatRate()).thenReturn(mock(VatRate.class));

        return p;


    }

    private Product mockUnitProductNetOnly(String name, Money netPerPiece) {
        Product p = mock(Product.class, name);
        UnitPrice pm = mock(UnitPrice.class);
        lenient().when(p.getPriceModel()).thenReturn(pm);

        lenient().when(p.calculatePrice(any(Quantity.class))).thenAnswer(inv -> {
            Quantity q = inv.getArgument(0);
            long qty = (long) q.getAmount(); // PIECE quantities are whole numbers in these tests
            return new Money(netPerPiece.getAmountInMinorUnits() * qty);
        });

        lenient().when(pm.getUnit()).thenReturn(Unit.PIECE);
        lenient().when(p.getName()).thenReturn(name);
        lenient().when(p.getPriceModel()).thenReturn(pm);
        lenient().when(p.getVatRate()).thenReturn(mock(VatRate.class));

        return p;
    }

    private Product mockUnitProductWithGross(String name, Money netPerPiece, Money grossPerPiece) {
        Product p = mock(Product.class, name);
        UnitPrice pm = mock(UnitPrice.class);
        lenient().when(p.getPriceModel()).thenReturn(pm);

        lenient().when(p.calculatePrice(any(Quantity.class))).thenAnswer(inv -> {
            Quantity q = inv.getArgument(0);
            long qty = (long) q.getAmount();
            return new Money(netPerPiece.getAmountInMinorUnits() * qty);
        });

        lenient().when(p.calculatePriceWithVat(any(Quantity.class))).thenAnswer(inv -> {
            Quantity q = inv.getArgument(0);
            long qty = (long) q.getAmount();
            return new Money(grossPerPiece.getAmountInMinorUnits() * qty);
        });

        lenient().when(pm.getUnit()).thenReturn(Unit.PIECE);
        lenient().when(p.getName()).thenReturn(name);
        lenient().when(p.getPriceModel()).thenReturn(pm);
        lenient().when(p.getVatRate()).thenReturn(mock(VatRate.class));

        return p;
    }

    private Product mockUnitProductGrossOnly(String name, Money grossPerPiece) {
        Product p = mock(Product.class, name);
        UnitPrice pm = mock(UnitPrice.class);
        lenient().when(p.getPriceModel()).thenReturn(pm);

        lenient().when(p.calculatePriceWithVat(any(Quantity.class))).thenAnswer(inv -> {
            Quantity q = inv.getArgument(0);
            long qty = (long) q.getAmount();
            return new Money(grossPerPiece.getAmountInMinorUnits() * qty);
        });

        lenient().when(p.calculatePrice(any(Quantity.class))).thenAnswer(inv -> {
            Quantity q = inv.getArgument(0);
            long qty = (long) q.getAmount();

            long gross = grossPerPiece.getAmountInMinorUnits() * qty;
            return new Money(gross);
        });



        lenient().when(pm.getUnit()).thenReturn(Unit.PIECE);  
        lenient().when(p.getName()).thenReturn(name);
        lenient().when(p.getPriceModel()).thenReturn(pm);
        lenient().when(p.getVatRate()).thenReturn(mock(VatRate.class));

        return p;
    }

    private Product mockUnitProductWithPantGrossOnly(String name, Money grossPerPiece) {
        Product p = mock(Product.class, name);
        UnitPriceWithPant pm = mock(UnitPriceWithPant.class);
        lenient().when(p.getPriceModel()).thenReturn(pm);
        lenient().when(pm.getPantPerPiece()).thenReturn(new Money(100));

        lenient().when(p.calculatePriceWithVat(any(Quantity.class))).thenAnswer(inv -> {
            Quantity q = inv.getArgument(0);
            long qty = (long) q.getAmount();
            return new Money(grossPerPiece.getAmountInMinorUnits() * qty);
        });

        lenient().when(p.calculatePrice(any(Quantity.class))).thenAnswer(inv -> {
            Quantity q = inv.getArgument(0);
            long qty = (long) q.getAmount();

            long gross = grossPerPiece.getAmountInMinorUnits() * qty;
            return new Money(gross);
        });

        lenient().when(pm.getUnit()).thenReturn(Unit.PIECE);
        lenient().when(p.getName()).thenReturn(name);
        lenient().when(p.getPriceModel()).thenReturn(pm);
        lenient().when(p.getVatRate()).thenReturn(mock(VatRate.class));

        return p;
    }

    private Product mockWeightProduct(String name) {
        Product p = mock(Product.class, name);
        PriceModel pm = mock(WeightPrice.class);
        lenient().when(p.getPriceModel()).thenReturn(pm);

        lenient().when(pm.getUnit()).thenReturn(Unit.KG);
        lenient().when(p.getName()).thenReturn(name);
        lenient().when(p.getPriceModel()).thenReturn(pm);
        lenient().when(p.getVatRate()).thenReturn(mock(VatRate.class));

        return p;

    }

    private Product mockUnitProductWithPant(String name) {
        Product p = mock(Product.class, name);
        PriceModel pm = mock(UnitPriceWithPant.class);
        lenient().when(p.getPriceModel()).thenReturn(pm);

        lenient().when(pm.getUnit()).thenReturn(Unit.PIECE);
        lenient().when(p.getName()).thenReturn(name);
        lenient().when(p.getPriceModel()).thenReturn(pm);
        lenient().when(p.getVatRate()).thenReturn(mock(VatRate.class));

        return p;
    }

    @Test
    @DisplayName("constructor: null cashier throws exception")
    void createPurchaseWithNullCashRegisterThrowsException(){
        assertThrows(IllegalArgumentException.class, 
            () -> new Purchase(null, mockCashier()));
    }

    @Test
    @DisplayName("constructor: null cashRegister throws exception")
    void createPurchaseWithNullCashierThrowsException(){
        assertThrows(IllegalArgumentException.class, 
            () -> new Purchase(mockCashRegister(), null));
    }

    @Test
    @DisplayName("addPiece: throws exception for null Product")
    void addPiece_onNullProduct_throws(){
        Purchase purchase = new Purchase(mockCashRegister(), mockCashier());
        assertThrows(NullPointerException.class, 
            () -> purchase.addPiece(null));
    }

    @Test
    @DisplayName("addPiece: throws exception for weightPice")
    void addPiece_onWeightProduct_throws(){
        Purchase purchase = new Purchase(mockCashRegister(), mockCashier());
        Product cheese = mockWeightProduct("Cheese");
        
        assertThrows(IllegalArgumentException.class,
            () -> purchase.addPiece(cheese));
    }

    @Test
    @DisplayName("addWeight: throws exception for null Product")
    void addWeight_onNullProduct_throws(){
        Purchase purchase = new Purchase(mockCashRegister(), mockCashier());
        assertThrows(NullPointerException.class, 
            () -> purchase.addWeight(null, 0.432, Unit.G)); // unit + weight doesn't matter.
    }

    @Test
    @DisplayName("addWeight: throws exception for Piece Product")
    void addWeight_onPieceProduct_throws() {
        Product beef = mockUnitProduct("beef");

        Purchase purchase = new Purchase(mockCashRegister(), mockCashier());
        assertThrows(IllegalArgumentException.class,
            () -> purchase.addWeight(beef, 0.432, Unit.G)); // unit + weight doesn't matter.
    }

    @Test
    @DisplayName("addWeight: add weight for KG product")
    void addWeight_addsAmount_forKGProduct() {
        Purchase purchase = new Purchase(mockCashRegister(), mockCashier());
        Product potato = mockWeightProduct("Potato");

        purchase.addWeight(potato, 0.350, Unit.KG);

        Map<Product,Quantity> items = purchase.getItemsView();
        assertEquals(1, items.size());
        Quantity q = items.get(potato);
        assertNotNull(q);
        assertEquals(0.350, q.getAmount(), 0.0001);
        assertEquals(Unit.KG, q.getUnit());
    }

    @Test
    @DisplayName("addWeight: add weight for HG product")
    void addWeight_addsAmount_forHGProduct() {
        Purchase purchase = new Purchase(mockCashRegister(), mockCashier());
        Product potato = mockWeightProduct("Potato");

        purchase.addWeight(potato, 0.350, Unit.HG);

        Map<Product,Quantity> items = purchase.getItemsView();
        assertEquals(1, items.size());
        Quantity q = items.get(potato);
        assertNotNull(q);
        assertEquals(0.350, q.getAmount(), 0.0001);
        assertEquals(Unit.HG, q.getUnit());
    }

    @Test
    @DisplayName("addWeight: add weight for G product")
    void addWeight_addsAmount_forGProduct() {
        Purchase purchase = new Purchase(mockCashRegister(), mockCashier());
        Product potato = mockWeightProduct("Potato");

        purchase.addWeight(potato, 0.350, Unit.G);

        Map<Product,Quantity> items = purchase.getItemsView();
        assertEquals(1, items.size());
        Quantity q = items.get(potato);
        assertNotNull(q);
        assertEquals(0.350, q.getAmount(), 0.0001);
        assertEquals(Unit.G, q.getUnit());
    }


    @Test
    @DisplayName("addPiece - add piece for piece product ")
    void addPiece_addsOnePiece() {
        Purchase purchase = new Purchase(mockCashRegister(), mockCashier());
        Product banana = mockUnitProduct("Banana");

        purchase.addPiece(banana);

        Map<Product,Quantity> items = purchase.getItemsView();
        assertEquals(1, items.size());
        Quantity q = items.get(banana);
        assertNotNull(q);
        assertEquals(1.0, q.getAmount(), 0.0001);
        assertEquals(Unit.PIECE, q.getUnit());
    }

    @Test
    @DisplayName("addPiece - add several pieces correctly")
    void addPiece_addsManyPieces() {
        Purchase purchase = new Purchase(mockCashRegister(), mockCashier());
        Product karinsLasagne = mockUnitProduct("Karins lasagne");

        for (int i = 0; i < 3; i++){
            purchase.addPiece(karinsLasagne);
        }

        Quantity q = purchase.getItemsView().get(karinsLasagne);
        assertEquals(3.0, q.getAmount(), 0.0001);
        assertEquals(Unit.PIECE, q.getUnit());
    
    }

    @Test
    @DisplayName("addPieceWithPant - add piece for piece with pant product ")
    void addPiece_addsOnePieceWithPant() {
        Purchase purchase = new Purchase(mockCashRegister(), mockCashier());
        Product cola = mockUnitProductWithPant("Coca Cola 33cl");

        purchase.addPiece(cola);

        Map<Product,Quantity> items = purchase.getItemsView();
        assertEquals(1, items.size());
        Quantity q = items.get(cola);
        assertNotNull(q);
        assertEquals(1.0, q.getAmount(), 0.0001);
        assertEquals(Unit.PIECE, q.getUnit());
        assertTrue(cola.getPriceModel() instanceof UnitPriceWithPant);
    }

    @Test
    @DisplayName("addPiece + addWeight- add Piece then weight then piece correctly")
    void addPiece_addsUnitsInDifferentOrder() {
        Purchase purchase = new Purchase(mockCashRegister(), mockCashier());
        Product karinsLasagne = mockUnitProduct("Karins lasagne");
        Product potato = mockWeightProduct("Potato");

        purchase.addPiece(karinsLasagne);
        purchase.addWeight(potato, 0.350, Unit.G);
        purchase.addPiece(karinsLasagne); //should be added to same quantity as first one

        Quantity q = purchase.getItemsView().get(karinsLasagne);
        assertEquals(2.0, q.getAmount(), 0.0001);
        assertEquals(Unit.PIECE, q.getUnit());
    
    }

    @Test
    @DisplayName("addPiece + addWeight- add Weight then Piece then Weight correctly")
    void addPiece_addsUnitsInDifferentOrderTwo() {
        Purchase purchase = new Purchase(mockCashRegister(), mockCashier());
        Product karinsLasagne = mockUnitProduct("Karins lasagne");
        Product potato = mockWeightProduct("Potato");

        purchase.addWeight(potato, 0.350, Unit.G);
        purchase.addPiece(karinsLasagne);
        purchase.addWeight(potato, 0.400, Unit.G); //should be added to same quantity as first one

        Quantity q = purchase.getItemsView().get(potato);
        assertEquals(0.750, q.getAmount(), 0.0001);
        assertEquals(Unit.G, q.getUnit());
    
    }

    @Test
    @DisplayName("removeProduct - removes the whole row")
    void removeProduct_removesLine(){
        Purchase purchase = new Purchase(mockCashRegister(), mockCashier());
        Product cola = mockUnitProductWithPant("Coca Cola 33cl");
        purchase.addPiece(cola);

        purchase.removeProduct(cola);
        assertTrue(purchase.getItemsView().isEmpty());
    }

    @Test
    @DisplayName("getTotalNet - calculates net price correctly")
    void getTotalNet_calculateTotalNet(){
        Purchase purchase = new Purchase(mockCashRegister(), mockCashier());
        Product karinsLasagne = mockUnitProductNetOnly("Karins lasagne", new Money(5000));
        Product billys = mockUnitProductNetOnly("Billys Pan Pizza", new Money(4000));
        Product kanelbulle = mockUnitProductNetOnly("Kanelbulle", new Money(1000));

        purchase.addPiece(karinsLasagne);
        purchase.addPiece(billys);
        purchase.addPiece(kanelbulle);

        Money netPrice = purchase.getTotalNet();


        assertEquals(10000L, netPrice.getAmountInMinorUnits());
    }

    @Test
    @DisplayName("GetTotalVat - calculates vat correctly")
    void getTotalVat_calculateTotalVat() {
        Purchase purchase = new Purchase(mockCashRegister(), mockCashier());
        Product karinsLasagne = mockUnitProductWithGross("Karins lasagne", new Money(5000), new Money(6250));
        Product billys = mockUnitProductWithGross("Billys Pan Pizza", new Money(4000), new Money(5000));
        Product kanelbulle = mockUnitProductWithGross("Kanelbulle", new Money(1000), new Money(1250));

        purchase.addPiece(karinsLasagne);
        purchase.addPiece(billys);
        purchase.addPiece(kanelbulle);

        Money vat = purchase.getTotalVat();
        assertEquals(2500L, vat.getAmountInMinorUnits());

    }

    @Test
    @DisplayName("GetTotalGross - calculates gross price correctly")
    void getTotalGross_calculateTotalGross(){
        Purchase purchase = new Purchase(mockCashRegister(), mockCashier());
        Product karinsLasagne = mockUnitProductGrossOnly("Karins lasagne", new Money(6250));
        Product billys = mockUnitProductGrossOnly("Billys Pan Pizza", new Money(5000));
        Product kanelbulle = mockUnitProductGrossOnly("Kanelbulle", new Money(1250));

        purchase.addPiece(karinsLasagne);
        purchase.addPiece(billys);
        purchase.addPiece(kanelbulle);

        Money totalPrice = purchase.getTotalGross();
        assertEquals(12500L, totalPrice.getAmountInMinorUnits());
    }

    @Test
    @DisplayName("GetTotalGross - calculates pant gross price correctly")
    void getTotalGross_calculateTotalPantGross(){
        Purchase purchase = new Purchase(mockCashRegister(), mockCashier());
        Product cola = mockUnitProductWithPantGrossOnly("Coca Cola 33cl", new Money(1625)); //13 kr + 25% moms - 3.25kr
        Product fanta = mockUnitProductWithPantGrossOnly("Fanta 33cl", new Money(1625)); //13 kr + 25% moms - 3.25kr

        purchase.addPiece(cola);
        purchase.addPiece(fanta);

        Money totalGrossWithPant = purchase.getTotalGross();
        
        assertEquals(3450L, totalGrossWithPant.getAmountInMinorUnits()); //32.5 kr + 2 pant
        

    }

    @Test
    @DisplayName("applyDiscounts - calculates PercentageDiscount correctly")
    void applyDiscounts_PercentageDiscount(){

        Product banana = mockUnitProductGrossOnly("Banana", new Money(1600));
        LocalDateTime ends = LocalDateTime.of(2099, 1, 1, 0, 0);
        PercentageDiscount percentageDiscount = new PercentageDiscount(banana, 25, ends);
        DiscountManager discountManager = new DiscountManager(percentageDiscount);

        Purchase purchase = new Purchase(mockCashRegister(), mockCashier(), discountManager);

        purchase.addPiece(banana);
        purchase.applyDiscounts();

        Long total = purchase.getTotalGross().getAmountInMinorUnits();

        assertEquals(1200L, total);
    }

    @Test
    @DisplayName("applyDiscounts - calculates best discount if there is 2 PercentageDiscounts correctly")
    void applyDiscounts_BestPercentageDiscount(){

        Product banana = mockUnitProductGrossOnly("Banana", new Money(1600));
        LocalDateTime ends = LocalDateTime.of(2099, 1, 1, 0, 0);
        PercentageDiscount percentageDiscount = new PercentageDiscount(banana, 12, ends); // 12 %
        PercentageDiscount percentageDiscountTwo = new PercentageDiscount(banana, 25, ends); // 25 %
        DiscountManager discountManager = new DiscountManager(percentageDiscount, percentageDiscountTwo);
        
        Purchase purchase = new Purchase(mockCashRegister(), mockCashier(), discountManager);

        purchase.addPiece(banana);
        purchase.applyDiscounts();

        Long total = purchase.getTotalGross().getAmountInMinorUnits();

        assertEquals(1200L, total);
    }

    @Test
    @DisplayName("applyDiscounts - calculates best discount if there is PercentageDiscount + NormalDiscount")
    void applyDiscounts_BestDiscountNormalAndPercentage(){
        
        Product banana = mockUnitProductGrossOnly("Banana", new Money(1600));
        LocalDateTime ends = LocalDateTime.of(2099, 1, 1, 0, 0);
        LocalDateTime starts = LocalDateTime.now();
        PercentageDiscount percentageDiscount = new PercentageDiscount(banana, 25, ends); // 25 % - ^ 1600 --> 1200
        NormalDiscount normalDiscount = new NormalDiscount(banana, 600, starts, ends); // 600kr - ^ 1600 --> 1000
        DiscountManager discountManager = new DiscountManager(percentageDiscount, normalDiscount);
        
        Purchase purchase = new Purchase(mockCashRegister(), mockCashier(), discountManager);

        purchase.addPiece(banana);
        purchase.applyDiscounts();

        Long total = purchase.getTotalGross().getAmountInMinorUnits();

        assertEquals(1000L, total);
    }

    @Test
    @DisplayName("applyDiscounts - identifies 3 for 2 discount correctly")
    void applyDiscounts_ThreeForTwoDiscount(){

        Product banana = mockUnitProductGrossOnly("Banana", new Money(1600));
        LocalDateTime ends = LocalDateTime.of(2099, 1, 1, 0, 0);
        ThreeForTwoDiscount threeForTwoDiscount = new ThreeForTwoDiscount(banana, ends); // 3 for 2 = 1600 x 3 - 1 unit = 3200kr
        DiscountManager discountManager = new DiscountManager(threeForTwoDiscount);
        
        Purchase purchase = new Purchase(mockCashRegister(), mockCashier(), discountManager);

        purchase.addPiece(banana);
        purchase.addPiece(banana);
        purchase.addPiece(banana);
        purchase.applyDiscounts();

        Long total = purchase.getTotalGross().getAmountInMinorUnits();

        assertEquals(3200L, total);
    }

    @Test
    @DisplayName("applyDiscounts - calculates discount / Vat / pant correctly")
    // Correct flow is net price + vat - discount + pant
    void applyDiscountOnPant(){
        Product cola = mockUnitProductWithPantGrossOnly("Coca Cola 33cl", new Money(1300)); //1kr pant
        LocalDateTime ends = LocalDateTime.of(2099, 1, 1, 0, 0);
        LocalDateTime starts = LocalDateTime.now();
        
        NormalDiscount normalDiscount = new NormalDiscount(cola, 200, starts, ends); // cola 1300 --> 1100
        DiscountManager discountManager = new DiscountManager(normalDiscount);
        Purchase purchase = new Purchase(mockCashRegister(), mockCashier(), discountManager);

        purchase.addPiece(cola); // 1300
        purchase.applyDiscounts(); // 1100

        Long total = purchase.getTotalGross().getAmountInMinorUnits(); // 1200

        assertEquals(1200, total);

    }

    @Test
    @DisplayName("applyDiscounts - handle several discounts and products correctly")
    void applyDiscounts_SeveralDiscountsAndProducts(){
        // Products
        Product banana = mockUnitProductGrossOnly("Banana", new Money(500));
        Product apple = mockUnitProductGrossOnly("Apple", new Money(400));
        Product cucumber = mockUnitProductGrossOnly("Cucumber", new Money(1200));
        Product tomato = mockWeightProductGrossOnly("Tomato", new Money(2500));
        Product cola = mockUnitProductWithPantGrossOnly("Coca Cola 33cl", new Money(1300)); //1kr pant

        //Discounts
        LocalDateTime ends = LocalDateTime.of(2099, 1, 1, 0, 0);
        LocalDateTime starts = LocalDateTime.now();
        ThreeForTwoDiscount threeForTwoDiscount = new ThreeForTwoDiscount(banana, ends); // 3 for 2 = 1000 istället för 1500
        PercentageDiscount percentageDiscount = new PercentageDiscount(banana, 25, ends); // banana 25% 500 --> 375
        NormalDiscount normalDiscount = new NormalDiscount(apple, 100, starts, ends); // apple 400 --> 300
        NormalDiscount normalDiscountTwo = new NormalDiscount(cola, 200, starts, ends); // cola 1300 --> 1100

        DiscountManager discountManager = new DiscountManager(threeForTwoDiscount, percentageDiscount, normalDiscount, normalDiscountTwo);

        Purchase purchase = new Purchase(mockCashRegister(), mockCashier(), discountManager);

        purchase.addPiece(banana);
        purchase.addPiece(banana);
        purchase.addPiece(apple);
        purchase.addPiece(cucumber);
        purchase.addWeight(tomato, 500, Unit.G); //500 g tomater = 1250
        purchase.addPiece(cola);
        purchase.addWeight(tomato, 250, Unit.G);
        purchase.addPiece(banana);


        purchase.applyDiscounts();

        Long total = purchase.getTotalGross().getAmountInMinorUnits();

        assertEquals(5575, total);
        
    }

    @Test
    @DisplayName("null discountManger in applyDiscount throws exception")
    void applyDiscountsWithNullDiscountManagerThrowsException(){
        Product banana = mockUnitProductGrossOnly("Banana", new Money(500));

        Purchase purchase = new Purchase(mockCashRegister(), mockCashier());

        purchase.addPiece(banana);

        assertThrows(Exception.class, () -> purchase.applyDiscounts());



    }




}
