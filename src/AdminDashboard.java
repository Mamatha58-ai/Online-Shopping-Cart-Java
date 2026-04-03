import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

import java.util.List;

import java.util.ArrayList;

public class AdminDashboard extends JFrame {

    private DefaultListModel<Product> productModel = new DefaultListModel<>();
    private DefaultListModel<Product> filteredModel = new DefaultListModel<>();
    private List<Product> allProducts = new ArrayList<>();
    private DefaultListModel<UserStore.User> userModel = new DefaultListModel<>();
    private List<UserStore.User> allUsers = new ArrayList<>();

    public AdminDashboard(UserStore.User user) {
        setTitle("Admin Dashboard");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header with Admin Info
        JPanel header = new JPanel(new BorderLayout(10, 10));
        header.setBackground(new Color(41, 128, 185));
        header.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel headerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        headerLeft.setBackground(new Color(41, 128, 185));
        headerLeft.add(createAvatarLabel(user));
        JLabel nameLbl = new JLabel(user.displayName != null && !user.displayName.isEmpty() ? user.displayName : user.username);
        nameLbl.setFont(new Font("Arial", Font.BOLD, 20));
        nameLbl.setForeground(Color.WHITE);
        headerLeft.add(nameLbl);
        
        JLabel adminTitle = new JLabel("Admin Dashboard");
        adminTitle.setFont(new Font("Arial", Font.BOLD, 18));
        adminTitle.setForeground(new Color(52, 152, 219));
        
        header.add(headerLeft, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Load products from database
        try {
            List<String> productStrings = DatabaseProductStore.getAllProducts();
            for (String productStr : productStrings) {
                // Parse the string: "ID: 1 | Name: Laptop | Price: 799.99 | Stock: 10"
                String[] parts = productStr.split("\\|");
                if (parts.length >= 5) {
                    int id = Integer.parseInt(parts[0].replace("ID:", "").trim());
                    String name = parts[1].replace("Name:", "").trim();
                    double price = Double.parseDouble(parts[2].replace("Price:", "").trim());
                    int stock = Integer.parseInt(parts[3].replace("Stock:", "").trim());
                    String imagePath = parts.length > 4 ? parts[4].replace("Image:", "").trim() : "";
                    Product p = new Product(id, name, price, imagePath, stock);
                    allProducts.add(p);
                    productModel.addElement(p);
                    filteredModel.addElement(p);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + ex.getMessage());
        }

        // Load users from database (was using file-based UserStore)
        try {
            List<String> userStrings = DatabaseProductStore.getAllUsers();
            for (String ustr : userStrings) {
                String[] parts = ustr.split("\\|", -1);
                if (parts.length >= 3) {
                    int id = Integer.parseInt(parts[0].replace("ID:", "").trim());
                    String username = parts[1].replace("Username:", "").trim();
                    String email = parts[2].replace("Email:", "").trim();
                    // use email as displayName for now
                    UserStore.User u = new UserStore.User(id, "User", username, "", email, "", 0);
                    allUsers.add(u);
                    userModel.addElement(u);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage());
        }

        JList<Product> productList = new JList<>(filteredModel);
        productList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productList.setCellRenderer(new ProductCellRenderer());
        productList.setFixedCellHeight(120);

        JPanel listPanel = new JPanel(new BorderLayout(5,5));
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel productsTitle = new JLabel("Product Inventory");
        productsTitle.setFont(new Font("Arial", Font.BOLD, 16));
        
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        JTextField searchField = new JTextField();
        searchField.setToolTipText("Search products by name...");
        JButton clearBtn = new JButton("Clear");
        clearBtn.setPreferredSize(new Dimension(80, 25));
        searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(clearBtn, BorderLayout.EAST);
        
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(productsTitle, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        
        listPanel.add(topPanel, BorderLayout.NORTH);
        listPanel.add(new JScrollPane(productList), BorderLayout.CENTER);

        // Create styled buttons for products
        JButton viewBtn = createStyledButton("View Product", new Color(52, 152, 219));
        JButton addBtn = createStyledButton("Add Product", new Color(46, 204, 113));
        JButton updateStockBtn = createStyledButton("Update Stock", new Color(241, 196, 15));
        JButton deleteBtn = createStyledButton("Delete Product", new Color(231, 76, 60));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2, 12, 12));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        buttonPanel.add(viewBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(updateStockBtn);
        buttonPanel.add(deleteBtn);

        listPanel.add(buttonPanel, BorderLayout.SOUTH);

        // User management panel
        JPanel userPanel = new JPanel(new BorderLayout(5,5));
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel usersTitle = new JLabel("User Management");
        usersTitle.setFont(new Font("Arial", Font.BOLD, 16));
        
        JList<UserStore.User> userList = new JList<>(userModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setCellRenderer(new UserCellRenderer());
        userList.setFixedCellHeight(50);
        
        userPanel.add(usersTitle, BorderLayout.NORTH);
        userPanel.add(new JScrollPane(userList), BorderLayout.CENTER);

        // Create styled buttons for users
        JButton viewUserBtn = createStyledButton("View User", new Color(52, 152, 219));
        JButton deleteUserBtn = createStyledButton("Delete User", new Color(231, 76, 60));

        JPanel userButtonPanel = new JPanel();
        userButtonPanel.setLayout(new GridLayout(2, 1, 12, 12));
        userButtonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        userButtonPanel.setBackground(new Color(240, 240, 240));
        userButtonPanel.add(viewUserBtn);
        userButtonPanel.add(deleteUserBtn);

        userPanel.add(userButtonPanel, BorderLayout.SOUTH);

        // Logout button in header
        JButton logoutBtn = createStyledButton("Logout", new Color(149, 165, 166));
        headerLeft.add(logoutBtn);

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Products", listPanel);
        tabbedPane.addTab("Users", userPanel);

        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);

        // Button actions
        viewBtn.addActionListener(e -> {
            Product sel = productList.getSelectedValue();
            if (sel != null) {
                String msg = "Product: " + sel.name
                    + "\nPrice: Rs. " + String.format("%.2f", sel.price)
                    + "\nStock: " + sel.stock
                    + "\nImage: " + sel.imagePath;
                JOptionPane.showMessageDialog(this, msg);
            } else {
                JOptionPane.showMessageDialog(this, "Select a product.");
            }
        });

        addBtn.addActionListener(e -> openAddProductDialog());

        updateStockBtn.addActionListener(e -> {
            Product sel = productList.getSelectedValue();
            if (sel != null) {
                String newStock = JOptionPane.showInputDialog(this, "Enter new stock quantity:", sel.stock);
                if (newStock != null && !newStock.isEmpty()) {
                    try {
                        int stock = Integer.parseInt(newStock);
                        DatabaseProductStore.updateProduct(sel.name, sel.price, stock);
                        sel.stock = stock;
                        filteredModel.set(productList.getSelectedIndex(), sel);
                        JOptionPane.showMessageDialog(this, "Stock updated successfully.");
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid number.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a product.");
            }
        });

        deleteBtn.addActionListener(e -> {
            Product sel = productList.getSelectedValue();
            if (sel != null) {
                int confirm = JOptionPane.showConfirmDialog(this, "Delete " + sel.name + "?");
                if (confirm == JOptionPane.YES_OPTION) {
                    DatabaseProductStore.deleteProduct(sel.name);
                    productModel.removeElement(sel);
                    filteredModel.removeElement(sel);
                    allProducts.remove(sel);
                    JOptionPane.showMessageDialog(this, "Product deleted successfully.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a product.");
            }
        });

        logoutBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        // User button actions
        viewUserBtn.addActionListener(e -> {
            UserStore.User sel = userList.getSelectedValue();
            if (sel != null) {
                String msg = "Username: " + sel.username
                    + "\nEmail: " + sel.displayName
                    + "\nRole: " + sel.role
                    + "\nTotal Items Purchased: " + sel.totalItemsPurchased;
                JOptionPane.showMessageDialog(this, msg);
            } else {
                JOptionPane.showMessageDialog(this, "Select a user.");
            }
        });

        deleteUserBtn.addActionListener(e -> {
            UserStore.User sel = userList.getSelectedValue();
            if (sel != null) {
                int confirm = JOptionPane.showConfirmDialog(this, "Delete user " + sel.username + "?");
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        // remove from database
                        DatabaseProductStore.deleteUser(sel.username);
                        userModel.removeElement(sel);
                        allUsers.remove(sel);
                        JOptionPane.showMessageDialog(this, "User deleted successfully.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a user.");
            }
        });

        // Search functionality
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterProducts(searchField.getText().toLowerCase());
            }
        });

        clearBtn.addActionListener(e -> {
            searchField.setText("");
            filterProducts("");
        });
    }

    private void filterProducts(String query) {
        filteredModel.clear();
        if (query.isEmpty()) {
            for (Product p : allProducts) {
                filteredModel.addElement(p);
            }
        } else {
            for (Product p : allProducts) {
                if (p.name.toLowerCase().contains(query)) {
                    filteredModel.addElement(p);
                }
            }
        }
    }

    private void openAddProductDialog() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField imageField = new JTextField("images\\");
        JTextField stockField = new JTextField();

        panel.add(new JLabel("Product Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Image Path:"));
        panel.add(imageField);
        panel.add(new JLabel("Stock:"));
        panel.add(stockField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                String imagePath = imageField.getText().trim();
                int stock = Integer.parseInt(stockField.getText().trim());

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Product name is required.");
                    return;
                }

                // Save to database
                DatabaseProductStore.addProduct(name, price, imagePath, stock);
                
                // Add to GUI list
                Product p = new Product(name, price, imagePath, stock);
                productModel.addElement(p);
                filteredModel.addElement(p);
                allProducts.add(p);
                JOptionPane.showMessageDialog(this, "Product added successfully.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price or stock.");
            }
        }
    }
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(bgColor, 1));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 50));
        
        // keep styling simple and predictable; no hover effects
        
        return btn;
    }
    private JLabel createAvatarLabel(UserStore.User user) {
        if (user.imagePath != null && !user.imagePath.trim().isEmpty()) {
            File f = new File(user.imagePath);
            if (f.exists()) {
                try {
                    ImageIcon ico = new ImageIcon(user.imagePath);
                    Image img = ico.getImage().getScaledInstance(64,64,Image.SCALE_SMOOTH);
                    JLabel lbl = new JLabel(new ImageIcon(img));
                    lbl.setPreferredSize(new Dimension(64,64));
                    return lbl;
                } catch (Exception ex) {
                    // fallback
                }
            }
        }
        String initials = "?";
        if (user.displayName != null && !user.displayName.trim().isEmpty()) {
            String[] parts = user.displayName.trim().split("\\s+");
            initials = "" + parts[0].charAt(0);
            if (parts.length > 1) initials += parts[1].charAt(0);
        } else if (user.username != null && !user.username.isEmpty()) initials = "" + user.username.charAt(0);

        JLabel lbl = new JLabel(initials, SwingConstants.CENTER);
        lbl.setPreferredSize(new Dimension(64,64));
        lbl.setOpaque(true);
        lbl.setBackground(Color.LIGHT_GRAY);
        lbl.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        lbl.setFont(new Font("Arial", Font.BOLD, 20));
        return lbl;
    }
}

class UserCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof UserStore.User) {
            UserStore.User user = (UserStore.User) value;
            setText(user.username + " (" + user.role + ") - " + user.displayName);
        }
        return this;
    }
}
