package com.example.dsaproject.Controllers;

import com.example.dsaproject.Model.User;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import javafx.geometry.Insets;

class SignupDialog extends Dialog<User> {

    public SignupDialog() {
        setTitle("Sign Up");
        setHeaderText("Create a new account");

        ButtonType signupButtonType = new ButtonType("Sign Up", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(signupButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0b10100));

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.setItems(FXCollections.observableArrayList("Student", "Admin"));
        roleCombo.setValue("Student");

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Confirm Password:"), 0, 3);
        grid.add(confirmPasswordField, 1, 3);
        grid.add(new Label("Role:"), 0, 4);
        grid.add(roleCombo, 1, 4);

        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == signupButtonType) {
                String name = nameField.getText();
                String email = emailField.getText();
                String password = passwordField.getText();
                String confirmPassword = confirmPasswordField.getText();
                String role = roleCombo.getValue().toLowerCase();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    showAlert("Error", "Please fill in all fields");
                    return null;
                }

                if (!password.equals(confirmPassword)) {
                    showAlert("Error", "Passwords do not match");
                    return null;
                }

                DatabaseManager dbManager = DatabaseManager.getInstance();
                if (dbManager.registerUser(name, email, password, role)) {
                    showAlert("Success", "Registration successful! Please login.");
                    return new User(0, name, email, role);
                } else {
                    showAlert("Error", "Registration failed. Email might already exist.");
                }
            }
            return null;
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Error") ?
                Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
