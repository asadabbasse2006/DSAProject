package com.example.dsaproject.Model;

/**
 * Bus Model Class
 * Represents a bus with its properties
 */
public class Bus {
    private int busId;
    private String busNo;
    private int capacity;
    private int driverId;
    private String driverName;
    private String status;
    private int currentOccupancy;

    public Bus(int busId, String busNo, int capacity, int driverId, String driverName) {
        this.busId = busId;
        this.busNo = busNo;
        this.capacity = capacity;
        this.driverId = driverId;
        this.driverName = driverName;
        this.status = "Active";
        this.currentOccupancy = 0;
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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getCurrentOccupancy() { return currentOccupancy; }
    public void setCurrentOccupancy(int occupancy) { this.currentOccupancy = occupancy; }

    public int getAvailableSeats() {
        return capacity - currentOccupancy;
    }

    public double getOccupancyPercentage() {
        return (currentOccupancy * 100.0) / capacity;
    }

    public boolean isFull() {
        return currentOccupancy >= capacity;
    }

    @Override
    public String toString() {
        return busNo + " (Capacity: " + capacity + ", Available: " + getAvailableSeats() + ")";
    }
}
