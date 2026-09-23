package examsystem;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory "database" of users. In a real application this would be
 * backed by a persistent store (file, database, etc.), but for this
 * demo application a static map is sufficient.
 */
public class UserStore {

    private static final Map<String, User> users = new HashMap<>();

    static {
        users.put("student1", new User("student1", "pass123", "Alice Johnson"));
        users.put("student2", new User("student2", "pass123", "Brian Lee"));
        users.put("admin", new User("admin", "admin123", "Admin User"));
    }

    /**
     * Attempts to authenticate a user. Returns the matching User object
     * on success, or null if the username/password combination is invalid.
     */
    public static User authenticate(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    /**
     * Persists changes made to a user (e.g. display name or password update).
     */
    public static void updateUser(User user) {
        users.put(user.getUsername(), user);
    }
}
