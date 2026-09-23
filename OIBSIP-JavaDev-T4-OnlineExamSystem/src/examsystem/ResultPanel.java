package examsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Displays the outcome of a completed exam: score, time taken, and a
 * per-question breakdown of correct/incorrect answers. Offers a logout
 * button that returns to the login screen.
 */
public class ResultPanel extends JPanel {

    public ResultPanel(Main app, User user, ExamSession session) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Exam Result", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        add(buildCenterPanel(user, session), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> app.showLogin());
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel buildCenterPanel(User user, ExamSession session) {
        int score = session.getScore();
        int total = session.getTotal();
        long timeTaken = session.getTimeTakenSeconds();
        long mins = timeTaken / 60;
        long secs = timeTaken % 60;

        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));

        JLabel candidateLabel = new JLabel("Candidate: " + user.getDisplayName());
        candidateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryPanel.add(candidateLabel);

        JLabel scoreLabel = new JLabel(String.format("Score: %d out of %d", score, total));
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        double pct = total == 0 ? 0 : (100.0 * score / total);
        scoreLabel.setForeground(pct >= 50 ? new Color(0, 130, 0) : Color.RED);
        scoreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryPanel.add(Box.createVerticalStrut(6));
        summaryPanel.add(scoreLabel);

        JLabel timeLabel = new JLabel(String.format("Time Taken: %02d:%02d", mins, secs));
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryPanel.add(Box.createVerticalStrut(6));
        summaryPanel.add(timeLabel);

        if (session.isAutoSubmitted()) {
            JLabel autoLabel = new JLabel("This exam was auto-submitted because the time expired.");
            autoLabel.setForeground(Color.RED);
            autoLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
            autoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            summaryPanel.add(Box.createVerticalStrut(6));
            summaryPanel.add(autoLabel);
        }

        JTable table = buildBreakdownTable(session);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Answer Breakdown"));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(summaryPanel, BorderLayout.NORTH);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    private JTable buildBreakdownTable(ExamSession session) {
        String[] columns = {"#", "Question", "Your Answer", "Correct Answer", "Result"};
        List<Question> questions = session.getQuestions();
        int[] answers = session.getSelectedAnswers();

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String yourAnswer = answers[i] == -1 ? "Not answered" : q.getOptions()[answers[i]];
            String correctAnswer = q.getOptions()[q.getCorrectIndex()];
            String result = (answers[i] == q.getCorrectIndex()) ? "Correct" : "Incorrect";
            model.addRow(new Object[]{i + 1, q.getText(), yourAnswer, correctAnswer, result});
        }

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setPreferredWidth(30);
        table.getColumnModel().getColumn(1).setPreferredWidth(320);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                             boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                String result = String.valueOf(t.getModel().getValueAt(row, 4));
                if (!isSelected) {
                    c.setBackground("Correct".equals(result) ? new Color(224, 247, 224) : new Color(253, 224, 224));
                }
                return c;
            }
        });

        return table;
    }
}
