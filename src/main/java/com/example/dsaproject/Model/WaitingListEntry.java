package com.example.dsaproject.Model;

public class WaitingListEntry {
    private int position;
    private int bookingId;
    private String studentName;
    private String routeName;
    private int priorityLevel;
    private String requestTime;

    public WaitingListEntry(int position, int bookingId, String studentName,
                            String routeName, int priorityLevel, String requestTime) {
        this.position = position;
        this.bookingId = bookingId;
        this.studentName = studentName;
        this.routeName = routeName;
        this.priorityLevel = priorityLevel;
        this.requestTime = requestTime;
    }

    public int getPosition() { return position; }
    public int getBookingId() { return bookingId; }
    public String getStudentName() { return studentName; }
    public String getRouteName() { return routeName; }
    public int getPriorityLevel() { return priorityLevel; }
    public String getRequestTime() { return requestTime; }
}
