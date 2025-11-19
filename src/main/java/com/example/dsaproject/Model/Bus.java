package com.example.dsaproject.Model;

public class Bus {
    public static int busId;
    public static String busNo;
    public static int capacity;
    public static int driverId;
    public static int availableSeats;

    public Bus(int busId, String busNo, int capacity, int driverId) {
        Bus.busId = busId;
        Bus.busNo = busNo;
        Bus.capacity = capacity;
        Bus.driverId = driverId;
    }

    public static int getBusId() {
        return busId;
    }

    public static void setBusId(int busId) {
        Bus.busId = busId;
    }

    public static String getBusNo() {
        return busNo;
    }

    public static void setBusNo(String busNo) {
        Bus.busNo = busNo;
    }

    public static int getCapacity() {
        return capacity;
    }

    public static void setCapacity(int capacity) {
        Bus.capacity = capacity;
    }

    public static int getDriverId() {
        return driverId;
    }

    public static void setDriverId(int driverId) {
        Bus.driverId = driverId;
    }

    public static int getAvailableSeats() {
        return availableSeats;
    }

    public static void setAvailableSeats(int availableSeats) {
        Bus.availableSeats = availableSeats;
    }
}
