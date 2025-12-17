package com.example.dsaproject.Model;

public class User {
    private int userId;
    private String name;
    private String email;
    private String password;
    private String role; // "student" or "admin"
    private String registrationId;

    // Constructors
    public User() {}

    public User(int userId, String name, String email, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // Full constructor
    public User(int userId, String name, String email, String password,
                String role, String registrationId) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.registrationId = registrationId;
    }

    public User(int i, String name, String email, String student, String registrationId) {

    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getRegistrationId() { return registrationId; }
    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }

    public boolean isStudent() {
        return "student".equalsIgnoreCase(role);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}