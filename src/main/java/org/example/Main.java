package org.example;

import javax.swing.SwingUtilities;

import org.example.controllers.AccountController;
import org.example.controllers.AdminController;
import org.example.controllers.UserController;
import org.example.ui.SignInView;

/**
 * Main entry point for the Banking System application.
 * Initializes controllers, loads dummy data, and launches the GUI.
 */
public class Main {
    public static void main(String[] args) {
        // Launch GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Initialize controllers
            UserController userController = new UserController();
            AccountController accountController = new AccountController(userController);
            AdminController adminController = new AdminController(userController, accountController);

            // Initialize dummy data
            DummyDataInitializer.initializeDummyData(userController, accountController);

            // Launch Sign In View
            SignInView signInView = new SignInView(userController, accountController, adminController);
            signInView.setVisible(true);
        });
    }
}
