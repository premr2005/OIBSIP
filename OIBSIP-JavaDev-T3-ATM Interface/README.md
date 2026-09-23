# Java ATM Console Simulation

## How to compile and run

```bash
cd src
javac *.java
java Main
```

## Demo accounts

| User ID | PIN  | Starting Balance |
|---------|------|-------------------|
| 1001    | 1234 | $1500.00          |
| 1002    | 5678 | $500.00           |
| 1003    | 4321 | $10000.00         |

You get 3 attempts to enter a valid User ID / PIN before the program denies access and exits.

## Class overview

- **Main** — entry point; seeds the bank with demo accounts and starts the ATM.
- **Bank** — owns the collection of `Account`s; handles authentication and cross-account transfers.
- **Account** — encapsulates balance, PIN, and that account's own transaction log (private fields + getters).
- **Transaction** — immutable record of a single deposit/withdrawal/transfer, with a timestamp and resulting balance.
- **ATM** — drives the console UI: login flow, main menu (switch-case), and each transaction handler.

## Feature checklist covered

- Startup prompt for User ID + PIN, 3-attempt lockout
- Main menu: Transaction History, Withdraw, Deposit, Transfer, Quit
- Balance check before withdrawal/transfer, with "Insufficient Funds" message
- All transactions logged in an `ArrayList<Transaction>` per account, viewable in history
- 5 classes: `Main`, `ATM`, `Account`, `Transaction`, `Bank`

## Notes / things you could extend

- Transfers require the recipient account ID to already exist in the `Bank`.
- PINs and balances reset every run since there's no persistence layer (in-memory only, as specified).
- `Account.setPin()` is there if you want to add a "change PIN" menu option later.
