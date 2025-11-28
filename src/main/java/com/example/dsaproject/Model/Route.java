package com.example.dsaproject.Model;

import java.util.List;
import java.util.ArrayList;

/**
 * Route Model Class
 * Represents a bus route with stops
 */
public class Route {
    private int routeId;
    private String routeName;
    private int totalStops;
    private List<String> stops;
    private String status;
    private double distance; // in kilometers

    public Route(int routeId, String routeName, int totalStops) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.totalStops = totalStops;
        this.stops = new ArrayList<>();
        this.status = "Active";
    }

    // Getters and Setters
    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public int getTotalStops() { return totalStops; }
    public void setTotalStops(int totalStops) { this.totalStops = totalStops; }

    public List<String> getStops() { return stops; }
    public void setStops(List<String> stops) {
        this.stops = stops;
        this.totalStops = stops.size();
    }

    public String[] getStopsArray() {
        return stops.toArray(new String[0]);
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public void addStop(String stop) {
        stops.add(stop);
        totalStops = stops.size();
    }

    public String getStopsString() {
        return String.join(" → ", stops);
    }

    @Override
    public String toString() {
        return routeName + " (" + totalStops + " stops)";
    }
}