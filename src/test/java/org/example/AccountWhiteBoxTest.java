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
 * White-box tests for Account.deposit() and Account.withdraw()
 * Targeting 100% branch coverage.
 *
 * ============================================================
 * CONTROL FLOW GRAPH - deposit(amount)
 * ============================================================
 *
 *     [START]
 *        |
 *        v
 *   (1) amount <= 0? ----YES----> [return false]
 *        |
 *        NO
 *        v
 *   (2) status == CLOSED? ----YES----> [return false]
 *        |
 *        NO
 *        v
 *   (3) status == SUSPENDED? ----YES----> [return false]
 *        |
 *        NO
 *        v
 *   (4) balance += amount
 *        |
 *        v
 *     [return true]
 *
 * Branches: 1T, 1F, 2T, 2F, 3T, 3F
 *
 * ============================================================
 * CONTROL FLOW GRAPH - withdraw(amount)
 * ============================================================
 *
 *     [START]
 *        |
 *        v
 *   (1) amount <= 0? ----YES----> [return false]
 *        |
 *        NO
 *        v
 *   (2) status != VERIFIED? ----YES----> [return false]
 *        |
 *        NO
 *        v
 *   (3) amount > balance? ----YES----> [return false]
 *        |
 *        NO
 *        v
 *   (4) balance -= amount
 *        |
 *        v
 *     [return true]
 *
 * Branches: 1T, 1F, 2T, 2F, 3T, 3F
 *
 * ============================================================
 */
@DisplayName("Account White-Box Tests - 100% Branch Coverage")
class AccountWhiteBoxTest {

    private Account account;

    @BeforeEach
    void setUp() {
        Account.resetCounter();
        account = new Account(100.0);
    }

    // ===== DEPOSIT BRANCH COVERAGE =====

    @Nested
    @DisplayName("deposit() - Branch Coverage")
    class DepositBranchCoverage {

        // Branch 1T: amount <= 0 (true path - negative)
        @Test
        @DisplayName("Branch 1T: amount < 0 → return false")
        void deposit_negativeAmount_branch1T() {
            assertFalse(account.deposit(-10.0));
            assertEquals(100.0, account.getBalance());
        }

        // Branch 1T: amount <= 0 (true path - zero)
        @Test
        @DisplayName("Branch 1T: amount == 0 → return false")
        void deposit_zeroAmount_branch1T() {
            assertFalse(account.deposit(0.0));
            assertEquals(100.0, account.getBalance());
        }

        // Branch 2T: status == CLOSED (true path)
        @Test
        @DisplayName("Branch 2T: status == CLOSED → return false")
        void deposit_closedAccount_branch2T() {
            account.verify();
            account.close();

            assertFalse(account.deposit(50.0));
            assertEquals(100.0, account.getBalance());
        }

        // Branch 3T: status == SUSPENDED (true path)
        @Test
        @DisplayName("Branch 3T: status == SUSPENDED → return false")
        void deposit_suspendedAccount_branch3T() {
            account.verify();
            account.suspend();

            assertFalse(account.deposit(50.0));
            assertEquals(100.0, account.getBalance());
        }

        // Branch 1F, 2F, 3F: All conditions false → success (UNVERIFIED)
        @Test
        @DisplayName("Branch 1F,2F,3F: valid deposit on UNVERIFIED → return true")
        void deposit_unverifiedAccount_allBranchesFalse() {
            assertEquals(Account.AccountStatus.UNVERIFIED, account.getStatus());

            assertTrue(account.deposit(50.0));
            assertEquals(150.0, account.getBalance());
        }

        // Branch 1F, 2F, 3F: All conditions false → success (VERIFIED)
        @Test
        @DisplayName("Branch 1F,2F,3F: valid deposit on VERIFIED → return true")
        void deposit_verifiedAccount_allBranchesFalse() {
            account.verify();

            assertTrue(account.deposit(50.0));
            assertEquals(150.0, account.getBalance());
        }
    }

    // ===== WITHDRAW BRANCH COVERAGE =====

    @Nested
    @DisplayName("withdraw() - Branch Coverage")
    class WithdrawBranchCoverage {

        // Branch 1T: amount <= 0 (true path - negative)
        @Test
        @DisplayName("Branch 1T: amount < 0 → return false")
        void withdraw_negativeAmount_branch1T() {
            account.verify();

            assertFalse(account.withdraw(-10.0));
            assertEquals(100.0, account.getBalance());
        }

        // Branch 1T: amount <= 0 (true path - zero)
        @Test
        @DisplayName("Branch 1T: amount == 0 → return false")
        void withdraw_zeroAmount_branch1T() {
            account.verify();

            assertFalse(account.withdraw(0.0));
            assertEquals(100.0, account.getBalance());
        }

        // Branch 2T: status != VERIFIED (UNVERIFIED)
        @Test
        @DisplayName("Branch 2T: status == UNVERIFIED → return false")
        void withdraw_unverifiedAccount_branch2T() {
            assertEquals(Account.AccountStatus.UNVERIFIED, account.getStatus());

            assertFalse(account.withdraw(50.0));
            assertEquals(100.0, account.getBalance());
        }

        // Branch 2T: status != VERIFIED (SUSPENDED)
        @Test
        @DisplayName("Branch 2T: status == SUSPENDED → return false")
        void withdraw_suspendedAccount_branch2T() {
            account.verify();
            account.suspend();

            assertFalse(account.withdraw(50.0));
            assertEquals(100.0, account.getBalance());
        }

        // Branch 2T: status != VERIFIED (CLOSED)
        @Test
        @DisplayName("Branch 2T: status == CLOSED → return false")
        void withdraw_closedAccount_branch2T() {
            account.verify();
            account.close();

            assertFalse(account.withdraw(50.0));
            assertEquals(100.0, account.getBalance());
        }

        // Branch 3T: amount > balance (overdraft)
        @Test
        @DisplayName("Branch 3T: amount > balance (overdraft) → return false")
        void withdraw_overdraft_branch3T() {
            account.verify();

            assertFalse(account.withdraw(150.0));
            assertEquals(100.0, account.getBalance());
        }

        // Branch 1F, 2F, 3F: All conditions false → success
        @Test
        @DisplayName("Branch 1F,2F,3F: valid withdraw → return true")
        void withdraw_validAmount_allBranchesFalse() {
            account.verify();

            assertTrue(account.withdraw(50.0));
            assertEquals(50.0, account.getBalance());
        }

        // Boundary: withdraw exact balance
        @Test
        @DisplayName("Branch 3F boundary: amount == balance → return true")
        void withdraw_exactBalance_branch3FBoundary() {
            account.verify();

            assertTrue(account.withdraw(100.0));
            assertEquals(0.0, account.getBalance());
        }
    }
}
