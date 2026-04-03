import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ProductCellRenderer extends JPanel implements ListCellRenderer<Product> {

    private JLabel nameLabel = new JLabel();
    private JLabel priceLabel = new JLabel();
    private JLabel stockLabel = new JLabel();
    private JLabel imageLabel = new JLabel();

    public ProductCellRenderer() {
        setLayout(new BorderLayout(10, 5));
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        // Image on left
        imageLabel.setPreferredSize(new Dimension(100, 100));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(imageLabel, BorderLayout.WEST);

        // Text on right
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 12));
        priceLabel.setForeground(Color.RED);
        stockLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        stockLabel.setForeground(Color.GRAY);

        textPanel.add(nameLabel);
        textPanel.add(priceLabel);
        textPanel.add(stockLabel);

        add(textPanel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Product> list, Product value,
            int index, boolean isSelected, boolean cellHasFocus) {

        nameLabel.setText(value.name);
        priceLabel.setText("₹" + String.format("%.2f", value.price));
        // Show only "In Stock" or "Out of Stock"
        stockLabel.setText(value.stock > 0 ? "In Stock" : "Out of Stock");
        stockLabel.setForeground(value.stock > 0 ? Color.GREEN : Color.RED);

        // Load image
        if (value.imagePath != null && !value.imagePath.isEmpty()) {
            File f = new File(value.imagePath);
            if (f.exists()) {
                try {
                    ImageIcon icon = new ImageIcon(value.imagePath);
                    Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
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
