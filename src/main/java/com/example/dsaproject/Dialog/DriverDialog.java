package com.example.dsaproject.Dialog;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.control.Alert.AlertType;
import com.example.dsaproject.Model.Driver;
import com.example.dsaproject.Util.ValidationUtil;
import javafx.stage.Modality;

public class DriverDialog extends Dialog<Driver> {

    private TextField nameField;
    private TextField phoneField;
    private TextField licenseField;
    private Driver existingDriver;

    public DriverDialog(Driver driver) {

        initModality(Modality.APPLICATION_MODAL); // ⭐ FIX 1
        this.existingDriver = driver;

        setTitle(driver == null ? "Add New Driver" : "Edit Driver");
        setHeaderText(driver == null
                ? "Enter driver details"
                : "Update driver: " + driver.getName());

        ButtonType saveButtonType = new ButtonType(
                driver == null ? "Add" : "Update",
                ButtonBar.ButtonData.OK_DONE
        );
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(15);
        grid.setVgap(12);

        nameField = new TextField();
        nameField.setPromptText("Driver Name");

        phoneField = new TextField();
        phoneField.setPromptText("Phone Number");

        licenseField = new TextField();
        licenseField.setPromptText("License Number");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("License:"), 0, 2);
        grid.add(licenseField, 1, 2);

        // Populate if editing
        if (driver != null) {
            nameField.setText(driver.getName());
            phoneField.setText(driver.getPhone());
            licenseField.setText(driver.getLicenseNo());
        }

        getDialogPane().setContent(grid);

        setResultConverter(button -> {
            if (button == saveButtonType && validateInputs()) {

                if (existingDriver != null) {
                    existingDriver.setName(nameField.getText());
                    existingDriver.setPhone(phoneField.getText());
                    existingDriver.setLicenseNo(licenseField.getText());
                    return existingDriver;
                } else {
                    return new Driver(
                            0,
                            nameField.getText(),
                            phoneField.getText(),
                            licenseField.getText()
                    );
                }
            }
            return null;
        });
    }

    private boolean validateInputs() {

        if (!ValidationUtil.isValidName(nameField.getText())) {
            showError("Invalid driver name");
            return false;
        }

        if (!ValidationUtil.isValidPhone(phoneField.getText())) {
            showError("Invalid phone number");
            return false;
        }

        return true;
    }

    private void showError(String msg) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
