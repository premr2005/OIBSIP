import java.util.Scanner;

/**
 * Entry point for the ATM console simulation.
 *
 * Demo accounts (for testing):
 *   User ID: 1001   PIN: 1234   Balance: $1500.00
 *   User ID: 1002   PIN: 5678   Balance: $500.00
 *   User ID: 1003   PIN: 4321   Balance: $10000.00
 */
public class Main {

    public static void main(String[] args) {
        Bank bank = new Bank("Prem National Bank");

        // Seed some demo accounts so the simulation is usable out of the box.
        bank.addAccount(new Account("1001", "Akshad Dive", "1234", 15000.00));
        bank.addAccount(new Account("1002", "Vedant Gadekar", "5678", 5000.00));
        bank.addAccount(new Account("1003", "Yash Karde", "4321", 10000.00));

        Scanner scanner = new Scanner(System.in);
        ATM atm = new ATM(bank, scanner);

        Account loggedInAccount = atm.login();
        if (loggedInAccount != null) {
            atm.runSession(loggedInAccount);
        }

        scanner.close();
    }
}