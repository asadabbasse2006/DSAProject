package com.example.dsaproject.Model;

public class Admin {
    public static int id;
    public static String username;
    public static String name;
    public static String licenseNo;
    public static String phoneNo;

    public Admin(int id, String username,String name,String licenseNo,String phoneNo) {
        Admin.id = id;
        Admin.username = username;
        Admin.name = name;
        Admin.licenseNo = licenseNo;
        Admin.phoneNo = phoneNo;
    }

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        Admin.id = id;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Admin.username = username;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Admin.name = name;
    }

    public static String getLicenseNo() {
        return licenseNo;
    }

    public static void setLicenseNo(String licenseNo) {
        Admin.licenseNo = licenseNo;
    }

    public static String getPhoneNo() {
        return phoneNo;
    }

    public static void setPhoneNo(String phoneNo) {
        Admin.phoneNo = phoneNo;
    }
}
