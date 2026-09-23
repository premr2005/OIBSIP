package examsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Application entry point. Hosts a CardLayout that switches between the
 * Login, Profile Update, Exam, and Result screens, and owns the window
 * close confirmation logic.
 */
public class Main extends JFrame {

    public static final String LOGIN = "LOGIN";
    public static final String PROFILE = "PROFILE";
    public static final String EXAM = "EXAM";
    public static final String RESULT = "RESULT";

    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    private JPanel loginPanel;
    private JPanel profilePanel;
    private JPanel examPanel;
    private JPanel resultPanel;

    private boolean examInProgress = false;

    public Main() {
        setTitle("Online Examination System");
        setSize(820, 620);
        setMinimumSize(new Dimension(700, 500));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        add(cardPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleClose();
            }
        });

        showLogin();
    }

    private void handleClose() {
        String message = examInProgress
                ? "An exam is currently in progress. If you quit now, your progress will be lost.\n"
                  + "Are you sure you want to quit?"
                : "Are you sure you want to quit?";
        int choice = JOptionPane.showConfirmDialog(
                this, message, "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                examInProgress ? JOptionPane.WARNING_MESSAGE : JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /** Swaps out the panel currently registered under {@code name} for a new one. */
    private void swapCard(JPanel oldPanel, JPanel newPanel, String name) {
        if (oldPanel != null) {
            cardPanel.remove(oldPanel);
        }
        cardPanel.add(newPanel, name);
        cardLayout.show(cardPanel, name);
        revalidate();
        repaint();
    }

    public void showLogin() {
        examInProgress = false;
        JPanel newLoginPanel = new LoginPanel(this);
        swapCard(loginPanel, newLoginPanel, LOGIN);
        loginPanel = newLoginPanel;
    }

    public void onLoginSuccess(User user) {
        JPanel newProfilePanel = new ProfileUpdatePanel(this, user);
        swapCard(profilePanel, newProfilePanel, PROFILE);
        profilePanel = newProfilePanel;
    }

    public void startExam(User user) {
        JPanel newExamPanel = new ExamPanel(this, user);
        swapCard(examPanel, newExamPanel, EXAM);
        examPanel = newExamPanel;
        examInProgress = true;
    }

    public void showResult(User user, ExamSession session) {
        examInProgress = false;
        JPanel newResultPanel = new ResultPanel(this, user, session);
        swapCard(resultPanel, newResultPanel, RESULT);
        resultPanel = newResultPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Fall back to default look and feel.
            }
            new Main().setVisible(true);
        });
    }
}
