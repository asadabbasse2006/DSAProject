package com.example.dsaproject.Model;

public class User {
    private int userId;
    private String name;
    private String email;
    private String role;

    public User(int userId, String name, String email, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    public static class Route {
        private int routeId;
        private String routeName;
        private int totalStops;

        public Route(int routeId, String routeName, int totalStops) {
            this.routeId = routeId;
            this.routeName = routeName;
            this.totalStops = totalStops;
        }

        public int getRouteId() { return routeId; }
        public String getRouteName() { return routeName; }
        public int getTotalStops() { return totalStops; }

        @Override
        public String toString() {
            return routeName;
        }
    }
}