package examsystem;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * The exam-taking screen. Shows one question at a time with 4 radio button
 * options, Previous/Next navigation, a live countdown timer that
 * auto-submits at zero, and a manual submit button with confirmation.
 */
public class ExamPanel extends JPanel {

    private static final int EXAM_DURATION_SECONDS = 30 * 60; // 30 minutes
    private static final int LOW_TIME_WARNING_SECONDS = 60;

    private final Main app;
    private final User user;
    private final List<Question> questions;
    private final int[] selectedAnswers;
    private final long startTimeMillis;

    private int currentIndex = 0;
    private int remainingSeconds = EXAM_DURATION_SECONDS;
    private javax.swing.Timer countdownTimer;

    private JLabel questionNumberLabel;
    private JLabel questionLabel;
    private JLabel timerLabel;
    private JLabel answeredCountLabel;
    private ButtonGroup optionsGroup;
    private JRadioButton[] optionButtons;
    private JButton prevButton;
    private JButton nextButton;

    public ExamPanel(Main app, User user) {
        this.app = app;
        this.user = user;
        this.questions = QuestionBank.getQuestions();
        this.selectedAnswers = new int[questions.size()];
        Arrays.fill(selectedAnswers, -1);
        this.startTimeMillis = System.currentTimeMillis();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        loadQuestion();
        startTimer();
    }

    private JPanel buildTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Candidate: " + user.getDisplayName());
        welcomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        timerLabel = new JLabel();
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        timerLabel.setForeground(new Color(0, 100, 0));
        topPanel.add(timerLabel, BorderLayout.EAST);

        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        return topPanel;
    }

    private JPanel buildCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        questionNumberLabel = new JLabel();
        questionNumberLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        questionNumberLabel.setForeground(new Color(70, 70, 70));
        questionNumberLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(questionNumberLabel);
        centerPanel.add(Box.createVerticalStrut(10));

        questionLabel = new JLabel();
        questionLabel.setFont(new Font("SansSerif", Font.PLAIN, 17));
        questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(questionLabel);
        centerPanel.add(Box.createVerticalStrut(20));

        optionsGroup = new ButtonGroup();
        optionButtons = new JRadioButton[4];
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            optionButtons[i].setFont(new Font("SansSerif", Font.PLAIN, 15));
            optionButtons[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            optionButtons[i].setFocusPainted(false);
            final int optionIndex = i;
            optionButtons[i].addActionListener(e -> {
                selectedAnswers[currentIndex] = optionIndex;
                updateAnsweredCount();
            });
            optionsGroup.add(optionButtons[i]);
            centerPanel.add(optionButtons[i]);
            centerPanel.add(Box.createVerticalStrut(10));
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(centerPanel, BorderLayout.CENTER);

        answeredCountLabel = new JLabel();
        answeredCountLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        answeredCountLabel.setForeground(Color.GRAY);
        answeredCountLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        wrapper.add(answeredCountLabel, BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel buildBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        prevButton = new JButton("Previous");
        nextButton = new JButton("Next");
        prevButton.addActionListener(e -> {
            if (currentIndex > 0) {
                currentIndex--;
                loadQuestion();
            }
        });
        nextButton.addActionListener(e -> {
            if (currentIndex < questions.size() - 1) {
                currentIndex++;
                loadQuestion();
            }
        });
        navPanel.add(prevButton);
        navPanel.add(nextButton);
        bottomPanel.add(navPanel, BorderLayout.WEST);

        JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton submitButton = new JButton("Submit Exam");
        submitButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        submitButton.addActionListener(e -> confirmManualSubmit());
        submitPanel.add(submitButton);
        bottomPanel.add(submitPanel, BorderLayout.EAST);

        return bottomPanel;
    }

    private void loadQuestion() {
        Question q = questions.get(currentIndex);
        questionNumberLabel.setText("Question " + (currentIndex + 1) + " of " + questions.size());
        questionLabel.setText("<html><body style='width:520px'>" + escape(q.getText()) + "</body></html>");

        optionsGroup.clearSelection();
        String[] opts = q.getOptions();
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText("<html><body style='width:480px'>" + escape(opts[i]) + "</body></html>");
        }
        if (selectedAnswers[currentIndex] != -1) {
            optionButtons[selectedAnswers[currentIndex]].setSelected(true);
        }

        prevButton.setEnabled(currentIndex > 0);
        nextButton.setEnabled(currentIndex < questions.size() - 1);
        updateAnsweredCount();
    }

    private void updateAnsweredCount() {
        int answered = 0;
        for (int a : selectedAnswers) {
            if (a != -1) answered++;
        }
        answeredCountLabel.setText(answered + " of " + questions.size() + " questions answered.");
    }

    private String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private void startTimer() {
        updateTimerLabel();
        countdownTimer = new javax.swing.Timer(1000, e -> {
            remainingSeconds--;
            updateTimerLabel();
            if (remainingSeconds <= 0) {
                countdownTimer.stop();
                autoSubmit();
            }
        });
        countdownTimer.start();
    }

    private void updateTimerLabel() {
        int m = remainingSeconds / 60;
        int s = remainingSeconds % 60;
        timerLabel.setText(String.format("Time Remaining: %02d:%02d", m, s));
        if (remainingSeconds <= LOW_TIME_WARNING_SECONDS) {
            timerLabel.setForeground(Color.RED);
        }
    }

    private void confirmManualSubmit() {
        int unanswered = 0;
        for (int a : selectedAnswers) {
            if (a == -1) unanswered++;
        }
        String message = "Are you sure you want to submit the exam?";
        if (unanswered > 0) {
            message = "You have " + unanswered + " unanswered question(s).\n" + message;
        }
        int choice = JOptionPane.showConfirmDialog(
                this, message, "Confirm Submission",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            finishExam(false);
        }
    }

    private void autoSubmit() {
        JOptionPane.showMessageDialog(
                this,
                "Time is up! Your exam is being submitted automatically.",
                "Time's Up",
                JOptionPane.WARNING_MESSAGE);
        finishExam(true);
    }

    private void finishExam(boolean autoSubmitted) {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        long endTimeMillis = System.currentTimeMillis();
        ExamSession session = new ExamSession(
                questions, selectedAnswers, startTimeMillis, endTimeMillis,
                EXAM_DURATION_SECONDS, autoSubmitted);
        app.showResult(user, session);
    }
}
