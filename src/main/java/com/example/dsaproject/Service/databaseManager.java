package com.example.dsaproject.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class databaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/busManagementSystem?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "asadabbasse2006";
    private static final String PASSWORD = "Asad@8042680";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully!");
            return conn;
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            return null;
        }
    }
}
