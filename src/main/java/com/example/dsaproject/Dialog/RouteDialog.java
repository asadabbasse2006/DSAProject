package com.example.dsaproject.Dialog;


import com.example.dsaproject.Model.Route;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import com.example.dsaproject.Util.ValidationUtil;
import java.util.Arrays;

public class RouteDialog extends Dialog<Route> {

    private TextField routeNameField;
    private TextField totalStopsField;
    private TextArea stopsArea;
    private TextField distanceField;
    private Route existingRoute;

    public RouteDialog(Route route) {
        this.existingRoute = route;

        if (route == null) {
            setTitle("Add New Route");
            setHeaderText("Enter route details");
        } else {
            setTitle("Edit Route");
            setHeaderText("Update route: " + route.getRouteName());
        }

        ButtonType saveButtonType = new ButtonType(
                route == null ? "Add" : "Update", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        routeNameField = new TextField();
        routeNameField.setPromptText("Route Name");
        routeNameField.setPrefWidth(350);

        totalStopsField = new TextField();
        totalStopsField.setPromptText("Total Stops");

        distanceField = new TextField();
        distanceField.setPromptText("Distance (km)");

        stopsArea = new TextArea();
        stopsArea.setPromptText("Enter stops (one per line)");
        stopsArea.setPrefRowCount(6);

        grid.add(new Label("Route Name:"), 0, 0);
        grid.add(routeNameField, 1, 0);
        grid.add(new Label("Total Stops:"), 0, 1);
        grid.add(totalStopsField, 1, 1);
        grid.add(new Label("Distance (km):"), 0, 2);
        grid.add(distanceField, 1, 2);
        grid.add(new Label("Stops:"), 0, 3);
        grid.add(stopsArea, 1, 3);

        // Populate fields if editing
        if (route != null) {
            routeNameField.setText(route.getRouteName());
            totalStopsField.setText(String.valueOf(route.getTotalStops()));
            distanceField.setText(String.valueOf(route.getDistance()));
            stopsArea.setText(String.join("\n", route.getStops()));
        }

        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (validateInputs()) {
                    Route newRoute;
                    if (existingRoute != null) {
                        newRoute = existingRoute;
                    } else {
                        newRoute = new Route(0, "", 0);
                    }

                    newRoute.setRouteName(routeNameField.getText());
                    newRoute.setTotalStops(Integer.parseInt(totalStopsField.getText()));
                    newRoute.setDistance(Double.parseDouble(distanceField.getText()));
                    newRoute.setStops(Arrays.asList(stopsArea.getText().split("\n")));

                    return newRoute;
                }
            }
            return null;
        });
    }

    private boolean validateInputs() {
        if (!ValidationUtil.isNotEmpty(routeNameField.getText())) {
            showError("Please enter route name");
            return false;
        }

        if (!ValidationUtil.isPositiveNumber(totalStopsField.getText())) {
            showError("Please enter valid number of stops");
            return false;
        }

        if (!ValidationUtil.isNotEmpty(stopsArea.getText())) {
            showError("Please enter at least one stop");
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
