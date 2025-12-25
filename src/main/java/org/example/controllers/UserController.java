package org.example.controllers;

import java.util.HashMap;
import java.util.Map;

import org.example.model.Account;
import org.example.model.User;

/**
 * Controller for user management and session handling.
 * Manages user registry and single-session authentication.
 */
public class UserController {
    private final Map<Integer, User> userRegistry;
    private final Map<String, User> userByEmail;
    private User currentLoggedInUser;

    public UserController() {
        this.userRegistry = new HashMap<>();
        this.userByEmail = new HashMap<>();
        this.currentLoggedInUser = null;
    }

    /**
     * Registers a new user in the system.
     *
     * @param name     User's full name
     * @param email    User's email address
     * @param password User's password
     * @param phone    User's phone number (optional)
     * @return true if registration successful, false if email already exists
     */
    public boolean registerUser(String name, String email, String password, String phone) {
        try {
            // Check if email already exists
            if (userByEmail.containsKey(email)) {
                return false;
            }

            // Create new user with CLIENT role
            User user = new User(name, User.Role.CLIENT, email, password, phone);
            
            // Store in registries
            userRegistry.put(user.getUserId(), user);
            userByEmail.put(email, user);
            
            // Auto-login the newly registered user
            currentLoggedInUser = user;
            
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Creates a new account for the currently logged-in user.
     *
     * @param initialBalance Starting balance for the account
     * @return true if account created successfully, false otherwise
     */
    public boolean createAccount(double initialBalance) {
        if (currentLoggedInUser == null) {
            return false;
        }

        try {
            Account account = new Account(initialBalance);
            currentLoggedInUser.addAccount(account);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Gets the currently logged-in user.
     *
     * @return Current user or null if no user is logged in
     */
    public User getCurrentUser() {
        return currentLoggedInUser;
    }

    /**
     * Sets the current logged-in user (for admin access).
     *
     * @param user User to set as logged in
     */
    public void setCurrentUser(User user) {
        this.currentLoggedInUser = user;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        this.currentLoggedInUser = null;
    }

    /**
     * Gets a user by their ID.
     *
     * @param userId User ID to look up
     * @return User object or null if not found
     */
    public User getUserById(int userId) {
        return userRegistry.get(userId);
    }

    /**
     * Gets a user by their email.
     *
     * @param email Email address to look up
     * @return User object or null if not found
     */
    public User getUserByEmail(String email) {
        return userByEmail.get(email);
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentLoggedInUser != null;
    }

    /**
     * Gets all registered users (for admin purposes).
     *
     * @return Map of all users
     */
    public Map<Integer, User> getAllUsers() {
        return new HashMap<>(userRegistry);
    }

    /**
     * Logs in a user with email and password.
     *
     * @param email    User's email address
     * @param password User's password
     * @return true if login successful, false otherwise
     */
    public boolean login(String email, String password) {
        User user = userByEmail.get(email);
        if (user == null) {
            return false;
        }
        
        if (!user.getPassword().equals(password)) {
            return false;
        }
        
        currentLoggedInUser = user;
        return true;
    }
}

