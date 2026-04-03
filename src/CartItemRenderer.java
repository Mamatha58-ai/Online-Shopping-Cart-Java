import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CartItemRenderer extends JPanel implements ListCellRenderer<UserDashboard.CartItem> {

    private JLabel nameLabel = new JLabel();
    private JLabel priceLabel = new JLabel();
    private JLabel totalLabel = new JLabel();
    private JLabel imageLabel = new JLabel();
    private JLabel quantityLabel = new JLabel();

    public CartItemRenderer() {
        setLayout(new BorderLayout(10, 5));
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        // Image on left
        imageLabel.setPreferredSize(new Dimension(80, 80));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(imageLabel, BorderLayout.WEST);

        // Text in center
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 12));
        priceLabel.setForeground(Color.RED);
        totalLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        totalLabel.setForeground(Color.BLUE);

        textPanel.add(nameLabel);
        textPanel.add(priceLabel);
        textPanel.add(totalLabel);

        add(textPanel, BorderLayout.CENTER);

        // Quantity display on right
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
        quantityLabel.setPreferredSize(new Dimension(40, 30));
        quantityLabel.setHorizontalAlignment(SwingConstants.CENTER);
        quantityLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        quantityPanel.add(new JLabel("Qty:"));
        quantityPanel.add(quantityLabel);
        
        add(quantityPanel, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends UserDashboard.CartItem> list, UserDashboard.CartItem value,
            int index, boolean isSelected, boolean cellHasFocus) {

        nameLabel.setText(value.p.name);
        priceLabel.setText("₹" + String.format("%.2f", value.p.price));
        totalLabel.setText("Total: ₹" + String.format("%.2f", value.p.price * value.q));
        
        // Quantity label
        quantityLabel.setText(String.valueOf(value.q));

        // Load image
        if (value.p.imagePath != null && !value.p.imagePath.isEmpty()) {
            File f = new File(value.p.imagePath);
            if (f.exists()) {
                try {
                    ImageIcon icon = new ImageIcon(value.p.imagePath);
                    Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(img));
                } catch (Exception ex) {
                    imageLabel.setText("No Image");
                    imageLabel.setIcon(null);
                }
            } else {
                imageLabel.setText("No Image");
                imageLabel.setIcon(null);
            }
        } else {
            imageLabel.setText("No Image");
            imageLabel.setIcon(null);
        }

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        setOpaque(true);
        return this;
    }
}
