package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Admin panel view.
 * Allows administrators to manage pending transactions and unverified accounts.
 */
public class AdminView extends JFrame {
    private final AdminController adminController;

    private JTable pendingTransactionsTable;
    private DefaultTableModel pendingTransactionsModel;
    private JTable unverifiedAccountsTable;
    private DefaultTableModel unverifiedAccountsModel;

    private JButton approveTransactionButton;
    private JButton rejectTransactionButton;
    private JButton verifyAccountButton;
    private JButton refreshButton;

    public AdminView(AdminController adminController) {
        this.adminController = adminController;

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

        // Unverified Accounts section
        JPanel accountsPanel = createUnverifiedAccountsPanel();
        centerPanel.add(accountsPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Refresh button at bottom
        JPanel bottomPanel = new JPanel();
        refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(e -> loadData());
        bottomPanel.add(refreshButton);
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
     * Creates the unverified accounts panel.
     */
    private JPanel createUnverifiedAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Unverified Accounts"));

        // Table setup
        String[] columnNames = {"Account Number", "Balance", "Status", "Owner ID"};
        unverifiedAccountsModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        unverifiedAccountsTable = new JTable(unverifiedAccountsModel);
        unverifiedAccountsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(unverifiedAccountsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        verifyAccountButton = new JButton("Verify Account");
        verifyAccountButton.addActionListener(e -> handleVerifyAccount());
        buttonPanel.add(verifyAccountButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Loads data from the admin controller.
     */
    private void loadData() {
        loadPendingTransactions();
        loadUnverifiedAccounts();
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
     * Loads unverified accounts into the table.
     */
    private void loadUnverifiedAccounts() {
        unverifiedAccountsModel.setRowCount(0);

        List<Account> unverifiedAccounts = adminController.getUnverifiedAccounts();

        if (unverifiedAccounts.isEmpty()) {
            Object[] emptyRow = {"No unverified accounts", "-", "-", "-"};
            unverifiedAccountsModel.addRow(emptyRow);
            return;
        }

        for (Account account : unverifiedAccounts) {
            Object[] row = new Object[4];
            row[0] = account.getAccountNumber();
            row[1] = String.format("$%.2f", account.getBalance());
            row[2] = account.getStatus().toString();
            row[3] = account.getOwnerUserId() != null ? account.getOwnerUserId() : "-";

            unverifiedAccountsModel.addRow(row);
        }
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
     * Handles verify account action.
     */
    private void handleVerifyAccount() {
        int selectedRow = unverifiedAccountsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select an account to verify.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object accountNumberObj = unverifiedAccountsModel.getValueAt(selectedRow, 0);
        if (accountNumberObj.equals("No unverified accounts")) {
            return;
        }

        int accountNumber = (Integer) accountNumberObj;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Verify account #" + accountNumber + "?",
                "Confirm Verification",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = adminController.verifyAccount(accountNumber);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Account verified successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to verify account.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
