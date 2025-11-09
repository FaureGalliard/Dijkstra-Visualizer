package com.fauregalliard.dijsktravisualizer.model;

import java.util.*;

public class DijkstraAlgorithm {

    public static class StepInfo {
        public enum StepType {
            INITIALIZE, VISIT_NODE, CHECK_NEIGHBOR, UPDATE_DISTANCE, COMPLETE
        }

        private final StepType type;
        private final Node currentNode;
        private final Node neighborNode;
        private final int distance;
        private final Edge edge;
        private final String description;

        public StepInfo(StepType type, Node currentNode, Node neighborNode,
                        int distance, Edge edge, String description) {
            this.type = type;
            this.currentNode = currentNode;
            this.neighborNode = neighborNode;
            this.distance = distance;
            this.edge = edge;
            this.description = description;
        }

        public StepType getType() { return type; }
        public Node getCurrentNode() { return currentNode; }
        public Node getNeighborNode() { return neighborNode; }
        public int getDistance() { return distance; }
        public Edge getEdge() { return edge; }
        public String getDescription() { return description; }
    }

    public static class AnimatedResult {
        private final List<StepInfo> steps;
        private final PathResult finalResult;

        public AnimatedResult(List<StepInfo> steps, PathResult finalResult) {
            this.steps = steps;
            this.finalResult = finalResult;
        }

        public List<StepInfo> getSteps() { return steps; }
        public PathResult getFinalResult() { return finalResult; }
    }

    public static AnimatedResult calculateShortestPathAnimated(Graph graph, Node start, Node end) {
        final int INF = Integer.MAX_VALUE;
        List<Node> nodes = graph.nodes;
        List<StepInfo> steps = new ArrayList<>();

        Map<Node, Integer> dist = new HashMap<>();
        Map<Node, Boolean> visited = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();

        // Inicialización
        for (Node node : nodes) {
            dist.put(node, INF);
            visited.put(node, false);
            previous.put(node, null);
        }
        dist.put(start, 0);

        steps.add(new StepInfo(
                StepInfo.StepType.INITIALIZE,
                start, null, 0, null,
                "Inicializando: Nodo origen con distancia 0"
        ));

        // Algoritmo principal
        for (int i = 0; i < nodes.size(); i++) {
            Node u = getMinDistanceNode(dist, visited);
            if (u == null) break;

            visited.put(u, true);
            steps.add(new StepInfo(
                    StepInfo.StepType.VISIT_NODE,
                    u, null, dist.get(u), null,
                    "Visitando nodo " + u.getId() + " (distancia: " + dist.get(u) + ")"
            ));

            // Explorar vecinos
            for (Edge edge : graph.edges) {
                if (edge.getFrom().equals(u) && !visited.get(edge.getTo())) {
                    Node v = edge.getTo();

                    steps.add(new StepInfo(
                            StepInfo.StepType.CHECK_NEIGHBOR,
                            u, v, dist.get(u), edge,
                            "Evaluando vecino " + v.getId() + " desde " + u.getId()
                    ));

                    int newDist = dist.get(u) + edge.getWeight();
                    if (newDist < dist.get(v)) {
                        dist.put(v, newDist);
                        previous.put(v, u);

                        steps.add(new StepInfo(
                                StepInfo.StepType.UPDATE_DISTANCE,
                                u, v, newDist, edge,
                                "Actualizando distancia de " + v.getId() + " a " + newDist
                        ));
                    }
                }
            }
        }

        // Construir el camino final
        List<Node> path = new ArrayList<>();
        for (Node at = end; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        PathResult finalResult = new PathResult(dist.get(end), path);

        steps.add(new StepInfo(
                StepInfo.StepType.COMPLETE,
                end, null, dist.get(end), null,
                "Algoritmo completado. Distancia final: " + dist.get(end)
        ));

        return new AnimatedResult(steps, finalResult);
    }

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