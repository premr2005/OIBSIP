public class Main {

    public static void main(String[] args) {

        Bank bank = new Bank();

        Account account1 =
                new Account("ACC1001", "user1", "1234", 10000);

        Account account2 =
                new Account("ACC1002", "user2", "5678", 5000);

        Account account3 =
                new Account("ACC1003", "user3", "9999", 7500);

        bank.addAccount(account1);
        bank.addAccount(account2);
        bank.addAccount(account3);

        ATM atm = new ATM(bank);

        atm.start();
    }
}