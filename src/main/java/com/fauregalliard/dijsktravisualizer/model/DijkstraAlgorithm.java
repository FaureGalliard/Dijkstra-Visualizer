package com.fauregalliard.dijsktravisualizer.model;

import com.fauregalliard.dijsktravisualizer.model.*;
import java.util.*;

public class DijkstraAlgorithm {

    public static PathResult calculateShortestPath(Graph graph, Node start, Node end) {
        final int INF = Integer.MAX_VALUE;
        List<Node> nodes = graph.nodes;

        Map<Node, Integer> dist = new HashMap<>();
        Map<Node, Boolean> visited = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();

        for (Node node : nodes) {
            dist.put(node, INF);
            visited.put(node, false);
            previous.put(node, null);
        }
        dist.put(start, 0);

        for (int i = 0; i < nodes.size(); i++) {
            Node u = getMinDistanceNode(dist, visited);
            if (u == null) break;
            visited.put(u, true);

            for (Edge edge : graph.edges) {
                if (edge.getFrom().equals(u) && !visited.get(edge.getTo())) {
                    int newDist = dist.get(u) + edge.getWeight();
                    if (newDist < dist.get(edge.getTo())) {
                        dist.put(edge.getTo(), newDist);
                        previous.put(edge.getTo(), u);
                    }
                }
            }
        }

        List<Node> path = new ArrayList<>();
        for (Node at = end; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        return new PathResult(dist.get(end), path);
    }

    private static Node getMinDistanceNode(Map<Node, Integer> dist, Map<Node, Boolean> visited) {
        Node minNode = null;
        int minDist = Integer.MAX_VALUE;
        for (Map.Entry<Node, Integer> entry : dist.entrySet()) {
            if (!visited.get(entry.getKey()) && entry.getValue() <= minDist) {
                minDist = entry.getValue();
                minNode = entry.getKey();
            }
        }
        return minNode;
    }

    public static class PathResult {
        private final int distance;
        private final List<Node> path;

        public PathResult(int distance, List<Node> path) {
            this.distance = distance;
            this.path = path;
        }

        public int getDistance() {
            return distance;
        }

        public List<Node> getPath() {
            return path;
        }
    }

}
