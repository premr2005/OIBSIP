import java.util.HashMap;
import java.util.Map;

/**
 * Represents the bank as a whole: a directory of accounts, plus
 * operations that touch more than one account (like transfers).
 */
public class Bank {

    private final String name;
    private final Map<String, Account> accounts;

    public Bank(String name) {
        this.name = name;
        this.accounts = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void addAccount(Account account) {
        accounts.put(account.getAccountId(), account);
    }

    public boolean accountExists(String accountId) {
        return accounts.containsKey(accountId);
    }

    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    /**
     * Authenticates a user by account ID and PIN.
     * Returns the Account on success, or null if the ID/PIN pair is invalid.
     */
    public Account authenticate(String accountId, String pin) {
        Account account = accounts.get(accountId);
        if (account != null && account.validatePin(pin)) {
            return account;
        }
        return null;
    }

    /**
     * Transfers funds from one account to another.
     * Returns true if the transfer succeeded, false if funds were insufficient
     * or the recipient account does not exist.
     */
    public boolean transfer(Account from, String toAccountId, double amount) {
        Account to = accounts.get(toAccountId);
        if (to == null) {
            return false;
        }
        if (!from.hasSufficientFunds(amount)) {
            return false;
        }
        from.recordTransferOut(amount, "Transfer to " + toAccountId);
        to.recordTransferIn(amount, "Transfer from " + from.getAccountId());
        return true;
    }
}
