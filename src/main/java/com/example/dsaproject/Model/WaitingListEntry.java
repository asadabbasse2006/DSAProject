package com.example.dsaproject.Model;

import java.util.Date;

public class WaitingListEntry {
    private int bookingId;
    private int userId;
    private int routeId;
    private Date timestamp;

    // For display
    private String studentName;
    private String routeName;

    public WaitingListEntry() {
        this.timestamp = new Date();
    }

    public WaitingListEntry(int bookingId, int userId, int routeId, Date timestamp) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.routeId = routeId;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }
}