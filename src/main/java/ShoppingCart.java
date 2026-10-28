import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Cart operations reserve stock immediately and restore it when items are removed. */
public final class ShoppingCart {
    private final Map<Integer, CartItem> items = new LinkedHashMap<>();
    private final InventoryManager inventory;

    public ShoppingCart(InventoryManager inventory) {
        if (inventory == null) throw new IllegalArgumentException("Inventory is required.");
        this.inventory = inventory;
    }

    public void addItem(int productId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero.");
        Product product = inventory.findProduct(productId);
        product.adjustStock(-quantity); // validates before changing the cart
        CartItem item = items.get(productId);
        if (item == null) items.put(productId, new CartItem(product, quantity));
        else item.setQuantity(Math.addExact(item.getQuantity(), quantity));
    }

    public void updateQuantity(int productId, int newQuantity) {
        if (newQuantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero; use remove to clear an item.");
        CartItem item = items.get(productId);
        if (item == null) throw new IllegalArgumentException("That product is not in the cart.");
        int difference = newQuantity - item.getQuantity();
        if (difference > 0) item.getProduct().adjustStock(-difference);
        else if (difference < 0) item.getProduct().adjustStock(-difference);
        item.setQuantity(newQuantity);
    }

    public void removeItem(int productId) {
        CartItem removed = items.remove(productId);
        if (removed == null) throw new IllegalArgumentException("That product is not in the cart.");
        removed.getProduct().adjustStock(removed.getQuantity());
    }

    public Collection<CartItem> getItems() { return Collections.unmodifiableCollection(items.values()); }
    public boolean isEmpty() { return items.isEmpty(); }

    public BigDecimal getSubtotal() {
        return items.values().stream().map(item -> Money.amount(item.getProduct().getPrice())
                .multiply(BigDecimal.valueOf(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Prints and finalizes the sale. Stock is already reserved by addItem. */
    public void checkout() {
        if (isEmpty()) throw new IllegalStateException("Your cart is empty.");
        BigDecimal subtotal = getSubtotal();
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.05"));
        BigDecimal discount = subtotal.compareTo(new BigDecimal("1000.00")) > 0
                ? subtotal.multiply(new BigDecimal("0.10")) : BigDecimal.ZERO;
        BigDecimal total = subtotal.add(tax).subtract(discount).setScale(2, RoundingMode.HALF_UP);

        System.out.println("\n====================== RECEIPT ==========================");
        System.out.printf("%-28s %5s %12s %12s%n", "Item", "Qty", "Unit price", "Amount");
        for (CartItem item : items.values())
            System.out.printf("%-28s %5d %12s %12s%n", item.getProduct().getName(), item.getQuantity(),
                    Money.format(item.getProduct().getPrice()), Money.format(item.getLineTotal()));
        System.out.println("---------------------------------------------------------");
        System.out.printf("%-38s %14s%n", "Subtotal:", Money.format(subtotal));
        System.out.printf("%-38s %14s%n", "Tax (5%):", Money.format(tax));
        System.out.printf("%-38s %14s%n", "Discount (10% over ₹1,000):", "-" + Money.format(discount));
        System.out.printf("%-38s %14s%n", "GRAND TOTAL:", Money.format(total));
        System.out.println("=========================================================");
        items.clear();
    }
}

/** Shared currency formatting and decimal conversion. */
final class Money {
    private Money() { }
    static BigDecimal amount(double value) { return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP); }
    static String format(double value) { return format(amount(value)); }
    static String format(BigDecimal value) { return "₹" + value.setScale(2, RoundingMode.HALF_UP).toPlainString(); }
}
