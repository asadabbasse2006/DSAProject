package com.example.dsaproject.factory;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.collections.FXCollections;
import com.example.dsaproject.Model.*;
import com.example.dsaproject.Util.DatabaseManager;
import java.util.function.Consumer;

/**
 * Factory class for creating Student Dashboard views
 */
public class ViewFactory {

    public static VBox createHomeView(User user, DatabaseManager dbManager) {
        VBox view = new VBox(25);
        view.setPadding(new Insets(30));
        view.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Welcome to COMSATS Transport System");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        // Stats Grid
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(25);
        statsGrid.setVgap(25);
        statsGrid.setAlignment(Pos.CENTER);

        VBox card1 = createStatCard("Total Routes", String.valueOf(dbManager.getAllRoutes().size()), "🗺️", "#4299e1");
        VBox card2 = createStatCard("Available Buses", String.valueOf(dbManager.getAllBuses().size()), "🚌", "#48bb78");
        VBox card3 = createStatCard("Your Bookings", String.valueOf(dbManager.getUserActiveBookings(user.getUserId())), "🎫", "#9f7aea");
        VBox card4 = createStatCard("Waiting List", String.valueOf(dbManager.getWaitingListCount()), "⏳", "#ed8936");

        statsGrid.add(card1, 0, 0);
        statsGrid.add(card2, 1, 0);
        statsGrid.add(card3, 0, 1);
        statsGrid.add(card4, 1, 1);

        // Announcement
        VBox announcementBox = new VBox(10);
        announcementBox.setStyle("-fx-background-color: #fff3cd; -fx-padding: 20px; " +
                "-fx-background-radius: 10px; -fx-border-color: #ffc107; " +
                "-fx-border-radius: 10px; -fx-border-width: 2px;");
        announcementBox.setMaxWidth(700);

        Label announcementTitle = new Label("📢 Latest Announcements");
        announcementTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #744210;");

        Label announcement1 = new Label("• New route to Sahiwal Airport starting from December 1st");
        Label announcement2 = new Label("• Online booking now available 24/7");
        Label announcement3 = new Label("• Special discounts for semester pass holders");

        announcement1.setStyle("-fx-text-fill: #744210;");
        announcement2.setStyle("-fx-text-fill: #744210;");
        announcement3.setStyle("-fx-text-fill: #744210;");

        announcementBox.getChildren().addAll(announcementTitle, announcement1, announcement2, announcement3);

        view.getChildren().addAll(title, statsGrid, announcementBox);
        return view;
    }

    public static VBox createRoutesView(DatabaseManager dbManager, Consumer<Route> onQuickBook) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(25));

        Label title = new Label("Available Routes");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        ListView<Route> routeList = new ListView<>();
        routeList.setItems(FXCollections.observableArrayList(dbManager.getAllRoutes()));
        routeList.setCellFactory(param -> new ListCell<Route>() {
            @Override
            protected void updateItem(Route route, boolean empty) {
                super.updateItem(route, empty);
                if (empty || route == null) {
                    setGraphic(null);
                } else {
                    HBox card = createRouteCard(route, dbManager, onQuickBook);
                    setGraphic(card);
                }
            }
        });

        view.getChildren().addAll(title, routeList);
        VBox.setVgrow(routeList, Priority.ALWAYS);
        return view;
    }

    private static HBox createRouteCard(Route route, DatabaseManager dbManager, Consumer<Route> onQuickBook) {
        HBox card = new HBox(20);
        card.setStyle("-fx-background-color: white; -fx-padding: 20px; " +
                "-fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        card.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(8);
        Label name = new Label(route.getRouteName());
        name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label stops = new Label(route.getTotalStops() + " stops • " + route.getDistance() + " km");
        stops.setStyle("-fx-text-fill: #718096;");

        Label stopsList = new Label(route.getStopsString());
        stopsList.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 11px;");
        stopsList.setWrapText(true);

        info.getChildren().addAll(name, stops, stopsList);
        HBox.setHgrow(info, Priority.ALWAYS);

        Button bookBtn = new Button("Quick Book");
        bookBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; " +
                "-fx-padding: 10px 20px; -fx-background-radius: 6px; -fx-cursor: hand;");
        bookBtn.setOnAction(e -> onQuickBook.accept(route));

        card.getChildren().addAll(info, bookBtn);
        return card;
    }

    public static VBox createBookingView(DatabaseManager dbManager, User user, Runnable onComplete) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(25));
        view.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Book a Seat");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        VBox formCard = new VBox(18);
        formCard.setStyle("-fx-background-color: white; -fx-padding: 30px; " +
                "-fx-background-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        formCard.setMaxWidth(600);

        // Route selection
        Label routeLabel = new Label("Select Route:");
        routeLabel.setStyle("-fx-font-weight: bold;");
        ComboBox<Route> routeCombo = new ComboBox<>();
        routeCombo.setItems(FXCollections.observableArrayList(dbManager.getAllRoutes()));
        routeCombo.setPromptText("Choose a route");
        routeCombo.setMaxWidth(Double.MAX_VALUE);

        // Bus selection
        Label busLabel = new Label("Select Bus:");
        busLabel.setStyle("-fx-font-weight: bold;");
        ComboBox<Bus> busCombo = new ComboBox<>();
        busCombo.setPromptText("Choose a bus");
        busCombo.setMaxWidth(Double.MAX_VALUE);

        routeCombo.setOnAction(e -> {
            Route selected = routeCombo.getValue();
            if (selected != null) {
                busCombo.setItems(FXCollections.observableArrayList(
                        dbManager.getBusesForRoute(selected.getRouteId())));
            }
        });

        // Date selection
        Label dateLabel = new Label("Travel Date:");
        dateLabel.setStyle("-fx-font-weight: bold;");
        DatePicker datePicker = new DatePicker();
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setValue(java.time.LocalDate.now());

        // Priority selection
        Label priorityLabel = new Label("Priority Level:");
        priorityLabel.setStyle("-fx-font-weight: bold;");
        ComboBox<String> priorityCombo = new ComboBox<>();
        priorityCombo.setItems(FXCollections.observableArrayList(
                "Normal (3)", "Medium (2)", "High (1)"));
        priorityCombo.setValue("Normal (3)");
        priorityCombo.setMaxWidth(Double.MAX_VALUE);

        Button bookBtn = new Button("Confirm Booking");
        bookBtn.setStyle("-fx-background-color: #48bb78; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-padding: 12px 30px; -fx-background-radius: 8px; -fx-cursor: hand;");
        bookBtn.setMaxWidth(Double.MAX_VALUE);

        bookBtn.setOnAction(e -> {
            if (routeCombo.getValue() == null || busCombo.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Please select route and bus");
                alert.showAndWait();
                return;
            }

            int priority = priorityCombo.getValue().contains("High") ? 1 :
                    priorityCombo.getValue().contains("Medium") ? 2 : 3;

            if (dbManager.createBooking(
                    user.getUserId(),
                    busCombo.getValue().getBusId(),
                    routeCombo.getValue().getRouteId(),
                    datePicker.getValue().toString(),
                    priority)) {
                onComplete.run();
            }
        });

        formCard.getChildren().addAll(
                routeLabel, routeCombo,
                busLabel, busCombo,
                dateLabel, datePicker,
                priorityLabel, priorityCombo,
                bookBtn
        );

        view.getChildren().addAll(title, formCard);
        return view;
    }

    public static VBox createMyBookingsView(DatabaseManager dbManager, User user, Consumer<Booking> onCancel) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(25));

        Label title = new Label("My Bookings");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        TableView<Booking> table = createBookingsTable(dbManager.getUserBookings(user.getUserId()), onCancel);

        view.getChildren().addAll(title, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return view;
    }

    public static VBox createScheduleView(DatabaseManager dbManager) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(25));

        Label title = new Label("Bus Schedule");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        ListView<String> scheduleList = new ListView<>();
        scheduleList.setItems(FXCollections.observableArrayList(
                "🕐 07:00 AM - Main Campus Route - Bus A-123",
                "🕐 07:30 AM - Railway Station Route - Bus B-456",
                "🕑 08:00 AM - Sahiwal City Route - Bus C-789",
                "🕒 08:30 AM - Airport Route - Bus D-101",
                "🕓 09:00 AM - Hospital Route - Bus E-202",
                "🕔 01:00 PM - Main Campus Route - Bus A-123",
                "🕕 02:00 PM - Railway Station Route - Bus B-456",
                "🕖 03:00 PM - Sahiwal City Route - Bus C-789",
                "🕗 04:00 PM - Airport Route - Bus D-101",
                "🕘 05:00 PM - Hospital Route - Bus E-202"
        ));

        view.getChildren().addAll(title, scheduleList);
        VBox.setVgrow(scheduleList, Priority.ALWAYS);
        return view;
    }

    public static VBox createProfileView(User user, DatabaseManager dbManager) {
        VBox view = new VBox(25);
        view.setPadding(new Insets(30));
        view.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("My Profile");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        VBox profileCard = new VBox(15);
        profileCard.setStyle("-fx-background-color: white; -fx-padding: 30px; " +
                "-fx-background-radius: 12px; -fx-max-width: 500px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        addProfileRow(grid, 0, "Name:", user.getName());
        addProfileRow(grid, 1, "Email:", user.getEmail());
        addProfileRow(grid, 2, "Role:", user.getRole().toUpperCase());
        addProfileRow(grid, 3, "Phone:", user.getPhone() != null ? user.getPhone() : "Not set");
        addProfileRow(grid, 4, "Address:", user.getAddress() != null ? user.getAddress() : "Not set");

        profileCard.getChildren().add(grid);

        view.getChildren().addAll(title, profileCard);
        return view;
    }

    private static void addProfileRow(GridPane grid, int row, String label, String value) {
        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568;");

        Label lblValue = new Label(value);
        lblValue.setStyle("-fx-text-fill: #2d3748;");

        grid.add(lblLabel, 0, row);
        grid.add(lblValue, 1, row);
    }

    private static TableView<Booking> createBookingsTable(java.util.List<Booking> bookings, Consumer<Booking> onCancel) {
        TableView<Booking> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(bookings));

        TableColumn<Booking, String> routeCol = new TableColumn<>("Route");
        routeCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getRouteName()));

        TableColumn<Booking, String> busCol = new TableColumn<>("Bus");
        busCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getBusNo()));

        TableColumn<Booking, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getBookingDate()));

        TableColumn<Booking, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getStatusBadge()));

        TableColumn<Booking, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<Booking, Void>() {
            private final Button cancelBtn = new Button("Cancel");
            {
                cancelBtn.setStyle("-fx-background-color: #f56565; -fx-text-fill: white; " +
                        "-fx-padding: 6px 15px; -fx-background-radius: 5px; -fx-cursor: hand;");
                cancelBtn.setOnAction(e -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    if (!booking.isCancelled()) {
                        onCancel.accept(booking);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Booking booking = getTableView().getItems().get(getIndex());
                    setGraphic(booking.isCancelled() ? null : cancelBtn);
                }
            }
        });

        table.getColumns().addAll(routeCol, busCol, dateCol, statusCol, actionCol);
        return table;
    }

    private static VBox createStatCard(String title, String value, String icon, String color) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-padding: 25px; " +
                "-fx-background-radius: 12px; -fx-pref-width: 200; -fx-pref-height: 160; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3); " +
                "-fx-border-width: 0 0 4 0; -fx-border-color: " + color + "; " +
                "-fx-border-radius: 12px;");

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 42px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #718096;");

        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);
        return card;
    }
}
