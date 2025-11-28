package com.example.dsaproject.Dialog;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import com.example.dsaproject.Model.User;
import com.example.dsaproject.Util.DatabaseManager;
import com.example.dsaproject.Util.ValidationUtil;

public class SignupDialog extends Dialog<User> {

    private TextField nameField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private TextField phoneField;
    private TextArea addressArea;
    private ComboBox<String> roleCombo;

    public SignupDialog() {
        setTitle("Create New Account");
        setHeaderText("Sign up for COMSATS Transport System");

        ButtonType signupButtonType = new ButtonType("Sign Up", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(signupButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.setPrefWidth(300);

        emailField = new TextField();
        emailField.setPromptText("Email Address");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password (min 6 characters)");

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        phoneField = new TextField();
        phoneField.setPromptText("0300-1234567");

        addressArea = new TextArea();
        addressArea.setPromptText("Your Address");
        addressArea.setPrefRowCount(2);

        roleCombo = new ComboBox<>();
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
        grid.add(new Label("Phone:"), 0, 4);
        grid.add(phoneField, 1, 4);
        grid.add(new Label("Address:"), 0, 5);
        grid.add(addressArea, 1, 5);
        grid.add(new Label("Register as:"), 0, 6);
        grid.add(roleCombo, 1, 6);

        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == signupButtonType) {
                if (validateInputs()) {
                    DatabaseManager dbManager = DatabaseManager.getInstance();
                    if (dbManager.registerUser(
                            nameField.getText(),
                            emailField.getText(),
                            passwordField.getText(),
                            roleCombo.getValue().toLowerCase())) {
                        return new User(0, nameField.getText(), emailField.getText(),
                                roleCombo.getValue().toLowerCase());
                    }
                }
            }
            return null;
        });
    }

    private boolean validateInputs() {
        if (!ValidationUtil.isNotEmpty(nameField.getText())) {
            showError("Please enter your name");
            return false;
        }

        if (!ValidationUtil.isValidEmail(emailField.getText())) {
            showError("Please enter a valid email address");
            return false;
        }

        if (!ValidationUtil.isValidPassword(passwordField.getText())) {
            showError("Password must be at least 6 characters");
            return false;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("Passwords do not match");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}