package com.example.dsaproject.Controllers;

import com.example.dsaproject.AlertMessage;
import com.example.dsaproject.Service.databaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class signupController {

    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField hiddenPasswordField;
    @FXML private TextField showPasswordField;
    @FXML private CheckBox showPasswordCheckBox;
    @FXML private Button registerButton;

    private Connection connect;
    private PreparedStatement preparedStatement;
    private final AlertMessage alertMessage = new AlertMessage();

    @FXML public void initialize() {
        roleComboBox.getItems().addAll("Admin", "User", "Driver");
        roleComboBox.setValue("User");
    }

    @FXML public void registerAccount() {
        String role = roleComboBox.getValue();
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = hiddenPasswordField.isVisible() ? hiddenPasswordField.getText() : showPasswordField.getText();

        if (role == null || username.isEmpty() || password.isEmpty() ||
                (role.equalsIgnoreCase("user") && email.isEmpty())) {
            alertMessage.errorMessage("Please fill in all required fields!");
            return;
        }

        connect = databaseManager.getConnection();
        String sql = "";

        try {
            switch (role.toLowerCase()) {
                case "admin":
                    sql = "INSERT INTO admin (admin_username, password) VALUES (?, ?)";
                    preparedStatement = connect.prepareStatement(sql);
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);
                    break;

                case "user":
                    sql = "INSERT INTO users (email, password, status) VALUES (?, ?, 'Pending')";
                    preparedStatement = connect.prepareStatement(sql);
                    preparedStatement.setString(1, email);
                    preparedStatement.setString(2, password);
                    break;

                case "driver":
                    sql = "INSERT INTO drivers (username, password, status) VALUES (?, ?, 'Pending')";
                    preparedStatement = connect.prepareStatement(sql);
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);
                    break;

                default:
                    alertMessage.errorMessage("Invalid role selected!");
                    return;
            }

            int rows = preparedStatement.executeUpdate();
            if (rows > 0) {
                alertMessage.successMessage(role + " registered successfully!");
                clearFields();
            } else {
                alertMessage.errorMessage("Registration failed. Please try again.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            alertMessage.errorMessage("Database error: " + e.getMessage());
        }
    }

    @FXML
    public void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            showPasswordField.setText(hiddenPasswordField.getText());
            showPasswordField.setVisible(true);
            hiddenPasswordField.setVisible(false);
        } else {
            hiddenPasswordField.setText(showPasswordField.getText());
            hiddenPasswordField.setVisible(true);
            showPasswordField.setVisible(false);
        }
    }

    private void clearFields() {
        usernameField.clear();
        emailField.clear();
        hiddenPasswordField.clear();
        showPasswordField.clear();
        roleComboBox.setValue("User");
        showPasswordCheckBox.setSelected(false);
        hiddenPasswordField.setVisible(true);
        showPasswordField.setVisible(false);
    }

    public void handleSignup(){

    }

    public void handleLogin(){

    }
}

