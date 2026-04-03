import javax.swing.*;
import java.awt.*;
// import java.awt.event.*;
// import java.io.IOException;

public class SignUpFrame extends JFrame {

    public SignUpFrame() {
        setTitle("Create Account");
        setSize(420, 480);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(10, 1, 8, 8));

        panel.add(new JLabel("Display Name (optional):"));
        JTextField nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Username (login ID):"));
        JTextField userField = new JTextField();
        panel.add(userField);

        panel.add(new JLabel("Email address:") );
        JTextField emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Password:"));
        JPasswordField pwdField = new JPasswordField();
        panel.add(pwdField);

        panel.add(new JLabel("Address (Mandatory):"));
        JTextField addrField = new JTextField();
        panel.add(addrField);

        // panel.add(new JLabel("Image Path (optional):"));
        // JTextField imgField = new JTextField();
        // panel.add(imgField);

        JButton createBtn = new JButton("Create Account");
        panel.add(createBtn);

        add(panel);
        setVisible(true);

        createBtn.addActionListener(e -> {
            if (addrField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Address is mandatory.");
                return;
            }
            String displayName = nameField.getText().trim();
            String username = userField.getText().trim();
            String email = emailField.getText().trim();
            String pwd = new String(pwdField.getPassword());

            if (username.isEmpty() || email.isEmpty() || pwd.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username, email and password are required.");
                return;
            }

            // Save to database (username, password, email)
            boolean success = DatabaseProductStore.addUser(username, pwd, email);
            if (success) {
                JOptionPane.showMessageDialog(this, "Account created successfully.");
                new LoginFrame();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error creating account. Please try again.");
            }
        });
    }
}
