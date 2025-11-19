package com.example.dsaproject.Model;

import com.example.dsaproject.DAO.databaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Route {
    private int routeId;
    private String routeName;
    private List<Bus> buses;
    private List<String> stops;

    // Constructor
    public Route(int routeId, String routeName) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.buses = new ArrayList<>();
        this.stops = new ArrayList<>();
        loadBusesFromDB();
        loadStopsFromDB();
    }

    // Load buses assigned to this route from DB
    private void loadBusesFromDB() {
        String sql = "SELECT b.bus_id, b.bus_no, b.capacity, b.available_seats, b.driver_id " +
                "FROM buses b JOIN route_bus rb ON b.bus_id = rb.bus_id WHERE rb.route_id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, routeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Bus bus = new Bus(
                        rs.getInt("bus_id"),
                        rs.getString("bus_no"),
                        rs.getInt("capacity"),
                        rs.getInt("driver_id")
                );
                bus.setAvailableSeats(rs.getInt("available_seats"));
                buses.add(bus);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load stops
    private void loadStopsFromDB() {
        String sql = "SELECT stop_name FROM stops WHERE route_id = ? ORDER BY stop_order";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, routeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stops.add(rs.getString("stop_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Assign a bus to this route
    public void assignBus(Bus bus) {
        if (!buses.contains(bus)) {
            buses.add(bus);
            String sql = "INSERT INTO route_bus (route_id, bus_id) VALUES (?, ?)";
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, routeId);
                stmt.setInt(2, bus.getBusId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Remove a bus from this route
    public void removeBus(Bus bus) {
        buses.remove(bus);
        String sql = "DELETE FROM route_bus WHERE route_id = ? AND bus_id = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, routeId);
            stmt.setInt(2, bus.getBusId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Calculate total available seats across all buses
    public int calculateAvailableSeats() {
        int totalSeats = 0;
        for (Bus bus : buses) {
            totalSeats += bus.getAvailableSeats();
        }
        return totalSeats;
    }

    // Add a stop to route
    public void addStop(String stop) {
        stops.add(stop);
        String sql = "INSERT INTO stops (route_id, stop_name, order_no) VALUES (?, ?, ?)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, routeId);
            stmt.setString(2, stop);
            stmt.setInt(3, stops.size()); // Stop order
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Remove a stop from route (DB + List)
    public void removeStop(String stop) {
        if (stops.remove(stop)) {
            String sql = "DELETE FROM stops WHERE route_id = ? AND stop_name = ?";
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, routeId);
                stmt.setString(2, stop);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Reorder stops
    public void reorderStops(List<String> newOrder) {
        if (newOrder.size() == stops.size() && stops.containsAll(newOrder)) {
            stops = new ArrayList<>(newOrder);
            String sql = "UPDATE stops SET stop_order = ? WHERE route_id = ? AND stop_name = ?";
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < stops.size(); i++) {
                    stmt.setInt(1, i + 1);
                    stmt.setInt(2, routeId);
                    stmt.setString(3, stops.get(i));
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid stop order.");
        }
    }

    // Calculate total stops
    public int calculateTotalStops() {
        return stops.size();
    }

    // Getters
    public int getRouteId() {
        return routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public List<Bus> getBuses() {
        return Collections.unmodifiableList(buses);
    }

    public List<String> getStops() {
        return Collections.unmodifiableList(stops);
    }

    // Display route info
    public void displayRouteInfo() {
        System.out.println("Route ID: " + routeId);
        System.out.println("Route Name: " + routeName);
        System.out.println("Stops: " + stops);
        System.out.println("Buses assigned: ");
        for (Bus bus : buses) {
            System.out.println("  - Bus ID: " + bus.getBusId() + ", Seats Available: " + bus.getAvailableSeats());
        }
    }
}
