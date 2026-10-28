/** A product and its reserved quantity in a shopping cart. */
public final class CartItem {
    private final Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        if (product == null) throw new IllegalArgumentException("Product is required.");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero.");
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getLineTotal() { return product.getPrice() * quantity; }
    void setQuantity(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero.");
        this.quantity = quantity;
    }
}
