package org.example;

import org.example.model.Account;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("State Based Testing")
class StateBasedTest {
    private Account account;

    @BeforeEach
    void setUp() {
        Account.resetCounter();
        account = new Account(100.0);
    }
    @Test
    @DisplayName("Deposit in Verified Account - Success")
    void deposit_in_verified() {
        account.verify();
        assertEquals(true, account.deposit(50.0));
        assertEquals(150.0, account.getBalance());
    }

    @Test
    @DisplayName("Withdraw from Verified Account - Success")
    void withdraw_from_verified() {
        account.verify();
        assertEquals(true, account.withdraw(50.0));
        assertEquals(50.0, account.getBalance());
    }

    @Test
    @DisplayName("Deposit in Unverified Account - Success")
    void deposit_in_unverified() {
        assertEquals(true, account.deposit(50.0));
        assertEquals(150.0, account.getBalance());
    }

    @Test
    @DisplayName("Withdraw from Unverified Account - Fail")
    void withdraw_from_unverified() {
        assertEquals(false, account.withdraw(50.0));
        assertEquals(100.0, account.getBalance());
    }

    @Test
    @DisplayName("View from Suspended Account - Success")
    void view_suspended() {
        account.verify();
        account.suspend();
        assertEquals(100.0, account.getBalance());
    }

    @Test
    @DisplayName("Deposit in Suspended Account - Fail")
    void deposit_in_suspended() {
        account.verify();
        account.suspend();
        assertEquals(false, account.deposit(50.0));
        assertEquals(100.0, account.getBalance());          
    }

    @Test
    @DisplayName("Withdraw from Suspended Account - Fail")
    void withdraw_from_suspended() {
        account.verify();
        account.suspend();
        assertEquals(false, account.withdraw(50.0));
        assertEquals(100.0, account.getBalance());          
    }

    @Test
    @DisplayName("Transfer from Suspended Account - Fail")
    void transfer_from_suspended() {
        // Setup
        account.verify();
        account.suspend();
        Account target = new Account(100.0);
        
        // Action & Assert
        assertEquals(false, account.transfer(target, 50.0), "Transfer should fail when account is suspended");
        assertEquals(100.0, account.getBalance(), "Source balance should remain unchanged");
        assertEquals(100.0, target.getBalance(), "Target balance should remain unchanged");
    }
    
    @Test
    @DisplayName("View from Closed Account - Success")
    void view_closed() {
        account.close();
        assertEquals(100.0, account.getBalance());
    }

    @Test
    @DisplayName("Deposit in Closed Account - Fail")
    void deposit_in_closed() {
        account.close();
        assertEquals(false, account.deposit(50.0));
        assertEquals(100.0, account.getBalance());          
    }

    @Test
    @DisplayName("Withdraw from Closed Account - Fail")
    void withdraw_from_closed() {
        account.close();
        assertEquals(false, account.withdraw(50.0));
        assertEquals(100.0, account.getBalance());          
    }

    @Test
    @DisplayName("Verified After Appeal - Success")
    void verified_after_appeal() {
        account.verify();
        account.suspend();
        account.appeal();
        assertEquals(Account.AccountStatus.VERIFIED, account.getStatus());
    }
}
