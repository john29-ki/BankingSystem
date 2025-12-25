package org.example.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.example.controllers.AccountController;
import org.example.controllers.AdminController;
import org.example.controllers.UserController;
import org.example.model.User;

/**
 * Sign-in screen.
 * Allows users to sign in with email and password.
 */
public class SignInView extends JFrame {
    private final UserController userController;
    private final AccountController accountController;
    private final AdminController adminController;

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton signInButton;
    private JButton registerButton;

    public SignInView(UserController userController, AccountController accountController, AdminController adminController) {
        this.userController = userController;
        this.accountController = accountController;
        this.adminController = adminController;

        initializeUI();
    }

    /**
     * Initializes the user interface components.
     */
    private void initializeUI() {
        setTitle("Banking System - Sign In");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        // Create main panel with vertical layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title label
        JLabel titleLabel = new JLabel("Sign In");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Email field
        mainPanel.add(createFieldPanel("Email:", emailField = new JTextField(20)));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Password field
        mainPanel.add(createFieldPanel("Password:", passwordField = new JPasswordField(20)));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

        signInButton = new JButton("Sign In");
        signInButton.addActionListener(e -> handleSignIn());
        buttonPanel.add(signInButton);

        registerButton = new JButton("Register");
        registerButton.addActionListener(e -> handleRegister());
        buttonPanel.add(registerButton);

        mainPanel.add(buttonPanel);

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
        
        // Set preferred size to match RegistrationView fields
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 25));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.add(field);

        return panel;
    }

    /**
     * Handles the sign-in process.
     */
    private void handleSignIn() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Email and Password are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Attempt login
        boolean success = userController.login(email, password);

        if (success) {
            User currentUser = userController.getCurrentUser();
            
            // Navigate based on user role
            if (currentUser.getRole() == User.Role.ADMIN) {
                openAdminView();
            } else {
                openUserHomeView();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid email or password.",
                    "Sign In Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the register button click.
     * Opens the registration view.
     */
    private void handleRegister() {
        SwingUtilities.invokeLater(() -> {
            RegistrationView registrationView = new RegistrationView(userController, accountController);
            registrationView.setVisible(true);
            dispose(); // Close sign-in window
        });
    }

    /**
     * Opens the User Home View and closes the sign-in window.
     */
    private void openUserHomeView() {
        SwingUtilities.invokeLater(() -> {
            UserHomeView userHomeView = new UserHomeView(userController, accountController, adminController);
            userHomeView.setVisible(true);
            dispose(); // Close sign-in window
        });
    }

    /**
     * Opens the Admin View and closes the sign-in window.
     */
    private void openAdminView() {
        SwingUtilities.invokeLater(() -> {
            AdminView adminView = new AdminView(adminController, userController, accountController);
            adminView.setVisible(true);
            dispose(); // Close sign-in window
        });
    }
}

