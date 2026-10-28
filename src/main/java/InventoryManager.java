import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Owns the product catalog and available stock. */
public final class InventoryManager {
    private final Map<Integer, Product> products = new LinkedHashMap<>();

    public void addProduct(Product product) {
        if (product == null) throw new IllegalArgumentException("Product is required.");
        if (products.putIfAbsent(product.getId(), product) != null)
            throw new IllegalArgumentException("A product with ID " + product.getId() + " already exists.");
    }

    public Product findProduct(int id) {
        Product product = products.get(id);
        if (product == null) throw new IllegalArgumentException("No product found with ID " + id + ".");
        return product;
    }

    /** Adds stock; negative adjustments are rejected if they would undercut reserved quantities. */
    public void updateStock(int productId, int quantityChange) {
        if (quantityChange == 0) throw new IllegalArgumentException("Stock change cannot be zero.");
        findProduct(productId).adjustStock(quantityChange);
    }

    public Collection<Product> getProducts() {
        return Collections.unmodifiableCollection(products.values());
    }

    public void displayProducts() {
        System.out.println("\n-------------------- PRODUCT CATALOG --------------------");
        System.out.printf("%-6s %-28s %12s %10s%n", "ID", "Name", "Price", "Available");
        System.out.println("---------------------------------------------------------");
        for (Product product : products.values())
            System.out.printf("%-6d %-28s %12s %10d%n", product.getId(), product.getName(),
                    Money.format(product.getPrice()), product.getStockQuantity());
        System.out.println("---------------------------------------------------------");
    }
}
