package com.example.dsaproject.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Booking Model Class
 * Represents a seat booking
 */
public class Booking {
    private int bookingId;
    private int userId;
    private String studentName;
    private int busId;
    private String busNo;
    private int routeId;
    private String routeName;
    private String bookingDate;
    private String status; // confirmed, waiting, cancelled
    private int priorityLevel;
    private LocalDateTime createdAt;
    private double fare;

    public Booking(int bookingId, int userId, String studentName, int busId,
                   String busNo, int routeId, String routeName, String bookingDate, String status) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.studentName = studentName;
        this.busId = busId;
        this.busNo = busNo;
        this.routeId = routeId;
        this.routeName = routeName;
        this.bookingDate = bookingDate;
        this.status = status;
        this.priorityLevel = 3; // default normal priority
    }

    // Getters and Setters
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public int getBusId() { return busId; }
    public void setBusId(int busId) { this.busId = busId; }

    public String getBusNo() { return busNo; }
    public void setBusNo(String busNo) { this.busNo = busNo; }

    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(int priorityLevel) { this.priorityLevel = priorityLevel; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public double getFare() { return fare; }
    public void setFare(double fare) { this.fare = fare; }

    public boolean isConfirmed() {
        return "confirmed".equalsIgnoreCase(status);
    }

    public boolean isWaiting() {
        return "waiting".equalsIgnoreCase(status);
    }

    public boolean isCancelled() {
        return "cancelled".equalsIgnoreCase(status);
    }

    public String getStatusBadge() {
        switch (status.toLowerCase()) {
            case "confirmed": return "✓ Confirmed";
            case "waiting": return "⏳ Waiting";
            case "cancelled": return "✗ Cancelled";
            default: return status;
        }
    }
}