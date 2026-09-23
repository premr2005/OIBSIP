package reservation;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.sql.SQLException;

public final class Main {

    private Main() {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            useNimbus();
            try {
                Db.init(); // creates reservation.db, tables, the admin user and sample trains
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        "Could not open the database.\n" + ex.getMessage()
                                + "\n\nIs the sqlite-jdbc driver on the classpath?",
                        "Startup error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            new LoginFrame().setVisible(true);
        });
    }

    private static void useNimbus() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
        } catch (Exception ignored) {
            // fall back to the default look and feel
        }
    }
}
