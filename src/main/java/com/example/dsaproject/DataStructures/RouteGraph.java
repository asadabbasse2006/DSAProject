package com.example.dsaproject.DataStructures;

import com.example.dsaproject.Model.Route;
import java.util.*;

/**
 * Graph structure for route management using Adjacency List
 * WHY: Efficiently finds shortest paths between stops
 * COMPLEXITY: BFS O(V+E), Space O(V+E)
 */
public class RouteGraph {

    private Map<String, List<Edge>> adjacencyList;

    private static class Edge {
        String destination;
        int distance;

        Edge(String dest, int dist) {
            this.destination = dest;
            this.distance = dist;
        }
    }

    public RouteGraph() {
        this.adjacencyList = new HashMap<>();
    }

    /**
     * Add stop to graph - O(1)
     */
    public void addStop(String stopName) {
        adjacencyList.putIfAbsent(stopName, new ArrayList<>());
    }

    /**
     * Connect two stops bidirectionally - O(1)
     */
    public void addConnection(String from, String to, int distance) {
        adjacencyList.get(from).add(new Edge(to, distance));
        adjacencyList.get(to).add(new Edge(from, distance));
    }

    /**
     * Find shortest path using BFS - O(V+E)
     */
    public List<String> findShortestPath(String start, String end) {
        if (!adjacencyList.containsKey(start) || !adjacencyList.containsKey(end)) {
            return new ArrayList<>();
        }

        Queue<String> queue = new LinkedList<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new HashSet<>();

        queue.offer(start);
        visited.add(start);
        parent.put(start, null);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            if (current.equals(end)) {
                return reconstructPath(parent, end);
            }

            for (Edge edge : adjacencyList.get(current)) {
                if (!visited.contains(edge.destination)) {
                    visited.add(edge.destination);
                    parent.put(edge.destination, current);
                    queue.offer(edge.destination);
                }
            }
        }

        return new ArrayList<>();
    }

    private List<String> reconstructPath(Map<String, String> parent, String end) {
        List<String> path = new ArrayList<>();
        String current = end;

        while (current != null) {
            path.add(0, current);
            current = parent.get(current);
        }

        return path;
    }

    /**
     * Load routes from database into graph
     */
    public void buildFromRoutes(List<Route> routes) {
        for (Route route : routes) {
            String[] stops = route.getStopsArray();

            for (String stop : stops) {
                addStop(stop.trim());
            }

            // Connect consecutive stops
            for (int i = 0; i < stops.length - 1; i++) {
                addConnection(stops[i].trim(), stops[i + 1].trim(), 1000);
            }
        }
    }

    /**
     * Get all connected stops from a starting point
     */
    public List<String> getAllReachableStops(String start) {
        List<String> reachable = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            reachable.add(current);

            for (Edge edge : adjacencyList.get(current)) {
                if (!visited.contains(edge.destination)) {
                    visited.add(edge.destination);
                    queue.offer(edge.destination);
                }
            }
        }

        return reachable;
    }
}