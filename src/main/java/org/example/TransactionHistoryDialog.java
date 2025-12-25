package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Transaction history dialog.
 * Displays transaction history for a specific account in a table.
 */
public class TransactionHistoryDialog extends JDialog {
    private final Account account;
    private final AccountController accountController;

    private JTable transactionTable;
    private DefaultTableModel tableModel;

    public TransactionHistoryDialog(JFrame parent, Account account, AccountController accountController) {
        super(parent, "Transaction History", true);
        this.account = account;
        this.accountController = accountController;

        initializeUI();
        loadTransactionData();
    }

    /**
     * Initializes the user interface components.
     */
    private void initializeUI() {
        setSize(700, 400);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Transaction History - Account #" + account.getAccountNumber());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"Transaction ID", "Type", "Amount", "Status", "Timestamp"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        transactionTable = new JTable(tableModel);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.getTableHeader().setReorderingAllowed(false);

        // Set column widths
        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(250); // Transaction ID
        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Type
        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Amount
        transactionTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Status
        transactionTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Timestamp

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        add(scrollPane, BorderLayout.CENTER);

        // Close button
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads transaction data from the account controller.
     */
    private void loadTransactionData() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Get transaction history from controller
        List<Transaction> transactions = accountController.getTransactionHistory(account);

        if (transactions.isEmpty()) {
            // Show empty message
            Object[] emptyRow = {"No transactions", "-", "-", "-", "-"};
            tableModel.addRow(emptyRow);
            return;
        }

        // Date formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Populate table with transaction data
        for (Transaction transaction : transactions) {
            Object[] row = new Object[5];
            row[0] = transaction.getTransactionId();
            row[1] = transaction.getType().toString();
            row[2] = String.format("$%.2f", transaction.getAmount());
            row[3] = transaction.getStatus().toString();
            row[4] = transaction.getTimestamp().format(formatter);

            tableModel.addRow(row);
        }
    }
}
