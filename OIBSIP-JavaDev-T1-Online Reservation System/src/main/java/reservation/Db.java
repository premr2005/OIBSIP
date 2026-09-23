package reservation;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Optional;

/**
 * All database access. Every query uses PreparedStatement so user input is never
 * concatenated into SQL (prevents SQL injection).
 */
public final class Db {

    private static final String URL = "jdbc:sqlite:reservation.db"; // created in the working directory
    private static final SecureRandom RNG = new SecureRandom();

    private Db() {}

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // ---------------------------------------------------------------- setup

    public static void init() throws SQLException {
        try (Connection c = connect(); Statement st = c.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "username TEXT PRIMARY KEY, salt TEXT NOT NULL, password_hash TEXT NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS trains ("
                    + "train_no INTEGER PRIMARY KEY, train_name TEXT NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS reservations ("
                    + "pnr TEXT PRIMARY KEY, passenger_name TEXT NOT NULL, train_no INTEGER NOT NULL, "
                    + "train_name TEXT NOT NULL, class_type TEXT NOT NULL, journey_date TEXT NOT NULL, "
                    + "source TEXT NOT NULL, destination TEXT NOT NULL, booked_by TEXT NOT NULL, "
                    + "booked_on TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            seedUsers(c);
            seedTrains(c);
        }
    }

    private static void seedUsers(Connection c) throws SQLException {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next() && rs.getInt(1) > 0) return;
        }
        addUser(c, "admin", "admin123".toCharArray());
    }

    private static void addUser(Connection c, String username, char[] password) throws SQLException {
        byte[] salt = new byte[16];
        RNG.nextBytes(salt);
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO users (username, salt, password_hash) VALUES (?, ?, ?)")) {
            ps.setString(1, username);
            ps.setString(2, Base64.getEncoder().encodeToString(salt));
            ps.setString(3, hash(password, salt));
            ps.executeUpdate();
        }
    }

    private static void seedTrains(Connection c) throws SQLException {
        Object[][] trains = {
                {12951, "Mumbai Rajdhani Express"},
                {12301, "Howrah Rajdhani Express"},
                {12627, "Karnataka Express"},
                {12009, "Shatabdi Express"},
                {12723, "Telangana Express"},
                {11039, "Maharashtra Express"},
                {12859, "Gitanjali Express"},
                {12137, "Punjab Mail"},
        };
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT OR IGNORE INTO trains (train_no, train_name) VALUES (?, ?)")) {
            for (Object[] t : trains) {
                ps.setInt(1, (Integer) t[0]);
                ps.setString(2, (String) t[1]);
                ps.executeUpdate();
            }
        }
    }

    // ----------------------------------------------------------------- login

    public static boolean authenticate(String username, char[] password) throws SQLException {
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT salt, password_hash FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                byte[] salt = Base64.getDecoder().decode(rs.getString(1));
                byte[] expected = rs.getString(2).getBytes(StandardCharsets.UTF_8);
                byte[] actual = hash(password, salt).getBytes(StandardCharsets.UTF_8);
                return MessageDigest.isEqual(expected, actual); // constant-time compare
            }
        }
    }

    private static String hash(char[] password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, 65_536, 256);
            byte[] out = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(out);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Password hashing unavailable", e);
        }
    }

    // ---------------------------------------------------------------- trains

    public static Optional<String> findTrainName(int trainNo) throws SQLException {
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement("SELECT train_name FROM trains WHERE train_no = ?")) {
            ps.setInt(1, trainNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(rs.getString(1)) : Optional.empty();
            }
        }
    }

    // ---------------------------------------------------------- reservations

    /** Saves the booking, generating a unique 10-digit PNR, and returns the saved copy. */
    public static Reservation book(Reservation r, String bookedBy) throws SQLException {
        try (Connection c = connect()) {
            String pnr = nextUniquePnr(c);
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO reservations (pnr, passenger_name, train_no, train_name, class_type, "
                            + "journey_date, source, destination, booked_by) VALUES (?,?,?,?,?,?,?,?,?)")) {
                ps.setString(1, pnr);
                ps.setString(2, r.passengerName());
                ps.setInt(3, r.trainNo());
                ps.setString(4, r.trainName());
                ps.setString(5, r.classType());
                ps.setString(6, r.journeyDate().toString()); // stored as ISO yyyy-MM-dd
                ps.setString(7, r.source());
                ps.setString(8, r.destination());
                ps.setString(9, bookedBy);
                ps.executeUpdate();
            }
            return r.withPnr(pnr);
        }
    }

    private static String nextUniquePnr(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM reservations WHERE pnr = ?")) {
            for (int i = 0; i < 20; i++) {
                String pnr = Long.toString(RNG.nextLong(1_000_000_000L, 10_000_000_000L));
                ps.setString(1, pnr);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return pnr;
                }
            }
        }
        throw new SQLException("Could not generate a unique PNR");
    }

    public static Optional<Reservation> findByPnr(String pnr) throws SQLException {
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM reservations WHERE pnr = ?")) {
            ps.setString(1, pnr);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new Reservation(
                        rs.getString("pnr"),
                        rs.getString("passenger_name"),
                        rs.getInt("train_no"),
                        rs.getString("train_name"),
                        rs.getString("class_type"),
                        LocalDate.parse(rs.getString("journey_date")),
                        rs.getString("source"),
                        rs.getString("destination")));
            }
        }
    }

    /** @return true if a booking was deleted, false if the PNR no longer exists */
    public static boolean cancel(String pnr) throws SQLException {
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement("DELETE FROM reservations WHERE pnr = ?")) {
            ps.setString(1, pnr);
            return ps.executeUpdate() > 0;
        }
    }
}
