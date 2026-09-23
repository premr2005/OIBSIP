package reservation;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.Arrays;

final class LoginFrame extends JFrame {

    private static final int MAX_ATTEMPTS = 3;
    private static final int LOCK_SECONDS = 10;

    private final JTextField userField = new JTextField(16);
    private final JPasswordField passField = new JPasswordField(16);
    private final JButton loginBtn = new JButton("Login");
    private int failures = 0;

    LoginFrame() {
        super("Online Reservation System - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel title = new JLabel("Online Reservation System", JLabel.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setBorder(BorderFactory.createEmptyBorder(16, 16, 4, 16));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(8, 16, 16, 16));
        Ui.addRow(form, 0, "Username", userField);
        Ui.addRow(form, 1, "Password", passField);

        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx = 0;
        bc.gridy = 2;
        bc.gridwidth = 2;
        bc.insets = new Insets(12, 8, 0, 8);
        form.add(loginBtn, bc);

        add(title, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);

        getRootPane().setDefaultButton(loginBtn); // Enter key submits
        loginBtn.addActionListener(e -> attemptLogin());

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void attemptLogin() {
        String user = userField.getText().trim();
        char[] pass = passField.getPassword();
        try {
            if (user.isEmpty() || pass.length == 0) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password.",
                        "Missing details", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (Db.authenticate(user, pass)) {
                dispose();
                new MainFrame(user).setVisible(true);
                return;
            }
            failures++;
            passField.setText("");
            JOptionPane.showMessageDialog(this, "Access denied: invalid username or password.",
                    "Login failed", JOptionPane.ERROR_MESSAGE);
            if (failures >= MAX_ATTEMPTS) lockTemporarily();
        } catch (SQLException ex) {
            Ui.showDbError(this, ex);
        } finally {
            Arrays.fill(pass, '\0'); // don't leave the password in memory
        }
    }

    private void lockTemporarily() {
        failures = 0;
        loginBtn.setEnabled(false);
        javax.swing.Timer unlock = new javax.swing.Timer(LOCK_SECONDS * 1000, e -> loginBtn.setEnabled(true));
        unlock.setRepeats(false);
        unlock.start();
        JOptionPane.showMessageDialog(this,
                "Too many failed attempts. Login is disabled for " + LOCK_SECONDS + " seconds.",
                "Locked", JOptionPane.WARNING_MESSAGE);
    }
}
