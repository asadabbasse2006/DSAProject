package com.example.dsaproject;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("COMSATS Transport Management System");

        showLoginScreen();

        primaryStage.show();
    }

    public static void showLoginScreen() throws Exception {
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(Main.class.getResource("/com/example/dsaproject/login.fxml"))
        );
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(
                Objects.requireNonNull(Main.class.getResource("/com/example/dsaproject/styles.css")).toExternalForm()
        );
        primaryStage.setScene(scene);
    }


    public static void showStudentDashboard() throws Exception {
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(Main.class.getResource("/com/example/dsaproject/studentDashboard.fxml"))
        );
        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(
                Objects.requireNonNull(Main.class.getResource("/com/example/dsaproject/styles.css")).toExternalForm()
        );
        primaryStage.setScene(scene);
    }

    public static void showAdminDashboard() throws Exception {
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(Main.class.getResource("/com/example/dsaproject/adminDashboard.fxml"))
        );
        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(
                Objects.requireNonNull(Main.class.getResource("/com/example/dsaproject/styles.css")).toExternalForm()
        );
        primaryStage.setScene(scene);
    }


    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
