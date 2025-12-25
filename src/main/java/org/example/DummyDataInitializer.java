package org.example;

import java.lang.reflect.Field;

import org.example.controllers.AccountController;
import org.example.controllers.UserController;
import org.example.model.Account;
import org.example.model.User;

/**
 * Initializes the banking system with dummy data for testing.
 * Creates 2 client users with bank accounts and 1 admin user.
 */
public class DummyDataInitializer {
    
    /**
     * Initializes dummy data for the banking system.
     * 
     * @param userController The user controller to populate
     * @param accountController The account controller to register accounts
     */
    public static void initializeDummyData(UserController userController, AccountController accountController) {
        // Reset counters to ensure consistent IDs
        User.resetIdCounter();
        Account.resetCounter();
        
        // Create User 1: John Doe (CLIENT)
        User user1 = new User("Hady", User.Role.CLIENT, "hady@gmail.com", "1234", "555-0101");
        addUserToController(userController, user1);
        
        // Create accounts for User 1
        Account account1_1 = new Account(1000.0);
        user1.addAccount(account1_1);
        accountController.registerAccount(account1_1);
        account1_1.verify();
        
        Account account1_2 = new Account(2500.50);
        user1.addAccount(account1_2);
        accountController.registerAccount(account1_2);
        account1_2.verify();
        
        // Create User 2: Jane Smith (CLIENT)
        User user2 = new User("Jane Smith", User.Role.CLIENT, "jane.smith@email.com", "password456", "555-0202");
        addUserToController(userController, user2);
        
        // Create accounts for User 2
        Account account2_1 = new Account(500.0);
        user2.addAccount(account2_1);
        accountController.registerAccount(account2_1);
        account2_1.verify();
        
        Account account2_2 = new Account(3000.75);
        user2.addAccount(account2_2);
        accountController.registerAccount(account2_2);
        account2_2.verify();
        
        // Create Admin User
        User admin = new User("Admin User", User.Role.ADMIN, "admin@bank.com", "admin123", "555-0000");
        addUserToController(userController, admin);
        
        // Ensure no user is logged in
        userController.logout();
    }
    
    /**
     * Helper method to add a user directly to the controller's registries.
     * This bypasses the registerUser method which auto-logs in.
     */
    private static void addUserToController(UserController userController, User user) {
        try {
            // Use reflection to access private fields
            Field userRegistryField = UserController.class.getDeclaredField("userRegistry");
            Field userByEmailField = UserController.class.getDeclaredField("userByEmail");
            
            userRegistryField.setAccessible(true);
            userByEmailField.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            java.util.Map<Integer, User> userRegistry = (java.util.Map<Integer, User>) userRegistryField.get(userController);
            @SuppressWarnings("unchecked")
            java.util.Map<String, User> userByEmail = (java.util.Map<String, User>) userByEmailField.get(userController);
            
            userRegistry.put(user.getUserId(), user);
            userByEmail.put(user.getEmail(), user);
            
            userRegistryField.setAccessible(false);
            userByEmailField.setAccessible(false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add user to controller", e);
        }
    }
}

