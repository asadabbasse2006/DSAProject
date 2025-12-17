package com.example.dsaproject.Dialog;

import com.example.dsaproject.Model.User;
import com.example.dsaproject.Util.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

/**
 * Dialog for student registration
 * Allows new students to create accounts
 */
public class SignupDialog extends Dialog<User> {

    private TextField nameField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private TextField registrationIdField;

    public SignupDialog() {
        setTitle("Student Registration");
        setHeaderText("Create New Account");
        setResizable(false);

        // Create the dialog pane
        DialogPane dialogPane = getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create form
        GridPane grid = createRegistrationForm();
        dialogPane.setContent(grid);

        // Style the dialog
        dialogPane.setStyle("-fx-background-color: white;");

        // Convert result when OK is clicked
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return validateAndRegister();
            }
            return null;
        });

        // Disable OK button initially
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        // Enable OK button when all fields are filled
        nameField.textProperty().addListener((obs, old, newVal) ->
                okButton.setDisable(!areAllFieldsFilled()));
        emailField.textProperty().addListener((obs, old, newVal) ->
                okButton.setDisable(!areAllFieldsFilled()));
        passwordField.textProperty().addListener((obs, old, newVal) ->
                okButton.setDisable(!areAllFieldsFilled()));
        confirmPasswordField.textProperty().addListener((obs, old, newVal) ->
                okButton.setDisable(!areAllFieldsFilled()));
        registrationIdField.textProperty().addListener((obs, old, newVal) ->
                okButton.setDisable(!areAllFieldsFilled()));
    }

    /**
     * Create registration form layout
     */
    private GridPane createRegistrationForm() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER);

        // Name field
        Label nameLabel = new Label("Full Name:");
        nameLabel.setStyle("-fx-font-weight: bold;");
        nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        nameField.setPrefWidth(300);

        // Email field
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-weight: bold;");
        emailField = new TextField();
        emailField.setPromptText("example@comsats.edu.pk");
        emailField.setPrefWidth(300);

        // Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-weight: bold;");
        passwordField = new PasswordField();
        passwordField.setPromptText("Minimum 6 characters");
        passwordField.setPrefWidth(300);

        // Confirm password field
        Label confirmPasswordLabel = new Label("Confirm Password:");
        confirmPasswordLabel.setStyle("-fx-font-weight: bold;");
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Re-enter password");
        confirmPasswordField.setPrefWidth(300);

        // Registration ID field
        Label regIdLabel = new Label("Registration ID:");
        regIdLabel.setStyle("-fx-font-weight: bold;");
        registrationIdField = new TextField();
        registrationIdField.setPromptText("e.g., SP24-BSE-082");
        registrationIdField.setPrefWidth(300);

        // Add fields to grid
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(confirmPasswordLabel, 0, 3);
        grid.add(confirmPasswordField, 1, 3);
        grid.add(regIdLabel, 0, 4);
        grid.add(registrationIdField, 1, 4);

        // Add info label
        Label infoLabel = new Label("* All fields are required");
        infoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        grid.add(infoLabel, 0, 5, 2, 1);

        return grid;
    }

    /**
     * Check if all fields are filled
     */
    private boolean areAllFieldsFilled() {
        return !nameField.getText().trim().isEmpty() &&
                !emailField.getText().trim().isEmpty() &&
                !passwordField.getText().isEmpty() &&
                !confirmPasswordField.getText().isEmpty() &&
                !registrationIdField.getText().trim().isEmpty();
    }

    /**
     * Validate input and register user
     */
    private User validateAndRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String registrationId = registrationIdField.getText().trim();

        // Validate name
        if (!ValidationUtil.isValidName(name)) {
            AlertMessage.showError("Validation Error",
                    "Name must be between 2 and 100 characters");
            return null;
        }

        // Validate email
        if (!ValidationUtil.isValidEmail(email)) {
            AlertMessage.showError("Validation Error",
                    "Please enter a valid email address\nExample: student@comsats.edu.pk");
            return null;
        }

        // Validate password
        if (!ValidationUtil.isValidPassword(password)) {
            AlertMessage.showError("Validation Error",
                    "Password must be at least 6 characters long");
            return null;
        }

        // Check password match
        if (!password.equals(confirmPassword)) {
            AlertMessage.showError("Validation Error",
                    "Passwords do not match");
            return null;
        }

        // Validate registration ID
        if (ValidationUtil.isEmpty(registrationId)) {
            AlertMessage.showError("Validation Error",
                    "Registration ID is required");
            return null;
        }

        // Attempt registration
        DatabaseManager dbManager = DatabaseManager.getInstance();
        boolean success = dbManager.registerUser(name, email, password, registrationId);

        if (!success) {
            AlertMessage.showError("Registration Failed",
                    "Email already exists or database error.\nPlease try a different email address.");
            return null;
        }

        // Return user object on success
        return new User(0, name, email, "student", registrationId);
    }
}