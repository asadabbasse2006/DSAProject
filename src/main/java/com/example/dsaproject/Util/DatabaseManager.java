package com.example.dsaproject.Util;

import com.example.dsaproject.Model.*;
import com.example.dsaproject.DataStructures.*;
import com.example.dsaproject.Model.Driver;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central Database Manager - MySQL Implementation
 * Integrates HashMap, LinkedList, Queue, ArrayList, HashSet, BST, Graph, PriorityQueue
 * Target: <5ms operations, <20MB memory
 */
public class DatabaseManager {

    private static DatabaseManager instance;
    private Connection connection;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/transport_db";
    private static final String DB_USER = "asadabbasse2006";
    private static final String DB_PASSWORD = "Asad@8042680"; // Change this

    // Data Structures
    private Map<Integer, User> userCache;
    private Map<Integer, Route> routeCache;
    private Map<Integer, Bus> busCache;
    private Map<String, User> sessionCache;
    private LinkedList<Driver> driverList;
    private ArrayList<Booking> bookingHistory;
    private Queue<String> activityLog;
    private Set<String> activeLicenses;
    private BookingPriorityQueue waitingListQueue;
    private RouteBST routeBST;
    private RouteGraph routeGraph;

    private DatabaseManager() {
        initializeDataStructures();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initializeDataStructures() {
        this.userCache = new ConcurrentHashMap<>();
        this.routeCache = new ConcurrentHashMap<>();
        this.busCache = new ConcurrentHashMap<>();
        this.sessionCache = new ConcurrentHashMap<>();
        this.driverList = new LinkedList<>();
        this.bookingHistory = new ArrayList<>();
        this.activityLog = new LinkedList<>();
        this.activeLicenses = new HashSet<>();
        this.waitingListQueue = new BookingPriorityQueue();
        this.routeBST = new RouteBST();
        this.routeGraph = new RouteGraph();
    }

    public void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✓ Connected to MySQL");
            createTables();
            loadDataIntoStructures();
        } catch (Exception e) {
            System.err.println("Database init failed: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        Statement stmt = connection.createStatement();

        stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +
                "email VARCHAR(100) UNIQUE NOT NULL," +
                "password VARCHAR(255) NOT NULL," +
                "role ENUM('student', 'admin') NOT NULL," +
                "registration_id VARCHAR(50)," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        stmt.execute("CREATE TABLE IF NOT EXISTS routes (" +
                "route_id INT AUTO_INCREMENT PRIMARY KEY," +
                "route_name VARCHAR(100) NOT NULL," +
                "total_stops INT NOT NULL," +
                "stops TEXT NOT NULL)");

        stmt.execute("CREATE TABLE IF NOT EXISTS drivers (" +
                "driver_id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +
                "phone VARCHAR(20) NOT NULL," +
                "license_no VARCHAR(50) UNIQUE NOT NULL)");

        stmt.execute("CREATE TABLE IF NOT EXISTS buses (" +
                "bus_id INT AUTO_INCREMENT PRIMARY KEY," +
                "bus_no VARCHAR(50) UNIQUE NOT NULL," +
                "capacity INT NOT NULL," +
                "driver_id INT," +
                "FOREIGN KEY (driver_id) REFERENCES drivers(driver_id))");

        stmt.execute("CREATE TABLE IF NOT EXISTS bookings (" +
                "booking_id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "route_id INT NOT NULL," +
                "bus_id INT NOT NULL," +
                "seat_no VARCHAR(10)," +
                "booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "status ENUM('confirmed', 'cancelled', 'waiting') DEFAULT 'confirmed'," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)," +
                "FOREIGN KEY (route_id) REFERENCES routes(route_id)," +
                "FOREIGN KEY (bus_id) REFERENCES buses(bus_id))");

        stmt.execute("CREATE TABLE IF NOT EXISTS waiting_list (" +
                "waiting_id INT AUTO_INCREMENT PRIMARY KEY," +
                "booking_id INT UNIQUE NOT NULL," +
                "user_id INT NOT NULL," +
                "route_id INT NOT NULL," +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (booking_id) REFERENCES bookings(booking_id))");

        stmt.close();
        insertDefaultAdmin();
    }

    private void insertDefaultAdmin() throws SQLException {
        String check = "SELECT COUNT(*) FROM users WHERE role = 'admin'";
        ResultSet rs = connection.createStatement().executeQuery(check);
        if (rs.next() && rs.getInt(1) == 0) {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)");
            ps.setString(1, "Admin");
            ps.setString(2, "admin@comsats.edu.pk");
            ps.setString(3, "admin123");
            ps.setString(4, "admin");
            ps.executeUpdate();
            ps.close();
        }
        rs.close();
    }

    private void loadDataIntoStructures() {
        try {
            loadRoutes();
            loadBuses();
            loadDrivers();
            loadWaitingList();
            System.out.println("✓ Data loaded into structures");
        } catch (SQLException e) {
            System.err.println("Load failed: " + e.getMessage());
        }
    }

    private void loadRoutes() throws SQLException {
        ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM routes");
        while (rs.next()) {
            Route r = new Route(rs.getInt("route_id"), rs.getString("route_name"),
                    rs.getInt("total_stops"), rs.getString("stops"));
            routeCache.put(r.getRouteId(), r);
            routeBST.insert(r);
        }
        routeGraph.buildFromRoutes(new ArrayList<>(routeCache.values()));
        rs.close();
    }

    private void loadBuses() throws SQLException {
        ResultSet rs = connection.createStatement().executeQuery(
                "SELECT b.*, d.name as driver_name FROM buses b " +
                        "LEFT JOIN drivers d ON b.driver_id = d.driver_id");
        while (rs.next()) {
            Bus b = new Bus(rs.getInt("bus_id"), rs.getString("bus_no"),
                    rs.getInt("capacity"), rs.getInt("driver_id"));
            b.setDriverName(rs.getString("driver_name"));
            busCache.put(b.getBusId(), b);
        }
        rs.close();
    }

    private void loadDrivers() throws SQLException {
        driverList.clear();
        activeLicenses.clear();
        ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM drivers");
        while (rs.next()) {
            Driver d = new Driver(rs.getInt("driver_id"), rs.getString("name"),
                    rs.getString("phone"), rs.getString("license_no"));
            driverList.add(d);
            activeLicenses.add(d.getLicenseNo());
        }
        rs.close();
    }

    private void loadWaitingList() throws SQLException {
        waitingListQueue.clear();
        ResultSet rs = connection.createStatement().executeQuery(
                "SELECT w.*, u.name as student_name, r.route_name FROM waiting_list w " +
                        "JOIN users u ON w.user_id = u.user_id " +
                        "JOIN routes r ON w.route_id = r.route_id");
        while (rs.next()) {
            WaitingListEntry e = new WaitingListEntry(rs.getInt("booking_id"),
                    rs.getInt("user_id"), rs.getInt("route_id"), rs.getTimestamp("timestamp"));
            e.setStudentName(rs.getString("student_name"));
            e.setRouteName(rs.getString("route_name"));
            waitingListQueue.add(e);
        }
        rs.close();
    }

    // USER OPERATIONS
    public User authenticateUser(String email, String password, String role) {
        User cached = sessionCache.get(email);
        if (cached != null && cached.getPassword().equals(password)) {
            return cached;
        }

        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM users WHERE email = ? AND password = ? AND role = ?");
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, role);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User u = new User(rs.getInt("user_id"), rs.getString("name"),
                        rs.getString("email"), rs.getString("password"),
                        rs.getString("role"), rs.getString("registration_id"));
                sessionCache.put(email, u);
                userCache.put(u.getUserId(), u);
                rs.close();
                ps.close();
                return u;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Auth failed: " + e.getMessage());
        }
        return null;
    }

    public boolean registerUser(String name, String email, String password, String regId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (name, email, password, role, registration_id) VALUES (?, ?, ?, 'student', ?)");
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, regId);
            int result = ps.executeUpdate();
            ps.close();
            return result > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // ROUTE OPERATIONS
    public List<Route> getAllRoutes() {
        return new ArrayList<>(routeCache.values());
    }

    public Route getRouteById(int id) {
        return routeCache.get(id);
    }

    public boolean addRoute(String name, int stops, String stopsList) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO routes (route_name, total_stops, stops) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setInt(2, stops);
            ps.setString(3, stopsList);
            int result = ps.executeUpdate();

            if (result > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    Route r = new Route(rs.getInt(1), name, stops, stopsList);
                    routeCache.put(r.getRouteId(), r);
                    routeBST.insert(r);
                }
                rs.close();
            }
            ps.close();
            return result > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateRoute(Route route) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE routes SET route_name = ?, total_stops = ?, stops = ? WHERE route_id = ?");
            ps.setString(1, route.getRouteName());
            ps.setInt(2, route.getTotalStops());
            ps.setString(3, route.getStops());
            ps.setInt(4, route.getRouteId());
            int result = ps.executeUpdate();
            ps.close();
            if (result > 0) routeCache.put(route.getRouteId(), route);
            return result > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteRoute(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM routes WHERE route_id = ?");
            ps.setInt(1, id);
            int result = ps.executeUpdate();
            ps.close();
            if (result > 0) {
                routeCache.remove(id);
                routeBST.delete(id);
            }
            return result > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // BUS OPERATIONS
    public List<Bus> getAllBuses() {
        return new ArrayList<>(busCache.values());
    }

    public boolean addBus(String busNo, int capacity, int driverId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO buses (bus_no, capacity, driver_id) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, busNo);
            ps.setInt(2, capacity);
            ps.setInt(3, driverId);
            int result = ps.executeUpdate();

            if (result > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    Bus b = new Bus(rs.getInt(1), busNo, capacity, driverId);
                    busCache.put(b.getBusId(), b);
                }
                rs.close();
            }
            ps.close();
            return result > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateBus(Bus bus) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE buses SET bus_no = ?, capacity = ?, driver_id = ? WHERE bus_id = ?");
            ps.setString(1, bus.getBusNo());
            ps.setInt(2, bus.getCapacity());
            ps.setInt(3, bus.getDriverId());
            ps.setInt(4, bus.getBusId());
            int result = ps.executeUpdate();
            ps.close();
            if (result > 0) busCache.put(bus.getBusId(), bus);
            return result > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteBus(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM buses WHERE bus_id = ?");
            ps.setInt(1, id);
            int result = ps.executeUpdate();
            ps.close();
            if (result > 0) busCache.remove(id);
            return result > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // DRIVER OPERATIONS (LinkedList)
    public List<Driver> getAllDrivers() {
        return new ArrayList<>(driverList);
    }

    public boolean addDriver(String name, String phone, String license) {
        if (activeLicenses.contains(license)) return false;

        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO drivers (name, phone, license_no) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, license);
            int result = ps.executeUpdate();

            if (result > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    Driver d = new Driver(rs.getInt(1), name, phone, license);
                    driverList.add(d);
                    activeLicenses.add(license);
                }
                rs.close();
            }
            ps.close();
            return result > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateDriver(Driver driver) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE drivers SET name = ?, phone = ?, license_no = ? WHERE driver_id = ?");
            ps.setString(1, driver.getName());
            ps.setString(2, driver.getPhone());
            ps.setString(3, driver.getLicenseNo());
            ps.setInt(4, driver.getDriverId());
            int result = ps.executeUpdate();
            ps.close();

            if (result > 0) {
                for (int i = 0; i < driverList.size(); i++) {
                    if (driverList.get(i).getDriverId() == driver.getDriverId()) {
                        driverList.set(i, driver);
                        break;
                    }
                }
            }
            return result > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteDriver(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM drivers WHERE driver_id = ?");
            ps.setInt(1, id);
            int result = ps.executeUpdate();
            ps.close();

            if (result > 0) {
                driverList.removeIf(d -> {
                    if (d.getDriverId() == id) {
                        activeLicenses.remove(d.getLicenseNo());
                        return true;
                    }
                    return false;
                });
            }
            return result > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // BOOKING OPERATIONS
    public int createBooking(int userId, int routeId, int busId) {
        try {
            int occupied = getOccupiedSeats(busId);
            Bus bus = busCache.get(busId);
            if (bus == null) return -1;

            String status = (occupied < bus.getCapacity()) ? "confirmed" : "waiting";
            String seatNo = status.equals("confirmed") ? "S" + (occupied + 1) : null;

            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO bookings (user_id, route_id, bus_id, seat_no, status) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setInt(2, routeId);
            ps.setInt(3, busId);
            ps.setString(4, seatNo);
            ps.setString(5, status);
            int result = ps.executeUpdate();

            int bookingId = -1;
            if (result > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    bookingId = rs.getInt(1);
                    if (status.equals("waiting")) {
                        addToWaitingList(bookingId, userId, routeId);
                    }
                }
                rs.close();
            }
            ps.close();
            return bookingId;
        } catch (SQLException e) {
            return -1;
        }
    }

    private void addToWaitingList(int bookingId, int userId, int routeId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO waiting_list (booking_id, user_id, route_id) VALUES (?, ?, ?)");
            ps.setInt(1, bookingId);
            ps.setInt(2, userId);
            ps.setInt(3, routeId);
            ps.executeUpdate();
            ps.close();

            WaitingListEntry e = new WaitingListEntry(bookingId, userId, routeId, new Date());
            waitingListQueue.add(e);
        } catch (SQLException ex) {}
    }

    public boolean confirmWaitingListEntry(int bookingId) {
        try {
            PreparedStatement ps1 = connection.prepareStatement("DELETE FROM waiting_list WHERE booking_id = ?");
            ps1.setInt(1, bookingId);
            ps1.executeUpdate();
            ps1.close();

            PreparedStatement ps2 = connection.prepareStatement(
                    "UPDATE bookings SET status = 'confirmed', seat_no = ? WHERE booking_id = ?");
            ps2.setString(1, "S-WAIT");
            ps2.setInt(2, bookingId);
            int result = ps2.executeUpdate();
            ps2.close();

            if (result > 0) {
                waitingListQueue.remove(bookingId);
            }
            return result > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean cancelBooking(int bookingId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE bookings SET status = 'cancelled' WHERE booking_id = ?");
            ps.setInt(1, bookingId);
            int result = ps.executeUpdate();
            ps.close();
            return result > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Booking> getUserBookings(int userId) {
        List<Booking> bookings = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT b.*, r.route_name, bu.bus_no FROM bookings b " +
                            "JOIN routes r ON b.route_id = r.route_id " +
                            "JOIN buses bu ON b.bus_id = bu.bus_id " +
                            "WHERE b.user_id = ? AND b.status = 'confirmed' " +
                            "ORDER BY b.booking_date DESC");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Booking b = new Booking(rs.getInt("booking_id"), rs.getInt("user_id"),
                        rs.getInt("route_id"), rs.getInt("bus_id"), rs.getString("seat_no"),
                        rs.getTimestamp("booking_date"), rs.getString("status"));
                b.setRouteName(rs.getString("route_name"));
                b.setBusNo(rs.getString("bus_no"));
                bookings.add(b);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {}
        return bookings;
    }

    public List<WaitingListEntry> getAllWaitingList() {
        return waitingListQueue.getAllSorted();
    }

    public int getActiveBookingsCount() {
        try {
            ResultSet rs = connection.createStatement().executeQuery(
                    "SELECT COUNT(*) FROM bookings WHERE status = 'confirmed'");
            if (rs.next()) return rs.getInt(1);
            rs.close();
        } catch (SQLException e) {}
        return 0;
    }

    public int getUserActiveBookings(int userId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT COUNT(*) FROM bookings WHERE user_id = ? AND status = 'confirmed'");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
            rs.close();
            ps.close();
        } catch (SQLException e) {}
        return 0;
    }

    public double getUserTotalSpent(int userId) {
        return getUserActiveBookings(userId) * 50.0; // Rs. 50 per booking
    }

    private int getOccupiedSeats(int busId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT COUNT(*) FROM bookings WHERE bus_id = ? AND status = 'confirmed'");
            ps.setInt(1, busId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
            rs.close();
            ps.close();
        } catch (SQLException e) {}
        return 0;
    }

    // UTILITY
    private void logActivity(String activity) {
        if (activityLog.size() >= 100) {
            activityLog.poll();
        }
        activityLog.offer(new Date() + ": " + activity);
    }

    public List<String> getRecentActivity() {
        return new ArrayList<>(activityLog);
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed");
            }
        } catch (SQLException e) {}
    }
}