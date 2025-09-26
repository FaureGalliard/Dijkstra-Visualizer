import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

class Grafo {
    private final List<Nodo> nodos;
    private final List<Arista> aristas;
    private final double nodeDiameter = 20;
    private final double spacing = 30;

    public Grafo() {
        nodos = new ArrayList<>();
        aristas = new ArrayList<>();
    }

    public void addNode(double x, double y, int nodeId) {
        String nodeName = generateNodeName(nodeId);
        Nodo node = new Nodo(nodeDiameter / 2, x, y, nodeId, nodeName);
        nodos.add(node);
    }

    public void addEdge(Nodo nodoU, Nodo nodoV, int weight) {
        Arista arista = new Arista(nodoU, nodoV, weight);
        aristas.add(arista);
    }

    public List<Nodo> getNodos() {
        return nodos;
    }

    public List<Arista> getAristas() {
        return aristas;
    }

    public Nodo getNodoPorId(int nodeId) {
        return nodos.stream()
                .filter(nodo -> nodo.getNodeId() == nodeId)
                .findFirst()
                .orElse(null);
    }

    public Nodo getNodoPorNombre(String name) {
        if (name == null || name.isEmpty()) return null;
        String upperName = name.toUpperCase(); // Use a new variable for uppercase
        return nodos.stream()
                .filter(nodo -> nodo.getName().equals(upperName))
                .findFirst()
                .orElse(null);
    }

    private String generateNodeName(int index) {
        StringBuilder name = new StringBuilder();
        while (index >= 0) {
            name.insert(0, (char) ('A' + (index % 26)));
            index = (index / 26) - 1;
            if (index < 0) break;
        }
        return name.toString();
    }

    public void createNodes(Pane canvasPane, int nodeCount) {
        double canvasWidth = canvasPane.getPrefWidth();
        int nodesPerRow = (int) (canvasWidth / (nodeDiameter + spacing));
        double startX = 10;
        double startY = 10;
        for (int i = 0; i < nodeCount; i++) {
            int row = i / nodesPerRow;
            int col = i % nodesPerRow;
            double x = startX + col * (nodeDiameter + spacing) + nodeDiameter / 2;
            double y = startY + row * (nodeDiameter + spacing) + nodeDiameter / 2;
            addNode(x, y, i);
        }
    }

    public void createEdges(Pane canvasPane, double density, int maxWeight) {
        Random random = new Random();
        aristas.clear();
        // Connect nodes in a chain
        for (int i = 0; i < nodos.size() - 1; i++) {
            addEdge(nodos.get(i), nodos.get(i + 1), random.nextInt(maxWeight) + 1);
        }
        // Add random edges based on density
        double prob = density / 100.0;
        for (int i = 0; i < nodos.size(); i++) {
            for (int j = i + 2; j < nodos.size(); j++) { // Skip adjacent nodes
                if (random.nextDouble() < prob) {
                    addEdge(nodos.get(i), nodos.get(j), random.nextInt(maxWeight) + 1);
                }
            }
        }
    }

    public void render(Pane canvasPane) {
        canvasPane.getChildren().clear();
        // Draw edges first
        for (Arista arista : aristas) {
            canvasPane.getChildren().addAll(arista.getEdgeGroup(), arista.getPesoText());
        }
        // Draw nodes and labels on top
        for (Nodo node : nodos) {
            Text label = new Text(node.getName());
            label.setTextAlignment(TextAlignment.CENTER);
            label.setMouseTransparent(true);
            label.setX(node.getCenterX() - label.getBoundsInLocal().getWidth() / 2);
            label.setY(node.getCenterY() + label.getBoundsInLocal().getHeight() / 4);
            node.centerXProperty().addListener((obs, old, newVal) ->
                    label.setX(newVal.doubleValue() - label.getBoundsInLocal().getWidth() / 2));
            node.centerYProperty().addListener((obs, old, newVal) ->
                    label.setY(newVal.doubleValue() + label.getBoundsInLocal().getHeight() / 4));
            canvasPane.getChildren().addAll(node, label);
        }
    }

    public List<Arista> getAdjacentEdges(Nodo node) {
        List<Arista> adj = new ArrayList<>();
        for (Arista e : aristas) {
            if (e.getNodoU() == node || e.getNodoV() == node) {
                adj.add(e);
            }
        }
        return adj;
    }

    public Arista getEdgeBetween(Nodo u, Nodo v) {
        for (Arista e : aristas) {
            if ((e.getNodoU() == u && e.getNodoV() == v) || (e.getNodoU() == v && e.getNodoV() == u)) {
                return e;
            }
        }
        return null;
    }

    public PathResult getShortestPath(Nodo source, Nodo target) {
        Map<Nodo, Integer> distances = new HashMap<>();
        Map<Nodo, Nodo> previous = new HashMap<>();
        PriorityQueue<Nodo> pq = new PriorityQueue<>((a, b) -> Integer.compare(
                distances.getOrDefault(a, Integer.MAX_VALUE),
                distances.getOrDefault(b, Integer.MAX_VALUE)
        ));

        for (Nodo n : nodos) {
            distances.put(n, Integer.MAX_VALUE);
        }
        distances.put(source, 0);
        pq.add(source);

        while (!pq.isEmpty()) {
            Nodo current = pq.poll();
            if (current == target) break;

            for (Arista e : getAdjacentEdges(current)) {
                Nodo neighbor = (e.getNodoU() == current) ? e.getNodoV() : e.getNodoU();
                int alt = distances.get(current) + e.getPeso();
                if (alt < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distances.put(neighbor, alt);
                    previous.put(neighbor, current);
                    pq.add(neighbor);
                }
            }
        }

        if (distances.get(target) == Integer.MAX_VALUE) {
            return null;
        }

        // Build path
        List<Nodo> path = new ArrayList<>();
        Nodo curr = target;
        while (curr != null) {
            path.add(curr);
            curr = previous.get(curr);
        }
        Collections.reverse(path);

        return new PathResult(path, distances.get(target));
    }

    public static class PathResult {
        public List<Nodo> path;
        public int distance;

        public PathResult(List<Nodo> path, int distance) {
            this.path = path;
            this.distance = distance;
        }
    }
}