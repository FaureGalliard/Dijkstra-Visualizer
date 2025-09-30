package com.fauregalliard.dijsktravisualizer.model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Grafo {
    private final List<Nodo> nodos = new ArrayList<>();
    private final List<Arista> aristas = new ArrayList<>();
    private final double nodeDiameter = 20;
    private final double spacing = 30;
    private static final String FONT_NAME = "Courier Prime";

    public void addNode(double x, double y, int nodeId) {
        Nodo node = new Nodo(nodeDiameter / 2, x, y, nodeId, generateNodeName(nodeId));
        node.setFill(Color.web("#8f8f8f"));
        nodos.add(node);
    }

    public void addEdge(Nodo nodoU, Nodo nodoV, int weight) {
        aristas.add(new Arista(nodoU, nodoV, weight));
    }

    public List<Nodo> getNodos() {
        return Collections.unmodifiableList(nodos);
    }

    public List<Arista> getAristas() {
        return Collections.unmodifiableList(aristas);
    }

    public Nodo getNodoPorId(int nodeId) {
        return nodos.stream()
                .filter(nodo -> nodo.getNodeId() == nodeId)
                .findFirst()
                .orElse(null);
    }

    public Nodo getNodoPorNombre(String name) {
        return name == null || name.isEmpty() ? null :
                nodos.stream()
                        .filter(nodo -> nodo.getName().equals(name.toUpperCase()))
                        .findFirst()
                        .orElse(null);
    }

    private String generateNodeName(int index) {
        StringBuilder name = new StringBuilder();
        do {
            name.insert(0, (char) ('A' + (index % 26)));
            index = (index / 26) - 1;
        } while (index >= 0);
        return name.toString();
    }

    public void createNodes(Pane canvasPane, int nodeCount) {
        double canvasWidth = canvasPane.getPrefWidth();
        int nodesPerRow = (int) (canvasWidth / (nodeDiameter + spacing));
        for (int i = 0; i < nodeCount; i++) {
            double x = 10 + (i % nodesPerRow) * (nodeDiameter + spacing) + nodeDiameter / 2;
            double y = 10 + (i / nodesPerRow) * (nodeDiameter + spacing) + nodeDiameter / 2;
            addNode(x, y, i);
        }
    }

    public void createEdges(Pane canvasPane, double density, int maxWeight) {
        Random random = new Random();
        aristas.clear();
        for (int i = 0; i < nodos.size() - 1; i++) {
            addEdge(nodos.get(i), nodos.get(i + 1), random.nextInt(maxWeight) + 1);
        }
        double prob = density / 100.0;
        for (int i = 0; i < nodos.size(); i++) {
            for (int j = i + 2; j < nodos.size(); j++) {
                if (random.nextDouble() < prob) {
                    addEdge(nodos.get(i), nodos.get(j), random.nextInt(maxWeight) + 1);
                }
            }
        }
    }

    public void render(Pane canvasPane) {
        canvasPane.getChildren().clear();
        canvasPane.getChildren().addAll(aristas.stream()
                .flatMap(a -> Stream.of(a.getEdgeGroup(), a.getPesoText()))
                .collect(Collectors.toList()));
        nodos.forEach(node -> {
            Text label = new Text(node.getName());
            label.setTextAlignment(TextAlignment.CENTER);
            label.setMouseTransparent(true);
            label.setFill(Color.web("#ffffff"));
            label.setFont(Font.font(FONT_NAME, 12));
            label.setX(node.getCenterX() - label.getBoundsInLocal().getWidth() / 2);
            label.setY(node.getCenterY() + label.getBoundsInLocal().getHeight() / 4);
            node.centerXProperty().addListener((obs, old, newVal) ->
                    label.setX(newVal.doubleValue() - label.getBoundsInLocal().getWidth() / 2));
            node.centerYProperty().addListener((obs, old, newVal) ->
                    label.setY(newVal.doubleValue() + label.getBoundsInLocal().getHeight() / 4));
            canvasPane.getChildren().addAll(node, label);
        });
    }

    public List<Arista> getAdjacentEdges(Nodo node) {
        return aristas.stream()
                .filter(e -> e.getNodoU() == node || e.getNodoV() == node)
                .collect(Collectors.toList());
    }

    public Arista getEdgeBetween(Nodo u, Nodo v) {
        return aristas.stream()
                .filter(e -> (e.getNodoU() == u && e.getNodoV() == v) || (e.getNodoU() == v && e.getNodoV() == u))
                .findFirst()
                .orElse(null);
    }

    public PathResult getShortestPath(Nodo source, Nodo target) {
        Map<Nodo, Integer> distances = new HashMap<>();
        Map<Nodo, Nodo> previous = new HashMap<>();
        PriorityQueue<Nodo> pq = new PriorityQueue<>((a, b) ->
                Integer.compare(distances.getOrDefault(a, Integer.MAX_VALUE), distances.getOrDefault(b, Integer.MAX_VALUE)));

        nodos.forEach(n -> distances.put(n, Integer.MAX_VALUE));
        distances.put(source, 0);
        pq.add(source);

        while (!pq.isEmpty()) {
            Nodo current = pq.poll();
            if (current == target) break;

            for (Arista e : getAdjacentEdges(current)) {
                Nodo neighbor = e.getNodoU() == current ? e.getNodoV() : e.getNodoU();
                int alt = distances.get(current) + e.getPeso();
                if (alt < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distances.put(neighbor, alt);
                    previous.put(neighbor, current);
                    pq.add(neighbor);
                }
            }
        }

        if (distances.get(target) == Integer.MAX_VALUE) return null;

        List<Nodo> path = new ArrayList<>();
        for (Nodo curr = target; curr != null; curr = previous.get(curr)) {
            path.add(curr);
        }
        Collections.reverse(path);
        return new PathResult(path, distances.get(target));
    }

    public static class PathResult {
        public final List<Nodo> path;
        public final int distance;

        public PathResult(List<Nodo> path, int distance) {
            this.path = path;
            this.distance = distance;
        }
    }
}