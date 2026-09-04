import java.util.ArrayList;

public class Account {

    private String accountId;
    private String userId;
    private String pin;
    private double balance;

    private ArrayList<Transaction> transactions;

    public Account(String accountId, String userId, String pin, double balance) {
        this.accountId = accountId;
        this.userId = userId;
        this.pin = pin;
        this.balance = balance;
        this.transactions = new ArrayList<>();
    }

    public String getAccountId() {
        return accountId;
    }

    public String getUserId() {
        return userId;
    }

    public double getBalance() {
        return balance;
    }

    public boolean verifyPin(String enteredPin) {
        return pin.equals(enteredPin);
    }

    public boolean hasSufficientBalance(double amount) {
        return balance >= amount;
    }

    public void withdraw(double amount) {
        balance = balance - amount;
    }

    public void deposit(double amount) {
        balance = balance + amount;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }
}