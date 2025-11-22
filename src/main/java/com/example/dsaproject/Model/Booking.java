package com.example.dsaproject.Model;

public class Booking {
    private int bookingId;
    private int userId;
    private String studentName;
    private int busId;
    private String busNo;
    private int routeId;
    private String routeName;
    private String bookingDate;
    private String status;

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
    }

    public int getBookingId() { return bookingId; }
    public int getUserId() { return userId; }
    public String getStudentName() { return studentName; }
    public int getBusId() { return busId; }
    public String getBusNo() { return busNo; }
    public int getRouteId() { return routeId; }
    public String getRouteName() { return routeName; }
    public String getBookingDate() { return bookingDate; }
    public String getStatus() { return status; }
}