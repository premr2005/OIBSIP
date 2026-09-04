import java.util.Scanner;

public class ATM {

    private Bank bank;
    private Scanner scanner;

    public ATM(Bank bank) {
        this.bank = bank;
        this.scanner = new Scanner(System.in);
    }

    public void start() {

        Account account = login();

        if (account == null) {
            System.out.println("\nAccess Denied.");
            System.out.println("Too many incorrect attempts.");
            return;
        }

        System.out.println("\nLogin Successful!");
        System.out.println("Welcome, " + account.getUserId() + "!");

        showMenu(account);
    }

    private Account login() {

        for (int attempt = 1; attempt <= 3; attempt++) {

            System.out.println("\n================================");
            System.out.println("       SECUREBANK ATM");
            System.out.println("================================");

            System.out.print("Enter User ID: ");
            String userId = scanner.nextLine();

            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine();

            Account account = bank.findAccountByUserId(userId);

            if (account != null && account.verifyPin(pin)) {
                return account;
            }

            System.out.println("Incorrect User ID or PIN.");
            System.out.println("Attempts remaining: " + (3 - attempt));
        }

        return null;
    }

    private void showMenu(Account account) {

        boolean running = true;

        while (running) {

            System.out.println("\n================================");
            System.out.println("          SECUREBANK ATM");
            System.out.println("================================");
            System.out.println("Account: " + account.getAccountId());
            System.out.printf("Balance: ₹%.2f%n", account.getBalance());
            System.out.println("--------------------------------");
            System.out.println("1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Quit");
            System.out.println("================================");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    showHistory(account);
                    break;

                case "2":
                    withdraw(account);
                    break;

                case "3":
                    deposit(account);
                    break;

                case "4":
                    transfer(account);
                    break;

                case "5":
                    System.out.println("\nThank you for using SecureBank ATM.");
                    System.out.println("Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void showHistory(Account account) {

        System.out.println("\n========== TRANSACTION HISTORY ==========");

        if (account.getTransactions().isEmpty()) {

            System.out.println("No transactions yet.");

        } else {

            for (Transaction transaction : account.getTransactions()) {
                System.out.println(transaction);
            }
        }
    }

    private void withdraw(Account account) {

        System.out.print("\nEnter withdrawal amount: ");

        try {

            double amount = Double.parseDouble(scanner.nextLine());

            if (amount <= 0) {
                System.out.println("Amount must be greater than zero.");
                return;
            }

            if (!account.hasSufficientBalance(amount)) {
                System.out.println("Insufficient Funds");
                return;
            }

            account.withdraw(amount);

            account.addTransaction(
                    new Transaction(
                            "WITHDRAW",
                            amount,
                            "Cash withdrawal"
                    )
            );

            System.out.printf("Withdrawal successful!%n");
            System.out.printf("New Balance: ₹%.2f%n",
                    account.getBalance());

        } catch (NumberFormatException e) {

            System.out.println("Invalid amount.");
        }
    }

    private void deposit(Account account) {

        System.out.print("\nEnter deposit amount: ");

        try {

            double amount = Double.parseDouble(scanner.nextLine());

            if (amount <= 0) {
                System.out.println("Amount must be greater than zero.");
                return;
            }

            account.deposit(amount);

            account.addTransaction(
                    new Transaction(
                            "DEPOSIT",
                            amount,
                            "Cash deposit"
                    )
            );

            System.out.println("Deposit successful!");
            System.out.printf("New Balance: ₹%.2f%n",
                    account.getBalance());

        } catch (NumberFormatException e) {

            System.out.println("Invalid amount.");
        }
    }

    private void transfer(Account sender) {

        System.out.print("\nEnter recipient account ID: ");
        String recipientId = scanner.nextLine();

        Account recipient = bank.findAccountById(recipientId);

        if (recipient == null) {
            System.out.println("Recipient account not found.");
            return;
        }

        if (recipient.getAccountId().equals(sender.getAccountId())) {
            System.out.println("You cannot transfer to your own account.");
            return;
        }

        System.out.print("Enter transfer amount: ");

        try {

            double amount = Double.parseDouble(scanner.nextLine());

            if (amount <= 0) {
                System.out.println("Amount must be greater than zero.");
                return;
            }

            if (!sender.hasSufficientBalance(amount)) {
                System.out.println("Insufficient Funds");
                return;
            }

            sender.withdraw(amount);
            recipient.deposit(amount);

            sender.addTransaction(
                    new Transaction(
                            "TRANSFER",
                            amount,
                            "Sent to " + recipientId
                    )
            );

            recipient.addTransaction(
                    new Transaction(
                            "RECEIVED",
                            amount,
                            "Received from " + sender.getAccountId()
                    )
            );

            System.out.println("Transfer successful!");
            System.out.printf("New Balance: ₹%.2f%n",
                    sender.getBalance());

        } catch (NumberFormatException e) {

            System.out.println("Invalid amount.");
        }
    }
}