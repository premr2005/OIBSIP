package examsystem;

import javax.swing.*;
import java.awt.*;

/**
 * Allows the logged-in user to update their display name and/or password
 * before starting the exam. Both changes are optional; the user can also
 * proceed directly to the exam without changing anything.
 */
public class ProfileUpdatePanel extends JPanel {

    public ProfileUpdatePanel(Main app, User user) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel title = new JLabel("Update Your Profile");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(title, gbc);

        JLabel subtitle = new JLabel("<html><i>Optional &mdash; update your details or just start the exam.</i></html>");
        gbc.gridy = 1;
        add(subtitle, gbc);

        gbc.gridwidth = 1;

        JLabel nameLabel = new JLabel("Display Name:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(nameLabel, gbc);

        JTextField nameField = new JTextField(user.getDisplayName(), 16);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(nameField, gbc);

        JLabel newPassLabel = new JLabel("New Password:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        add(newPassLabel, gbc);

        JPasswordField newPassField = new JPasswordField(16);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        add(newPassField, gbc);

        JLabel confirmPassLabel = new JLabel("Confirm Password:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        add(confirmPassLabel, gbc);

        JPasswordField confirmPassField = new JPasswordField(16);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        add(confirmPassField, gbc);

        JLabel statusLabel = new JLabel(" ");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(statusLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save Changes");
        JButton startButton = new JButton("Start Exam");
        buttonPanel.add(saveButton);
        buttonPanel.add(startButton);
        gbc.gridy = 6;
        add(buttonPanel, gbc);

        saveButton.addActionListener(e -> {
            String newName = nameField.getText().trim();
            String newPass = new String(newPassField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());

            if (newName.isEmpty()) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Display name cannot be empty.");
                return;
            }
            if (!newPass.isEmpty() || !confirmPass.isEmpty()) {
                if (!newPass.equals(confirmPass)) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Passwords do not match.");
                    return;
                }
                user.setPassword(newPass);
            }
            user.setDisplayName(newName);
            UserStore.updateUser(user);

            statusLabel.setForeground(new Color(0, 128, 0));
            statusLabel.setText("Profile updated successfully.");
            newPassField.setText("");
            confirmPassField.setText("");
        });

        startButton.addActionListener(e -> {
            // Ensure a display name change typed but not saved still takes effect.
            String newName = nameField.getText().trim();
            if (!newName.isEmpty()) {
                user.setDisplayName(newName);
                UserStore.updateUser(user);
            }
            app.startExam(user);
        });
    }
}
