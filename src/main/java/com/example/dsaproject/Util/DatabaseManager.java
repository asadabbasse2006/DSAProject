package com.example.dsaproject.Util;

import com.example.dsaproject.Model.*;
import com.example.dsaproject.Model.Driver;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;

    // --- CHANGE THESE TO YOUR MYSQL CREDENTIALS ---
    private static final String DB_USER = "asadabbasse2006";
    private static final String DB_PASS = "Asad@8042680"; // set your password
    // -----------------------------------------------

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/transportdb?useSSL=false&serverTimezone=UTC";

    private DatabaseManager() {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("MySQL database connection established");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
        }
    }

    /**
     * Get singleton instance
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Initialize database (create database if not exists and tables)
     */
    public void initializeDatabase() {
        try {
            // Ensure database exists (attempt only if connection failed due to missing DB)
            // Note: If DB doesn't exist, user should create it or the connection URL should use
            // a server-level URL without DB to create it. For simplicity, we assume DB exists.
            createTables();
            insertSampleData();
            System.out.println("✓ Database initialized successfully");
        } catch (SQLException e) {
            System.err.println("✗ Database initialization failed!");
            e.printStackTrace();
        }
    }

    /**
     * Create all database tables (MySQL-compatible)
     */
    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {

            // Users table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "name VARCHAR(255) NOT NULL, " +
                            "email VARCHAR(255) UNIQUE NOT NULL, " +
                            "password VARCHAR(255) NOT NULL, " +
                            "role VARCHAR(50) NOT NULL, " +
                            "phone VARCHAR(50), " +
                            "address VARCHAR(500), " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ") ENGINE=InnoDB;"
            );

            // Drivers table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS drivers (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "name VARCHAR(255) NOT NULL, " +
                            "phone VARCHAR(50), " +
                            "license_no VARCHAR(100) UNIQUE, " +
                            "email VARCHAR(255), " +
                            "experience INT DEFAULT 0, " +
                            "status VARCHAR(50) DEFAULT 'Active'" +
                            ") ENGINE=InnoDB;"
            );

            // Buses table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS buses (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "bus_no VARCHAR(50) UNIQUE NOT NULL, " +
                            "capacity INT NOT NULL, " +
                            "driver_id INT, " +
                            "status VARCHAR(50) DEFAULT 'Active', " +
                            "FOREIGN KEY (driver_id) REFERENCES drivers(id) ON DELETE SET NULL" +
                            ") ENGINE=InnoDB;"
            );

            // Routes table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS routes (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "route_name VARCHAR(255) NOT NULL, " +
                            "total_stops INT NOT NULL, " +
                            "distance DOUBLE DEFAULT 0, " +
                            "status VARCHAR(50) DEFAULT 'Active'" +
                            ") ENGINE=InnoDB;"
            );

            // Stops table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS stops (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "route_id INT, " +
                            "stop_name VARCHAR(255) NOT NULL, " +
                            "order_no INT, " +
                            "FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE" +
                            ") ENGINE=InnoDB;"
            );

            // Bookings table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS bookings (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "user_id INT, " +
                            "bus_id INT, " +
                            "route_id INT, " +
                            "booking_date DATE, " +
                            "status VARCHAR(50) DEFAULT 'confirmed', " +
                            "priority_level INT DEFAULT 3, " +
                            "fare DOUBLE DEFAULT 50.0, " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                            "FOREIGN KEY (bus_id) REFERENCES buses(id) ON DELETE SET NULL, " +
                            "FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE SET NULL" +
                            ") ENGINE=InnoDB;"
            );

        }
    }

    /**
     * Insert sample data if database is empty
     */
    private void insertSampleData() throws SQLException {
        // Check if data already exists
        String checkUsersSql = "SELECT COUNT(*) FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(checkUsersSql)) {
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Data already exists
            }
        }

        // We will use transactions for bulk inserts for performance and atomicity
        connection.setAutoCommit(false);
        try {
            // Insert sample users
            String insertUserSql = "INSERT INTO users (name, email, password, role, phone, address) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement userStmt = connection.prepareStatement(insertUserSql)) {
                // Admin user
                userStmt.setString(1, "Admin User");
                userStmt.setString(2, "admin@comsats.edu.pk");
                userStmt.setString(3, "admin123");
                userStmt.setString(4, "admin");
                userStmt.setString(5, "0300-1234567");
                userStmt.setString(6, "COMSATS University, Sahiwal");
                userStmt.executeUpdate();

                // Sample students
                String[][] students = {
                        {"Ahmed Ali", "ahmed@student.comsats.edu.pk", "student123", "0301-2345678", "Farid Town, Sahiwal"},
                        {"Fatima Khan", "fatima@student.comsats.edu.pk", "student123", "0302-3456789", "Civil Lines, Sahiwal"},
                        {"Hassan Raza", "hassan@student.comsats.edu.pk", "student123", "0303-4567890", "Model Town, Sahiwal"},
                        {"Ayesha Malik", "ayesha@student.comsats.edu.pk", "student123", "0304-5678901", "Jinnah Park, Sahiwal"}
                };

                for (String[] student : students) {
                    userStmt.setString(1, student[0]);
                    userStmt.setString(2, student[1]);
                    userStmt.setString(3, student[2]);
                    userStmt.setString(4, "student");
                    userStmt.setString(5, student[3]);
                    userStmt.setString(6, student[4]);
                    userStmt.executeUpdate();
                }
            }

            // Insert sample drivers
            String insertDriverSql = "INSERT INTO drivers (name, phone, license_no, email, experience) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement driverStmt = connection.prepareStatement(insertDriverSql)) {
                String[][] drivers = {
                        {"Muhammad Hassan", "0300-1111111", "LHR-1234567", "hassan.driver@comsats.edu.pk", "5"},
                        {"Ali Raza", "0301-2222222", "LHR-2345678", "ali.driver@comsats.edu.pk", "8"},
                        {"Usman Khalid", "0302-3333333", "LHR-3456789", "usman.driver@comsats.edu.pk", "3"},
                        {"Bilal Ahmed", "0303-4444444", "LHR-4567890", "bilal.driver@comsats.edu.pk", "6"},
                        {"Kamran Shah", "0304-5555555", "LHR-5678901", "kamran.driver@comsats.edu.pk", "4"}
                };

                for (String[] driver : drivers) {
                    driverStmt.setString(1, driver[0]);
                    driverStmt.setString(2, driver[1]);
                    driverStmt.setString(3, driver[2]);
                    driverStmt.setString(4, driver[3]);
                    driverStmt.setInt(5, Integer.parseInt(driver[4]));
                    driverStmt.executeUpdate();
                }
            }

            // Insert sample buses
            String insertBusSql = "INSERT INTO buses (bus_no, capacity, drivers_id) VALUES (?, ?, ?)";
            try (PreparedStatement busStmt = connection.prepareStatement(insertBusSql)) {
                String[][] buses = {
                        {"A-123", "40", "1"},
                        {"B-456", "40", "2"},
                        {"C-789", "40", "3"},
                        {"D-101", "35", "4"},
                        {"E-202", "35", "5"}
                };

                for (String[] bus : buses) {
                    busStmt.setString(1, bus[0]);
                    busStmt.setInt(2, Integer.parseInt(bus[1]));
                    busStmt.setInt(3, Integer.parseInt(bus[2]));
                    busStmt.executeUpdate();
                }
            }

            // Insert sample routes
            String insertRouteSql = "INSERT INTO routes (route_name, total_stops, distance) VALUES (?, ?, ?)";
            try (PreparedStatement routeStmt = connection.prepareStatement(insertRouteSql)) {
                String[][] routes = {
                        {"Main Campus Route", "10", "15.5"},
                        {"Railway Station Route", "6", "8.2"},
                        {"Sahiwal City Route", "8", "12.0"},
                        {"Airport Route", "4", "25.0"},
                        {"Hospital Route", "7", "10.5"}
                };

                for (String[] route : routes) {
                    routeStmt.setString(1, route[0]);
                    routeStmt.setInt(2, Integer.parseInt(route[1]));
                    routeStmt.setDouble(3, Double.parseDouble(route[2]));
                    routeStmt.executeUpdate();
                }
            }

            // Insert stops for routes
            String insertStopSql = "INSERT INTO stops (route_id, stop_name, order_no) VALUES (?, ?, ?)";
            try (PreparedStatement stopStmt = connection.prepareStatement(insertStopSql)) {
                // Main Campus Route stops
                String[] mainCampusStops = {
                        "COMSATS Main Gate", "Farid Town", "Civil Hospital", "Railway Station",
                        "City Center", "Jinnah Park", "Model Town", "Bus Stand", "Chowk Sarwar Shaheed", "University Gate"
                };
                for (int i = 0; i < mainCampusStops.length; i++) {
                    stopStmt.setInt(1, 1);
                    stopStmt.setString(2, mainCampusStops[i]);
                    stopStmt.setInt(3, i + 1);
                    stopStmt.executeUpdate();
                }

                // Railway Station Route stops
                String[] railwayStops = {
                        "COMSATS Gate", "Farid Town Stop", "Civil Lines", "Railway Junction",
                        "Main Station", "Station Exit"
                };
                for (int i = 0; i < railwayStops.length; i++) {
                    stopStmt.setInt(1, 2);
                    stopStmt.setString(2, railwayStops[i]);
                    stopStmt.setInt(3, i + 1);
                    stopStmt.executeUpdate();
                }

                // Sahiwal City Route stops
                String[] cityStops = {
                        "University Entrance", "City Mall", "GPO Chowk", "District Court",
                        "DHQ Hospital", "Commissioner Office", "City Park", "COMSATS Back Gate"
                };
                for (int i = 0; i < cityStops.length; i++) {
                    stopStmt.setInt(1, 3);
                    stopStmt.setString(2, cityStops[i]);
                    stopStmt.setInt(3, i + 1);
                    stopStmt.executeUpdate();
                }

                // Airport Route stops
                String[] airportStops = {
                        "COMSATS Campus", "Highway Toll", "Airport Junction", "Sahiwal Airport"
                };
                for (int i = 0; i < airportStops.length; i++) {
                    stopStmt.setInt(1, 4);
                    stopStmt.setString(2, airportStops[i]);
                    stopStmt.setInt(3, i + 1);
                    stopStmt.executeUpdate();
                }

                // Hospital Route stops
                String[] hospitalStops = {
                        "University Gate", "Medical Store", "Civil Hospital Main", "Emergency Ward",
                        "OPD Block", "Children Hospital", "Back to Campus"
                };
                for (int i = 0; i < hospitalStops.length; i++) {
                    stopStmt.setInt(1, 5);
                    stopStmt.setString(2, hospitalStops[i]);
                    stopStmt.setInt(3, i + 1);
                    stopStmt.executeUpdate();
                }
            }

            // Insert sample bookings
            String insertBookingSql = "INSERT INTO bookings (user_id, bus_id, route_id, booking_date, status, priority_level, fare) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement bookingStmt = connection.prepareStatement(insertBookingSql)) {
                bookingStmt.setInt(1, 2); // Ahmed
                bookingStmt.setInt(2, 1);
                bookingStmt.setInt(3, 1);
                bookingStmt.setDate(4, Date.valueOf("2024-11-27"));
                bookingStmt.setString(5, "confirmed");
                bookingStmt.setInt(6, 3);
                bookingStmt.setDouble(7, 50.0);
                bookingStmt.executeUpdate();

                bookingStmt.setInt(1, 3); // Fatima
                bookingStmt.setInt(2, 2);
                bookingStmt.setInt(3, 2);
                bookingStmt.setDate(4, Date.valueOf("2024-11-27"));
                bookingStmt.setString(5, "confirmed");
                bookingStmt.setInt(6, 1);
                bookingStmt.setDouble(7, 40.0);
                bookingStmt.executeUpdate();
            }

            connection.commit();
            System.out.println("✓ Sample data inserted successfully");
        } catch (SQLException e) {
            connection.rollback();
            System.err.println("✗ Failed to insert sample data, rolled back.");
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // ========================================
    // AUTHENTICATION METHODS
    // ========================================

    public User authenticateUser(String email, String password, String role) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ? AND role = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.setString(3, role);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getString("phone"),
                            rs.getString("address")
                    );
                    System.out.println("✓ User authenticated: " + user.getName());
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerUser(String name, String email, String password, String role) {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, role);
            stmt.executeUpdate();
            System.out.println("✓ User registered: " + email);
            return true;
        } catch (SQLException e) {
            System.err.println("✗ Registration failed: " + e.getMessage());
            return false;
        }
    }

    // ========================================
    // ROUTE OPERATIONS
    // ========================================

    public List<Route> getAllRoutes() {
        List<Route> routes = new ArrayList<>();
        String sql = "SELECT * FROM routes WHERE status = 'Active'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Route route = new Route(
                        rs.getInt("id"),
                        rs.getString("route_name"),
                        rs.getInt("total_stops")
                );
                route.setDistance(rs.getDouble("distance"));
                route.setStatus(rs.getString("status"));

                // Load stops for this route
                List<String> stops = getStopsForRoute(route.getRouteId());
                route.setStops(stops);

                routes.add(route);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return routes;
    }

    private List<String> getStopsForRoute(int routeId) {
        List<String> stops = new ArrayList<>();
        String sql = "SELECT stop_name FROM stops WHERE route_id = ? ORDER BY order_no";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, routeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    stops.add(rs.getString("stop_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stops;
    }

    public boolean addRoute(String routeName, int totalStops, List<String> stops) {
        String insertRouteSql = "INSERT INTO routes (route_name, total_stops) VALUES (?, ?)";
        String insertStopSql = "INSERT INTO stops (route_id, stop_name, order_no) VALUES (?, ?, ?)";

        try (PreparedStatement routeStmt = connection.prepareStatement(insertRouteSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement stopStmt = connection.prepareStatement(insertStopSql)) {

            // Start transaction
            connection.setAutoCommit(false);

            // Insert route
            routeStmt.setString(1, routeName);
            routeStmt.setInt(2, totalStops);
            routeStmt.executeUpdate();

            int routeId = -1;
            try (ResultSet rs = routeStmt.getGeneratedKeys()) {
                if (rs.next()) {
                    routeId = rs.getInt(1);
                } else {
                    System.err.println("Failed to retrieve route ID.");
                    connection.rollback();
                    return false;
                }
            }

            // Insert stops
            for (int i = 0; i < stops.size(); i++) {
                stopStmt.setInt(1, routeId);
                stopStmt.setString(2, stops.get(i));
                stopStmt.setInt(3, i + 1);
                stopStmt.executeUpdate();
            }

            // Commit changes
            connection.commit();
            System.out.println("✓ Route added: " + routeName);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public boolean updateRoute(Route route) {
        String updateRouteSql = "UPDATE routes SET route_name = ?, total_stops = ? WHERE id = ?";
        String deleteStopsSql = "DELETE FROM stops WHERE route_id = ?";
        String insertStopSql = "INSERT INTO stops (route_id, stop_name, order_no) VALUES (?, ?, ?)";
        try (PreparedStatement updateRouteStmt = connection.prepareStatement(updateRouteSql);
             PreparedStatement deleteStopsStmt = connection.prepareStatement(deleteStopsSql);
             PreparedStatement insertStopStmt = connection.prepareStatement(insertStopSql)) {

            updateRouteStmt.setString(1, route.getRouteName());
            updateRouteStmt.setInt(2, route.getTotalStops());
            updateRouteStmt.setInt(3, route.getRouteId());
            updateRouteStmt.executeUpdate();

            deleteStopsStmt.setInt(1, route.getRouteId());
            deleteStopsStmt.executeUpdate();

            List<String> stops = route.getStops();
            for (int i = 0; i < stops.size(); i++) {
                insertStopStmt.setInt(1, route.getRouteId());
                insertStopStmt.setString(2, stops.get(i));
                insertStopStmt.setInt(3, i + 1);
                insertStopStmt.executeUpdate();
            }

            System.out.println("✓ Route updated: " + route.getRouteName());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRoute(int routeId) {
        String sql = "DELETE FROM routes WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, routeId);
            stmt.executeUpdate();
            System.out.println("✓ Route deleted: " + routeId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getRouteStops(int routeId) {
        StringBuilder stops = new StringBuilder();
        String sql = "SELECT stop_name FROM stops WHERE route_id = ? ORDER BY order_no";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, routeId);
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 1;
                while (rs.next()) {
                    stops.append(count++).append(". ").append(rs.getString("stop_name")).append("\n");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stops.toString();
    }

    // ========================================
    // BUS OPERATIONS
    // ========================================

    public List<Bus> getAllBuses() {
        List<Bus> buses = new ArrayList<>();
        String sql = "SELECT b.*, d.name as driver_name FROM buses b " +
                "LEFT JOIN drivers d ON b.drivers_id = d.drivers_id " +
                "WHERE b.status = 'Active'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Bus bus = new Bus(
                        rs.getInt("id"),
                        rs.getString("bus_no"),
                        rs.getInt("capacity"),
                        rs.getInt("drivers_id"),
                        rs.getString("driver_name")
                );
                bus.setStatus(rs.getString("status"));

                // Get current occupancy
                int occupancy = getBusOccupancy(bus.getBusId());
                bus.setCurrentOccupancy(occupancy);

                buses.add(bus);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buses;
    }

    private int getBusOccupancy(int busId) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE bus_id = ? AND status = 'confirmed'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, busId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Bus> getBusesForRoute(int routeId) {
        // For simplicity, return all available buses (can be enhanced to filter by route)
        return getAllBuses();
    }

    public boolean addBus(String busNo, int capacity, int driverId) {
        String sql = "INSERT INTO buses (bus_no, capacity, driver_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, busNo);
            stmt.setInt(2, capacity);
            stmt.setInt(3, driverId);
            stmt.executeUpdate();
            System.out.println("✓ Bus added: " + busNo);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBus(Bus bus) {
        String sql = "UPDATE buses SET bus_no = ?, capacity = ?, drivers_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, bus.getBusNo());
            stmt.setInt(2, bus.getCapacity());
            stmt.setInt(3, bus.getDriverId());
            stmt.setInt(4, bus.getBusId());
            stmt.executeUpdate();
            System.out.println("✓ Bus updated: " + bus.getBusNo());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBus(int busId) {
        String sql = "DELETE FROM buses WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, busId);
            stmt.executeUpdate();
            System.out.println("✓ Bus deleted: " + busId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========================================
    // DRIVER OPERATIONS
    // ========================================

    public List<Driver> getAllDrivers() {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers WHERE status = 'Active'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Driver driver = new Driver(
                        rs.getInt("drivers_id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("license_no")
                );
                driver.setEmail(rs.getString("email"));
                driver.setExperience(rs.getInt("experience"));
                driver.setStatus(rs.getString("status"));
                drivers.add(driver);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drivers;
    }

    public boolean addDriver(String name, String phone, String licenseNo) {
        String sql = "INSERT INTO drivers (name, phone, license_no) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.setString(3, licenseNo);
            stmt.executeUpdate();
            System.out.println("✓ Driver added: " + name);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDriver(Driver driver) {
        String sql = "UPDATE drivers SET name = ?, phone = ?, license_no = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, driver.getName());
            stmt.setString(2, driver.getPhone());
            stmt.setString(3, driver.getLicenseNo());
            stmt.setInt(4, driver.getDriverId());
            stmt.executeUpdate();
            System.out.println("✓ Driver updated: " + driver.getName());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDriver(int driverId) {
        String sql = "DELETE FROM drivers WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            stmt.executeUpdate();
            System.out.println("✓ Driver deleted: " + driverId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========================================
    // BOOKING OPERATIONS
    // ========================================

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, u.name as student_name, bus.bus_no, r.route_name " +
                "FROM bookings b " +
                "LEFT JOIN users u ON b.user_id = u.id " +
                "LEFT JOIN buses bus ON b.bus_id = bus.id " +
                "LEFT JOIN routes r ON b.route_id = r.id " +
                "ORDER BY b.created_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Booking booking = new Booking(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("student_name"),
                        rs.getInt("bus_id"),
                        rs.getString("bus_no"),
                        rs.getInt("route_id"),
                        rs.getString("route_name"),
                        rs.getString("booking_date"),
                        rs.getString("status")
                );
                booking.setPriorityLevel(rs.getInt("priority_level"));
                booking.setFare(rs.getDouble("fare"));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public List<Booking> getUserBookings(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, u.name as student_name, bus.bus_no, r.route_name " +
                "FROM bookings b " +
                "LEFT JOIN users u ON b.user_id = u.id " +
                "LEFT JOIN buses bus ON b.bus_id = bus.id " +
                "LEFT JOIN routes r ON b.route_id = r.id " +
                "WHERE b.user_id = ? " +
                "ORDER BY b.created_at DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("student_name"),
                            rs.getInt("bus_id"),
                            rs.getString("bus_no"),
                            rs.getInt("route_id"),
                            rs.getString("route_name"),
                            rs.getString("booking_date"),
                            rs.getString("status")
                    );
                    booking.setPriorityLevel(rs.getInt("priority_level"));
                    booking.setFare(rs.getDouble("fare"));
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public List<Booking> getBookingsByStatus(String status) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, u.name as student_name, bus.bus_no, r.route_name " +
                "FROM bookings b " +
                "LEFT JOIN users u ON b.user_id = u.id " +
                "LEFT JOIN buses bus ON b.bus_id = bus.id " +
                "LEFT JOIN routes r ON b.route_id = r.id " +
                "WHERE b.status = ? " +
                "ORDER BY b.created_at DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("student_name"),
                            rs.getInt("bus_id"),
                            rs.getString("bus_no"),
                            rs.getInt("route_id"),
                            rs.getString("route_name"),
                            rs.getString("booking_date"),
                            rs.getString("status")
                    );
                    booking.setPriorityLevel(rs.getInt("priority_level"));
                    booking.setFare(rs.getDouble("fare"));
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public boolean createBooking(int userId, int busId, int routeId, String date, int priority) {
        try {
            // Check bus capacity
            String capacitySql = "SELECT capacity FROM buses WHERE id = ?";
            int capacity = 0;
            try (PreparedStatement capStmt = connection.prepareStatement(capacitySql)) {
                capStmt.setInt(1, busId);
                try (ResultSet rs = capStmt.executeQuery()) {
                    if (rs.next()) {
                        capacity = rs.getInt("capacity");
                    }
                }
            }

            // Count current bookings
            String countSql = "SELECT COUNT(*) as booked FROM bookings WHERE bus_id = ? AND status = 'confirmed' AND booking_date = ?";
            int booked = 0;
            try (PreparedStatement countStmt = connection.prepareStatement(countSql)) {
                countStmt.setInt(1, busId);
                countStmt.setDate(2, Date.valueOf(date));
                try (ResultSet rs = countStmt.executeQuery()) {
                    if (rs.next()) {
                        booked = rs.getInt("booked");
                    }
                }
            }

            // Determine status
            String status = (booked < capacity) ? "confirmed" : "waiting";

            // Create booking
            String insertSql = "INSERT INTO bookings (user_id, bus_id, route_id, booking_date, status, priority_level, fare) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, busId);
                insertStmt.setInt(3, routeId);
                insertStmt.setDate(4, Date.valueOf(date));
                insertStmt.setString(5, status);
                insertStmt.setInt(6, priority);
                insertStmt.setDouble(7, 50.0); // Default fare
                insertStmt.executeUpdate();
            }

            System.out.println("✓ Booking created with status: " + status);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelBooking(int bookingId) {
        String sql = "UPDATE bookings SET status = 'cancelled' WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            stmt.executeUpdate();
            System.out.println("✓ Booking cancelled: " + bookingId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getUserActiveBookings(int userId) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE user_id = ? AND status = 'confirmed'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getActiveBookingsCount() {
        String sql = "SELECT COUNT(*) FROM bookings WHERE status = 'confirmed'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getUserTotalSpent(int userId) {
        String sql = "SELECT SUM(fare) FROM bookings WHERE user_id = ? AND status != 'cancelled'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // ========================================
    // WAITING LIST OPERATIONS (Priority Queue)
    // ========================================

    public List<WaitingListEntry> getWaitingList() {
        List<WaitingListEntry> waitingList = new ArrayList<>();
        String sql = "SELECT b.id, u.name as student_name, r.route_name, b.priority_level, b.created_at " +
                "FROM bookings b " +
                "JOIN users u ON b.user_id = u.id " +
                "JOIN routes r ON b.route_id = r.id " +
                "WHERE b.status = 'waiting' " +
                "ORDER BY b.priority_level ASC, b.created_at ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            int position = 1;
            while (rs.next()) {
                WaitingListEntry entry = new WaitingListEntry(
                        position++,
                        rs.getInt("id"),
                        rs.getString("student_name"),
                        rs.getString("route_name"),
                        rs.getInt("priority_level"),
                        rs.getString("created_at")
                );
                waitingList.add(entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return waitingList;
    }

    public int getWaitingListCount() {
        String sql = "SELECT COUNT(*) FROM bookings WHERE status = 'waiting'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean confirmWaitingListEntry(int bookingId) {
        String sql = "UPDATE bookings SET status = 'confirmed' WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            stmt.executeUpdate();
            System.out.println("✓ Waiting list entry confirmed: " + bookingId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========================================
    // REPORTS & ANALYTICS
    // ========================================

    public List<RouteOccupancy> getRouteOccupancyReport() {
        List<RouteOccupancy> report = new ArrayList<>();
        String sql = "SELECT r.route_name, " +
                "COALESCE(SUM(bus.capacity), 0) AS total_seats, " +
                "COUNT(DISTINCT book.id) AS booked_seats " +
                "FROM routes r " +
                "LEFT JOIN bookings book ON r.id = book.route_id AND book.status = 'confirmed' " +
                "LEFT JOIN buses bus ON book.bus_id = bus.id " +
                "WHERE r.status = 'Active' " +
                "GROUP BY r.id, r.route_name";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

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

        // Sample data fallback
        if (report.isEmpty()) {
            report.add(new RouteOccupancy("Main Campus Route", 120, 102));
            report.add(new RouteOccupancy("Railway Station Route", 80, 65));
            report.add(new RouteOccupancy("Sahiwal City Route", 120, 88));
            report.add(new RouteOccupancy("Airport Route", 40, 35));
        }

        return report;
    }


    // ========================================
    // USER MANAGEMENT
    // ========================================

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("phone"),
                        rs.getString("address")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, phone = ?, address = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhone());
            stmt.setString(4, user.getAddress());
            stmt.setInt(5, user.getUserId());
            stmt.executeUpdate();
            System.out.println("✓ User updated: " + user.getName());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            System.out.println("✓ User deleted: " + userId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========================================
    // UTILITY METHODS
    // ========================================

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public Collection<? extends WaitingListEntry> getAllWaitingList() {

        return List.of();
    }
}

