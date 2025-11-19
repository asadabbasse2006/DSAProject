package com.example.dsaproject.Model;

public class User {
    public static int id;
    public static String email;
    public static String password;
    public static String name;
    public static String role;
    public static String status;

    public User(int id, String email,String password,String name,String role, String status) {
        User.email = email;
        User.name = name;
        User.id = id;
        User.password = password;
        User.role = role;
        User.status = status;
    }

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        User.id = id;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        User.email = email;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        User.password = password;
    }

    public static String getRole() {
        return role;
    }

    public static void setRole(String role) {
        User.role = role;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        User.name = name;
    }

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        User.status = status;
    }
}
