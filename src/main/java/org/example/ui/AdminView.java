package org.example.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.example.controllers.AccountController;
import org.example.controllers.AdminController;
import org.example.controllers.UserController;
import org.example.model.Account;
import org.example.model.Transaction;
import org.example.model.User;

/**
 * Admin panel view.
 * Allows administrators to manage pending transactions and unverified accounts.
 */
public class AdminView extends JFrame {
    private final AdminController adminController;
    private final UserController userController;
    private final AccountController accountController;

    private JTable pendingTransactionsTable;
    private DefaultTableModel pendingTransactionsModel;
    private JTable usersAccountsTable;
    private DefaultTableModel usersAccountsModel;
    private List<AccountRowData> accountRowDataList;

    private JButton approveTransactionButton;
    private JButton rejectTransactionButton;
    private JButton refreshButton;
    private JButton logoutButton;

    /**
     * Helper class to store account row data
     */
    private static class AccountRowData {
        Account account;
        
        AccountRowData(Account account) {
            this.account = account;
        }
    }

    public AdminView(AdminController adminController) {
        this.adminController = adminController;
        this.userController = null;
        this.accountController = null;
        initializeUI();
        loadData();
    }

    public AdminView(AdminController adminController, UserController userController, AccountController accountController) {
        this.adminController = adminController;
        this.userController = userController;
        this.accountController = accountController;
        initializeUI();
        loadData();
    }

    /**
     * Initializes the user interface components.
     */
    private void initializeUI() {
        setTitle("Banking System - Admin Panel");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Admin Panel");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Center panel with two sections
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        // Pending Transactions section
        JPanel transactionsPanel = createPendingTransactionsPanel();
        centerPanel.add(transactionsPanel);

        // Users and Accounts section
        JPanel accountsPanel = createUsersAccountsPanel();
        centerPanel.add(accountsPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Refresh and Logout buttons at bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(e -> loadData());
        bottomPanel.add(refreshButton);
        
        if (userController != null && accountController != null) {
            logoutButton = new JButton("Logout");
            logoutButton.addActionListener(e -> handleLogout());
            bottomPanel.add(logoutButton);
        }
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Creates the pending transactions panel.
     */
    private JPanel createPendingTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Pending Transactions"));

        // Table setup
        String[] columnNames = {"Transaction ID", "Type", "Amount", "Source Account", "Target Account"};
        pendingTransactionsModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        pendingTransactionsTable = new JTable(pendingTransactionsModel);
        pendingTransactionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(pendingTransactionsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        approveTransactionButton = new JButton("Approve");
        approveTransactionButton.addActionListener(e -> handleApproveTransaction());
        buttonPanel.add(approveTransactionButton);

        rejectTransactionButton = new JButton("Reject");
        rejectTransactionButton.addActionListener(e -> handleRejectTransaction());
        buttonPanel.add(rejectTransactionButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the users and accounts panel with status dropdowns.
     */
    private JPanel createUsersAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("All Users and Accounts"));

        // Table setup
        String[] columnNames = {"User Name", "User Email", "Account Number", "Balance", "Status"};
        usersAccountsModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only status column is editable
            }
        };

        usersAccountsTable = new JTable(usersAccountsModel);
        usersAccountsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set custom editor and renderer for status column
        usersAccountsTable.getColumn("Status").setCellRenderer(new StatusCellRenderer());
        usersAccountsTable.getColumn("Status").setCellEditor(new StatusCellEditor());
        
        JScrollPane scrollPane = new JScrollPane(usersAccountsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Loads data from the admin controller.
     */
    private void loadData() {
        loadPendingTransactions();
        loadUsersAccounts();
    }

    /**
     * Loads pending transactions into the table.
     */
    private void loadPendingTransactions() {
        pendingTransactionsModel.setRowCount(0);

        List<Transaction> pendingTransactions = adminController.getPendingTransactions();

        if (pendingTransactions.isEmpty()) {
            Object[] emptyRow = {"No pending transactions", "-", "-", "-", "-"};
            pendingTransactionsModel.addRow(emptyRow);
            return;
        }

        for (Transaction transaction : pendingTransactions) {
            Object[] row = new Object[5];
            row[0] = transaction.getTransactionId();
            row[1] = transaction.getType().toString();
            row[2] = String.format("$%.2f", transaction.getAmount());
            row[3] = transaction.getSourceAccountNumber() != null ? transaction.getSourceAccountNumber() : "-";
            row[4] = transaction.getTargetAccountNumber() != null ? transaction.getTargetAccountNumber() : "-";

            pendingTransactionsModel.addRow(row);
        }
    }

    /**
     * Loads all users and their accounts into the table with status dropdowns.
     */
    private void loadUsersAccounts() {
        usersAccountsModel.setRowCount(0);
        accountRowDataList = new ArrayList<>();

        if (userController == null || accountController == null) {
            Object[] emptyRow = {"-", "-", "-", "-", "-"};
            usersAccountsModel.addRow(emptyRow);
            return;
        }

        Map<Integer, User> allUsers = adminController.getAllUsers();

        if (allUsers.isEmpty()) {
            Object[] emptyRow = {"No users", "-", "-", "-", "-"};
            usersAccountsModel.addRow(emptyRow);
            return;
        }

        // Iterate through all users and their accounts
        for (User user : allUsers.values()) {
            List<Account> userAccounts = user.getAccounts();
            if(user.getRole() != User.Role.CLIENT) {
                continue;
            }
            if (userAccounts.isEmpty()) {
                // Show user even if they have no accounts
                Object[] row = new Object[5];
                row[0] = user.getName();
                row[1] = user.getEmail();
                row[2] = "No accounts";
                row[3] = "-";
                row[4] = "-";
                usersAccountsModel.addRow(row);
            } else {
                for (Account account : userAccounts) {
                    AccountRowData rowData = new AccountRowData(account);
                    accountRowDataList.add(rowData);
                    
                    Object[] row = new Object[5];
                    row[0] = user.getName();
                    row[1] = user.getEmail();
                    row[2] = account.getAccountNumber();
                    row[3] = String.format("$%.2f", account.getBalance());
                    row[4] = account.getStatus().toString(); // Store status as string
                    
                    usersAccountsModel.addRow(row);
                }
            }
        }
    }
    
    /**
     * Custom renderer for status column - displays as label
     */
    private class StatusCellRenderer implements javax.swing.table.TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel(value != null ? value.toString() : "");
            label.setOpaque(true);
            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            } else {
                label.setBackground(table.getBackground());
                label.setForeground(table.getForeground());
            }
            return label;
        }
    }
    
    /**
     * Custom editor for status column - shows dropdown
     */
    private class StatusCellEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
        private final JComboBox<String> comboBox;
        private Account currentAccount;
        
        public StatusCellEditor() {
            comboBox = new JComboBox<>();
            comboBox.addItem("UNVERIFIED");
            comboBox.addItem("VERIFIED");
            comboBox.addItem("SUSPENDED");
            comboBox.addItem("CLOSED");
        }
        
        @Override
        public Object getCellEditorValue() {
            return comboBox.getSelectedItem();
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            // Get the account for this row
            if (row < accountRowDataList.size()) {
                currentAccount = accountRowDataList.get(row).account;
                comboBox.setSelectedItem(value);
            } else {
                currentAccount = null;
            }
            return comboBox;
        }
        
        @Override
        public boolean stopCellEditing() {
            if (currentAccount != null) {
                String newStatusStr = (String) comboBox.getSelectedItem();
                handleStatusChange(currentAccount, newStatusStr);
            }
            return super.stopCellEditing();
        }
    }
    
    /**
     * Handles status change from dropdown.
     */
    private void handleStatusChange(Account account, String newStatusStr) {
        Account.AccountStatus currentStatus = account.getStatus();
        Account.AccountStatus newStatus = Account.AccountStatus.valueOf(newStatusStr);
        
        // If status hasn't actually changed, do nothing
        if (currentStatus == newStatus) {
            return;
        }
        
        boolean success = false;
        String action = "";
        
        // Apply transitions based on rules
        switch (newStatus) {
            case VERIFIED -> {
                if (currentStatus == Account.AccountStatus.UNVERIFIED) {
                    // Verify → Verified
                    success = adminController.verifyAccount(account.getAccountNumber());
                    action = "verified";
                } else if (currentStatus == Account.AccountStatus.SUSPENDED) {
                    // Appeal → Verified
                    success = adminController.appealAccount(account.getAccountNumber());
                    action = "appealed and verified";                
                } 
            }
            case SUSPENDED -> {
                if (currentStatus == Account.AccountStatus.VERIFIED) {
                    // Violation → Suspended
                    success = adminController.suspendAccount(account.getAccountNumber());
                    action = "suspended";
                }
            }
            case CLOSED -> {
                // AdminAction → Closed (from any state)
                success = adminController.closeAccount(account.getAccountNumber());
                action = "closed";
            }
            case UNVERIFIED -> {
                // Cannot go back to UNVERIFIED
                JOptionPane.showMessageDialog(this,
                        "Cannot change account status back to UNVERIFIED.",
                        "Invalid Status Change",
                        JOptionPane.WARNING_MESSAGE);
                if (usersAccountsTable.isEditing()) {
                    usersAccountsTable.getCellEditor().cancelCellEditing();
                }
                // Reset combo box to current status
                refreshUsersAccountsTable();
                return;
            }
        }
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Account #" + account.getAccountNumber() + " has been " + action + " successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            // Refresh table to show updated status
            refreshUsersAccountsTable();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to change account status. Invalid transition from " + currentStatus + " to " + newStatus + ".",
                    "Status Change Error",
                    JOptionPane.ERROR_MESSAGE);

            if (usersAccountsTable.isEditing()) {
                usersAccountsTable.getCellEditor().cancelCellEditing();
            }
            // Reset combo box to current status
            refreshUsersAccountsTable();
        }
    }
    
    /**
     * Refreshes the users accounts table to reflect current statuses.
     */
    private void refreshUsersAccountsTable() {
        loadUsersAccounts();
    }

    /**
     * Handles approve transaction action.
     */
    private void handleApproveTransaction() {
        int selectedRow = pendingTransactionsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a transaction to approve.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String transactionId = (String) pendingTransactionsModel.getValueAt(selectedRow, 0);
        if (transactionId.equals("No pending transactions")) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Approve transaction " + transactionId + "?",
                "Confirm Approval",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = adminController.approveTransaction(transactionId);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Transaction approved successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to approve transaction.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles reject transaction action.
     */
    private void handleRejectTransaction() {
        int selectedRow = pendingTransactionsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a transaction to reject.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String transactionId = (String) pendingTransactionsModel.getValueAt(selectedRow, 0);
        if (transactionId.equals("No pending transactions")) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Reject transaction " + transactionId + "?",
                "Confirm Rejection",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = adminController.rejectTransaction(transactionId);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Transaction rejected successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to reject transaction.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    /**
     * Handles logout action.
     * Logs out the user and navigates back to sign-in page.
     */
    private void handleLogout() {
        if (userController != null) {
            userController.logout();
            SwingUtilities.invokeLater(() -> {
                SignInView signInView = new SignInView(userController, accountController, adminController);
                signInView.setVisible(true);
                dispose(); // Close admin view
            });
        }
    }
}

