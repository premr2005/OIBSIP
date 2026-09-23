import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single banking transaction (deposit, withdrawal, or transfer).
 * Immutable record of what happened, when, and the resulting balance. it means that records means saves the proces in it.
 */
public class Transaction {

    public enum Type {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER_OUT,
        TRANSFER_IN
    }

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Type type;
    private final double amount;
    private final double balanceAfter;
    private final LocalDateTime timestamp;
    private final String description;

    public Transaction(Type type, double amount, double balanceAfter, String description) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public Type getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-14s $%-10.2f %-30s Balance: $%.2f",
                timestamp.format(FORMATTER),
                type,
                amount,
                description,
                balanceAfter);
    }
}
