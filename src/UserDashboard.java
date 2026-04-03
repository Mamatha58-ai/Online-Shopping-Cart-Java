import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class UserDashboard extends JFrame {
    // Inner static class representing an item in the shopping cart
    static class CartItem {
        Product p; int q;                    // product and quantity
        CartItem(Product p, int q) { this.p = p; this.q = q; }
        // String representation shown in cart list
        public String toString() { 
            return p.name + " x" + q + " - Rs. " + String.format("%.2f", p.price * q);
        }
    }

    // Models used for product lists
    private DefaultListModel<Product> pm = new DefaultListModel<>(), fm = new DefaultListModel<>();
    private DefaultListModel<CartItem> cm = new DefaultListModel<>();
    private java.util.List<Product> ap = new ArrayList<>();      // all products
    // Labels for totals and item count
    private JLabel tl = new JLabel("Total: Rs. 0.00"), dl = new JLabel("Discount: Rs. 0.00"),
             fl = new JLabel("Final: Rs. 0.00"), il = new JLabel("Items: 0");
    private double dp = 0.0; private String ac = "";                // discount percent and applied code
    private JSpinner qs = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
    private UserStore.User u;                                     // current user
    private JList<Product> pl; private JList<CartItem> cl;         // product list & cart list
    private JTextField sf, cf;                                    // search field & coupon field
    private JButton ab, rb, cb, clb, mb, pb, acb, scb;            // various buttons

    public UserDashboard(UserStore.User user) {
        u = user;
        setTitle("User Dashboard - Browse & Cart"); setSize(900, 600);
        setLocationRelativeTo(null); setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        loadData();        // populate product & cart data
        createUI();        // build UI components
        setupListeners();  // wire up event handlers
        setVisible(true);
    }

    // Load product list and current cart from the "database" store
    private void loadData() {
        try {
            for (String s : DatabaseProductStore.getAllProducts()) {
                String[] p = s.split("\\|");
                if (p.length >= 5) {
                    Product prod = new Product(
                        Integer.parseInt(p[0].replace("ID:","").trim()),
                        p[1].replace("Name:","").trim(),
                        Double.parseDouble(p[2].replace("Price:","").trim()),
                        p.length > 4 ? p[4].replace("Image:","").trim() : "",
                        Integer.parseInt(p[3].replace("Stock:","").trim()));
                    ap.add(prod); pm.addElement(prod); fm.addElement(prod);
                }
            }
            // load cart items for current user
            for (String s : DatabaseProductStore.getCart(u.id)) {
                String[] c = s.split("\\|");
                if (c.length >= 4) {
                    String n = c[0].replace("Name:","").trim();
                    Product prod = ap.stream().filter(p -> p.name.equals(n)).findFirst().orElse(null);
                    if (prod != null) cm.addElement(new CartItem(prod,
                            Integer.parseInt(c[3].replace("Quantity:","").trim())));
                }
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    // Build the graphical interface
    private void createUI() {
        add(createHeader(), BorderLayout.NORTH);
        if (ap.isEmpty()) JOptionPane.showMessageDialog(this, "No products");

        pl = new JList<>(fm); pl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pl.setCellRenderer(new ProductCellRenderer()); pl.setFixedCellHeight(120);
        cl = new JList<>(cm); cl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cl.setCellRenderer(new CartItemRenderer()); cl.setFixedCellHeight(80);

        // instantiate buttons and fields
        ab = new JButton("Add"); rb = new JButton("Remove"); cb = new JButton("Checkout");
        clb = new JButton("Clear"); mb = new JButton("-"); pb = new JButton("+"); acb = new JButton("Apply");
        sf = new JTextField(); sf.setToolTipText("Search..."); scb = new JButton("Clear");
        cf = new JTextField(15); cf.setToolTipText("Coupon");

        // left pane: search + product list + qty controls
        JPanel lp = new JPanel(new BorderLayout(5,5));
        JPanel sp = new JPanel(new BorderLayout());
        sp.add(new JLabel("Search:"), BorderLayout.WEST);
        sp.add(sf, BorderLayout.CENTER); sp.add(scb, BorderLayout.EAST); lp.add(sp, BorderLayout.NORTH);
        lp.add(new JScrollPane(pl), BorderLayout.CENTER);
        JPanel qp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        qp.add(new JLabel("Qty:")); qp.add(mb); qp.add(qs); qp.add(pb); qp.add(ab);
        lp.add(qp, BorderLayout.SOUTH);

        // right pane: cart and controls
        JPanel rp = new JPanel(new BorderLayout(5,5)); rp.add(new JLabel("Cart"), BorderLayout.NORTH);
        rp.add(new JScrollPane(cl), BorderLayout.CENTER);
        JPanel cp = new JPanel(new GridLayout(6,2,5,5));
        cp.add(rb); cp.add(clb);
        cp.add(new JLabel("Coupon:")); cp.add(cf); cp.add(acb); cp.add(new JLabel(""));
        cp.add(tl); cp.add(dl); cp.add(fl); cp.add(cb);
        rp.add(cp, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, lp, rp);
        split.setDividerLocation(450); add(split, BorderLayout.CENTER);
    }

    // Create header with avatar, name, item count, logout button
    private JPanel createHeader() {
        JPanel h = new JPanel(new BorderLayout(10,10));
        JPanel l = new JPanel(new FlowLayout(FlowLayout.LEFT));
        l.add(createAvatar(u));
        l.add(new JLabel(u.displayName != null && !u.displayName.isEmpty() ? u.displayName : u.username));
        il.setText("Items: " + u.totalItemsPurchased); il.setForeground(Color.BLUE);
        JButton lb = new JButton("Logout");
        lb.setBackground(new Color(231,76,60)); lb.setForeground(Color.WHITE);
        lb.addActionListener(e -> { new LoginFrame(); dispose(); });
        JPanel r = new JPanel(new BorderLayout()); r.add(il, BorderLayout.NORTH); r.add(lb, BorderLayout.SOUTH);
        h.add(l, BorderLayout.WEST); h.add(r, BorderLayout.EAST);
        return h;
    }

    // Wire up event listeners for all controls
    private void setupListeners() {
        // double-click cart item to edit quantity
        cl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int i = cl.locationToIndex(e.getPoint());
                    if (i >= 0) {
                        CartItem item = cm.get(i);
                        String q = JOptionPane.showInputDialog("Qty for " + item.p.name + ":", item.q);
                        if (q != null && !q.trim().isEmpty()) {
                            try { int nq = Integer.parseInt(q);
                                if (nq > 0) { item.q = nq; cm.set(i, item); updateTotals(); }
                            } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(null, "Invalid"); }
                        }
                    }
                }
            }
        });

        // add selected product to cart
        ab.addActionListener(e -> {
            Product s = pl.getSelectedValue();
            if (s != null) {
                if (s.stock <= 0) { JOptionPane.showMessageDialog(this, "Out of stock"); return; }
                int q = (Integer) qs.getValue();
                if (q <= 0) { JOptionPane.showMessageDialog(this, "Invalid qty"); return; }
                CartItem ex = null;
                for (int i = 0; i < cm.size(); i++) {
                    if (cm.get(i).p.name.equals(s.name)) {
                        ex = cm.get(i); ex.q += q; cm.set(i, ex); break;
                    }
                }
                if (ex == null) cm.addElement(new CartItem(s, q));
                DatabaseProductStore.addToCart(u.id, s.id, q);
                qs.setValue(1); updateTotals();
            } else JOptionPane.showMessageDialog(this, "Select product");
        });

        // quantity spinner increment/decrement buttons
        mb.addActionListener(e -> { int v = (Integer) qs.getValue(); if (v > 1) qs.setValue(v - 1); });
        pb.addActionListener(e -> qs.setValue((Integer) qs.getValue() + 1));

        // remove item or clear cart
        rb.addActionListener(e -> {
            CartItem s = cl.getSelectedValue();
            if (s != null) {
                cm.removeElement(s);
                DatabaseProductStore.removeFromCart(u.id, s.p.id);
                updateTotals();
            } else JOptionPane.showMessageDialog(this, "Select item");
        });
        clb.addActionListener(e -> { cm.clear(); DatabaseProductStore.clearCart(u.id); updateTotals(); });

        // checkout logic: validate stock, update totals, clear cart
        cb.addActionListener(e -> {
            if (cm.isEmpty()) { JOptionPane.showMessageDialog(this, "Cart empty"); return; }
            int tq = 0;
            for (int i = 0; i < cm.size(); i++) {
                CartItem item = cm.get(i); tq += item.q;
                if (item.q > item.p.stock) {
                    JOptionPane.showMessageDialog(this, "Not enough stock for " + item.p.name); return;
                }
            }
            double tot = getTotal() * (1 - dp);
            try {
                // decrement stock in database
                for (int i = 0; i < cm.size(); i++) {
                    CartItem item = cm.get(i);
                    DatabaseProductStore.updateProduct(item.p.name, item.p.price, item.p.stock - item.q);
                }
                // clear user's cart in database
                DatabaseProductStore.clearCart(u.id);
                // update total items (file store left as-is or could be migrated)
                UserStore.updateUserTotalItems(u.role, u.username, tq);
                u.totalItemsPurchased += tq;
                il.setText("Items: " + u.totalItemsPurchased);
                // refresh product list from database
                ap.clear(); fm.clear();
                for (String s : DatabaseProductStore.getAllProducts()) {
                    String[] p = s.split("\\|");
                    if (p.length >= 5) {
                        Product prod = new Product(
                            Integer.parseInt(p[0].replace("ID:", "").trim()),
                            p[1].replace("Name:", "").trim(),
                            Double.parseDouble(p[2].replace("Price:", "").trim()),
                            p.length > 4 ? p[4].replace("Image:", "").trim() : "",
                            Integer.parseInt(p[3].replace("Stock:", "").trim()));
                        ap.add(prod); fm.addElement(prod);
                    }
                }
                cm.clear(); dp = 0; ac = ""; cf.setText(""); updateTotals();
                JOptionPane.showMessageDialog(this, "Order placed! Rs. " + String.format("%.2f", tot));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // apply coupon code
        acb.addActionListener(e -> {
            String c = cf.getText().trim().toUpperCase();
            if (c.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter code"); return; }
            if (applyCoupon(c)) JOptionPane.showMessageDialog(this, "Applied!");
            else {
                JOptionPane.showMessageDialog(this, "Invalid");
                dp = 0; ac = ""; cf.setText("");
            }
            updateTotals();
        });

        // search field filtering
        sf.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterProducts(sf.getText().toLowerCase());
            }
        });
        scb.addActionListener(e -> { sf.setText(""); filterProducts(""); });
    }

    // show only products matching query
    private void filterProducts(String q) {
        fm.clear();
        for (Product p : ap)
            if (p.name.toLowerCase().contains(q))
                fm.addElement(p);
    }

    // refresh total/discount/final labels
    private void updateTotals() {
        double s = getTotal(), d = s * dp, f = s - d;
        tl.setText("Total: Rs. " + String.format("%.2f", s));
        dl.setText("Discount: -Rs. " + String.format("%.2f", d));
        fl.setText("Final: Rs. " + String.format("%.2f", f));
    }

    // compute sum of cart line items
    private double getTotal() {
        double sum = 0;
        for (int i = 0; i < cm.size(); i++)
            sum += cm.get(i).p.price * cm.get(i).q;
        return sum;
    }

    // simple coupon logic setting discount percent
    private boolean applyCoupon(String c) {
        switch (c) {
            case "SAVE10": dp = 0.10; ac = c; return true;
            case "SAVE20": dp = 0.20; ac = c; return true;
            case "WELCOME": dp = 0.05; ac = c; return true;
            case "FESTIVAL50": dp = 0.50; ac = c; return true;
            default: return false;
        }
    }

    // build avatar label: image if available otherwise initials
    private JLabel createAvatar(UserStore.User user) {
        if (user.imagePath != null && !user.imagePath.trim().isEmpty()) {
            File f = new File(user.imagePath);
            if (f.exists()) {
                try {
                    ImageIcon i = new ImageIcon(user.imagePath);
                    Image img = i.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                    JLabel lbl = new JLabel(new ImageIcon(img));
                    lbl.setPreferredSize(new Dimension(64, 64));
                    return lbl;
                } catch (Exception ex) {}
            }
        }
        String in = user.username != null && !user.username.isEmpty() ? "" + user.username.charAt(0) : "?";
        if (user.displayName != null && !user.displayName.trim().isEmpty()) {
            String[] parts = user.displayName.trim().split("\\s+");
            in = "" + parts[0].charAt(0);
            if (parts.length > 1) in += parts[1].charAt(0);
        }
        JLabel lbl = new JLabel(in, SwingConstants.CENTER);
        lbl.setPreferredSize(new Dimension(64, 64));
        lbl.setOpaque(true); lbl.setBackground(Color.LIGHT_GRAY);
        lbl.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        lbl.setFont(new Font("Arial", Font.BOLD, 20));
        return lbl;
    }
}
