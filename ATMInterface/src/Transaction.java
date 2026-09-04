import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {

    private String type;
    private double amount;
    private String details;
    private LocalDateTime dateTime;

    public Transaction(String type, double amount, String details) {
        this.type = type;
        this.amount = amount;
        this.details = details;
        this.dateTime = LocalDateTime.now();
    }

    @Override
    public String toString() {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        return type + " | ₹" + String.format("%.2f", amount)
                + " | " + details
                + " | " + dateTime.format(formatter);
    }
}