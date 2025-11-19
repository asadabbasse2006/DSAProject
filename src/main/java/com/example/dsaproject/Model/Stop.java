package com.example.dsaproject.Model;

public class Stop {
    public static int stopId;
    public static int routeId;
    public static String routeName;
    public static int orderNo;

    public Stop(int stopId, int routeId, String routeName, int orderNo) {
        Stop.orderNo = orderNo;
        Stop.routeId = routeId;
        Stop.stopId = stopId;
        Stop.routeName = routeName;
    }
}
