package org.example;

import javax.swing.*;
import java.awt.*;

/**
 * User registration screen.
 * Allows new users to register with the banking system.
 */
public class RegistrationView extends JFrame {
    private final UserController userController;
    private final AccountController accountController;

    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField phoneField;
    private JButton registerButton;

    public RegistrationView(UserController userController, AccountController accountController) {
        this.userController = userController;
        this.accountController = accountController;

        initializeUI();
    }

    /**
     * Initializes the user interface components.
     */
    private void initializeUI() {
        setTitle("Banking System - User Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        // Create main panel with vertical layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title label
        JLabel titleLabel = new JLabel("User Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Name field
        mainPanel.add(createFieldPanel("Name:", nameField = new JTextField(20)));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Email field
        mainPanel.add(createFieldPanel("Email:", emailField = new JTextField(20)));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Password field
        mainPanel.add(createFieldPanel("Password:", passwordField = new JPasswordField(20)));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Phone field (optional)
        mainPanel.add(createFieldPanel("Phone (optional):", phoneField = new JTextField(20)));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Register button
        registerButton = new JButton("Register");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> handleRegistration());
        mainPanel.add(registerButton);

        add(mainPanel);
    }

    /**
     * Creates a panel with a label and text field.
     */
    private JPanel createFieldPanel(String labelText, JTextField field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(120, 25));
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        panel.add(field);

        return panel;
    }

    /**
     * Handles the registration process.
     * Calls UserController to register the user and opens UserHomeView on success.
     */
    private void handleRegistration() {
        // Get input values
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String phone = phoneField.getText().trim();

        // Validate input
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Name, Email, and Password are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Convert empty phone to null
        if (phone.isEmpty()) {
            phone = null;
        }

        // Call controller to register user
        boolean success = userController.registerUser(name, email, password, phone);

        if (success) {
            // Show success message
            JOptionPane.showMessageDialog(this,
                    "Registration successful! User ID: " + userController.getCurrentUser().getUserId(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // Open User Home View
            openUserHomeView();
        } else {
            // Show error message
            JOptionPane.showMessageDialog(this,
                    "Registration failed. Email may already be registered or input is invalid.",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Opens the User Home View and closes the registration window.
     */
    private void openUserHomeView() {
        SwingUtilities.invokeLater(() -> {
            UserHomeView userHomeView = new UserHomeView(userController, accountController);
            userHomeView.setVisible(true);
            dispose(); // Close registration window
        });
    }
}
