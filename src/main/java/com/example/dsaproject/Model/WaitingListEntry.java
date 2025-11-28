package com.example.dsaproject.Model;

/**
 * Waiting List Entry Model
 * Represents a student in waiting list (Priority Queue implementation)
 */
public class WaitingListEntry implements Comparable<WaitingListEntry> {
    private int position;
    private int bookingId;
    private String studentName;
    private String routeName;
    private int priorityLevel; // 1=High, 2=Medium, 3=Normal
    private String requestTime;
    private long timestamp;

    public WaitingListEntry(int position, int bookingId, String studentName,
                            String routeName, int priorityLevel, String requestTime) {
        this.position = position;
        this.bookingId = bookingId;
        this.studentName = studentName;
        this.routeName = routeName;
        this.priorityLevel = priorityLevel;
        this.requestTime = requestTime;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public int getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(int priorityLevel) { this.priorityLevel = priorityLevel; }

    public String getRequestTime() { return requestTime; }
    public void setRequestTime(String requestTime) { this.requestTime = requestTime; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getPriorityLabel() {
        switch (priorityLevel) {
            case 1: return "High";
            case 2: return "Medium";
            case 3: return "Normal";
            default: return "Unknown";
        }
    }

    @Override
    public int compareTo(WaitingListEntry other) {
        // Compare by priority level first (lower number = higher priority)
        if (this.priorityLevel != other.priorityLevel) {
            return Integer.compare(this.priorityLevel, other.priorityLevel);
        }
        // If same priority, compare by timestamp (earlier = higher priority)
        return Long.compare(this.timestamp, other.timestamp);
    }
}