package com.example.dsaproject.DataStructures;

import com.example.dsaproject.Model.Route;
import java.util.*;

public class RouteBST {

    private class Node {
        Route route;
        Node left, right;

        Node(Route route) {
            this.route = route;
        }
    }

    private Node root;
    private int size;

    public RouteBST() {
        this.root = null;
        this.size = 0;
    }

    public void insert(Route route) {
        root = insertRec(root, route);
        size++;
    }

    private Node insertRec(Node node, Route route) {
        if (node == null) {
            return new Node(route);
        }

        if (route.getRouteId() < node.route.getRouteId()) {
            node.left = insertRec(node.left, route);
        } else if (route.getRouteId() > node.route.getRouteId()) {
            node.right = insertRec(node.right, route);
        }

        return node;
    }

    public Route search(int routeId) {
        return searchRec(root, routeId);
    }

    private Route searchRec(Node node, int routeId) {
        if (node == null) {
            return null;
        }

        if (routeId == node.route.getRouteId()) {
            return node.route;
        }

        return routeId < node.route.getRouteId()
                ? searchRec(node.left, routeId)
                : searchRec(node.right, routeId);
    }

    /**
     * Get all routes sorted - O(n)
     */
    public List<Route> getAllSorted() {
        List<Route> routes = new ArrayList<>();
        inOrder(root, routes);
        return routes;
    }

    private void inOrder(Node node, List<Route> routes) {
        if (node != null) {
            inOrder(node.left, routes);
            routes.add(node.route);
            inOrder(node.right, routes);
        }
    }

    /**
     * Delete route - O(log n)
     */
    public boolean delete(int routeId) {
        if (search(routeId) == null) {
            return false;
        }
        root = deleteRec(root, routeId);
        size--;
        return true;
    }

    private Node deleteRec(Node node, int routeId) {
        if (node == null) {
            return null;
        }

        if (routeId < node.route.getRouteId()) {
            node.left = deleteRec(node.left, routeId);
        } else if (routeId > node.route.getRouteId()) {
            node.right = deleteRec(node.right, routeId);
        } else {
            // Node found
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;

            // Two children: get min from right subtree
            Node min = findMin(node.right);
            node.route = min.route;
            node.right = deleteRec(node.right, min.route.getRouteId());
        }

        return node;
    }

    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return root == null;
    }
}