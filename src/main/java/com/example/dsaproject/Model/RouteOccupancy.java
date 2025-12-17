package com.example.dsaproject.Model;

public class RouteOccupancy {
    private int routeId;
    private String routeName;
    private int totalCapacity;
    private int occupiedSeats;
    private int availableSeats;

    public RouteOccupancy() {}

    public RouteOccupancy(int routeId, String routeName, int totalCapacity,
                          int occupiedSeats) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.totalCapacity = totalCapacity;
        this.occupiedSeats = occupiedSeats;
        this.availableSeats = totalCapacity - occupiedSeats;
    }

    // Getters and Setters
    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public int getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
        this.availableSeats = totalCapacity - occupiedSeats;
    }

    public int getOccupiedSeats() { return occupiedSeats; }
    public void setOccupiedSeats(int occupiedSeats) {
        this.occupiedSeats = occupiedSeats;
        this.availableSeats = totalCapacity - occupiedSeats;
    }

    public int getAvailableSeats() { return availableSeats; }

    public double getOccupancyPercentage() {
        return totalCapacity > 0 ? (occupiedSeats * 100.0) / totalCapacity : 0;
    }

    public boolean hasAvailableSeats() {
        return availableSeats > 0;
    }
}