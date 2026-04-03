import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AdminCredentialStore {
    private static final String ADMIN_FILE = "admin_credentials.txt";
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin123";

    private static Path adminPath() {
        return Paths.get(ADMIN_FILE).toAbsolutePath();
    }

    private static synchronized void ensureFile() throws IOException {
        Path path = adminPath();
        if (!Files.exists(path)) {
            Files.writeString(path, DEFAULT_USERNAME + "|" + DEFAULT_PASSWORD);
        }
    }

    private static synchronized String getAdminPassword() {
        try {
            ensureFile();
            String content = Files.readString(adminPath()).trim();
            if (content.isEmpty()) {
                return DEFAULT_PASSWORD;
            }

            String[] parts = content.split("\\|", 2);
            if (parts.length == 2 && DEFAULT_USERNAME.equalsIgnoreCase(parts[0].trim())) {
                return parts[1];
            }
        } catch (IOException ex) {
            System.out.println("Error reading admin credentials: " + ex.getMessage());
        }
        return DEFAULT_PASSWORD;
    }

    public static synchronized boolean verifyAdminPassword(String password) {
        return getAdminPassword().equals(password);
    }

    public static synchronized boolean updateAdminPassword(String newPassword) {
        try {
            ensureFile();
            Files.writeString(adminPath(), DEFAULT_USERNAME + "|" + newPassword);
            return true;
        } catch (IOException ex) {
            System.out.println("Error updating admin credentials: " + ex.getMessage());
            return false;
        }
    }
}
