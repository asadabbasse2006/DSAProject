package com.example.dsaproject.Controllers;

import com.example.dsaproject.AlertMessage;
import com.example.dsaproject.DAO.databaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class signupController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Hyperlink loginLink;
    @FXML private Button signupBtn;

    private Connection connect;
    private PreparedStatement preparedStatement;
    private final AlertMessage alertMessage = new AlertMessage();

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    public void handleSignup() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            alertMessage.errorMessage("All fields are required!");
            return;
        }

        if (password.length() < 8) {
            alertMessage.errorMessage("Your password must contain at least 8 characters!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            alertMessage.errorMessage("Passwords do not match!");
            return;
        }

        if (!email.endsWith("@students.cuisahiwal.edu.pk")) {
            alertMessage.errorMessage("Email must be CU Student email!");
            return;
        }

        // Check if email is already registered or not
        String checkSql = "SELECT * FROM users WHERE email = ?";
        String insertSql = "INSERT INTO users (name, email, password, role, status) VALUES (?, ?, ?, 'Student', 'Pending')";

        connect = databaseManager.getConnection();

        try {
            // Check for duplicate email
            PreparedStatement checkStmt = connect.prepareStatement(checkSql);
            checkStmt.setString(1, email);

            var result = checkStmt.executeQuery();
            if (result.next()) {
                alertMessage.errorMessage("This email is already registered!");
                return;
            }

            PreparedStatement insertStmt = connect.prepareStatement(insertSql);
            insertStmt.setString(1, name);
            insertStmt.setString(2, email);
            insertStmt.setString(3, password);

            int rows = insertStmt.executeUpdate();
            if (rows > 0) {
                alertMessage.successMessage("User registered successfully!");
                clearFields();
            } else {
                alertMessage.errorMessage("Registration failed!");
            }

        } catch (SQLException e) {
            alertMessage.errorMessage("Database error occurred!");
            System.out.println("Error: " + e.getMessage());
        }
    }


    public void handleLogin(ActionEvent event){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/dsaproject/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 800));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

