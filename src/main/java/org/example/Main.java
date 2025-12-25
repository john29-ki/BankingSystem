package org.example;

import javax.swing.*;

/**
 * Main entry point for the Banking System application.
 * Initializes controllers and launches the GUI.
 */
public class Main {
    public static void main(String[] args) {
        // Launch GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Initialize controllers
            UserController userController = new UserController();
            AccountController accountController = new AccountController(userController);
            AdminController adminController = new AdminController(userController, accountController);

            // Launch User Registration View
            RegistrationView registrationView = new RegistrationView(userController, accountController);
            registrationView.setVisible(true);

            // Optional: Launch Admin View in separate window for testing
            // Uncomment the lines below to open Admin Panel on startup
            
            AdminView adminView = new AdminView(adminController);
            adminView.setVisible(true);

        });
    }

    /**
     * Alternative main method for launching Admin Panel directly.
     * Use this for admin access without going through registration.
     */
    public static void launchAdminPanel(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Initialize controllers
            UserController userController = new UserController();
            AccountController accountController = new AccountController(userController);
            AdminController adminController = new AdminController(userController, accountController);

            // Launch Admin View
            AdminView adminView = new AdminView(adminController);
            adminView.setVisible(true);
        });
    }
}
