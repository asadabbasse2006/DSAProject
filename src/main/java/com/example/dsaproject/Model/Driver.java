package com.example.dsaproject.Model;

public class Driver {
    private int driverId;
    private String name;
    private String phone;
    private String licenseNo;
    private String email;
    private String experience;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public Driver() {}

    public Driver(int driverId, String name, String phone, String licenseNo) {
        this.driverId = driverId;
        this.name = name;
        this.phone = phone;
        this.licenseNo = licenseNo;
    }

    // Getters and Setters
    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLicenseNo() { return licenseNo; }
    public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }

    @Override
    public String toString() {
        return name + " (" + licenseNo + ")";
    }
}