package com.example.dsaproject.Controllers;

import com.example.dsaproject.Util.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class COMSATSTransport extends Application {

    private static Stage primaryStage;
    private static final String TITLE = "COMSATS Transport Management System";
    private static final String CSS_PATH = "/com/example/dsaproject/CSS/Styles.css";

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        primaryStage.setTitle(TITLE);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        // Initialize database
        DatabaseManager.getInstance().initializeDatabase();

        // Load login screen
        showLoginScreen();

        primaryStage.show();
    }

    /**
     * Loads an FXML file and applies CSS.
     */
    private static Scene loadScene(String fxmlPath) throws Exception {
        FXMLLoader loader = new FXMLLoader(COMSATSTransport.class.getResource(fxmlPath));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(COMSATSTransport.class.getResource(CSS_PATH).toExternalForm());

        return scene;
    }

    /**
     * Display login screen (natural FXML size)
     */
    public static void showLoginScreen() throws Exception {
        Scene scene = loadScene("/com/example/dsaproject/fxml/Login.fxml");

        primaryStage.setScene(scene);

        // Resize window exactly to FXML layout size (fixes "too large screen" issue)
        primaryStage.sizeToScene();
    }

    /**
     * Display the Student Dashboard
     */
    public static void showStudentDashboard() throws Exception {
        Scene scene = loadScene("/com/example/dsaproject/fxml/StudentDashboard.fxml");

        // Dashboard requires fixed larger size
        primaryStage.setScene(scene);
        primaryStage.setWidth(1280);
        primaryStage.setHeight(750);
    }

    /**
     * Display Admin Dashboard
     */
    public static void showAdminDashboard() throws Exception {
        Scene scene = loadScene("/com/example/dsaproject/fxml/AdminDashboard.fxml");

        primaryStage.setScene(scene);
        primaryStage.setWidth(1280);
        primaryStage.setHeight(750);
    }

    /**
     * Returns the primary stage for dialogs, popups, etc.
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void stop() {
        DatabaseManager.getInstance().closeConnection();
        System.out.println("Application closed successfully.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
