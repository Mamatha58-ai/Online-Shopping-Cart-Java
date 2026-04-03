import java.nio.file.*;
import java.io.*;
import java.util.*;
import java.io.File;

public class ProductStore {

    public static final String PRODUCTS_FILE = "products.txt";

    private static String getProductsFilePath() {
        String projectPath = "c:\\Users\\mamat\\OneDrive\\Desktop\\OnlineShoppingCart";
        return projectPath + File.separator + PRODUCTS_FILE;
    }

    private static Path productsPath() {
        return Paths.get(getProductsFilePath());
    }

    public static void ensureFile() throws IOException {
        Path p = productsPath();
        if (!Files.exists(p)) {
            Files.createFile(p);
            // Add default products
            addDefaultProducts();
        }
    }

    private static void addDefaultProducts() throws IOException {
        List<String> defaults = Arrays.asList(
            "Laptop|799.99|C:\\Users\\mamat\\OneDrive\\Desktop\\OnlineShoppingCart\\images\\laptop.jpg|10",
            "Smartphone|399.50|C:\\Users\\mamat\\OneDrive\\Desktop\\OnlineShoppingCart\\images\\phone.jpg|15",
            "Headphones|59.99|C:\\Users\\mamat\\OneDrive\\Desktop\\OnlineShoppingCart\\images\\headphones.jpg|20",
            "Keyboard|29.99|C:\\Users\\mamat\\OneDrive\\Desktop\\OnlineShoppingCart\\images\\keyboard.jpg|25",
            "Mouse|19.99|C:\\Users\\mamat\\OneDrive\\Desktop\\OnlineShoppingCart\\images\\mouse.jpg|30"
        );
        Files.write(productsPath(), defaults, StandardOpenOption.APPEND);
    }

    public static synchronized boolean addProduct(Product p) throws IOException {
        ensureFile();
        String line = String.join("|", p.name, String.valueOf(p.price), p.imagePath == null ? "" : p.imagePath, String.valueOf(p.stock));
        Files.write(productsPath(), Arrays.asList(line), StandardOpenOption.APPEND);
        return true;
    }

    public static synchronized List<Product> loadAll() throws IOException {
        ensureFile();
        List<Product> out = new ArrayList<>();
        List<String> lines = Files.readAllLines(productsPath());
        for (String ln : lines) {
            if (ln.trim().isEmpty()) continue;
            String[] parts = ln.split("\\|", -1);
            if (parts.length < 4) continue;
            try {
                Product p = new Product(
                    parts[0],
                    Double.parseDouble(parts[1]),
                    parts[2].isEmpty() ? "" : parts[2],
                    Integer.parseInt(parts[3])
                );
                out.add(p);
            } catch (NumberFormatException e) {
                // skip malformed line
            }
        }
        return out;
    }

    public static synchronized boolean updateStock(String productName, int newStock) throws IOException {
        ensureFile();
        List<Product> all = loadAll();
        List<String> lines = new ArrayList<>();
        boolean found = false;
        for (Product p : all) {
            if (p.name.equalsIgnoreCase(productName)) {
                p.stock = newStock;
                found = true;
            }
            lines.add(String.join("|", p.name, String.valueOf(p.price), p.imagePath == null ? "" : p.imagePath, String.valueOf(p.stock)));
        }
        if (found) {
            Files.write(productsPath(), lines);
            return true;
        }
        return false;
    }

    public static synchronized boolean deleteProduct(String name) throws IOException {
        ensureFile();
        List<Product> all = loadAll();
        List<String> lines = new ArrayList<>();
        for (Product p : all) {
            if (!p.name.equalsIgnoreCase(name)) {
                lines.add(String.join("|", p.name, String.valueOf(p.price), p.imagePath == null ? "" : p.imagePath, String.valueOf(p.stock)));
            }
        }
        Files.write(productsPath(), lines);
        return true;
    }

    public static synchronized Product findProduct(String name) throws IOException {
        List<Product> all = loadAll();
        for (Product p : all) {
            if (p.name.equalsIgnoreCase(name)) return p;
        }
        return null;
    }
}
