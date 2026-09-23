package examsystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Supplies the set of questions used for the exam. Replace this with a
 * file/database-backed loader if you need configurable question sets.
 */
public class QuestionBank {

    public static List<Question> getQuestions() {
        List<Question> list = new ArrayList<>();

        list.add(new Question(
                "Which keyword is used to inherit a class in Java?",
                new String[]{"implements", "extends", "inherits", "super"}, 1));

        list.add(new Question(
                "Which of these is NOT a primitive data type in Java?",
                new String[]{"int", "boolean", "String", "double"}, 2));

        list.add(new Question(
                "What does JVM stand for?",
                new String[]{"Java Virtual Machine", "Java Verified Method",
                        "Java Variable Module", "Java Visual Machine"}, 0));

        list.add(new Question(
                "Which collection class allows duplicate elements and maintains insertion order?",
                new String[]{"HashSet", "TreeSet", "ArrayList", "HashMap"}, 2));

        list.add(new Question(
                "Which layout manager arranges components like a deck of cards, showing one at a time?",
                new String[]{"BorderLayout", "CardLayout", "GridLayout", "FlowLayout"}, 1));

        list.add(new Question(
                "What is the default value of a boolean instance variable in Java?",
                new String[]{"true", "false", "0", "null"}, 1));

        list.add(new Question(
                "Which component group ensures only one radio button can be selected at a time?",
                new String[]{"ButtonGroup", "ActionGroup", "RadioGroup", "SelectionGroup"}, 0));

        list.add(new Question(
                "Which class is used to schedule repeated events (like a countdown) in Swing?",
                new String[]{"java.util.Timer", "javax.swing.Timer", "TimerTask", "ScheduledExecutor"}, 1));

        list.add(new Question(
                "What is the capital of France?",
                new String[]{"Berlin", "Madrid", "Paris", "Rome"}, 2));

        list.add(new Question(
                "Which operator is used to compare two values for equality in Java?",
                new String[]{"=", "==", "!=", "==="}, 1));

        return list;
    }
}
