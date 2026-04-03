import java.nio.file.*;
import java.io.*;
import java.util.*;
public class UserStore {
    public static final String USERS_FILE = "users.txt";
    private static String getUsersFilePath() {
        return Paths.get(USERS_FILE).toAbsolutePath().toString();
    }
    public static class User {
        public int id;
        public String role;
        public String username;
        public String password;
        public String displayName;
        public String imagePath;
        public int totalItemsPurchased;
        public User(String role, String username, String password, String displayName, String imagePath) {
            this(0, role, username, password, displayName, imagePath, 0);
        }
        public User(String role, String username, String password, String displayName, String imagePath, int totalItemsPurchased) {
            this(0, role, username, password, displayName, imagePath, totalItemsPurchased);
        }
        public User(int id, String role, String username, String password, String displayName, String imagePath, int totalItemsPurchased) {
            this.id = id;
            this.role = role;
            this.username = username;
            this.password = password;
            this.displayName = displayName;
            this.imagePath = imagePath;
            this.totalItemsPurchased = totalItemsPurchased;
        }
    }
    private static Path usersPath() {
        return Paths.get(getUsersFilePath());
    }
    public static void ensureFile() throws IOException {
        Path p = usersPath();
        if (!Files.exists(p)) Files.createFile(p);
    }
    public static synchronized boolean addUser(User u) throws IOException {
        ensureFile();
        List<User> all = loadAll();
        for (User ex : all) {
            if (ex.role.equalsIgnoreCase(u.role) && ex.username.equalsIgnoreCase(u.username)) return false;
        }
        String line = String.join("|", u.role, u.username, u.password, u.displayName == null ? "" : u.displayName, u.imagePath == null ? "" : u.imagePath, String.valueOf(u.totalItemsPurchased));
        Files.write(usersPath(), Arrays.asList(line), StandardOpenOption.APPEND);
        return true;
    }
    public static synchronized List<User> loadAll() throws IOException {
        ensureFile();
        List<User> out = new ArrayList<>();
        List<String> lines = Files.readAllLines(usersPath());
        for (String ln : lines) {
            if (ln.trim().isEmpty()) continue;
            String[] parts = ln.split("\\|", -1);
            if (parts.length < 5) continue;
            int totalItems = 0;
            if (parts.length >= 6) {
                try {
                    totalItems = Integer.parseInt(parts[5]);
                } catch (NumberFormatException ex) {
                    totalItems = 0;
                }
            }
            out.add(new User(parts[0], parts[1], parts[2], parts[3], parts[4], totalItems));
        }
        return out;
    }
    public static synchronized User findUser(String role, String username, String password) throws IOException {
        List<User> all = loadAll();
        for (User u : all) {
            if (u.role.equalsIgnoreCase(role) && u.username.equalsIgnoreCase(username) && u.password.equals(password)) return u;
        }
        return null;
    }
    public static synchronized void updateUserTotalItems(String role, String username, int addQuantity) throws IOException {
        List<User> all = loadAll();
        boolean found = false;
        for (User u : all) {
            if (u.role.equalsIgnoreCase(role) && u.username.equalsIgnoreCase(username)) {
                u.totalItemsPurchased += addQuantity;
                found = true;
                break;
            }
        }
        if (found) {
            List<String> lines = new ArrayList<>();
            for (User u : all) {
                String line = String.join("|", u.role, u.username, u.password, u.displayName == null ? "" : u.displayName, u.imagePath == null ? "" : u.imagePath, String.valueOf(u.totalItemsPurchased));
                lines.add(line);
            }
            Files.write(usersPath(), lines, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
    public static synchronized boolean deleteUser(String role, String username) throws IOException {
        List<User> all = loadAll();
        boolean found = false;
        for (Iterator<User> it = all.iterator(); it.hasNext(); ) {
            User u = it.next();
            if (u.role.equalsIgnoreCase(role) && u.username.equalsIgnoreCase(username)) {
                it.remove();
                found = true;
                break;
            }
        }
        if (found) {
            List<String> lines = new ArrayList<>();
            for (User u : all) {
                String line = String.join("|", u.role, u.username, u.password, u.displayName == null ? "" : u.displayName, u.imagePath == null ? "" : u.imagePath, String.valueOf(u.totalItemsPurchased));
                lines.add(line);
            }
            Files.write(usersPath(), lines, StandardOpenOption.TRUNCATE_EXISTING);
        }
        return found;
    }
}
