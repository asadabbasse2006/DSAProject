package com.example.dsaproject.Dialog;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import com.example.dsaproject.Model.Bus;
import com.example.dsaproject.Model.Driver;
import com.example.dsaproject.Util.ValidationUtil;
import java.util.List;

public class BusDialog extends Dialog<Bus> {

    private TextField busNoField;
    private TextField capacityField;
    private ComboBox<Driver> driverCombo;
    private Bus existingBus;

    public BusDialog(Bus bus, List<Driver> drivers) {
        this.existingBus = bus;

        if (bus == null) {
            setTitle("Add New Bus");
            setHeaderText("Enter bus details");
        } else {
            setTitle("Edit Bus");
            setHeaderText("Update bus: " + bus.getBusNo());
        }

        ButtonType saveButtonType = new ButtonType(
                bus == null ? "Add" : "Update", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        busNoField = new TextField();
        busNoField.setPromptText("Bus Number (e.g., A-123)");
        busNoField.setPrefWidth(300);

        capacityField = new TextField();
        capacityField.setPromptText("Capacity");

        driverCombo = new ComboBox<>();
        driverCombo.setItems(FXCollections.observableArrayList(drivers));
        driverCombo.setPromptText("Select Driver");
        driverCombo.setPrefWidth(300);

        grid.add(new Label("Bus Number:"), 0, 0);
        grid.add(busNoField, 1, 0);
        grid.add(new Label("Capacity:"), 0, 1);
        grid.add(capacityField, 1, 1);
        grid.add(new Label("Driver:"), 0, 2);
        grid.add(driverCombo, 1, 2);

        // Populate fields if editing
        if (bus != null) {
            busNoField.setText(bus.getBusNo());
            capacityField.setText(String.valueOf(bus.getCapacity()));

            // Select the current driver
            for (Driver d : drivers) {
                if (d.getDriverId() == bus.getDriverId()) {
                    driverCombo.setValue(d);
                    break;
                }
            }
        }

        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (validateInputs()) {
                    Driver selectedDriver = driverCombo.getValue();

                    if (existingBus != null) {
                        existingBus.setBusNo(busNoField.getText());
                        existingBus.setCapacity(Integer.parseInt(capacityField.getText()));
                        existingBus.setDriverId(selectedDriver.getDriverId());
                        existingBus.setDriverName(selectedDriver.getName());
                        return existingBus;
                    } else {
                        return new Bus(
                                0,
                                busNoField.getText(),
                                Integer.parseInt(capacityField.getText()),
                                selectedDriver.getDriverId(),
                                selectedDriver.getName()
                        );
                    }
                }
            }
            return null;
        });
    }

    private boolean validateInputs() {
        if (!ValidationUtil.isValidBusNo(busNoField.getText())) {
            showError("Please enter valid bus number (format: A-123)");
            return false;
        }

        if (!ValidationUtil.isPositiveNumber(capacityField.getText())) {
            showError("Please enter valid capacity");
            return false;
        }

        if (driverCombo.getValue() == null) {
            showError("Please select a driver");
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
