import javax.swing.*;
import java.awt.*;
// import java.awt.event.*;
//import java.io.IOException;

public class LoginFrame extends JFrame { 

    public LoginFrame() {
        setTitle("Online Shopping Cart - Login");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10));

        JLabel title = new JLabel("Welcome", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JButton loginBtn = new JButton("Sign In");
        JButton forgotBtn = new JButton("Forgot Password");
        JButton signupBtn = new JButton("Create Account");

        panel.add(title);
        panel.add(new JLabel(""));
        panel.add(loginBtn);
        panel.add(forgotBtn);
        panel.add(signupBtn);

        add(panel);

        // Button actions
        loginBtn.addActionListener(e -> openLoginDialog());
        forgotBtn.addActionListener(e -> openForgotPasswordDialog());
        signupBtn.addActionListener(e -> {
            new SignUpFrame();
            dispose();
        });

        setVisible(true);
    }

    private void openLoginDialog() {
        String[] options = {"Admin", "User"};
        int role = JOptionPane.showOptionDialog(
                this,
                "Login as:",
                "Select Role",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (role == JOptionPane.CLOSED_OPTION) return;
        String roleStr = role == 0 ? "Admin" : "User";

        JPanel creds = new JPanel(new GridLayout(2,2,5,5));
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        creds.add(new JLabel("Username:"));
        creds.add(userField);
        creds.add(new JLabel("Password:"));
        creds.add(passField);

        int res = JOptionPane.showConfirmDialog(this, creds, roleStr + " Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());

            System.out.println("Login attempt role=" + roleStr + " user=" + user);
            
            // Check admin credentials
            if ("Admin".equalsIgnoreCase(roleStr) && "admin".equalsIgnoreCase(user)
                    && AdminCredentialStore.verifyAdminPassword(pass)) {
                UserStore.User admin = new UserStore.User("Admin", "admin", pass, "Administrator", "");
                new AdminDashboard(admin);
                dispose();
                return;
            }

            // Check database for users
            if ("User".equalsIgnoreCase(roleStr)) {
                if (!DatabaseProductStore.isDatabaseAvailable()) {
                    JOptionPane.showMessageDialog(this,
                            "Database connection failed.\nMake sure MySQL is running and credentials in DatabaseProductStore are correct.");
                    return;
                }

                int userId = DatabaseProductStore.verifyUserAndGetId(user, pass);
                if (userId != -1) {
                    UserStore.User found = new UserStore.User(userId, "User", user, pass, user, "", 0);
                    new UserDashboard(found);
                    dispose();
                    return;
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password for User login.");
                    return;
                }
            }

            JOptionPane.showMessageDialog(this, "Invalid credentials.");
        }
    }

    private void openForgotPasswordDialog() {
        String[] options = {"Admin", "User"};
        int role = JOptionPane.showOptionDialog(
                this,
                "Reset password for:",
                "Forgot Password",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (role == JOptionPane.CLOSED_OPTION) return;

        if (role == 0) {
            resetAdminPassword();
        } else {
            resetUserPassword();
        }
    }

    private void resetAdminPassword() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField userField = new JTextField();
        JPasswordField newPassField = new JPasswordField();
        JPasswordField confirmPassField = new JPasswordField();

        panel.add(new JLabel("Admin Username:"));
        panel.add(userField);
        panel.add(new JLabel("New Password:"));
        panel.add(newPassField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPassField);

        int res = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Reset Admin Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (res != JOptionPane.OK_OPTION) return;

        String username = userField.getText().trim();
        String newPassword = new String(newPassField.getPassword());
        String confirmPassword = new String(confirmPassField.getPassword());

        if (!"admin".equalsIgnoreCase(username)) {
            JOptionPane.showMessageDialog(this, "Admin username not found.");
            return;
        }

        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "New password is required.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }

        if (AdminCredentialStore.updateAdminPassword(newPassword)) {
            JOptionPane.showMessageDialog(this, "Admin password updated successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Unable to update admin password.");
        }
    }

    private void resetUserPassword() {
        if (!DatabaseProductStore.isDatabaseAvailable()) {
            JOptionPane.showMessageDialog(this,
                    "Database connection failed.\nMake sure MySQL is running and credentials in DatabaseProductStore are correct.");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField userField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField newPassField = new JPasswordField();
        JPasswordField confirmPassField = new JPasswordField();

        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("New Password:"));
        panel.add(newPassField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPassField);

        int res = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Reset User Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (res != JOptionPane.OK_OPTION) return;

        String username = userField.getText().trim();
        String email = emailField.getText().trim();
        String newPassword = new String(newPassField.getPassword());
        String confirmPassword = new String(confirmPassField.getPassword());

        if (username.isEmpty() || email.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username, email and new password are required.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }

        boolean updated = DatabaseProductStore.resetUserPassword(username, email, newPassword);
        if (updated) {
            JOptionPane.showMessageDialog(this, "User password updated successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "No matching user found for the provided username and email.");
        }
    }
}
