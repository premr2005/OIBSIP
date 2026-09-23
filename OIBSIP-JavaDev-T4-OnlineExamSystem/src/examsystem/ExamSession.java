package examsystem;

import java.util.List;

/**
 * Captures the outcome of a completed exam attempt: the questions asked,
 * the answers selected, and timing information, so the result screen can
 * compute a score and breakdown.
 */
public class ExamSession {
    private final List<Question> questions;
    private final int[] selectedAnswers; // -1 means unanswered
    private final long startTimeMillis;
    private final long endTimeMillis;
    private final int durationSeconds;
    private final boolean autoSubmitted;

    public ExamSession(List<Question> questions, int[] selectedAnswers, long startTimeMillis,
                        long endTimeMillis, int durationSeconds, boolean autoSubmitted) {
        this.questions = questions;
        this.selectedAnswers = selectedAnswers;
        this.startTimeMillis = startTimeMillis;
        this.endTimeMillis = endTimeMillis;
        this.durationSeconds = durationSeconds;
        this.autoSubmitted = autoSubmitted;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public int[] getSelectedAnswers() {
        return selectedAnswers;
    }

    public boolean isAutoSubmitted() {
        return autoSubmitted;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public int getScore() {
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (selectedAnswers[i] == questions.get(i).getCorrectIndex()) {
                score++;
            }
        }
        return score;
    }

    public int getTotal() {
        return questions.size();
    }

    public long getTimeTakenSeconds() {
        return (endTimeMillis - startTimeMillis) / 1000;
    }
}
