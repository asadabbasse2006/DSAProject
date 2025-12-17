package com.example.dsaproject.Model;

public class Route {
    private int routeId;
    private String routeName;
    private int totalStops;
    private String stops; // Comma-separated stops
    private int distance;

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Route(int i, String space, int i1) {}

    public Route(int routeId, String routeName, int totalStops, String stops) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.totalStops = totalStops;
        this.stops = stops;
    }

    // Getters and Setters
    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public int getTotalStops() { return totalStops; }
    public void setTotalStops(int totalStops) { this.totalStops = totalStops; }

    public String getStops() { return stops; }
    public void setStops(String stops) { this.stops = stops; }

    public String[] getStopsArray() {
        return stops != null ? stops.split(",") : new String[0];
    }

    @Override
    public String toString() {
        return routeName;
    }
}