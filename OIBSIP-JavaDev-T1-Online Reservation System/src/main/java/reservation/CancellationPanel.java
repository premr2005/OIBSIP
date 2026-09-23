package reservation;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.Optional;

/** "Cancel Ticket" tab: fetch a booking by PNR, then confirm and delete it. */
final class CancellationPanel extends JPanel {

    private static final String PROMPT = "Enter a PNR and click Fetch to see the booking.";

    private final JTextField pnrField = new JTextField(14);
    private final JTextArea details = Ui.detailsArea(PROMPT);
    private final JButton cancelBtn = new JButton("Cancel Booking");
    private Reservation current; // the booking currently shown, or null

    CancellationPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JButton fetchBtn = new JButton("Fetch");
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.add(new JLabel("PNR number:"));
        top.add(pnrField);
        top.add(fetchBtn);

        details.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Booking details"),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));

        cancelBtn.setEnabled(false); // only usable after a successful Fetch
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottom.add(cancelBtn);

        add(top, BorderLayout.NORTH);
        add(details, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        fetchBtn.addActionListener(e -> fetch());
        pnrField.addActionListener(e -> fetch());
        cancelBtn.addActionListener(e -> cancel());
        // Editing the PNR after a fetch invalidates what is on screen, so a stale booking can't be cancelled.
        Ui.onChange(pnrField, this::resetView);
    }

    private void fetch() {
        String pnr = pnrField.getText().trim();
        if (Validation.isBlank(pnr)) {
            warn("PNR number is required.");
            return;
        }
        if (!Validation.isValidPnr(pnr)) {
            warn("PNR must be exactly 10 digits.");
            return;
        }
        try {
            Optional<Reservation> found = Db.findByPnr(pnr);
            if (found.isEmpty()) {
                resetView();
                warn("No booking found for PNR " + pnr + ".");
                return;
            }
            current = found.get();
            details.setText(current.toDisplayText());
            cancelBtn.setEnabled(true);
        } catch (SQLException ex) {
            Ui.showDbError(this, ex);
        }
    }

    private void cancel() {
        if (current == null) return;
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this booking?\n\n"
                        + "PNR: " + current.pnr() + "\n"
                        + "Passenger: " + current.passengerName() + "\n"
                        + "Train: " + current.trainNo() + " - " + current.trainName(),
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;

        try {
            boolean removed = Db.cancel(current.pnr());
            JOptionPane.showMessageDialog(this,
                    removed ? "Booking " + current.pnr() + " has been cancelled."
                            : "That booking no longer exists.",
                    "Cancellation", JOptionPane.INFORMATION_MESSAGE);
            pnrField.setText(""); // triggers resetView()
        } catch (SQLException ex) {
            Ui.showDbError(this, ex);
        }
    }

    private void resetView() {
        current = null;
        cancelBtn.setEnabled(false);
        details.setText(PROMPT);
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Cancellation", JOptionPane.WARNING_MESSAGE);
    }
}
