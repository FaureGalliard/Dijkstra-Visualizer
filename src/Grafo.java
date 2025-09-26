import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.shape.Line;

class Grafo {
    private final List<Nodo> nodos = new ArrayList<>();
    private final List<Arista> aristas = new ArrayList<>();
    private final double nodeDiameter = 20;
    private final double spacing = 30;
    private final double nodeSizeWithSpacing = nodeDiameter + spacing;

    public void agregarNodo(Nodo nodo) {
        nodos.add(nodo);
    }

    public void agregarArista(Arista arista) {
        aristas.add(arista);
    }

    public List<Nodo> getNodos() {
        return nodos;
    }

    public List<Arista> getAristas() {
        return aristas;
    }

    public Nodo getNodoPorId(int nodeId) {
        for (Nodo nodo : nodos) {
            if (nodo.getNodeId() == nodeId) {
                return nodo;
            }
        }
        return null;
    }

    public String getNodeName(int index) {
        StringBuilder name = new StringBuilder();
        while (index >= 0) {
            name.insert(0, (char) ('A' + (index % 26)));
            index = (index / 26) - 1;
            if (index < 0) break;
        }
        return name.toString();
    }

    public Integer getNodeIdByName(String name) {
        if (name == null || name.isEmpty()) return null;
        name = name.toUpperCase();
        int index = 0;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c < 'A' || c > 'Z') return null;
            index = index * 26 + (c - 'A' + 1);
        }
        index -= 1;
        if (index < 0 || index >= nodos.size()) return null;
        return index;
    }

    public void addNode(Pane canvasPane, double x, double y, int nodeId) {
        Nodo node = new Nodo(nodeDiameter / 2, x, y);
        node.setNodeId(nodeId);
        agregarNodo(node);
        Text label = new Text(getNodeName(nodeId));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setMouseTransparent(true);
        label.setX(x - label.getBoundsInLocal().getWidth() / 2);
        label.setY(y + label.getBoundsInLocal().getHeight() / 4);
        node.centerXProperty().addListener((obs, old, newVal) ->
                label.setX(newVal.doubleValue() - label.getBoundsInLocal().getWidth() / 2));
        node.centerYProperty().addListener((obs, old, newVal) ->
                label.setY(newVal.doubleValue() + label.getBoundsInLocal().getHeight() / 4));
        canvasPane.getChildren().addAll(node, label);
    }

    public void createNodes(Pane canvasPane, int nodeCount) {
        double canvasWidth = canvasPane.getPrefWidth();
        int nodesPerRow = (int) (canvasWidth / nodeSizeWithSpacing);
        double startX = 10;
        double startY = 10;
        for (int i = 0; i < nodeCount; i++) {
            int row = i / nodesPerRow;
            int col = i % nodesPerRow;
            double x = startX + col * nodeSizeWithSpacing + nodeDiameter / 2;
            double y = startY + row * nodeSizeWithSpacing + nodeDiameter / 2;
            addNode(canvasPane, x, y, i);
        }
    }

    public void createAristas(Pane canvasPane, double density, int maxWeight) {
        Random random = new Random();
        aristas.clear();
        // Remove old edges from canvas
        canvasPane.getChildren().removeIf(node -> node instanceof Line || (node instanceof Text && !node.isMouseTransparent()));

        double prob = density; // Assuming density is already in [0,1]; adjust if percentage

        // Step 1: Connect nodes in a chain
        for (int i = 0; i < nodos.size() - 1; i++) {
            Nodo nodoU = nodos.get(i);
            Nodo nodoV = nodos.get(i + 1);
            int weight = random.nextInt(maxWeight) + 1;
            Arista arista = new Arista(nodoU, nodoV, weight);
            agregarArista(arista);
            canvasPane.getChildren().addAll(arista.getLine(), arista.getPesoText());
        }

        // Step 2: Add additional edges based on density
        for (int i = 0; i < nodos.size(); i++) {
            for (int j = i + 1; j < nodos.size(); j++) {
                if (j == i + 1) continue; // Skip chain edges to avoid duplicates

                if (random.nextDouble() < prob) {
                    Nodo nodoU = nodos.get(i);
                    Nodo nodoV = nodos.get(j);
                    int weight = random.nextInt(maxWeight) + 1;
                    Arista arista = new Arista(nodoU, nodoV, weight);
                    agregarArista(arista);
                    canvasPane.getChildren().addAll(arista.getLine(), arista.getPesoText());
                }
            }
        }
    }

    public void resetGraphLayout(Pane canvasPane) {
        // Clear the canvas
        canvasPane.getChildren().clear();
        // Redraw edges first
        for (Arista arista : aristas) {
            canvasPane.getChildren().addAll(arista.getLine(), arista.getPesoText());
        }
        // Redraw nodes and labels on top, repositioning to grid
        double canvasWidth = canvasPane.getPrefWidth();
        int nodesPerRow = (int) (canvasWidth / nodeSizeWithSpacing);
        double startX = 10;
        double startY = 10;
        for (int i = 0; i < nodos.size(); i++) {
            int row = i / nodesPerRow;
            int col = i % nodesPerRow;
            double x = startX + col * nodeSizeWithSpacing + nodeDiameter / 2;
            double y = startY + row * nodeSizeWithSpacing + nodeDiameter / 2;
            Nodo node = nodos.get(i);
            node.setCenterX(x);
            node.setCenterY(y);
            Text label = new Text(getNodeName(i));
            label.setTextAlignment(TextAlignment.CENTER);
            label.setMouseTransparent(true);
            label.setX(x - label.getBoundsInLocal().getWidth() / 2);
            label.setY(y + label.getBoundsInLocal().getHeight() / 4);
            node.centerXProperty().addListener((obs, old, newVal) ->
                    label.setX(newVal.doubleValue() - label.getBoundsInLocal().getWidth() / 2));
            node.centerYProperty().addListener((obs, old, newVal) ->
                    label.setY(newVal.doubleValue() + label.getBoundsInLocal().getHeight() / 4));
            canvasPane.getChildren().addAll(node, label);
        }
    }
}