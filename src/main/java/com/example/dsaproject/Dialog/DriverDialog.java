package com.example.dsaproject.Dialog;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import com.example.dsaproject.Model.Driver;
import com.example.dsaproject.Util.ValidationUtil;

public class DriverDialog extends Dialog<Driver> {

    private TextField nameField;
    private TextField phoneField;
    private TextField licenseField;
    private TextField emailField;
    private TextField experienceField;
    private Driver existingDriver;

    public DriverDialog(Driver driver) {
        this.existingDriver = driver;

        if (driver == null) {
            setTitle("Add New Driver");
            setHeaderText("Enter driver details");
        } else {
            setTitle("Edit Driver");
            setHeaderText("Update driver: " + driver.getName());
        }

        ButtonType saveButtonType = new ButtonType(
                driver == null ? "Add" : "Update", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        nameField = new TextField();
        nameField.setPromptText("Driver Name");
        nameField.setPrefWidth(300);

        phoneField = new TextField();
        phoneField.setPromptText("0300-1234567");

        licenseField = new TextField();
        licenseField.setPromptText("License Number");

        emailField = new TextField();
        emailField.setPromptText("Email (optional)");

        experienceField = new TextField();
        experienceField.setPromptText("Years of Experience");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("License No:"), 0, 2);
        grid.add(licenseField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Experience (years):"), 0, 4);
        grid.add(experienceField, 1, 4);

        // Populate fields if editing
        if (driver != null) {
            nameField.setText(driver.getName());
            phoneField.setText(driver.getPhone());
            licenseField.setText(driver.getLicenseNo());
            emailField.setText(driver.getEmail());
            experienceField.setText(String.valueOf(driver.getExperience()));
        }

        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (validateInputs()) {
                    Driver newDriver;
                    if (existingDriver != null) {
                        newDriver = existingDriver;
                    } else {
                        newDriver = new Driver(0, "", "", "");
                    }

                    newDriver.setName(nameField.getText());
                    newDriver.setPhone(phoneField.getText());
                    newDriver.setLicenseNo(licenseField.getText());
                    newDriver.setEmail(emailField.getText());

                    if (ValidationUtil.isPositiveNumber(experienceField.getText())) {
                        newDriver.setExperience(Integer.parseInt(experienceField.getText()));
                    }

                    return newDriver;
                }
            }
            return null;
        });
    }

    private boolean validateInputs() {
        if (!ValidationUtil.isNotEmpty(nameField.getText())) {
            showError("Please enter driver name");
            return false;
        }

        if (!ValidationUtil.isValidPhone(phoneField.getText())) {
            showError("Please enter valid phone number (format: 0300-1234567)");
            return false;
        }

        if (!ValidationUtil.isNotEmpty(licenseField.getText())) {
            showError("Please enter license number");
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