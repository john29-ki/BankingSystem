package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for account operations.
 * Handles deposits, withdrawals, transfers, and transaction history.
 */
public class AccountController {
    private final Map<Integer, Account> accountRegistry;
    private final UserController userController;

    public AccountController(UserController userController) {
        this.accountRegistry = new HashMap<>();
        this.userController = userController;
    }

    /**
     * Registers an account in the global registry.
     *
     * @param account Account to register
     */
    public void registerAccount(Account account) {
        accountRegistry.put(account.getAccountNumber(), account);
    }

    /**
     * Finds an account by account number.
     *
     * @param accountNumber Account number to look up
     * @return Account object or null if not found
     */
    public Account findAccount(int accountNumber) {
        return accountRegistry.get(accountNumber);
    }

    /**
     * Deposits money into an account.
     *
     * @param account Account to deposit into
     * @param amount  Amount to deposit
     * @return true if deposit successful, false otherwise
     */
    public boolean deposit(Account account, double amount) {
        if (account == null) {
            return false;
        }
        return account.deposit(amount);
    }

    /**
     * Withdraws money from an account.
     *
     * @param account Account to withdraw from
     * @param amount  Amount to withdraw
     * @return true if withdrawal successful, false otherwise
     */
    public boolean withdraw(Account account, double amount) {
        if (account == null) {
            return false;
        }
        return account.withdraw(amount);
    }

    /**
     * Transfers money between two accounts.
     *
     * @param fromAccountNumber Source account number
     * @param toAccountNumber   Target account number
     * @param amount            Amount to transfer
     * @return true if transfer successful, false otherwise
     */
    public boolean transfer(int fromAccountNumber, int toAccountNumber, double amount) {
        Account fromAccount = findAccount(fromAccountNumber);
        Account toAccount = findAccount(toAccountNumber);

        if (fromAccount == null || toAccount == null) {
            return false;
        }

        return fromAccount.transfer(toAccount, amount);
    }

    /**
     * Gets the transaction history for an account.
     *
     * @param account Account to get history for
     * @return List of transactions
     */
    public List<Transaction> getTransactionHistory(Account account) {
        if (account == null) {
            return List.of();
        }
        return account.getTransactionHistory();
    }

    /**
     * Gets the current logged-in user's accounts.
     *
     * @return List of accounts for current user
     */
    public List<Account> getCurrentUserAccounts() {
        User currentUser = userController.getCurrentUser();
        if (currentUser == null) {
            return List.of();
        }
        return currentUser.getAccounts();
    }

    /**
     * Gets all accounts in the system (for admin purposes).
     *
     * @return Map of all accounts
     */
    public Map<Integer, Account> getAllAccounts() {
        return new HashMap<>(accountRegistry);
    }
}
