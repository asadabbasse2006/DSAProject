package com.example.dsaproject.Controllers;

import com.example.dsaproject.Main;
import com.example.dsaproject.Model.User;
import com.example.dsaproject.Util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private RadioButton studentRadio;
    @FXML private RadioButton adminRadio;
    @FXML private Label errorLabel;
    @FXML private ToggleGroup userTypeGroup;

    private DatabaseManager dbManager = DatabaseManager.getInstance();

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }
        if (!email.endsWith("cuisahiwal.edu.pk")){
            showError("Your email does not offered by your institution.");
            return;
        }

        boolean isStudent = studentRadio.isSelected();
        String role = isStudent ? "student" : "admin";

        User user = dbManager.authenticateUser(email, password, role);

        if (user != null) {
            SessionManager.getInstance().setCurrentUser(user);

            try {
                if (isStudent) {
                    Main.showStudentDashboard();
                } else {
                    Main.showAdminDashboard();
                }
            } catch (Exception e) {
                showError("Error loading dashboard: " + e.getMessage());
            }
        } else {
            showError("Invalid email or password");
        }
    }

    @FXML
    private void handleSignup() {
        try {
            SignupDialog dialog = new SignupDialog();
            dialog.showAndWait();
        } catch (Exception e) {
            showError("Error opening signup: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}