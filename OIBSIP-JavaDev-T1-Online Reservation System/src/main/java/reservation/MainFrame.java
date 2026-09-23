package reservation;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

/** Shown after a successful login: one tab for booking, one for cancelling. */
final class MainFrame extends JFrame {

    MainFrame(String username) {
        super("Online Reservation System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(8, 12, 4, 12));
        header.add(new JLabel("Logged in as: " + username), BorderLayout.WEST);
        header.add(logout, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Book Ticket", new ReservationPanel(username));
        tabs.addTab("Cancel Ticket", new CancellationPanel());

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        setSize(600, 500);
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }
}
