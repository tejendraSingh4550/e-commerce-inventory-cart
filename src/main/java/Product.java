import java.util.Objects;

/** A product in the store catalog. */
public final class Product {
    private final int id;
    private final String name;
    private final double price;
    private int stockQuantity;

    public Product(int id, String name, double price, int stockQuantity) {
        if (id <= 0) throw new IllegalArgumentException("Product ID must be positive.");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Product name cannot be blank.");
        if (!Double.isFinite(price) || price < 0) throw new IllegalArgumentException("Price must be a finite, non-negative amount.");
        if (stockQuantity < 0) throw new IllegalArgumentException("Stock cannot be negative.");
        this.id = id;
        this.name = name.trim();
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }

    void adjustStock(int change) {
        int updated = Math.addExact(stockQuantity, change);
        if (updated < 0) throw new IllegalArgumentException("Insufficient stock for " + name + ".");
        stockQuantity = updated;
    }

    @Override public boolean equals(Object other) {
        return this == other || other instanceof Product product && id == product.id;
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
