package examsystem;

import javax.swing.*;
import java.awt.*;

/**
 * Login screen: prompts for a username and password. On success, hands
 * control to the profile update screen.
 */
public class LoginPanel extends JPanel {

    public LoginPanel(Main app) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel title = new JLabel("Online Examination System");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(title, gbc);

        gbc.gridwidth = 1;

        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(userLabel, gbc);

        JTextField userField = new JTextField(16);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(userField, gbc);

        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(16);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(passField, gbc);

        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(statusLabel, gbc);

        JButton loginButton = new JButton("Login");
        gbc.gridy = 4;
        add(loginButton, gbc);

        JLabel hint = new JLabel("<html><i>Demo accounts: student1 / pass123, student2 / pass123</i></html>");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        gbc.gridy = 5;
        add(hint, gbc);

        Runnable attemptLogin = () -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please enter both username and password.");
                return;
            }
            User user = UserStore.authenticate(username, password);
            if (user != null) {
                app.onLoginSuccess(user);
            } else {
                statusLabel.setText("Invalid username or password.");
                passField.setText("");
            }
        };

        loginButton.addActionListener(e -> attemptLogin.run());
        passField.addActionListener(e -> attemptLogin.run());
    }
}
