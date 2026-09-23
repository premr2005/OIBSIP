package reservation;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.sql.SQLException;

/** Tiny helpers so the form classes stay readable. */
final class Ui {

    private Ui() {}

    /** Adds "label | field" as one row of a GridBagLayout panel. */
    static void addRow(JPanel p, int row, String label, JComponent field) {
        GridBagConstraints l = new GridBagConstraints();
        l.gridx = 0;
        l.gridy = row;
        l.anchor = GridBagConstraints.WEST;
        l.insets = new Insets(6, 8, 6, 8);
        p.add(new JLabel(label), l);

        GridBagConstraints f = new GridBagConstraints();
        f.gridx = 1;
        f.gridy = row;
        f.weightx = 1;
        f.fill = GridBagConstraints.HORIZONTAL;
        f.insets = new Insets(6, 8, 6, 8);
        p.add(field, f);
    }

    /** Read-only, monospaced text block (keeps the aligned columns of toDisplayText()). */
    static JTextArea detailsArea(String text) {
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        return area;
    }

    /** Runs the callback whenever the text of the component changes. */
    static void onChange(JTextComponent c, Runnable r) {
        c.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { r.run(); }
            @Override public void removeUpdate(DocumentEvent e) { r.run(); }
            @Override public void changedUpdate(DocumentEvent e) { r.run(); }
        });
    }

    static void showDbError(Component parent, SQLException ex) {
        JOptionPane.showMessageDialog(parent, "Database error:\n" + ex.getMessage(),
                "Database error", JOptionPane.ERROR_MESSAGE);
    }
}
