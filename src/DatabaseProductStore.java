import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DatabaseProductStore {
    
    private static final String URL = "jdbc:mysql://localhost:3306/shopping_cart?serverTimezone=UTC&useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "1008";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found in runtime classpath.", e);
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean isDatabaseAvailable() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException ex) {
            return false;
        }
    }
    
    // ===== PRODUCTS METHODS =====
    
    // Method to ADD a product (called from AdminDashboard)
    public static void addProduct(String name, double price, int stock) {
        addProduct(name, price, "", stock);
    }
    
    // Overloaded method with image path
    public static void addProduct(String name, double price, String imagePath, int stock) {
        String sql = "INSERT INTO PRODUCTS (name, price, image_path, stock) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setString(3, imagePath);
            stmt.setInt(4, stock);
            stmt.executeUpdate();
            System.out.println("Added: " + name);
        } catch (SQLException ex) {
            System.out.println("Error adding product: " + ex.getMessage());
        }
    }
    
    // Method to UPDATE a product (called from AdminDashboard)
    public static void updateProduct(String name, double price, int stock) {
        String sql = "UPDATE PRODUCTS SET price = ?, stock = ? WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, price);
            stmt.setInt(2, stock);
            stmt.setString(3, name);
            stmt.executeUpdate();
            System.out.println("Updated: " + name);
        } catch (SQLException ex) {
            System.out.println("Error updating product: " + ex.getMessage());
        }
    }
    
    // Method to DELETE a product (called from AdminDashboard)
    public static void deleteProduct(String name) {
        String sql = "DELETE FROM PRODUCTS WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            System.out.println("Deleted: " + name);
        } catch (SQLException ex) {
            System.out.println("Error deleting product: " + ex.getMessage());
        }
    }
    
    // Method to DISPLAY all products
    public static List<String> getAllProducts() {
        List<String> products = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTS";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String product = "ID: " + rs.getInt("id") + " | Name: " + rs.getString("name") + 
                                " | Price: " + rs.getDouble("price") + " | Stock: " + rs.getInt("stock") + 
                                " | Image: " + rs.getString("image_path");
                products.add(product);
                //System.out.println("--------LIST OF PRODUCTS--------");
                // System.out.println(product);
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return products;
    }
    
    // ===== USERS METHODS =====
    
    // Method to ADD a user (called from SignUpFrame)
    public static boolean addUser(String username, String password, String email) {
        String sql = "INSERT INTO USERS (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.executeUpdate();
            System.out.println("User registered: " + username);
            return true;
        } catch (SQLException ex) {
            System.out.println("Error adding user: " + ex.getMessage());
            return false;
        }
    }
    
    // Method to UPDATE a user (called from UserDashboard)
    public static void updateUser(String username, String password, String email) {
        String sql = "UPDATE USERS SET password = ?, email = ? WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, password);
            stmt.setString(2, email);
            stmt.setString(3, username);
            stmt.executeUpdate();
            System.out.println("Updated: " + username);
        } catch (SQLException ex) {
            System.out.println("Error updating user: " + ex.getMessage());
        }
    }

    public static boolean resetUserPassword(String username, String email, String newPassword) {
        String sql = "UPDATE USERS SET password = ? WHERE username = ? AND email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            stmt.setString(3, email);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Error resetting user password: " + ex.getMessage());
            return false;
        }
    }
    
    // Method to DELETE a user
    public static void deleteUser(String username) {
        String sql = "DELETE FROM USERS WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
            System.out.println("Deleted: " + username);
        } catch (SQLException ex) {
            System.out.println("Error deleting user: " + ex.getMessage());
        }
    }
    
    // Method to GET all users
    public static List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        String sql = "SELECT * FROM USERS";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String user = "ID: " + rs.getInt("id") + " | Username: " + rs.getString("username") + 
                             " | Email: " + rs.getString("email");
                users.add(user);
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return users;
    }
    
    // Method to VERIFY login and get user id
    public static int verifyUserAndGetId(String username, String password) {
        String sql = "SELECT id FROM USERS WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException ex) {
            System.out.println("Error during verifyUserAndGetId: " + ex.getMessage());
            ex.printStackTrace();
        }
        return -1; // invalid or error
    }
    
    // ===== CART METHODS =====
    
    // Method to ADD item to cart
    public static void addToCart(int userId, int productId, int quantity) {
        String checkSql = "SELECT quantity FROM CART WHERE user_id = ? AND product_id = ?";
        String updateSql = "UPDATE CART SET quantity = ? WHERE user_id = ? AND product_id = ?";
        String insertSql = "INSERT INTO CART (user_id, product_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, productId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                // Update quantity
                int existingQty = rs.getInt("quantity");
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, existingQty + quantity);
                    updateStmt.setInt(2, userId);
                    updateStmt.setInt(3, productId);
                    updateStmt.executeUpdate();
                }
            } else {
                // Insert new
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, productId);
                    insertStmt.setInt(3, quantity);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error adding to cart: " + ex.getMessage());
        }
    }
    
    // Method to REMOVE item from cart
    public static void removeFromCart(int userId, int productId) {
        String sql = "DELETE FROM CART WHERE user_id = ? AND product_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error removing from cart: " + ex.getMessage());
        }
    }
    
    // Method to GET cart items for user
    public static List<String> getCart(int userId) {
        List<String> cartItems = new ArrayList<>();
        String sql = "SELECT p.name, p.price, p.image_path, c.quantity FROM CART c JOIN PRODUCTS p ON c.product_id = p.id WHERE c.user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String item = "Name: " + rs.getString("name") + " | Price: " + rs.getDouble("price") + " | Image: " + rs.getString("image_path") + " | Quantity: " + rs.getInt("quantity");
                cartItems.add(item);
            }
        } catch (SQLException ex) {
            System.out.println("Error getting cart: " + ex.getMessage());
        }
        return cartItems;
    }
    
    // Method to CLEAR cart for user
    public static void clearCart(int userId) {
        String sql = "DELETE FROM CART WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error clearing cart: " + ex.getMessage());
        }
    }
}
