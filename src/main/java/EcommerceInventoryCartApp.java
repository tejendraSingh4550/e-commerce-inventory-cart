import java.util.Scanner;

/** Console entry point for the inventory and shopping cart application. */
public final class EcommerceInventoryCartApp {
    private EcommerceInventoryCartApp() { }

    public static void main(String[] args) {
        InventoryManager inventory = createSampleInventory();
        ShoppingCart cart = new ShoppingCart(inventory);
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Welcome to the E-Commerce Inventory Cart!");
            boolean running = true;
            while (running) {
                printMenu();
                int choice = readInt(scanner, "Choose an option: ");
                try {
                    switch (choice) {
                        case 1 -> inventory.displayProducts();
                        case 2 -> addToCart(scanner, cart);
                        case 3 -> displayCart(cart);
                        case 4 -> removeFromCart(scanner, cart);
                        case 5 -> cart.checkout();
                        case 6 -> running = false;
                        case 7 -> updateCartQuantity(scanner, cart);
                        default -> System.out.println("Please choose a menu option from 1 to 7.");
                    }
                } catch (IllegalArgumentException | IllegalStateException exception) {
                    System.out.println("Unable to complete that action: " + exception.getMessage());
                } catch (ArithmeticException exception) {
                    System.out.println("That quantity is too large.");
                }
            }
            System.out.println("Thank you for shopping. Goodbye!");
        }
    }

    private static InventoryManager createSampleInventory() {
        InventoryManager inventory = new InventoryManager();
        inventory.addProduct(new Product(101, "Wireless Mouse", 799.00, 20));
        inventory.addProduct(new Product(102, "Mechanical Keyboard", 2499.00, 12));
        inventory.addProduct(new Product(103, "USB-C Cable", 299.00, 50));
        inventory.addProduct(new Product(104, "Laptop Stand", 1199.00, 15));
        inventory.addProduct(new Product(105, "Headphones", 1599.00, 10));
        return inventory;
    }

    private static void printMenu() {
        System.out.println("\n1. View Products\n2. Add to Cart\n3. View Cart\n4. Remove from Cart\n5. Checkout\n6. Exit\n7. Update Cart Quantity");
    }

    private static void addToCart(Scanner scanner, ShoppingCart cart) {
        int id = readInt(scanner, "Enter product ID: ");
        int quantity = readInt(scanner, "Enter quantity: ");
        cart.addItem(id, quantity);
        System.out.println("Item added to cart; available stock has been reserved.");
    }

    private static void removeFromCart(Scanner scanner, ShoppingCart cart) {
        int id = readInt(scanner, "Enter product ID to remove: ");
        cart.removeItem(id);
        System.out.println("Item removed and its stock returned to inventory.");
    }

    private static void updateCartQuantity(Scanner scanner, ShoppingCart cart) {
        int id = readInt(scanner, "Enter product ID to update: ");
        int quantity = readInt(scanner, "Enter new quantity: ");
        cart.updateQuantity(id, quantity);
        System.out.println("Cart quantity updated.");
    }

    private static void displayCart(ShoppingCart cart) {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        System.out.println("\n------------------------ YOUR CART ----------------------");
        System.out.printf("%-6s %-28s %8s %12s %12s%n", "ID", "Product", "Qty", "Unit price", "Amount");
        for (CartItem item : cart.getItems())
            System.out.printf("%-6d %-28s %8d %12s %12s%n", item.getProduct().getId(), item.getProduct().getName(),
                    item.getQuantity(), Money.format(item.getProduct().getPrice()), Money.format(item.getLineTotal()));
        System.out.println("Subtotal: " + Money.format(cart.getSubtotal()));
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a whole number.");
            }
        }
    }
}
