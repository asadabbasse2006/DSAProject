package com.example.dsaproject.Controllers;

import com.example.dsaproject.Controllers.COMSATSTransport;
import com.example.dsaproject.Dialog.SignupDialog;
import com.example.dsaproject.Util.*;
import com.example.dsaproject.Model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

/**
 * Controller for Login Screen
 * Handles user authentication and navigation
 */
public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private RadioButton studentRadio;
    @FXML private RadioButton adminRadio;
    @FXML private Label errorLabel;
    @FXML private ToggleGroup userTypeGroup;
    @FXML private Button loginButton;
    @FXML private Hyperlink signupLink;
    @FXML private ProgressIndicator progressIndicator;

    private DatabaseManager dbManager;

    @FXML
    private void initialize() {
        dbManager = DatabaseManager.getInstance();
        errorLabel.setVisible(false);

        if (progressIndicator != null) {
            progressIndicator.setVisible(false);
        }

        // Add input validation listeners
        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            errorLabel.setVisible(false);
        });

        // Add enter key support
        passwordField.setOnAction(e -> handleLogin());

        // Set default selection
        studentRadio.setSelected(true);

        // Set up toggle group
        userTypeGroup = new ToggleGroup();
        studentRadio.setToggleGroup(userTypeGroup);
        adminRadio.setToggleGroup(userTypeGroup);
    }

    /**
     * Handle login button click
     */
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Validate inputs
        if (!validateInputs(email, password)) {
            return;
        }

        // Show loading indicator
        showLoading(true);

        // Determine user role
        boolean isStudent = studentRadio.isSelected();
        String role = isStudent ? "student" : "admin";

        // Authenticate user in separate thread
        new Thread(() -> {
            try {
                Thread.sleep(300); // Simulate network delay
                User user = dbManager.authenticateUser(email, password, role);

                javafx.application.Platform.runLater(() -> {
                    showLoading(false);

                    if (user != null) {
                        SessionManager.getInstance().setCurrentUser(user);
                        navigateToDashboard(isStudent);
                    } else {
                        showError("Invalid email, password, or user type selected");
                    }
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    showLoading(false);
                    showError("Login failed: " + e.getMessage());
                });
            }
        }).start();
    }

    /**
     * Handle signup link click
     */
    @FXML
    private void handleSignup() {
        try {
            SignupDialog dialog = new SignupDialog();
            dialog.showAndWait().ifPresent(user -> {
                if (user != null) {
                    emailField.setText(user.getEmail());
                    AlertMessage.showSuccess("Registration Successful",
                            "Account created! Please login with your credentials.");
                }
            });
        } catch (Exception e) {
            AlertMessage.showError("Signup Error",
                    "Failed to open signup dialog: " + e.getMessage());
        }
    }

    /**
     * Validate login inputs
     */
    private boolean validateInputs(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return false;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            showError("Please enter a valid email address");
            return false;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return false;
        }

        return true;
    }

    /**
     * Navigate to appropriate dashboard
     */
    private void navigateToDashboard(boolean isStudent) {
        try {
            if (isStudent) {
                COMSATSTransport.showStudentDashboard();
            } else {
                COMSATSTransport.showAdminDashboard();
            }
        } catch (Exception e) {
            showError("Navigation error: " + e.getMessage());
        }
    }

    /**
     * Show error message with animation
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);

        FadeTransition fade = new FadeTransition(Duration.millis(300), errorLabel);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    /**
     * Show/hide loading indicator
     */
    private void showLoading(boolean show) {
        if (progressIndicator != null) {
            progressIndicator.setVisible(show);
        }
        loginButton.setDisable(show);
        emailField.setDisable(show);
        passwordField.setDisable(show);
        studentRadio.setDisable(show);
        adminRadio.setDisable(show);
    }
}