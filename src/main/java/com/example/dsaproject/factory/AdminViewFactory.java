package com.example.dsaproject.factory;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.collections.FXCollections;
import com.example.dsaproject.Model.*;
import com.example.dsaproject.Util.DatabaseManager;
import java.util.function.Consumer;

/**
 * Factory class for creating Admin Dashboard views
 */
public class AdminViewFactory {

    public static VBox createDashboardView(DatabaseManager dbManager) {
        VBox view = new VBox(25);
        view.setPadding(new Insets(30));

        Label title = new Label("Admin Dashboard Overview");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        // Stats Grid
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(25);
        statsGrid.setVgap(25);
        statsGrid.setAlignment(Pos.CENTER);

        VBox card1 = createStatCard("Total Routes", String.valueOf(dbManager.getAllRoutes().size()), "🗺️", "#4299e1");
        VBox card2 = createStatCard("Total Buses", String.valueOf(dbManager.getAllBuses().size()), "🚌", "#48bb78");
        VBox card3 = createStatCard("Total Drivers", String.valueOf(dbManager.getAllDrivers().size()), "👤", "#9f7aea");
        VBox card4 = createStatCard("Active Bookings", String.valueOf(dbManager.getActiveBookingsCount()), "✅", "#ed8936");
        VBox card5 = createStatCard("Total Bookings", String.valueOf(dbManager.getAllBookings().size()), "📋", "#718096");
        VBox card6 = createStatCard("Waiting List", String.valueOf(dbManager.getWaitingListCount()), "⏳", "#f56565");

        statsGrid.add(card1, 0, 0);
        statsGrid.add(card2, 1, 0);
        statsGrid.add(card3, 2, 0);
        statsGrid.add(card4, 0, 1);
        statsGrid.add(card5, 1, 1);
        statsGrid.add(card6, 2, 1);

        // Recent Activity
        Label activityTitle = new Label("Recent Activity");
        activityTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        ListView<String> activityList = new ListView<>();
        activityList.setPrefHeight(200);
        activityList.setItems(FXCollections.observableArrayList(
                "✓ New booking by Ahmed Ali on Main Campus Route",
                "✓ Bus A-123 maintenance completed",
                "✓ Route to Airport updated with new stop",
                "✓ Driver Muhammad Hassan assigned to Bus B-456",
                "⚠ Bus C-789 reaching capacity on Sahiwal City Route",
                "✓ 5 bookings confirmed from waiting list",
                "✓ New driver registered: Bilal Ahmed"
        ));

        view.getChildren().addAll(title, statsGrid, activityTitle, activityList);
        return view;
    }

    public static VBox createRoutesManagementView(DatabaseManager dbManager,
                                                  Runnable onAdd, Consumer<Route> onEdit, Consumer<Route> onDelete) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(25));

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Routes Management");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+ Add New Route");
        addBtn.setStyle("-fx-background-color: #48bb78; -fx-text-fill: white; " +
                "-fx-padding: 10px 20px; -fx-background-radius: 6px; -fx-cursor: hand; -fx-font-weight: bold;");
        addBtn.setOnAction(e -> onAdd.run());

        header.getChildren().addAll(title, spacer, addBtn);

        TableView<Route> table = createRoutesTable(dbManager.getAllRoutes(), onEdit, onDelete);

        view.getChildren().addAll(header, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return view;
    }

    private static TableView<Route> createRoutesTable(java.util.List<Route> routes,
                                                      Consumer<Route> onEdit, Consumer<Route> onDelete) {
        TableView<Route> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(routes));

        TableColumn<Route, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getRouteId()).asObject());
        idCol.setPrefWidth(60);

        TableColumn<Route, String> nameCol = new TableColumn<>("Route Name");
        nameCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getRouteName()));
        nameCol.setPrefWidth(200);

        TableColumn<Route, Integer> stopsCol = new TableColumn<>("Stops");
        stopsCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getTotalStops()).asObject());
        stopsCol.setPrefWidth(80);

        TableColumn<Route, String> distanceCol = new TableColumn<>("Distance (km)");
        distanceCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(String.format("%.1f", data.getValue().getDistance())));
        distanceCol.setPrefWidth(120);

        TableColumn<Route, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> createActionCell(onEdit, onDelete));
        actionCol.setPrefWidth(180);

        table.getColumns().addAll(idCol, nameCol, stopsCol, distanceCol, actionCol);
        return table;
    }

    public static VBox createBusesManagementView(DatabaseManager dbManager,
                                                 Runnable onAdd, Consumer<Bus> onEdit, Consumer<Bus> onDelete) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(25));

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Buses Management");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+ Add New Bus");
        addBtn.setStyle("-fx-background-color: #48bb78; -fx-text-fill: white; " +
                "-fx-padding: 10px 20px; -fx-background-radius: 6px; -fx-cursor: hand; -fx-font-weight: bold;");
        addBtn.setOnAction(e -> onAdd.run());

        header.getChildren().addAll(title, spacer, addBtn);

        TableView<Bus> table = createBusesTable(dbManager.getAllBuses(), onEdit, onDelete);

        view.getChildren().addAll(header, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return view;
    }

    private static TableView<Bus> createBusesTable(java.util.List<Bus> buses,
                                                   Consumer<Bus> onEdit, Consumer<Bus> onDelete) {
        TableView<Bus> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(buses));

        TableColumn<Bus, String> busNoCol = new TableColumn<>("Bus Number");
        busNoCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getBusNo()));

        TableColumn<Bus, Integer> capacityCol = new TableColumn<>("Capacity");
        capacityCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCapacity()).asObject());

        TableColumn<Bus, String> driverCol = new TableColumn<>("Driver");
        driverCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDriverName()));

        TableColumn<Bus, String> occupancyCol = new TableColumn<>("Occupancy");
        occupancyCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCurrentOccupancy() + "/" + data.getValue().getCapacity()));

        TableColumn<Bus, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> createActionCell(onEdit, onDelete));

        table.getColumns().addAll(busNoCol, capacityCol, driverCol, occupancyCol, actionCol);
        return table;
    }

    public static VBox createDriversManagementView(DatabaseManager dbManager,
                                                   Runnable onAdd, Consumer<Driver> onEdit, Consumer<Driver> onDelete) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(25));

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Drivers Management");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+ Add New Driver");
        addBtn.setStyle("-fx-background-color: #48bb78; -fx-text-fill: white; " +
                "-fx-padding: 10px 20px; -fx-background-radius: 6px; -fx-cursor: hand; -fx-font-weight: bold;");
        addBtn.setOnAction(e -> onAdd.run());

        header.getChildren().addAll(title, spacer, addBtn);

        TableView<Driver> table = createDriversTable(dbManager.getAllDrivers(), onEdit, onDelete);

        view.getChildren().addAll(header, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return view;
    }

    private static TableView<Driver> createDriversTable(java.util.List<Driver> drivers,
                                                        Consumer<Driver> onEdit, Consumer<Driver> onDelete) {
        TableView<Driver> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(drivers));

        TableColumn<Driver, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        TableColumn<Driver, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));

        TableColumn<Driver, String> licenseCol = new TableColumn<>("License No");
        licenseCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getLicenseNo()));

        TableColumn<Driver, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> createActionCell(onEdit, onDelete));

        table.getColumns().addAll(nameCol, phoneCol, licenseCol, actionCol);
        return table;
    }

    public static VBox createBookingsView(DatabaseManager dbManager) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(25));

        Label title = new Label("All Bookings");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        HBox filterBox = new HBox(15);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        Label filterLabel = new Label("Filter:");
        filterLabel.setStyle("-fx-font-weight: bold;");

        ComboBox<String> filterCombo = new ComboBox<>();
        filterCombo.setItems(FXCollections.observableArrayList("All", "Confirmed", "Waiting", "Cancelled"));
        filterCombo.setValue("All");

        filterBox.getChildren().addAll(filterLabel, filterCombo);

        TableView<Booking> table = createBookingsTable(dbManager.getAllBookings());

        filterCombo.setOnAction(e -> {
            String filter = filterCombo.getValue();
            if (filter.equals("All")) {
                table.setItems(FXCollections.observableArrayList(dbManager.getAllBookings()));
            } else {
                table.setItems(FXCollections.observableArrayList(
                        dbManager.getBookingsByStatus(filter.toLowerCase())));
            }
        });

        view.getChildren().addAll(title, filterBox, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return view;
    }

    private static TableView<Booking> createBookingsTable(java.util.List<Booking> bookings) {
        TableView<Booking> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(bookings));

        TableColumn<Booking, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getStudentName()));

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

        table.getColumns().addAll(studentCol, routeCol, busCol, dateCol, statusCol);
        return table;
    }

    public static VBox createWaitingListView(DatabaseManager dbManager, Consumer<WaitingListEntry> onConfirm) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(25));

        Label title = new Label("Waiting List (Priority Queue)");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        Label info = new Label("Students are automatically ordered by: Priority Level → Registration Time");
        info.setStyle("-fx-text-fill: #718096; -fx-font-style: italic;");

        TableView<WaitingListEntry> table = createWaitingListTable(dbManager.getWaitingList(), onConfirm);

        view.getChildren().addAll(title, info, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return view;
    }

    private static TableView<WaitingListEntry> createWaitingListTable(
            java.util.List<WaitingListEntry> entries, Consumer<WaitingListEntry> onConfirm) {
        TableView<WaitingListEntry> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(entries));

        TableColumn<WaitingListEntry, Integer> posCol = new TableColumn<>("Position");
        posCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getPosition()).asObject());
        posCol.setPrefWidth(80);

        TableColumn<WaitingListEntry, String> nameCol = new TableColumn<>("Student");
        nameCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getStudentName()));

        TableColumn<WaitingListEntry, String> routeCol = new TableColumn<>("Route");
        routeCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getRouteName()));

        TableColumn<WaitingListEntry, String> priorityCol = new TableColumn<>("Priority");
        priorityCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getPriorityLabel()));
        priorityCol.setPrefWidth(100);

        TableColumn<WaitingListEntry, String> timeCol = new TableColumn<>("Request Time");
        timeCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getRequestTime()));

        TableColumn<WaitingListEntry, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<WaitingListEntry, Void>() {
            private final Button confirmBtn = new Button("Confirm Seat");
            {
                confirmBtn.setStyle("-fx-background-color: #48bb78; -fx-text-fill: white; " +
                        "-fx-padding: 6px 15px; -fx-background-radius: 5px; -fx-cursor: hand;");
                confirmBtn.setOnAction(e -> {
                    WaitingListEntry entry = getTableView().getItems().get(getIndex());
                    onConfirm.accept(entry);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : confirmBtn);
            }
        });

        table.getColumns().addAll(posCol, nameCol, routeCol, priorityCol, timeCol, actionCol);
        return table;
    }

    public static VBox createReportsView(DatabaseManager dbManager) {
        VBox view = new VBox(25);
        view.setPadding(new Insets(25));

        Label title = new Label("Reports & Analytics");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        // Occupancy Report
        VBox occupancySection = new VBox(15);
        Label occupancyTitle = new Label("Route Occupancy Report");
        occupancyTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<RouteOccupancy> occupancyTable = createOccupancyTable(dbManager.getRouteOccupancyReport());
        occupancyTable.setPrefHeight(250);

        occupancySection.getChildren().addAll(occupancyTitle, occupancyTable);

        view.getChildren().addAll(title, occupancySection);
        return view;
    }

    private static TableView<RouteOccupancy> createOccupancyTable(java.util.List<RouteOccupancy> report) {
        TableView<RouteOccupancy> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(report));

        TableColumn<RouteOccupancy, String> routeCol = new TableColumn<>("Route");
        routeCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getRouteName()));

        TableColumn<RouteOccupancy, Integer> totalCol = new TableColumn<>("Total Seats");
        totalCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getTotalSeats()).asObject());

        TableColumn<RouteOccupancy, Integer> bookedCol = new TableColumn<>("Booked");
        bookedCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getBookedSeats()).asObject());

        TableColumn<RouteOccupancy, String> percentCol = new TableColumn<>("Occupancy");
        percentCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        String.format("%.1f%%", data.getValue().getOccupancyPercent())));

        table.getColumns().addAll(routeCol, totalCol, bookedCol, percentCol);
        return table;
    }

    public static VBox createUsersManagementView(DatabaseManager dbManager) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(25));

        Label title = new Label("Users Management");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        TableView<User> table = createUsersTable(dbManager.getAllUsers());

        view.getChildren().addAll(title, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return view;
    }

    private static TableView<User> createUsersTable(java.util.List<User> users) {
        TableView<User> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(users));

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getRole().toUpperCase()));

        TableColumn<User, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));

        table.getColumns().addAll(nameCol, emailCol, roleCol, phoneCol);
        return table;
    }

    private static <T> TableCell<T, Void> createActionCell(Consumer<T> onEdit, Consumer<T> onDelete) {
        return new TableCell<T, Void>() {
            private final HBox buttons = new HBox(8);
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");

            {
                editBtn.setStyle("-fx-background-color: #4299e1; -fx-text-fill: white; " +
                        "-fx-padding: 6px 15px; -fx-background-radius: 5px; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #f56565; -fx-text-fill: white; " +
                        "-fx-padding: 6px 15px; -fx-background-radius: 5px; -fx-cursor: hand;");

                editBtn.setOnAction(e -> {
                    T item = getTableView().getItems().get(getIndex());
                    onEdit.accept(item);
                });

                deleteBtn.setOnAction(e -> {
                    T item = getTableView().getItems().get(getIndex());
                    onDelete.accept(item);
                });

                buttons.getChildren().addAll(editBtn, deleteBtn);
                buttons.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        };
    }

    private static VBox createStatCard(String title, String value, String icon, String color) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-padding: 25px; " +
                "-fx-background-radius: 12px; -fx-pref-width: 220; -fx-pref-height: 160; " +
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
