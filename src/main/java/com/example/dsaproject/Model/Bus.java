package com.example.dsaproject.Model;

public class Bus {
    private int busId;
    private String busNo;
    private int capacity;
    private int driverId;
    private String driverName; // For display purposes

    public Bus(int i, String text, int parseInt, int driverId, String name) {}

    public Bus(int busId, String busNo, int capacity, int driverId) {
        this.busId = busId;
        this.busNo = busNo;
        this.capacity = capacity;
        this.driverId = driverId;
    }

    // Getters and Setters
    public int getBusId() { return busId; }
    public void setBusId(int busId) { this.busId = busId; }

    public String getBusNo() { return busNo; }
    public void setBusNo(String busNo) { this.busNo = busNo; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    @Override
    public String toString() {
        return busNo + " (Capacity: " + capacity + ")";
    }
}