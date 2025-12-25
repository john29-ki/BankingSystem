package org.example;

import org.example.model.Account;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Black-box tests for Account using Equivalence Partitioning.
 *
 * Deposit Equivalence Classes:
 * - Amount: Negative (invalid), Positive (valid)
 * - Account Status: CLOSED (invalid), Other (valid)
 *
 * Withdraw Equivalence Classes:
 * - Amount vs Balance: amount <= balance (valid), amount > balance (invalid)
 */
@DisplayName("Account - Equivalence Partitioning Tests")
class AccountBlackBoxTest {

    private Account account;

    @BeforeEach
    void setUp() {
        Account.resetCounter();
        account = new Account(100.0);
        account.verify(); // Most tests need verified account
    }

    // ===== DEPOSIT TESTS =====

    @Nested
    @DisplayName("Deposit Tests")
    class DepositTests {

        @Test
        @DisplayName("Negative deposit → invalid")
        void deposit_negativeAmount_returnsFalse() {
            boolean result = account.deposit(-50.0);

            assertFalse(result);
            assertEquals(100.0, account.getBalance());
        }

        @Test
        @DisplayName("Valid deposit → success")
        void deposit_positiveAmount_returnsTrue() {
            boolean result = account.deposit(50.0);

            assertTrue(result);
            assertEquals(150.0, account.getBalance());
        }

        @Test
        @DisplayName("Deposit in Closed → fail")
        void deposit_closedAccount_returnsFalse() {
            account.close();

            boolean result = account.deposit(50.0);

            assertFalse(result);
            assertEquals(100.0, account.getBalance());
        }
    }

    // ===== WITHDRAW TESTS =====

    @Nested
    @DisplayName("Withdraw Tests")
    class WithdrawTests {

        @Test
        @DisplayName("Withdraw amount <= balance → success")
        void withdraw_amountLessThanOrEqualBalance_returnsTrue() {
            boolean result = account.withdraw(50.0);

            assertTrue(result);
            assertEquals(50.0, account.getBalance());
        }

        @Test
        @DisplayName("Withdraw exact balance → success")
        void withdraw_exactBalance_returnsTrue() {
            boolean result = account.withdraw(100.0);

            assertTrue(result);
            assertEquals(0.0, account.getBalance());
        }

        @Test
        @DisplayName("Withdraw amount > balance → fail")
        void withdraw_amountGreaterThanBalance_returnsFalse() {
            boolean result = account.withdraw(150.0);

            assertFalse(result);
            assertEquals(100.0, account.getBalance());
        }
    }
}
