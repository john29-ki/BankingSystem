package org.example.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Account {

    private static int counter = 1000;

    private final int accountNumber;
    private double balance;
    private AccountStatus status;
    private Integer ownerUserId;
    private final List<Transaction> transactionHistory = new ArrayList<>();

    public enum AccountStatus {
        UNVERIFIED,
        VERIFIED,
        SUSPENDED,
        CLOSED
    }

    public Account(double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        this.accountNumber = counter++;
        this.balance = initialBalance;
        this.status = AccountStatus.UNVERIFIED;
    }

    // ===== Core Actions =====

    /**
     * Deposits money into the account.
     *
     * @param amount the amount to deposit (must be positive)
     * @return true if successful, false if amount is invalid or account is closed/suspended
     */
    public boolean deposit(double amount) {
        if (amount <= 0) {
            return false;
        }
        if (status == AccountStatus.CLOSED || status == AccountStatus.SUSPENDED) {
            return false;
        }
        balance += amount;
        return true;
    }

    /**
     * Withdraws money from the account.
     *
     * @param amount the amount to withdraw (must be positive)
     * @return true if successful, false if amount is invalid, insufficient funds, or account not verified
     */
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            return false;
        }
        if (status != AccountStatus.VERIFIED) {
            return false;
        }
        if (amount > balance) {
            return false;
        }
        balance -= amount;
        return true;
    }

    /**
     * Transfers money to another account.
     *
     * @param target the account to transfer to
     * @param amount the amount to transfer
     * @return true if successful, false otherwise
     */
    public boolean transfer(Account target, double amount) {
        if (target == null || target == this) {
            return false;
        }
        if (!this.withdraw(amount)) {
            return false;
        }
        if (!target.deposit(amount)) {
            // Rollback withdrawal if deposit fails
            this.balance += amount;
            return false;
        }
        return true;
    }

    // ===== State Transitions =====

    /**
     * Verifies the account. Only works if currently unverified.
     *
     * @return true if status changed, false otherwise
     */
    public boolean verify() {
        if (status == AccountStatus.UNVERIFIED) {
            status = AccountStatus.VERIFIED;
            return true;
        }
        return false;
    }

    /**
     * Suspends the account. Only works if currently verified.
     *
     * @return true if status changed, false otherwise
     */
    public boolean suspend() {
        if (status == AccountStatus.VERIFIED) {
            status = AccountStatus.SUSPENDED;
            return true;
        }
        return false;
    }

    /**
     * Appeals a suspension. Only works if currently suspended.
     *
     * @return true if status changed, false otherwise
     */
    public boolean appeal() {
        if (status == AccountStatus.SUSPENDED) {
            status = AccountStatus.VERIFIED;
            return true;
        }
        return false;
    }

    /**
     * Closes the account. Can be done from any status.
     *
     * @return true if status changed, false if already closed
     */
    public boolean close() {
        if (status == AccountStatus.CLOSED) {
            return false;
        }
        status = AccountStatus.CLOSED;
        return true;
    }

    // ===== Transaction History =====

    public void addTransaction(Transaction transaction) {
        if (transaction != null) {
            transactionHistory.add(transaction);
        }
    }

    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    // ===== Getters =====

    public int getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public Integer getOwnerUserId() {
        return ownerUserId;
    }

    public boolean isActive() {
        return status == AccountStatus.VERIFIED;
    }

    // ===== Package-Private Ownership Methods =====

    /**
     * Assigns this account to a user.
     *
     * @param userId the user ID to assign
     * @return true if successful, false if already owned by a different user
     */
    boolean assignToUser(int userId) {
        if (ownerUserId != null && ownerUserId != userId) {
            return false;
        }
        ownerUserId = userId;
        return true;
    }

    /**
     * Removes ownership from this account.
     */
    void clearOwner() {
        ownerUserId = null;
    }

    // ===== Object Methods =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountNumber == account.accountNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber=" + accountNumber +
                ", balance=" + balance +
                ", status=" + status +
                ", ownerUserId=" + ownerUserId +
                '}';
    }

    // ===== Testing Support =====

    /**
     * Resets the account number counter. Only use in tests.
     */
    public static void resetCounter() {
        counter = 1000;
    }
}

