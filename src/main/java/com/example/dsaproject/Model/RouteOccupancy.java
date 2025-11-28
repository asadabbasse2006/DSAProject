package com.example.dsaproject.Model;

/**
 * Route Occupancy Report Model
 * Used for analytics and reporting
 */
public class RouteOccupancy {
    private String routeName;
    private int totalSeats;
    private int bookedSeats;
    private int availableSeats;

    public RouteOccupancy(String routeName, int totalSeats, int bookedSeats) {
        this.routeName = routeName;
        this.totalSeats = totalSeats;
        this.bookedSeats = bookedSeats;
        this.availableSeats = totalSeats - bookedSeats;
    }

    // Getters
    public String getRouteName() { return routeName; }
    public int getTotalSeats() { return totalSeats; }
    public int getBookedSeats() { return bookedSeats; }
    public int getAvailableSeats() { return availableSeats; }

    public double getOccupancyPercent() {
        return totalSeats > 0 ? (bookedSeats * 100.0 / totalSeats) : 0;
    }

    public String getOccupancyStatus() {
        double percent = getOccupancyPercent();
        if (percent >= 90) return "Full";
        if (percent >= 75) return "High";
        if (percent >= 50) return "Moderate";
        return "Low";
    }
}
