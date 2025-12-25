package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * User home screen.
 * Displays user information, account selection, and action buttons.
 */
public class UserHomeView extends JFrame {
    private final UserController userController;
    private final AccountController accountController;

    private JLabel userNameLabel;
    private JComboBox<String> accountComboBox;
    private JLabel accountNumberLabel;
    private JLabel balanceLabel;
    private JLabel statusLabel;
    private JButton depositButton;
    private JButton withdrawButton;
    private JButton transferButton;
    private JButton viewTransactionsButton;
    private JButton createAccountButton;
    private JButton refreshButton;

    public UserHomeView(UserController userController, AccountController accountController) {
        this.userController = userController;
        this.accountController = accountController;

        initializeUI();
        loadUserData();
    }

    /**
     * Initializes the user interface components.
     */
    private void initializeUI() {
        setTitle("Banking System - User Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel - User info
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        userNameLabel = new JLabel("Welcome, User");
        userNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(userNameLabel);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel - Account details
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Account Details"));

        // Account selection
        JPanel accountSelectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        accountSelectPanel.add(new JLabel("Select Account:"));
        accountComboBox = new JComboBox<>();
        accountComboBox.addActionListener(e -> updateAccountDetails());
        accountSelectPanel.add(accountComboBox);
        centerPanel.add(accountSelectPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Account details
        accountNumberLabel = new JLabel("Account Number: -");
        balanceLabel = new JLabel("Balance: -");
        statusLabel = new JLabel("Status: -");

        centerPanel.add(accountNumberLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        centerPanel.add(balanceLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        centerPanel.add(statusLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel - Action buttons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        depositButton = new JButton("Deposit");
        depositButton.addActionListener(e -> handleDeposit());
        buttonPanel.add(depositButton);

        withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(e -> handleWithdraw());
        buttonPanel.add(withdrawButton);

        transferButton = new JButton("Transfer");
        transferButton.addActionListener(e -> handleTransfer());
        buttonPanel.add(transferButton);

        viewTransactionsButton = new JButton("View Transactions");
        viewTransactionsButton.addActionListener(e -> handleViewTransactions());
        buttonPanel.add(viewTransactionsButton);

        createAccountButton = new JButton("Create Account");
        createAccountButton.addActionListener(e -> handleCreateAccount());
        buttonPanel.add(createAccountButton);

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshAccountData());
        buttonPanel.add(refreshButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Loads user data and populates the account dropdown.
     */
    private void loadUserData() {
        User currentUser = userController.getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this,
                    "No user logged in.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        userNameLabel.setText("Welcome, " + currentUser.getName());

        // Populate account dropdown
        refreshAccountData();
    }

    /**
     * Refreshes account data from the controller.
     */
    private void refreshAccountData() {
        List<Account> accounts = accountController.getCurrentUserAccounts();

        accountComboBox.removeAllItems();

        if (accounts.isEmpty()) {
            accountComboBox.addItem("No accounts available");
            updateAccountDetails();
        } else {
            for (Account account : accounts) {
                accountComboBox.addItem("Account #" + account.getAccountNumber());
                // Register account if not already registered
                accountController.registerAccount(account);
            }
            updateAccountDetails();
        }
    }

    /**
     * Updates account details based on selected account.
     */
    private void updateAccountDetails() {
        Account selectedAccount = getSelectedAccount();

        if (selectedAccount == null) {
            accountNumberLabel.setText("Account Number: -");
            balanceLabel.setText("Balance: -");
            statusLabel.setText("Status: -");
            return;
        }

        accountNumberLabel.setText("Account Number: " + selectedAccount.getAccountNumber());
        balanceLabel.setText("Balance: $" + String.format("%.2f", selectedAccount.getBalance()));
        statusLabel.setText("Status: " + selectedAccount.getStatus());
    }

    /**
     * Gets the currently selected account.
     */
    private Account getSelectedAccount() {
        String selected = (String) accountComboBox.getSelectedItem();
        if (selected == null || selected.equals("No accounts available")) {
            return null;
        }

        List<Account> accounts = accountController.getCurrentUserAccounts();
        int index = accountComboBox.getSelectedIndex();

        if (index >= 0 && index < accounts.size()) {
            return accounts.get(index);
        }

        return null;
    }

    /**
     * Handles deposit action.
     */
    private void handleDeposit() {
        Account account = getSelectedAccount();
        if (account == null) {
            JOptionPane.showMessageDialog(this, "No account selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String amountStr = JOptionPane.showInputDialog(this, "Enter deposit amount:", "Deposit", JOptionPane.PLAIN_MESSAGE);
        if (amountStr == null) return;

        try {
            double amount = Double.parseDouble(amountStr);
            boolean success = accountController.deposit(account, amount);

            if (success) {
                JOptionPane.showMessageDialog(this, "Deposit successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshAccountData();
            } else {
                JOptionPane.showMessageDialog(this, "Deposit failed. Check account status and amount.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles withdraw action.
     */
    private void handleWithdraw() {
        Account account = getSelectedAccount();
        if (account == null) {
            JOptionPane.showMessageDialog(this, "No account selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String amountStr = JOptionPane.showInputDialog(this, "Enter withdrawal amount:", "Withdraw", JOptionPane.PLAIN_MESSAGE);
        if (amountStr == null) return;

        try {
            double amount = Double.parseDouble(amountStr);
            boolean success = accountController.withdraw(account, amount);

            if (success) {
                JOptionPane.showMessageDialog(this, "Withdrawal successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshAccountData();
            } else {
                JOptionPane.showMessageDialog(this, "Withdrawal failed. Check balance, account status, and amount.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles transfer action.
     */
    private void handleTransfer() {
        Account fromAccount = getSelectedAccount();
        if (fromAccount == null) {
            JOptionPane.showMessageDialog(this, "No account selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create dialog for transfer
        JDialog transferDialog = new JDialog(this, "Transfer", true);
        transferDialog.setSize(300, 150);
        transferDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField targetAccountField = new JTextField(15);
        JTextField amountField = new JTextField(15);

        panel.add(new JLabel("Target Account Number:"));
        panel.add(targetAccountField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton confirmButton = new JButton("Transfer");
        confirmButton.addActionListener(e -> {
            try {
                int targetAccountNumber = Integer.parseInt(targetAccountField.getText());
                double amount = Double.parseDouble(amountField.getText());

                boolean success = accountController.transfer(fromAccount.getAccountNumber(), targetAccountNumber, amount);

                if (success) {
                    JOptionPane.showMessageDialog(transferDialog, "Transfer successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    transferDialog.dispose();
                    refreshAccountData();
                } else {
                    JOptionPane.showMessageDialog(transferDialog, "Transfer failed. Check accounts and amount.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(transferDialog, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(confirmButton);
        transferDialog.add(panel);
        transferDialog.setVisible(true);
    }

    /**
     * Handles view transactions action.
     */
    private void handleViewTransactions() {
        Account account = getSelectedAccount();
        if (account == null) {
            JOptionPane.showMessageDialog(this, "No account selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TransactionHistoryDialog dialog = new TransactionHistoryDialog(this, account, accountController);
        dialog.setVisible(true);
    }

    /**
     * Handles create account action.
     */
    private void handleCreateAccount() {
        String balanceStr = JOptionPane.showInputDialog(this, "Enter initial balance:", "Create Account", JOptionPane.PLAIN_MESSAGE);
        if (balanceStr == null) return;

        try {
            double initialBalance = Double.parseDouble(balanceStr);
            boolean success = userController.createAccount(initialBalance);

            if (success) {
                JOptionPane.showMessageDialog(this, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshAccountData();
            } else {
                JOptionPane.showMessageDialog(this, "Account creation failed. Check initial balance.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
