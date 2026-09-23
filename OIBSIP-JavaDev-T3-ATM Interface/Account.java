import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a single bank account. Encapsulates balance, PIN, and
 * transaction history behind private fields with controlled access.
 */
public class Account {

    private final String accountId;
    private final String holderName;
    private String pin;
    private double balance;
    private final List<Transaction> transactionHistory;

    public Account(String accountId, String holderName, String pin, double initialBalance) {
        this.accountId = accountId;
        this.holderName = holderName;
        this.pin = pin;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
    }

    public String getAccountId() {
        return accountId;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    public boolean validatePin(String enteredPin) {
        return pin.equals(enteredPin);
    }

    public void setPin(String newPin) {
        this.pin = newPin;
    }

    /** Read-only view of this account's transaction history. */
    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    public boolean hasSufficientFunds(double amount) {
        return balance >= amount;
    }

    /**
     * Adds funds to the account and logs the transaction.
     */
    public void deposit(double amount, String description) {
        balance += amount;
        transactionHistory.add(new Transaction(Transaction.Type.DEPOSIT, amount, balance, description));
    }

    /**
     * Removes funds from the account and logs the transaction.
     * Caller is responsible for checking hasSufficientFunds() first.
     */
    public void withdraw(double amount, String description) {
        balance -= amount;
        transactionHistory.add(new Transaction(Transaction.Type.WITHDRAWAL, amount, balance, description));
    }

    /** Logs an outgoing transfer without re-deducting balance handling logic (kept explicit for clarity). */
    public void recordTransferOut(double amount, String description) {
        balance -= amount;
        transactionHistory.add(new Transaction(Transaction.Type.TRANSFER_OUT, amount, balance, description));
    }

    /** Logs an incoming transfer. */
    public void recordTransferIn(double amount, String description) {
        balance += amount;
        transactionHistory.add(new Transaction(Transaction.Type.TRANSFER_IN, amount, balance, description));
    }

    @Override
    public String toString() {
        return String.format("Account[%s] Holder: %s, Balance: $%.2f", accountId, holderName, balance);
    }
}
