package com.example.dsaproject.Model;

public class Driver {
    public static int id;
    public static String username;
    public static String password;
    public static String name;
    public static String phoneNo;
    public static String licenseNo;
    public static String status;

    public Driver(int id,String username,String password,String name,String phoneNo,String licenseNo,String status) {
        Driver.id = id;
        Driver.username = username;
        Driver.password = password;
        Driver.name = name;
        Driver.phoneNo = phoneNo;
        Driver.licenseNo = licenseNo;
        Driver.status = status;
    }

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        Driver.id = id;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Driver.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        Driver.password = password;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Driver.name = name;
    }

    public static String getPhoneNo() {
        return phoneNo;
    }

    public static void setPhoneNo(String phoneNo) {
        Driver.phoneNo = phoneNo;
    }

    public static String getLicenseNo() {
        return licenseNo;
    }

    public static void setLicenseNo(String licenseNo) {
        Driver.licenseNo = licenseNo;
    }

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        Driver.status = status;
    }
}
