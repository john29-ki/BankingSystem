package org.example;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Transaction {

    private final String transactionId;
    private final TransactionType type;
    private final double amount;
    private final LocalDateTime timestamp;
    private final Integer sourceAccountNumber;
    private final Integer targetAccountNumber;
    private TransactionStatus status;

    public enum TransactionType {
        DEPOSIT,
        WITHDRAW,
        TRANSFER
    }

    public enum TransactionStatus {
        PENDING,
        SUCCESS,
        FAILED
    }

    /**
     * Creates a transaction with auto-generated ID.
     */
    public Transaction(TransactionType type, double amount,
                       Integer sourceAccountNumber, Integer targetAccountNumber) {
        this(UUID.randomUUID().toString(), type, amount, sourceAccountNumber, targetAccountNumber);
    }

    /**
     * Creates a transaction with a specific ID.
     */
    public Transaction(String transactionId, TransactionType type, double amount,
                       Integer sourceAccountNumber, Integer targetAccountNumber) {
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        validateAccountNumbers(type, sourceAccountNumber, targetAccountNumber);

        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.sourceAccountNumber = sourceAccountNumber;
        this.targetAccountNumber = targetAccountNumber;
        this.timestamp = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
    }

    private void validateAccountNumbers(TransactionType type,
                                        Integer sourceAccountNumber,
                                        Integer targetAccountNumber) {
        switch (type) {
            case DEPOSIT:
                if (targetAccountNumber == null) {
                    throw new IllegalArgumentException("Deposit requires a target account");
                }
                break;
            case WITHDRAW:
                if (sourceAccountNumber == null) {
                    throw new IllegalArgumentException("Withdrawal requires a source account");
                }
                break;
            case TRANSFER:
                if (sourceAccountNumber == null || targetAccountNumber == null) {
                    throw new IllegalArgumentException("Transfer requires both source and target accounts");
                }
                if (sourceAccountNumber.equals(targetAccountNumber)) {
                    throw new IllegalArgumentException("Cannot transfer to the same account");
                }
                break;
        }
    }

    // ===== Status Management =====

    public void markSuccess() {
        if (status == TransactionStatus.PENDING) {
            status = TransactionStatus.SUCCESS;
        }
    }

    public void markFailed() {
        if (status == TransactionStatus.PENDING) {
            status = TransactionStatus.FAILED;
        }
    }

    public boolean isPending() {
        return status == TransactionStatus.PENDING;
    }

    public boolean isSuccessful() {
        return status == TransactionStatus.SUCCESS;
    }

    // ===== Getters =====

    public String getTransactionId() {
        return transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Integer getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public Integer getTargetAccountNumber() {
        return targetAccountNumber;
    }

    // ===== Object Methods =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", type=" + type +
                ", amount=" + amount +
                ", status=" + status +
                ", timestamp=" + timestamp +
                ", source=" + sourceAccountNumber +
                ", target=" + targetAccountNumber +
                '}';
    }
}
