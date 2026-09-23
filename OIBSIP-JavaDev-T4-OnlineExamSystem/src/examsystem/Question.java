package examsystem;

/**
 * Represents a single multiple-choice question with exactly four options.
 */
public class Question {
    private final String text;
    private final String[] options;
    private final int correctIndex;

    public Question(String text, String[] options, int correctIndex) {
        if (options.length != 4) {
            throw new IllegalArgumentException("Each question must have exactly 4 options.");
        }
        this.text = text;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    public String getText() {
        return text;
    }

    public String[] getOptions() {
        return options;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }
}
