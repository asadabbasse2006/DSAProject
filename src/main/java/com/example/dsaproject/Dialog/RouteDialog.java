package com.example.dsaproject.Dialog;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.control.Alert.AlertType;
import com.example.dsaproject.Model.Route;
import com.example.dsaproject.Util.ValidationUtil;
import javafx.stage.Modality;

public class RouteDialog extends Dialog<Route> {

    private TextField routeNameField;
    private TextField stopsCountField;
    private TextArea stopsArea;
    private Route existingRoute;

    public RouteDialog(Route route) {

        initModality(Modality.APPLICATION_MODAL);
        this.existingRoute = route;

        setTitle(route == null ? "Add New Route" : "Edit Route");
        setHeaderText(route == null
                ? "Enter route details"
                : "Update route: " + route.getRouteName());

        ButtonType saveButtonType = new ButtonType(
                route == null ? "Add" : "Update",
                ButtonBar.ButtonData.OK_DONE
        );
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(15);
        grid.setVgap(12);

        routeNameField = new TextField();
        routeNameField.setPromptText("Route Name");

        stopsCountField = new TextField();
        stopsCountField.setPromptText("Total Stops");

        stopsArea = new TextArea();
        stopsArea.setPromptText("Enter stops separated by commas");
        stopsArea.setPrefRowCount(3);

        grid.add(new Label("Route Name:"), 0, 0);
        grid.add(routeNameField, 1, 0);
        grid.add(new Label("Total Stops:"), 0, 1);
        grid.add(stopsCountField, 1, 1);
        grid.add(new Label("Stops:"), 0, 2);
        grid.add(stopsArea, 1, 2);

        // Populate if editing
        if (route != null) {
            routeNameField.setText(route.getRouteName());
            stopsCountField.setText(String.valueOf(route.getTotalStops()));
            stopsArea.setText(route.getStops());
        }

        getDialogPane().setContent(grid);

        setResultConverter(button -> {
            if (button == saveButtonType && validateInputs()) {

                int totalStops = Integer.parseInt(stopsCountField.getText());

                if (existingRoute != null) {
                    existingRoute.setRouteName(routeNameField.getText());
                    existingRoute.setTotalStops(totalStops);
                    existingRoute.setStops(stopsArea.getText());
                    return existingRoute;
                } else {
                    return new Route(
                            0,
                            routeNameField.getText(),
                            totalStops,
                            stopsArea.getText()
                    );
                }
            }
            return null;
        });
    }

    private boolean validateInputs() {

        if (!ValidationUtil.isValidName(routeNameField.getText())) {
            showError("Route name is invalid");
            return false;
        }

        if (stopsCountField.getText().isEmpty()) {
            showError("Total stops cannot be empty");
            return false;
        }

        int stops;
        try {
            stops = Integer.parseInt(stopsCountField.getText());
        } catch (NumberFormatException e) {
            showError("Total stops must be a number");
            return false;
        }

        if (stops <= 0) {
            showError("Total stops must be greater than zero");
            return false;
        }

        if (stopsArea.getText().isEmpty()) {
            showError("Stops list cannot be empty");
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
