package com.example.dsaproject.Model;

public class Booking {
    public static int bookingId;
    public static int userId;
    public static int routeId;
    public static String status;

    public Booking(int bookingId, int userId, int routeId, String status) {
        Booking.bookingId = bookingId;
        Booking.userId = userId;
        Booking.routeId = routeId;
        Booking.status = status;
    }

    public static int getBookingId() {
        return bookingId;
    }

    public static void setBookingId(int bookingId) {
        Booking.bookingId = bookingId;
    }

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        Booking.userId = userId;
    }

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        Booking.status = status;
    }

    public static int getRouteId() {
        return routeId;
    }

    public static void setRouteId(int routeId) {
        Booking.routeId = routeId;
    }
}
