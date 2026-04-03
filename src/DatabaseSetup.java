import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {
    
    // Database credentials
    // base connection URL WITHOUT database or query parameters (they are appended later)
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "shopping_cart";
    private static final String USER = "root";
    private static final String PASSWORD = "1008";
    // helper to build full URL with database and properties
    private static String urlForDb(String db) {
        return JDBC_URL + db + "?serverTimezone=UTC&useSSL=false";
    }
    
    public static void main(String[] args) {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Please add the MySQL Connector/J JAR to your classpath.");
            e.printStackTrace();
            return;
        }
        
        Connection conn = null;
        try {
            // Step 1: Connect to MySQL server (without database)
            conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            System.out.println("Connected to MySQL server!");
            
            Statement stmt = conn.createStatement();
            
            // Step 2: Create database if it doesn't exist
            String createDBSQL = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            stmt.executeUpdate(createDBSQL);
            System.out.println("Database 'shopping_cart' created or already exists!");
            
            // Step 3: Close connection and reconnect to the new database
            stmt.close();
            conn.close();
            
            // reconnect to the newly created/selected database
            conn = DriverManager.getConnection(urlForDb(DB_NAME), USER, PASSWORD);
            System.out.println("Connected to shopping_cart database!");
            stmt = conn.createStatement();
            
            // Step 4: Drop existing tables if they exist (order matters due to FK)
            try {
                stmt.executeUpdate("DROP TABLE IF EXISTS CART");
                stmt.executeUpdate("DROP TABLE IF EXISTS PRODUCTS");
                stmt.executeUpdate("DROP TABLE IF EXISTS USERS");
            } catch (SQLException e) {
                System.err.println("Warning: could not drop tables (they may not exist yet)");
                e.printStackTrace();
            }
            String productsSQL = "CREATE TABLE PRODUCTS (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "price DECIMAL(10, 2) NOT NULL, " +
                    "image_path VARCHAR(255), " +
                    "stock INT NOT NULL)";
            stmt.executeUpdate(productsSQL);
            System.out.println("Created PRODUCTS table successfully!");
            
            // Step 5: Create USERS table
            String usersSQL = "CREATE TABLE USERS (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "email VARCHAR(100))";
            stmt.executeUpdate(usersSQL);
            System.out.println("Created USERS table successfully!");
            
            // Step 6: Create CART table
            String cartSQL = "CREATE TABLE CART (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT NOT NULL, " +
                    "product_id INT NOT NULL, " +
                    "quantity INT NOT NULL, " +
                    "FOREIGN KEY (user_id) REFERENCES USERS(id), " +
                    "FOREIGN KEY (product_id) REFERENCES PRODUCTS(id))";
            stmt.executeUpdate(cartSQL);
            System.out.println("Created CART table successfully!");
            
            // Step 7: Insert default products
            String checkProducts = "SELECT COUNT(*) FROM PRODUCTS";
            java.sql.ResultSet rs = stmt.executeQuery(checkProducts);
            rs.next();
            int productCount = rs.getInt(1);
            
            if (productCount == 0) {
                System.out.println("\nInserting default products...");
                String[] products = {
                    "INSERT INTO PRODUCTS (name, price, image_path, stock) VALUES ('Lenovo Laptop i5', 899.99, 'images/Lenovo Laptop  i5.jpg', 5)",
                    "INSERT INTO PRODUCTS (name, price, image_path, stock) VALUES ('Samsung Galaxy S25', 699.99, 'images/SamsungGalaxy S25.jpg', 10)",
                    "INSERT INTO PRODUCTS (name, price, image_path, stock) VALUES ('Dyson Headphones', 299.99, 'images/Dyson Headphones.jpg', 8)",
                    "INSERT INTO PRODUCTS (name, price, image_path, stock) VALUES ('Earbuds', 49.99, 'images/Earbuds.jpg', 15)",
                    "INSERT INTO PRODUCTS (name, price, image_path, stock) VALUES ('Smart TV', 599.99, 'images/Smart TV.jpg', 3)",
                    "INSERT INTO PRODUCTS (name, price, image_path, stock) VALUES ('Whirlpool Fridge', 799.99, 'images/Wirlpool fridge.jpg', 2)",
                    "INSERT INTO PRODUCTS (name, price, image_path, stock) VALUES ('Titan Watch', 199.99, 'images/Titan Watch.jpg', 12)",
                    "INSERT INTO PRODUCTS (name, price, image_path, stock) VALUES ('Recliner Sofa', 499.99, 'images/Recliner Sofa.jpg', 4)",
                    "INSERT INTO PRODUCTS (name, price, image_path, stock) VALUES ('Caspian Cupboard', 349.99, 'images/Caspian cupboard.jpg', 6)",
                    "INSERT INTO PRODUCTS (name, price, image_path, stock) VALUES ('Suitcase', 89.99, 'images/suitecase.jpg', 20)"
                };
                for (String product : products) {
                    stmt.executeUpdate(product);
                }
                System.out.println("Default products inserted!");
            }
            
            // Step 8: Insert test user
            String checkUsers = "SELECT COUNT(*) FROM USERS";
            rs = stmt.executeQuery(checkUsers);
            rs.next();
            int userCount = rs.getInt(1);
            
            if (userCount == 0) {
                System.out.println("Inserting test user...");
                String testUser = "INSERT INTO USERS (username, password, email) VALUES ('testuser', 'test123', 'test@gmail.com')";
                stmt.executeUpdate(testUser);
                System.out.println("Test user created! Username: testuser, Password: test123");
            }
            
            stmt.close();
            conn.close();
            System.out.println("\nDatabase setup completed successfully!");
            System.out.println("Database: " + DB_NAME);
            System.out.println("Tables: PRODUCTS, USERS, CART");
            
        } catch (SQLException e) {
            System.err.println("Database setup failed!");
            e.printStackTrace();
        }
    }
}
