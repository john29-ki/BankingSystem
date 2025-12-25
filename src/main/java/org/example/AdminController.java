package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller for administrative operations.
 * Handles account verification, transaction approval, and system-wide queries.
 */
public class AdminController {
    private final UserController userController;
    private final AccountController accountController;

    public AdminController(UserController userController, AccountController accountController) {
        this.userController = userController;
        this.accountController = accountController;
    }

    /**
     * Verifies an unverified account.
     *
     * @param accountNumber Account number to verify
     * @return true if verification successful, false otherwise
     */
    public boolean verifyAccount(int accountNumber) {
        Account account = accountController.findAccount(accountNumber);
        if (account == null) {
            return false;
        }

        try {
            account.verify();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * Suspends an account.
     *
     * @param accountNumber Account number to suspend
     * @return true if suspension successful, false otherwise
     */
    public boolean suspendAccount(int accountNumber) {
        Account account = accountController.findAccount(accountNumber);
        if (account == null) {
            return false;
        }

        try {
            account.suspend();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * Appeals a suspended account (reactivates it).
     *
     * @param accountNumber Account number to appeal
     * @return true if appeal successful, false otherwise
     */
    public boolean appealAccount(int accountNumber) {
        Account account = accountController.findAccount(accountNumber);
        if (account == null) {
            return false;
        }

        try {
            account.appeal();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * Closes an account.
     *
     * @param accountNumber Account number to close
     * @return true if closure successful, false otherwise
     */
    public boolean closeAccount(int accountNumber) {
        Account account = accountController.findAccount(accountNumber);
        if (account == null) {
            return false;
        }

        account.close();
        return true;
    }

    /**
     * Gets all unverified accounts in the system.
     *
     * @return List of unverified accounts
     */
    public List<Account> getUnverifiedAccounts() {
        List<Account> unverified = new ArrayList<>();
        Map<Integer, Account> allAccounts = accountController.getAllAccounts();

        for (Account account : allAccounts.values()) {
            if (account.getStatus() == Account.AccountStatus.UNVERIFIED) {
                unverified.add(account);
            }
        }

        return unverified;
    }

    /**
     * Gets all pending transactions across all accounts.
     *
     * @return List of pending transactions
     */
    public List<Transaction> getPendingTransactions() {
        List<Transaction> pending = new ArrayList<>();
        Map<Integer, Account> allAccounts = accountController.getAllAccounts();

        for (Account account : allAccounts.values()) {
            for (Transaction transaction : account.getTransactionHistory()) {
                if (transaction.isPending()) {
                    pending.add(transaction);
                }
            }
        }

        return pending;
    }

    /**
     * Approves a pending transaction.
     *
     * @param transactionId Transaction ID to approve
     * @return true if approval successful, false otherwise
     */
    public boolean approveTransaction(String transactionId) {
        Transaction transaction = findTransaction(transactionId);
        if (transaction == null) {
            return false;
        }

        try {
//            transaction.approve();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * Rejects a pending transaction.
     *
     * @param transactionId Transaction ID to reject
     * @return true if rejection successful, false otherwise
     */
    public boolean rejectTransaction(String transactionId) {
        Transaction transaction = findTransaction(transactionId);
        if (transaction == null) {
            return false;
        }

        try {
//            transaction.reject();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * Finds a transaction by its ID across all accounts.
     *
     * @param transactionId Transaction ID to find
     * @return Transaction object or null if not found
     */
    private Transaction findTransaction(String transactionId) {
        Map<Integer, Account> allAccounts = accountController.getAllAccounts();

        for (Account account : allAccounts.values()) {
            for (Transaction transaction : account.getTransactionHistory()) {
                if (transaction.getTransactionId().equals(transactionId)) {
                    return transaction;
                }
            }
        }

        return null;
    }

    /**
     * Gets all users in the system.
     *
     * @return Map of all users
     */
    public Map<Integer, User> getAllUsers() {
        return userController.getAllUsers();
    }

    /**
     * Gets all accounts in the system.
     *
     * @return Map of all accounts
     */
    public Map<Integer, Account> getAllAccounts() {
        return accountController.getAllAccounts();
    }
}
