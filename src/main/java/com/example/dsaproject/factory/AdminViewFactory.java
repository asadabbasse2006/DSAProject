package com.example.dsaproject.factory;

import com.example.dsaproject.Model.*;
import com.example.dsaproject.Util.DatabaseManager;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.collections.*;
import java.util.function.Consumer;

/**
 * Factory for creating admin view components
 */
public class AdminViewFactory {

    /**
     * Create admin dashboard view
     */
    public static VBox createDashboardView(DatabaseManager db) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        view.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Admin Dashboard");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Statistics cards
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);

        VBox routesCard = createStatCard("Total Routes", String.valueOf(db.getAllRoutes().size()), "#4CAF50");
        VBox busesCard = createStatCard("Total Buses", String.valueOf(db.getAllBuses().size()), "#2196F3");
        VBox driversCard = createStatCard("Total Drivers", String.valueOf(db.getAllDrivers().size()), "#FF9800");
        VBox bookingsCard = createStatCard("Active Bookings", String.valueOf(db.getActiveBookingsCount()), "#9C27B0");

        statsBox.getChildren().addAll(routesCard, busesCard, driversCard, bookingsCard);

        // System info
        Label sysInfo = new Label("System Status: Online | Data Structures: Loaded");
        sysInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");

        view.getChildren().addAll(title, statsBox, new Separator(), sysInfo);
        return view;
    }

    /**
     * Create routes management view
     */
    public static VBox createRoutesManagementView(DatabaseManager db, Runnable onAdd,
                                                  Consumer<Route> onEdit, Consumer<Route> onDelete) {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));

        // Header
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Manage Routes");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button addBtn = new Button("+ Add Route");
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        addBtn.setOnAction(e -> onAdd.run());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(title, spacer, addBtn);

        // Table
        TableView<Route> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Route, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("routeId"));
        idCol.setPrefWidth(50);

        TableColumn<Route, String> nameCol = new TableColumn<>("Route Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("routeName"));
        nameCol.setPrefWidth(150);

        TableColumn<Route, Integer> stopsCol = new TableColumn<>("Total Stops");
        stopsCol.setCellValueFactory(new PropertyValueFactory<>("totalStops"));
        stopsCol.setPrefWidth(100);

        TableColumn<Route, String> stopsListCol = new TableColumn<>("Stops");
        stopsListCol.setCellValueFactory(new PropertyValueFactory<>("stops"));
        stopsListCol.setPrefWidth(400);

        TableColumn<Route, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(150);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox buttons = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                editBtn.setOnAction(event -> {
                    Route route = getTableView().getItems().get(getIndex());
                    onEdit.accept(route);
                });

                deleteBtn.setOnAction(event -> {
                    Route route = getTableView().getItems().get(getIndex());
                    onDelete.accept(route);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });

        table.getColumns().addAll(idCol, nameCol, stopsCol, stopsListCol, actionCol);
        table.setItems(FXCollections.observableArrayList(db.getAllRoutes()));

        view.getChildren().addAll(header, table);
        return view;
    }

    /**
     * Create buses management view
     */
    public static VBox createBusesManagementView(DatabaseManager db, Runnable onAdd,
                                                 Consumer<Bus> onEdit, Consumer<Bus> onDelete) {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));

        // Header
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Manage Buses");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button addBtn = new Button("+ Add Bus");
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        addBtn.setOnAction(e -> onAdd.run());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(title, spacer, addBtn);

        // Table
        TableView<Bus> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Bus, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("busId"));
        idCol.setPrefWidth(50);

        TableColumn<Bus, String> busNoCol = new TableColumn<>("Bus Number");
        busNoCol.setCellValueFactory(new PropertyValueFactory<>("busNo"));
        busNoCol.setPrefWidth(150);

        TableColumn<Bus, Integer> capacityCol = new TableColumn<>("Capacity");
        capacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        capacityCol.setPrefWidth(100);

        TableColumn<Bus, String> driverCol = new TableColumn<>("Driver");
        driverCol.setCellValueFactory(new PropertyValueFactory<>("driverName"));
        driverCol.setPrefWidth(200);

        TableColumn<Bus, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(150);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox buttons = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                editBtn.setOnAction(event -> {
                    Bus bus = getTableView().getItems().get(getIndex());
                    onEdit.accept(bus);
                });

                deleteBtn.setOnAction(event -> {
                    Bus bus = getTableView().getItems().get(getIndex());
                    onDelete.accept(bus);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });

        table.getColumns().addAll(idCol, busNoCol, capacityCol, driverCol, actionCol);
        table.setItems(FXCollections.observableArrayList(db.getAllBuses()));

        view.getChildren().addAll(header, table);
        return view;
    }

    /**
     * Create drivers management view
     */
    public static VBox createDriversManagementView(DatabaseManager db, Runnable onAdd,
                                                   Consumer<Driver> onEdit, Consumer<Driver> onDelete) {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));

        // Header
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Manage Drivers");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button addBtn = new Button("+ Add Driver");
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        addBtn.setOnAction(e -> onAdd.run());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(title, spacer, addBtn);

        // Table
        TableView<Driver> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Driver, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("driverId"));
        idCol.setPrefWidth(50);

        TableColumn<Driver, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<Driver, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setPrefWidth(150);

        TableColumn<Driver, String> licenseCol = new TableColumn<>("License Number");
        licenseCol.setCellValueFactory(new PropertyValueFactory<>("licenseNo"));
        licenseCol.setPrefWidth(150);

        TableColumn<Driver, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(150);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox buttons = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                editBtn.setOnAction(event -> {
                    Driver driver = getTableView().getItems().get(getIndex());
                    onEdit.accept(driver);
                });

                deleteBtn.setOnAction(event -> {
                    Driver driver = getTableView().getItems().get(getIndex());
                    onDelete.accept(driver);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });

        table.getColumns().addAll(idCol, nameCol, phoneCol, licenseCol, actionCol);
        table.setItems(FXCollections.observableArrayList(db.getAllDrivers()));

        view.getChildren().addAll(header, table);
        return view;
    }

    /**
     * Create bookings view
     */
    public static VBox createBookingsView(DatabaseManager db) {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));

        Label title = new Label("All Bookings");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label info = new Label("Total Active Bookings: " + db.getActiveBookingsCount());
        info.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");

        view.getChildren().addAll(title, info, new Label("Booking details displayed here"));
        return view;
    }

    /**
     * Create reports view
     */
    public static VBox createReportsView(DatabaseManager db) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));

        Label title = new Label("Reports & Analytics");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Summary section
        GridPane summary = new GridPane();
        summary.setHgap(20);
        summary.setVgap(15);

        addReportRow(summary, 0, "Total Routes:", String.valueOf(db.getAllRoutes().size()));
        addReportRow(summary, 1, "Total Buses:", String.valueOf(db.getAllBuses().size()));
        addReportRow(summary, 2, "Total Drivers:", String.valueOf(db.getAllDrivers().size()));
        addReportRow(summary, 3, "Active Bookings:", String.valueOf(db.getActiveBookingsCount()));
        addReportRow(summary, 4, "Waiting List:", String.valueOf(db.getAllWaitingList().size()));

        view.getChildren().addAll(title, new Separator(), summary);
        return view;
    }

    /**
     * Create waiting list view
     */
    public static VBox createWaitingListView(DatabaseManager db, Consumer<WaitingListEntry> onConfirm) {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));

        Label title = new Label("Waiting List Management");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<WaitingListEntry> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<WaitingListEntry, Integer> idCol = new TableColumn<>("Booking ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("bookingId"));

        TableColumn<WaitingListEntry, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));

        TableColumn<WaitingListEntry, String> routeCol = new TableColumn<>("Route");
        routeCol.setCellValueFactory(new PropertyValueFactory<>("routeName"));

        TableColumn<WaitingListEntry, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button confirmBtn = new Button("Confirm");
            {
                confirmBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                confirmBtn.setOnAction(event -> {
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

        table.getColumns().addAll(idCol, studentCol, routeCol, actionCol);
        table.setItems(FXCollections.observableArrayList(db.getAllWaitingList()));

        view.getChildren().addAll(title, table);
        return view;
    }

    /**
     * Create users management view
     */
    public static VBox createUsersManagementView(DatabaseManager db) {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));

        Label title = new Label("Manage Users");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label info = new Label("User management features coming soon");
        info.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");

        view.getChildren().addAll(title, info);
        return view;
    }

    // Helper methods
    private static VBox createStatCard(String label, String value, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10;");
        card.setPrefWidth(180);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label labelLabel = new Label(label);
        labelLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");

        card.getChildren().addAll(valueLabel, labelLabel);
        return card;
    }

    private static void addReportRow(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label valueNode = new Label(value);
        valueNode.setStyle("-fx-font-size: 14px;");

        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }
}