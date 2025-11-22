package com.example.dsaproject.Model;

public class RouteOccupancy {
    private String routeName;
    private int totalSeats;
    private int bookedSeats;

    public RouteOccupancy(String routeName, int totalSeats, int bookedSeats) {
        this.routeName = routeName;
        this.totalSeats = totalSeats;
        this.bookedSeats = bookedSeats;
    }

    public String getRouteName() { return routeName; }
    public int getTotalSeats() { return totalSeats; }
    public int getBookedSeats() { return bookedSeats; }
    public double getOccupancyPercent() {
        return totalSeats > 0 ? (bookedSeats * 100.0 / totalSeats) : 0;
    }
}