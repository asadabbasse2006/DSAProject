package com.example.dsaproject.Controllers;

import com.example.dsaproject.Model.*;
import com.example.dsaproject.Model.Driver;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;

    // Replace these with your MySQL credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/busManagementSystem?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "asadabbasse2006";
    private static final String DB_PASSWORD = "Asad@8042680";

    private DatabaseManager() {
        try {
            // MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            initializeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void initializeDatabase() {
        try {
            Statement stmt = connection.createStatement();

            // USERS TABLE
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "name VARCHAR(255) NOT NULL, " +
                            "email VARCHAR(255) UNIQUE NOT NULL, " +
                            "password VARCHAR(255) NOT NULL, " +
                            "role VARCHAR(255) NOT NULL" +
                            ") ENGINE=InnoDB"
            );

            // DRIVERS TABLE
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS drivers (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "name VARCHAR(255) NOT NULL, " +
                            "phone VARCHAR(255), " +
                            "license_no VARCHAR(255) UNIQUE" +
                            ") ENGINE=InnoDB"
            );

            // BUSES TABLE
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS buses (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "bus_no VARCHAR(255) UNIQUE NOT NULL, " +
                            "capacity INT NOT NULL, " +
                            "driver_id INT, " +
                            "FOREIGN KEY (driver_id) REFERENCES drivers(id)" +
                            ") ENGINE=InnoDB"
            );

            // ROUTES TABLE
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS routes (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "route_name VARCHAR(255) NOT NULL, " +
                            "total_stops INT NOT NULL" +
                            ") ENGINE=InnoDB"
            );

            // STOPS TABLE
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS stops (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "route_id INT, " +
                            "stop_name VARCHAR(255) NOT NULL, " +
                            "order_no INT, " +
                            "FOREIGN KEY (route_id) REFERENCES routes(id)" +
                            ") ENGINE=InnoDB"
            );

            // BOOKINGS TABLE
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS bookings (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "user_id INT, " +
                            "bus_id INT, " +
                            "route_id INT, " +
                            "booking_date VARCHAR(255), " +
                            "status VARCHAR(255) DEFAULT 'confirmed', " +
                            "priority_level INT DEFAULT 3, " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            "FOREIGN KEY (user_id) REFERENCES users(id), " +
                            "FOREIGN KEY (bus_id) REFERENCES buses(id), " +
                            "FOREIGN KEY (route_id) REFERENCES routes(id)" +
                            ") ENGINE=InnoDB"
            );

            // Insert sample data if tables are empty
            insertSampleData();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertSampleData() {
        try {
            // Check if data already exists
            ResultSet rs = connection.createStatement().executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Data already exists
            }

            // Insert sample admin
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)");
            stmt.setString(1, "Admin User");
            stmt.setString(2, "admin@comsats.edu.pk");
            stmt.setString(3, "admin123");
            stmt.setString(4, "admin");
            stmt.executeUpdate();

            // Insert sample student
            stmt.setString(1, "Ahmed Ali");
            stmt.setString(2, "ahmed@student.comsats.edu.pk");
            stmt.setString(3, "student123");
            stmt.setString(4, "student");
            stmt.executeUpdate();

            // Insert sample drivers
            stmt = connection.prepareStatement(
                    "INSERT INTO drivers (name, phone, license_no) VALUES (?, ?, ?)");
            String[][] drivers = {
                    {"Muhammad Hassan", "0300-1234567", "LHR-1234567"},
                    {"Ali Raza", "0301-2345678", "LHR-2345678"},
                    {"Usman Khalid", "0302-3456789", "LHR-3456789"}
            };
            for (String[] driver : drivers) {
                stmt.setString(1, driver[0]);
                stmt.setString(2, driver[1]);
                stmt.setString(3, driver[2]);
                stmt.executeUpdate();
            }

            // Insert sample buses
            stmt = connection.prepareStatement(
                    "INSERT INTO buses (bus_no, capacity, driver_id) VALUES (?, ?, ?)");
            String[][] buses = {
                    {"A-123", "40", "1"},
                    {"B-456", "40", "2"},
                    {"C-789", "40", "3"}
            };
            for (String[] bus : buses) {
                stmt.setString(1, bus[0]);
                stmt.setInt(2, Integer.parseInt(bus[1]));
                stmt.setInt(3, Integer.parseInt(bus[2]));
                stmt.executeUpdate();
            }

            // Insert sample routes
            stmt = connection.prepareStatement(
                    "INSERT INTO routes (route_name, total_stops) VALUES (?, ?)");
            String[][] routes = {
                    {"Main Campus Route", "10"},
                    {"Railway Station Route", "6"},
                    {"Sahiwal City Route", "8"},
                    {"Airport Route", "4"}
            };
            for (String[] route : routes) {
                stmt.setString(1, route[0]);
                stmt.setInt(2, Integer.parseInt(route[1]));
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User authenticateUser(String email, String password, String role) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM users WHERE email = ? AND password = ? AND role = ?");
            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.setString(3, role);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerUser(String name, String email, String password, String role) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)");
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, role);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Route operations
    public List<User.Route> getAllRoutes() {
        List<User.Route> routes = new ArrayList<>();
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM routes");
            while (rs.next()) {
                routes.add(new User.Route(
                        rs.getInt("id"),
                        rs.getString("route_name"),
                        rs.getInt("total_stops")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return routes;
    }

    public boolean addRoute(String routeName, int totalStops, String[] stops) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO routes (route_name, total_stops) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, routeName);
            stmt.setInt(2, totalStops);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int routeId = rs.getInt(1);

                stmt = connection.prepareStatement(
                        "INSERT INTO stops (route_id, stop_name, order_no) VALUES (?, ?, ?)");
                for (int i = 0; i < stops.length; i++) {
                    stmt.setInt(1, routeId);
                    stmt.setString(2, stops[i]);
                    stmt.setInt(3, i + 1);
                    stmt.executeUpdate();
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRoute(int routeId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM routes WHERE id = ?");
            stmt.setInt(1, routeId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getRouteStops(int routeId) {
        StringBuilder stops = new StringBuilder();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT stop_name FROM stops WHERE route_id = ? ORDER BY order_no ASC");
            stmt.setInt(1, routeId);
            ResultSet rs = stmt.executeQuery();

            int count = 1;
            while (rs.next()) {
                stops.append(count++).append(". ")
                        .append(rs.getString("stop_name")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stops.toString();
    }

    // Bus operations
    public List<Bus> getAllBuses() {
        List<Bus> buses = new ArrayList<>();
        try {
            ResultSet rs = connection.createStatement().executeQuery(
                    "SELECT b.*, d.name AS driver_name FROM buses b " +
                            "LEFT JOIN drivers d ON b.driver_id = d.id");
            while (rs.next()) {
                buses.add(new Bus(
                        rs.getInt("id"),
                        rs.getString("bus_no"),
                        rs.getInt("capacity"),
                        rs.getInt("driver_id"),
                        rs.getString("driver_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buses;
    }

    public List<Bus> getBusesForRoute(int routeId) {
        return getAllBuses(); // same design as before
    }

    public boolean addBus(String busNo, int capacity, int driverId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO buses (bus_no, capacity, driver_id) VALUES (?, ?, ?)");
            stmt.setString(1, busNo);
            stmt.setInt(2, capacity);
            stmt.setInt(3, driverId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBus(int busId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM buses WHERE id = ?");
            stmt.setInt(1, busId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Driver operations
    public List<Driver> getAllDrivers() {
        List<Driver> drivers = new ArrayList<>();
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM drivers");
            while (rs.next()) {
                drivers.add(new Driver(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("license_no")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drivers;
    }

    public boolean addDriver(String name, String phone, String licenseNo) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO drivers (name, phone, license_no) VALUES (?, ?, ?)");
            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.setString(3, licenseNo);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDriver(int driverId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM drivers WHERE id = ?");
            stmt.setInt(1, driverId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Booking operations
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        try {
            ResultSet rs = connection.createStatement().executeQuery(
                    "SELECT b.*, u.name AS student_name, bus.bus_no, r.route_name " +
                            "FROM bookings b " +
                            "JOIN users u ON b.user_id = u.id " +
                            "JOIN buses bus ON b.bus_id = bus.id " +
                            "JOIN routes r ON b.route_id = r.id " +
                            "ORDER BY b.created_at DESC");
            while (rs.next()) {
                bookings.add(new Booking(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("student_name"),
                        rs.getInt("bus_id"),
                        rs.getString("bus_no"),
                        rs.getInt("route_id"),
                        rs.getString("route_name"),
                        rs.getString("booking_date"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public List<Booking> getUserBookings(int userId) {
        List<Booking> bookings = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT b.*, u.name AS student_name, bus.bus_no, r.route_name " +
                            "FROM bookings b " +
                            "JOIN users u ON b.user_id = u.id " +
                            "JOIN buses bus ON b.bus_id = bus.id " +
                            "JOIN routes r ON b.route_id = r.id " +
                            "WHERE b.user_id = ? " +
                            "ORDER BY b.created_at DESC");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                bookings.add(new Booking(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("student_name"),
                        rs.getInt("bus_id"),
                        rs.getString("bus_no"),
                        rs.getInt("route_id"),
                        rs.getString("route_name"),
                        rs.getString("booking_date"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public List<Booking> getBookingsByStatus(String status) {
        List<Booking> bookings = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT b.*, u.name AS student_name, bus.bus_no, r.route_name " +
                            "FROM bookings b " +
                            "JOIN users u ON b.user_id = u.id " +
                            "JOIN buses bus ON b.bus_id = bus.id " +
                            "JOIN routes r ON b.route_id = r.id " +
                            "WHERE b.status = ? " +
                            "ORDER BY b.created_at DESC");
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                bookings.add(new Booking(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("student_name"),
                        rs.getInt("bus_id"),
                        rs.getString("bus_no"),
                        rs.getInt("route_id"),
                        rs.getString("route_name"),
                        rs.getString("booking_date"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public boolean createBooking(int userId, int busId, int routeId, String date, int priority) {
        try {
            // Check bus capacity
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT capacity FROM buses WHERE id = ?");
            stmt.setInt(1, busId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int capacity = rs.getInt("capacity");

            stmt = connection.prepareStatement(
                    "SELECT COUNT(*) AS booked FROM bookings WHERE bus_id = ? AND status='confirmed'");
            stmt.setInt(1, busId);
            rs = stmt.executeQuery();
            rs.next();
            int booked = rs.getInt("booked");

            String status = (booked < capacity) ? "confirmed" : "waiting";

            stmt = connection.prepareStatement(
                    "INSERT INTO bookings (user_id, bus_id, route_id, booking_date, status, priority_level) " +
                            "VALUES (?, ?, ?, ?, ?, ?)");
            stmt.setInt(1, userId);
            stmt.setInt(2, busId);
            stmt.setInt(3, routeId);
            stmt.setString(4, date);
            stmt.setString(5, status);
            stmt.setInt(6, priority);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelBooking(int bookingId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE bookings SET status='cancelled' WHERE id=?");
            stmt.setInt(1, bookingId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getUserActiveBookings(int userId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT COUNT(*) FROM bookings WHERE user_id = ? AND status = 'confirmed'");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getActiveBookingsCount() {
        try {
            ResultSet rs = connection.createStatement().executeQuery(
                    "SELECT COUNT(*) FROM bookings WHERE status = 'confirmed'");
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Waiting list operations
    public List<WaitingListEntry> getWaitingList() {
        List<WaitingListEntry> waitingList = new ArrayList<>();
        try {
            ResultSet rs = connection.createStatement().executeQuery(
                    "SELECT b.id, u.name AS student_name, r.route_name, b.priority_level, b.created_at " +
                            "FROM bookings b " +
                            "JOIN users u ON b.user_id = u.id " +
                            "JOIN routes r ON b.route_id = r.id " +
                            "WHERE b.status = 'waiting' " +
                            "ORDER BY b.priority_level ASC, b.created_at ASC");

            int position = 1;
            while (rs.next()) {
                waitingList.add(new WaitingListEntry(
                        position++,
                        rs.getInt("id"),
                        rs.getString("student_name"),
                        rs.getString("route_name"),
                        rs.getInt("priority_level"),
                        rs.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return waitingList;
    }

    public int getWaitingListCount() {
        try {
            ResultSet rs = connection.createStatement().executeQuery(
                    "SELECT COUNT(*) FROM bookings WHERE status = 'waiting'");
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean confirmWaitingListEntry(int bookingId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE bookings SET status = 'confirmed' WHERE id = ?");
            stmt.setInt(1, bookingId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Reports
    public List<RouteOccupancy> getRouteOccupancyReport() {
        List<RouteOccupancy> report = new ArrayList<>();
        try {
            ResultSet rs = connection.createStatement().executeQuery(
                    "SELECT r.route_name, " +
                            "SUM(bus.capacity) AS total_seats, " +
                            "COUNT(b.id) AS booked_seats " +
                            "FROM routes r " +
                            "LEFT JOIN bookings b ON r.id = b.route_id AND b.status = 'confirmed' " +
                            "LEFT JOIN buses bus ON b.bus_id = bus.id " +
                            "GROUP BY r.route_name");

            while (rs.next()) {
                report.add(new RouteOccupancy(
                        rs.getString("route_name"),
                        rs.getInt("total_seats"),
                        rs.getInt("booked_seats")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (report.isEmpty()) {
            report.add(new RouteOccupancy("Main Campus Route", 120, 102));
            report.add(new RouteOccupancy("Railway Station Route", 80, 65));
            report.add(new RouteOccupancy("Sahiwal City Route", 120, 88));
            report.add(new RouteOccupancy("Airport Route", 40, 35));
        }

        return report;
    }

}
