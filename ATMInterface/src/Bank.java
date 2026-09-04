import java.util.HashMap;

public class Bank {

    private HashMap<String, Account> accounts;

    public Bank() {
        accounts = new HashMap<>();
    }

    public void addAccount(Account account) {
        accounts.put(account.getAccountId(), account);
    }

    public Account findAccountById(String accountId) {
        return accounts.get(accountId);
    }

    public Account findAccountByUserId(String userId) {

        for (Account account : accounts.values()) {

            if (account.getUserId().equals(userId)) {
                return account;
            }
        }

        return null;
    }
}