package com.example.dsaproject.Model;

public class Driver {
    private int driverId;
    private String name;
    private String phone;
    private String licenseNo;

    public Driver(int driverId, String name, String phone, String licenseNo) {
        this.driverId = driverId;
        this.name = name;
        this.phone = phone;
        this.licenseNo = licenseNo;
    }


    public int getDriverId() { return driverId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getLicenseNo() { return licenseNo; }

    @Override
    public String toString() {
        return name;
    }
}
