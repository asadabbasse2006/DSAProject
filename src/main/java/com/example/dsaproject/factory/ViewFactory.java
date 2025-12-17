package com.example.dsaproject.factory;

import com.example.dsaproject.Model.*;
import com.example.dsaproject.Util.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.collections.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Factory for creating student view components
 */
public class ViewFactory {

    /**
     * Create home/dashboard view
     */
    public static VBox createHomeView(User user, DatabaseManager db) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        view.setAlignment(Pos.TOP_CENTER);

        // Welcome section
        Label welcomeLabel = new Label("Welcome to COMSATS Transport System");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label userInfo = new Label("Student: " + user.getName() + " (" + user.getRegistrationId() + ")");
        userInfo.setStyle("-fx-font-size: 16px;");

        // Stats section
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);

        int activeBookings = db.getUserActiveBookings(user.getUserId());
        int totalRoutes = db.getAllRoutes().size();

        VBox stat1 = createStatCard("Active Bookings", String.valueOf(activeBookings), "#4CAF50");
        VBox stat2 = createStatCard("Available Routes", String.valueOf(totalRoutes), "#2196F3");
        VBox stat3 = createStatCard("Total Spent", "Rs. " + db.getUserTotalSpent(user.getUserId()), "#FF9800");

        statsBox.getChildren().addAll(stat1, stat2, stat3);

        // Quick actions
        Label actionsLabel = new Label("Quick Actions");
        actionsLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox actionsBox = new HBox(15);
        actionsBox.setAlignment(Pos.CENTER);

        Button bookSeatBtn = new Button("Book a Seat");
        bookSeatBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");

        Button viewRoutesBtn = new Button("View Routes");
        viewRoutesBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");

        Button myBookingsBtn = new Button("My Bookings");
        myBookingsBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");

        actionsBox.getChildren().addAll(bookSeatBtn, viewRoutesBtn, myBookingsBtn);

        view.getChildren().addAll(welcomeLabel, userInfo, new Separator(), statsBox,
                new Separator(), actionsLabel, actionsBox);

        return view;
    }

    /**
     * Create routes view with table
     */
    public static VBox createRoutesView(DatabaseManager db, Consumer<Route> onQuickBook) {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));

        Label title = new Label("Available Routes");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Create table
        TableView<Route> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Route, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("routeId"));
        idCol.setPrefWidth(50);

        TableColumn<Route, String> nameCol = new TableColumn<>("Route Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("routeName"));
        nameCol.setPrefWidth(200);

        TableColumn<Route, Integer> stopsCol = new TableColumn<>("Stops");
        stopsCol.setCellValueFactory(new PropertyValueFactory<>("totalStops"));
        stopsCol.setPrefWidth(80);

        TableColumn<Route, String> stopsListCol = new TableColumn<>("Stop List");
        stopsListCol.setCellValueFactory(new PropertyValueFactory<>("stops"));
        stopsListCol.setPrefWidth(400);

        TableColumn<Route, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(100);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button bookBtn = new Button("Book");
            {
                bookBtn.setOnAction(event -> {
                    Route route = getTableView().getItems().get(getIndex());
                    if (onQuickBook != null) {
                        onQuickBook.accept(route);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : bookBtn);
            }
        });

        table.getColumns().addAll(idCol, nameCol, stopsCol, stopsListCol, actionCol);
        table.setItems(FXCollections.observableArrayList(db.getAllRoutes()));

        view.getChildren().addAll(title, table);
        return view;
    }

    /**
     * Create booking view
     */
    public static VBox createBookingView(DatabaseManager db, User user, Runnable onComplete) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        view.setMaxWidth(600);

        Label title = new Label("Book a Seat");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Form
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);

        Label routeLabel = new Label("Select Route:");
        ComboBox<Route> routeCombo = new ComboBox<>();
        routeCombo.setItems(FXCollections.observableArrayList(db.getAllRoutes()));
        routeCombo.setPrefWidth(300);

        Label busLabel = new Label("Select Bus:");
        ComboBox<Bus> busCombo = new ComboBox<>();
        busCombo.setPrefWidth(300);

        routeCombo.setOnAction(e -> {
            Route selected = routeCombo.getValue();
            if (selected != null) {
                busCombo.setItems(FXCollections.observableArrayList(db.getAllBuses()));
            }
        });

        Button bookBtn = new Button("Confirm Booking");
        bookBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 30;");
        bookBtn.setOnAction(e -> {
            Route route = routeCombo.getValue();
            Bus bus = busCombo.getValue();

            if (route == null || bus == null) {
                AlertMessage.showError("Error", "Please select route and bus");
                return;
            }

            int bookingId = db.createBooking(user.getUserId(), route.getRouteId(), bus.getBusId());

            if (bookingId > 0) {
                if (onComplete != null) {
                    onComplete.run();
                }
            } else {
                AlertMessage.showError("Error", "Failed to create booking");
            }
        });

        form.add(routeLabel, 0, 0);
        form.add(routeCombo, 1, 0);
        form.add(busLabel, 0, 1);
        form.add(busCombo, 1, 1);
        form.add(bookBtn, 1, 2);

        view.getChildren().addAll(title, form);
        return view;
    }

    /**
     * Create my bookings view
     */
    public static VBox createMyBookingsView(DatabaseManager db, User user, Consumer<Booking> onCancel) {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));

        Label title = new Label("My Bookings");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<Booking> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Booking, Integer> idCol = new TableColumn<>("Booking ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("bookingId"));

        TableColumn<Booking, String> routeCol = new TableColumn<>("Route");
        routeCol.setCellValueFactory(new PropertyValueFactory<>("routeName"));

        TableColumn<Booking, String> busCol = new TableColumn<>("Bus");
        busCol.setCellValueFactory(new PropertyValueFactory<>("busNo"));

        TableColumn<Booking, String> seatCol = new TableColumn<>("Seat");
        seatCol.setCellValueFactory(new PropertyValueFactory<>("seatNo"));

        TableColumn<Booking, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Booking, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button cancelBtn = new Button("Cancel");
            {
                cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                cancelBtn.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    if (onCancel != null) {
                        onCancel.accept(booking);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : cancelBtn);
            }
        });

        table.getColumns().addAll(idCol, routeCol, busCol, seatCol, statusCol, actionCol);
        table.setItems(FXCollections.observableArrayList(db.getUserBookings(user.getUserId())));

        view.getChildren().addAll(title, table);
        return view;
    }

    /**
     * Create schedule view
     */
    public static VBox createScheduleView(DatabaseManager db) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));

        Label title = new Label("Bus Schedules");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label info = new Label("All buses depart at 7:30 AM from respective routes");
        info.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");

        // Create schedule table
        TableView<Bus> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Bus, String> busCol = new TableColumn<>("Bus Number");
        busCol.setCellValueFactory(new PropertyValueFactory<>("busNo"));

        TableColumn<Bus, Integer> capacityCol = new TableColumn<>("Capacity");
        capacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        TableColumn<Bus, String> driverCol = new TableColumn<>("Driver");
        driverCol.setCellValueFactory(new PropertyValueFactory<>("driverName"));

        table.getColumns().addAll(busCol, capacityCol, driverCol);
        table.setItems(FXCollections.observableArrayList(db.getAllBuses()));

        view.getChildren().addAll(title, info, table);
        return view;
    }

    /**
     * Create profile view
     */
    public static VBox createProfileView(User user, DatabaseManager db) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        view.setMaxWidth(500);

        Label title = new Label("My Profile");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        GridPane info = new GridPane();
        info.setHgap(20);
        info.setVgap(15);

        addInfoRow(info, 0, "Name:", user.getName());
        addInfoRow(info, 1, "Email:", user.getEmail());
        addInfoRow(info, 2, "Registration ID:", user.getRegistrationId());
        addInfoRow(info, 3, "Role:", user.getRole());
        addInfoRow(info, 4, "Total Bookings:", String.valueOf(db.getUserActiveBookings(user.getUserId())));
        addInfoRow(info, 5, "Total Spent:", "Rs. " + db.getUserTotalSpent(user.getUserId()));

        view.getChildren().addAll(title, new Separator(), info);
        return view;
    }

    // Helper methods
    private static VBox createStatCard(String label, String value, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10;");
        card.setPrefWidth(200);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label labelLabel = new Label(label);
        labelLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

        card.getChildren().addAll(valueLabel, labelLabel);
        return card;
    }

    private static void addInfoRow(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label valueNode = new Label(value != null ? value : "N/A");
        valueNode.setStyle("-fx-font-size: 14px;");

        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }
}