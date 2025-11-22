package com.example.dsaproject.Model;

public class Bus {
    private int busId;
    private String busNo;
    private int capacity;
    private int driverId;
    private String driverName;

    public Bus(int busId, String busNo, int capacity, int driverId, String driverName) {
        this.busId = busId;
        this.busNo = busNo;
        this.capacity = capacity;
        this.driverId = driverId;
        this.driverName = driverName;
    }

    public int getBusId() { return busId; }
    public String getBusNo() { return busNo; }
    public int getCapacity() { return capacity; }
    public int getDriverId() { return driverId; }
    public String getDriverName() { return driverName; }

    @Override
    public String toString() {
        return busNo + " (Capacity: " + capacity + ")";
    }
}