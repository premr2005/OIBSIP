package reservation;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** "Book Ticket" tab. */
final class ReservationPanel extends JPanel {

    private static final String[] CLASSES = {
            "Sleeper (SL)", "AC 3 Tier (3A)", "AC 2 Tier (2A)",
            "AC First Class (1A)", "Chair Car (CC)", "Second Sitting (2S)"
    };

    private final String username;
    private final JTextField nameField = new JTextField(20);
    private final JTextField trainNoField = new JTextField(20);
    private final JTextField trainNameField = new JTextField(20);
    private final JComboBox<String> classBox = new JComboBox<>(CLASSES);
    private final JTextField dateField = new JTextField(20);
    private final JTextField fromField = new JTextField(20);
    private final JTextField toField = new JTextField(20);

    ReservationPanel(String username) {
        this.username = username;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        trainNameField.setEditable(false); // auto-populated from the train number
        dateField.setToolTipText("Format: " + Validation.DATE_HINT);

        Ui.addRow(this, 0, "Passenger name *", nameField);
        Ui.addRow(this, 1, "Train number *", trainNoField);
        Ui.addRow(this, 2, "Train name", trainNameField);
        Ui.addRow(this, 3, "Class type *", classBox);
        Ui.addRow(this, 4, "Date of journey * (" + Validation.DATE_HINT + ")", dateField);
        Ui.addRow(this, 5, "Source station *", fromField);
        Ui.addRow(this, 6, "Destination station *", toField);

        JButton bookBtn = new JButton("Book Ticket");
        JButton clearBtn = new JButton("Clear");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.add(clearBtn);
        buttons.add(bookBtn);

        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx = 0;
        bc.gridy = 7;
        bc.gridwidth = 2;
        bc.anchor = GridBagConstraints.EAST;
        bc.insets = new Insets(14, 8, 0, 8);
        add(buttons, bc);

        GridBagConstraints filler = new GridBagConstraints(); // pushes everything to the top
        filler.gridy = 8;
        filler.weighty = 1;
        add(new JPanel(), filler);

        Ui.onChange(trainNoField, this::refreshTrainName);
        bookBtn.addActionListener(e -> book());
        clearBtn.addActionListener(e -> clearForm());
    }

    /** Fills the read-only train name box as the user types the train number. */
    private void refreshTrainName() {
        String t = trainNoField.getText().trim();
        if (t.isEmpty()) {
            trainNameField.setText("");
        } else if (!Validation.isNumeric(t)) {
            trainNameField.setText("Train number must be numeric");
        } else {
            try {
                trainNameField.setText(Db.findTrainName(Integer.parseInt(t)).orElse("Train not found"));
            } catch (SQLException ex) {
                trainNameField.setText("Lookup failed");
            }
        }
    }

    private void book() {
        List<String> errors = new ArrayList<>();

        String name = nameField.getText().trim();
        String trainNoText = trainNoField.getText().trim();
        String dateText = dateField.getText().trim();
        String from = fromField.getText().trim();
        String to = toField.getText().trim();

        if (Validation.isBlank(name)) {
            errors.add("Passenger name is required.");
        } else if (!Validation.isValidName(name)) {
            errors.add("Passenger name may contain only letters, spaces, . ' and - (max 50).");
        }

        int trainNo = 0;
        String trainName = null;
        if (Validation.isBlank(trainNoText)) {
            errors.add("Train number is required.");
        } else if (!Validation.isNumeric(trainNoText)) {
            errors.add("Train number must be numeric.");
        } else {
            trainNo = Integer.parseInt(trainNoText);
            try {
                trainName = Db.findTrainName(trainNo).orElse(null);
            } catch (SQLException ex) {
                Ui.showDbError(this, ex);
                return;
            }
            if (trainName == null) errors.add("Train number " + trainNo + " does not exist.");
        }

        LocalDate date = null;
        if (Validation.isBlank(dateText)) {
            errors.add("Date of journey is required.");
        } else {
            date = Validation.parseDate(dateText);
            if (date == null) {
                errors.add("Date of journey must be a real date in " + Validation.DATE_HINT + " format.");
            } else if (date.isBefore(LocalDate.now())) {
                errors.add("Date of journey cannot be in the past.");
            }
        }

        if (Validation.isBlank(from)) errors.add("Source station is required.");
        if (Validation.isBlank(to)) errors.add("Destination station is required.");
        if (!from.isEmpty() && from.equalsIgnoreCase(to)) {
            errors.add("Source and destination must be different.");
        }

        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(this, String.join("\n", errors),
                    "Please fix the following", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Reservation draft = new Reservation(null, name, trainNo, trainName,
                (String) classBox.getSelectedItem(), date, from, to);
        try {
            Reservation saved = Db.book(draft, username);
            JOptionPane.showMessageDialog(this,
                    Ui.detailsArea("Booking confirmed!\n\n" + saved.toDisplayText()
                            + "\n\nKeep your PNR safe - you need it to cancel."),
                    "Booking Confirmation", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } catch (SQLException ex) {
            Ui.showDbError(this, ex);
        }
    }

    private void clearForm() {
        nameField.setText("");
        trainNoField.setText(""); // also clears the train name via the listener
        classBox.setSelectedIndex(0);
        dateField.setText("");
        fromField.setText("");
        toField.setText("");
        nameField.requestFocusInWindow();
    }
}
