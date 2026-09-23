import java.util.List;
import java.util.Scanner;

/**
 * Represents the ATM machine itself. Handles the login flow and,
 * once a user is authenticated, drives the main transaction menu.
 */
public class ATM {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    private final Bank bank;
    private final Scanner scanner;

    public ATM(Bank bank, Scanner scanner) {
        this.bank = bank;
        this.scanner = scanner;
    }

    /**
     * Runs the full startup + login flow.
     * Returns the authenticated Account, or null if the user ran out of attempts.
     */
    public Account login() {
        System.out.println("========================================");
        System.out.println(" Welcome to " + bank.getName());
        System.out.println("========================================");

        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {
            System.out.print("Enter User ID: ");
            String userId = scanner.nextLine().trim();
            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();

            Account account = bank.authenticate(userId, pin);
            if (account != null) {
                System.out.println("\nLogin successful. Welcome, " + account.getHolderName() + "!\n");
                return account;
            } else {
                int remaining = MAX_LOGIN_ATTEMPTS - attempt;
                if (remaining > 0) {
                    System.out.println("Incorrect User ID or PIN. Attempts remaining: " + remaining + "\n");
                } else {
                    System.out.println("\nToo many incorrect attempts. Access denied.");
                }
            }
        }
        return null;
    }

    /**
     * Runs the main menu loop for a logged-in account until the user quits.
     */
    public void runSession(Account account) {
        boolean running = true;
        while (running) {
            printMenu(account);
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    showTransactionHistory(account);
                    break;
                case "2":
                    handleWithdraw(account);
                    break;
                case "3":
                    handleDeposit(account);
                    break;
                case "4":
                    handleTransfer(account);
                    break;
                case "5":
                    System.out.println("\nThank you for banking with " + bank.getName() + ". Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1-5.\n");
            }
        }
    }

    private void printMenu(Account account) {
        System.out.println("----------------------------------------");
        System.out.printf("Account: %s   Balance: $%.2f%n", account.getAccountId(), account.getBalance());
        System.out.println("1. Transaction History");
        System.out.println("2. Withdraw");
        System.out.println("3. Deposit");
        System.out.println("4. Transfer");
        System.out.println("5. Quit");
        System.out.print("Choose an option: ");
    }

    private void showTransactionHistory(Account account) {
        List<Transaction> history = account.getTransactionHistory();
        System.out.println("\n--- Transaction History ---");
        if (history.isEmpty()) {
            System.out.println("No transactions yet this session.");
        } else {
            for (Transaction t : history) {
                System.out.println(t);
            }
        }
        System.out.println();
    }

    private void handleWithdraw(Account account) {
        double amount = readAmount("Enter amount to withdraw: ");
        if (amount <= 0) {
            System.out.println("Amount must be positive.\n");
            return;
        }
        if (!account.hasSufficientFunds(amount)) {
            System.out.println("Insufficient Funds\n");
            return;
        }
        account.withdraw(amount, "ATM withdrawal");
        System.out.printf("Withdrawal successful. New balance: $%.2f%n%n", account.getBalance());
    }

    private void handleDeposit(Account account) {
        double amount = readAmount("Enter amount to deposit: ");
        if (amount <= 0) {
            System.out.println("Amount must be positive.\n");
            return;
        }
        account.deposit(amount, "ATM deposit");
        System.out.printf("Deposit successful. New balance: $%.2f%n%n", account.getBalance());
    }

    private void handleTransfer(Account account) {
        System.out.print("Enter recipient account ID: ");
        String recipientId = scanner.nextLine().trim();

        if (recipientId.equals(account.getAccountId())) {
            System.out.println("Cannot transfer to your own account.\n");
            return;
        }
        if (!bank.accountExists(recipientId)) {
            System.out.println("Recipient account not found.\n");
            return;
        }

        double amount = readAmount("Enter amount to transfer: ");
        if (amount <= 0) {
            System.out.println("Amount must be positive.\n");
            return;
        }
        if (!account.hasSufficientFunds(amount)) {
            System.out.println("Insufficient Funds\n");
            return;
        }

        boolean success = bank.transfer(account, recipientId, amount);
        if (success) {
            System.out.printf("Transfer successful. New balance: $%.2f%n%n", account.getBalance());
        } else {
            System.out.println("Transfer failed. Please try again.\n");
        }
    }

    private double readAmount(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered.");
            return -1;
        }
    }
}
