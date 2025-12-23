package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class User {

    private static int idCounter = 1;

    private final int userId;
    private String name;
    private String email;
    private String phone;
    private String password;
    private Role role;
    private final List<Account> accounts = new ArrayList<>();

    public enum Role {
        CLIENT,
        ADMIN
    }

    public User(String name, Role role, String email, String password, String phone) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }

        this.userId = idCounter++;
        this.name = name;
        this.role = role;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    // ===== Getters =====

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        this.password = password;
    }

    // ===== Account Management =====

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    /**
     * Adds an account to this user.
     *
     * @param account the account to add
     * @return true if successful, false if account is null or already owned by another user
     */
    public boolean addAccount(Account account) {
        if (account == null || accounts.contains(account)) {
            return false;
        }
        if (!account.assignToUser(this.userId)) {
            return false;
        }
        accounts.add(account);
        return true;
    }

    /**
     * Removes an account from this user.
     *
     * @param account the account to remove
     * @return true if the account was removed, false otherwise
     */
    public boolean removeAccount(Account account) {
        if (accounts.remove(account)) {
            account.clearOwner();
            return true;
        }
        return false;
    }

    // ===== Object Methods =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", accountCount=" + accounts.size() +
                '}';
    }

    // ===== Testing Support =====

    /**
     * Resets the ID counter. Only use in tests.
     */
    public static void resetIdCounter() {
        idCounter = 1;
    }
}
