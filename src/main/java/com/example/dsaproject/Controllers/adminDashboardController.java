package com.example.dsaproject.Controllers;

import com.example.dsaproject.factory.AdminViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.*;
import javafx.scene.chart.*;
import com.example.dsaproject.Model.*;
import com.example.dsaproject.Util.*;
import com.example.dsaproject.Dialog.*;

/**
 * Controller for Admin Dashboard
 * Handles all administrative operations
 */
public class adminDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label emailLabel;
    @FXML private StackPane contentArea;
    @FXML private Label totalRoutesLabel;
    @FXML private Label totalBusesLabel;
    @FXML private Label totalDriversLabel;
    @FXML private Label activeBookingsLabel;
    @FXML private Button dashboardBtn;
    @FXML private Button routesBtn;
    @FXML private Button busesBtn;
    @FXML private Button driversBtn;
    @FXML private Button bookingsBtn;
    @FXML private Button reportsBtn;
    @FXML private Button waitingListBtn;
    @FXML private Button usersBtn;

    private DatabaseManager dbManager;
    private User currentUser;
    private Button selectedButton;

    @FXML
    private void initialize() {
        dbManager = DatabaseManager.getInstance();
        currentUser = SessionManager.getInstance().getCurrentUser();

        welcomeLabel.setText("Admin: " + currentUser.getName());
        emailLabel.setText(currentUser.getEmail());

        updateStats();
        showDashboard();
        highlightButton(dashboardBtn);
    }

    @FXML
    private void showDashboard() {
        VBox dashboard = AdminViewFactory.createDashboardView(dbManager);
        loadContent(dashboard);
        highlightButton(dashboardBtn);
    }

    @FXML
    private void showRoutes() {
        VBox routes = AdminViewFactory.createRoutesManagementView(
                dbManager, this::handleAddRoute, this::handleEditRoute, this::handleDeleteRoute);
        loadContent(routes);
        highlightButton(routesBtn);
    }

    @FXML
    private void showBuses() {
        VBox buses = AdminViewFactory.createBusesManagementView(
                dbManager, this::handleAddBus, this::handleEditBus, this::handleDeleteBus);
        loadContent(buses);
        highlightButton(busesBtn);
    }

    @FXML
    private void showDrivers() {
        VBox drivers = AdminViewFactory.createDriversManagementView(
                dbManager, this::handleAddDriver, this::handleEditDriver, this::handleDeleteDriver);
        loadContent(drivers);
        highlightButton(driversBtn);
    }

    @FXML
    private void showBookings() {
        VBox bookings = AdminViewFactory.createBookingsView(dbManager);
        loadContent(bookings);
        highlightButton(bookingsBtn);
    }

    @FXML
    private void showReports() {
        VBox reports = AdminViewFactory.createReportsView(dbManager);
        loadContent(reports);
        highlightButton(reportsBtn);
    }

    @FXML
    private void showWaitingList() {
        VBox waiting = AdminViewFactory.createWaitingListView(
                dbManager, this::handleConfirmWaiting);
        loadContent(waiting);
        highlightButton(waitingListBtn);
    }

    @FXML
    private void showUsers() {
        VBox users = AdminViewFactory.createUsersManagementView(dbManager);
        loadContent(users);
        highlightButton(usersBtn);
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Logout");
        confirm.setHeaderText("Logout from Admin Panel?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    SessionManager.getInstance().logout();
                    COMSATSTransport.showLoginScreen();
                } catch (Exception e) {
                    AlertMessage.showError("Logout Error", "Failed to logout: " + e.getMessage());
                }
            }
        });
    }

    private void loadContent(VBox content) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);
    }

    private void highlightButton(Button button) {
        if (selectedButton != null) {
            selectedButton.getStyleClass().remove("selected");
        }
        button.getStyleClass().add("selected");
        selectedButton = button;
    }

    private void updateStats() {
        totalRoutesLabel.setText(String.valueOf(dbManager.getAllRoutes().size()));
        totalBusesLabel.setText(String.valueOf(dbManager.getAllBuses().size()));
        totalDriversLabel.setText(String.valueOf(dbManager.getAllDrivers().size()));
        activeBookingsLabel.setText(String.valueOf(dbManager.getActiveBookingsCount()));
    }

    // CRUD Operation Handlers
    private void handleAddRoute() {
        RouteDialog dialog = new RouteDialog(null);
        dialog.showAndWait().ifPresent(route -> {
            if (dbManager.addRoute(route.getRouteName(), route.getTotalStops(), route.getStops())) {
                AlertMessage.showSuccess("Success", "Route added successfully!");
                updateStats();
                showRoutes();
            } else {
                AlertMessage.showError("Error", "Failed to add route");
            }
        });
    }

    private void handleEditRoute(Route route) {
        RouteDialog dialog = new RouteDialog(route);
        dialog.showAndWait().ifPresent(updatedRoute -> {
            if (dbManager.updateRoute(updatedRoute)) {
                AlertMessage.showSuccess("Success", "Route updated successfully!");
                showRoutes();
            } else {
                AlertMessage.showError("Error", "Failed to update route");
            }
        });
    }

    private void handleDeleteRoute(Route route) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Route");
        confirm.setHeaderText("Delete " + route.getRouteName() + "?");
        confirm.setContentText("This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (dbManager.deleteRoute(route.getRouteId())) {
                    AlertMessage.showSuccess("Deleted", "Route deleted successfully!");
                    updateStats();
                    showRoutes();
                } else {
                    AlertMessage.showError("Error", "Failed to delete route");
                }
            }
        });
    }

    private void handleAddBus() {
        BusDialog dialog = new BusDialog(null, dbManager.getAllDrivers());
        dialog.showAndWait().ifPresent(bus -> {
            if (dbManager.addBus(bus.getBusNo(), bus.getCapacity(), bus.getDriverId())) {
                AlertMessage.showSuccess("Success", "Bus added successfully!");
                updateStats();
                showBuses();
            } else {
                AlertMessage.showError("Error", "Failed to add bus");
            }
        });
    }

    private void handleEditBus(Bus bus) {
        BusDialog dialog = new BusDialog(bus, dbManager.getAllDrivers());
        dialog.showAndWait().ifPresent(updatedBus -> {
            if (dbManager.updateBus(updatedBus)) {
                AlertMessage.showSuccess("Success", "Bus updated successfully!");
                showBuses();
            } else {
                AlertMessage.showError("Error", "Failed to update bus");
            }
        });
    }

    private void handleDeleteBus(Bus bus) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Bus");
        confirm.setHeaderText("Delete Bus " + bus.getBusNo() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (dbManager.deleteBus(bus.getBusId())) {
                    AlertMessage.showSuccess("Deleted", "Bus deleted successfully!");
                    updateStats();
                    showBuses();
                } else {
                    AlertMessage.showError("Error", "Failed to delete bus");
                }
            }
        });
    }

    private void handleAddDriver() {
        DriverDialog dialog = new DriverDialog(null);
        dialog.showAndWait().ifPresent(driver -> {
            if (dbManager.addDriver(driver.getName(), driver.getPhone(), driver.getLicenseNo())) {
                AlertMessage.showSuccess("Success", "Driver added successfully!");
                updateStats();
                showDrivers();
            } else {
                AlertMessage.showError("Error", "Failed to add driver");
            }
        });
    }

    private void handleEditDriver(Driver driver) {
        DriverDialog dialog = new DriverDialog(driver);
        dialog.showAndWait().ifPresent(updatedDriver -> {
            if (dbManager.updateDriver(updatedDriver)) {
                AlertMessage.showSuccess("Success", "Driver updated successfully!");
                showDrivers();
            } else {
                AlertMessage.showError("Error", "Failed to update driver");
            }
        });
    }

    private void handleDeleteDriver(Driver driver) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Driver");
        confirm.setHeaderText("Delete driver " + driver.getName() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (dbManager.deleteDriver(driver.getDriverId())) {
                    AlertMessage.showSuccess("Deleted", "Driver deleted successfully!");
                    updateStats();
                    showDrivers();
                } else {
                    AlertMessage.showError("Error", "Failed to delete driver");
                }
            }
        });
    }

    private void handleConfirmWaiting(WaitingListEntry entry) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Booking");
        confirm.setHeaderText("Confirm seat for " + entry.getStudentName() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (dbManager.confirmWaitingListEntry(entry.getBookingId())) {
                    AlertMessage.showSuccess("Confirmed", "Booking confirmed successfully!");
                    showWaitingList();
                } else {
                    AlertMessage.showError("Error", "Failed to confirm booking");
                }
            }
        });
    }
}