package com.example.dsaproject.Controllers;

import com.example.dsaproject.factory.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.chart.*;
import java.time.LocalDate;
import java.util.List;
import com.example.dsaproject.Controllers.COMSATSTransport;
import com.example.dsaproject.Model.*;
import com.example.dsaproject.Util.*;

/**
 * Controller for Student Dashboard
 * Handles all student-related operations
 */
public class studentDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label emailLabel;
    @FXML private StackPane contentArea;
    @FXML private Label activeBookingsLabel;
    @FXML private Label availableRoutesLabel;
    @FXML private Label totalSpentLabel;
    @FXML private Button homeBtn;
    @FXML private Button routesBtn;
    @FXML private Button bookingBtn;
    @FXML private Button myBookingsBtn;
    @FXML private Button scheduleBtn;
    @FXML private Button profileBtn;

    private DatabaseManager dbManager;
    private User currentUser;
    private Button selectedButton;

    @FXML
    private void initialize() {
        dbManager = DatabaseManager.getInstance();
        currentUser = SessionManager.getInstance().getCurrentUser();

        // Set user information
        welcomeLabel.setText("Welcome, " + currentUser.getName());
        emailLabel.setText(currentUser.getEmail());

        // Update statistics
        updateStats();

        // Show home view by default
        showHome();
        highlightButton(homeBtn);
    }

    @FXML
    private void showHome() {
        VBox home = ViewFactory.createHomeView(currentUser, dbManager);
        loadContent(home);
        highlightButton(homeBtn);
    }

    @FXML
    private void showRoutes() {
        VBox routes = ViewFactory.createRoutesView(dbManager, this::handleQuickBook);
        loadContent(routes);
        highlightButton(routesBtn);
    }

    @FXML
    private void showBooking() {
        VBox booking = ViewFactory.createBookingView(dbManager, currentUser, this::handleBookingComplete);
        loadContent(booking);
        highlightButton(bookingBtn);
    }

    @FXML
    private void showMyBookings() {
        VBox bookings = ViewFactory.createMyBookingsView(dbManager, currentUser, this::handleCancelBooking);
        loadContent(bookings);
        highlightButton(myBookingsBtn);
    }

    @FXML
    private void showSchedule() {
        VBox schedule = ViewFactory.createScheduleView(dbManager);
        loadContent(schedule);
        highlightButton(scheduleBtn);
    }

    @FXML
    private void showProfile() {
        VBox profile = ViewFactory.createProfileView(currentUser, dbManager);
        loadContent(profile);
        highlightButton(profileBtn);
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Logout");
        confirm.setHeaderText("Are you sure you want to logout?");
        confirm.setContentText("You will need to login again to access the system.");

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
        int activeCount = dbManager.getUserActiveBookings(currentUser.getUserId());
        int routeCount = dbManager.getAllRoutes().size();
        double totalSpent = dbManager.getUserTotalSpent(currentUser.getUserId());

        activeBookingsLabel.setText(String.valueOf(activeCount));
        availableRoutesLabel.setText(String.valueOf(routeCount));
        totalSpentLabel.setText(String.format("Rs. %.2f", totalSpent));
    }

    private void handleQuickBook(Route route) {
        showBooking();
        // Auto-select the route in booking view
    }

    private void handleBookingComplete() {
        updateStats();
        AlertMessage.showSuccess("Booking Confirmed", "Your seat has been booked successfully!");
        showMyBookings();
    }

    private void handleCancelBooking(Booking booking) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Booking");
        confirm.setHeaderText("Cancel this booking?");
        confirm.setContentText("Route: " + booking.getRouteName() + "\nBus: " + booking.getBusNo());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (dbManager.cancelBooking(booking.getBookingId())) {
                    AlertMessage.showSuccess("Cancelled", "Booking cancelled successfully!");
                    updateStats();
                    showMyBookings();
                } else {
                    AlertMessage.showError("Error", "Failed to cancel booking");
                }
            }
        });
    }
}