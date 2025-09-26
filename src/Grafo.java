import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

class Grafo {
    private List<Nodo> nodos;
    private List<Arista> aristas;
    double nodeDiameter = 20;
    double spacing = 30;
    double nodeSizeWithSpacing = nodeDiameter + spacing;

    public Grafo() {
        nodos = new ArrayList<>();
        aristas = new ArrayList<>();
    }

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
        for (int i = name.length() - 1, power = 0; i >= 0; i--, power++) {
            char c = name.charAt(i);
            if (c < 'A' || c > 'Z') return null;
            int value = c - 'A';
            index += value * Math.pow(26, power);
            if (i > 0) index += Math.pow(26, power);
        }
        if (index >= nodos.size()) return null;
        return index;
    }

    public void AddNode(Pane canvasPane, double x, double y, int nodeId) {
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

    public void CreateNodes(Pane canvasPane, int nodeCount) {
        double canvasWidth = canvasPane.getPrefWidth();
        int nodesPerRow = (int) (canvasWidth / nodeSizeWithSpacing);
        double startX = 10;
        double startY = 10;
        for (int i = 0; i < nodeCount; i++) {
            int row = i / nodesPerRow;
            int col = i % nodesPerRow;
            double x = startX + col * nodeSizeWithSpacing + nodeDiameter / 2;
            double y = startY + row * nodeSizeWithSpacing + nodeDiameter / 2;
            AddNode(canvasPane, x, y, i);
        }
    }

    public void CreateAristas(Pane canvasPane, double density, int maxWeight) {
        Random random = new Random();
        aristas.clear();
        canvasPane.getChildren().removeIf(node -> !(node instanceof Nodo || node instanceof Text));

        double prob = density / 100.0;

        // Conectar nodos en una cadena
        for (int i = 0; i < nodos.size() - 1; i++) {
            Nodo nodoU = nodos.get(i);
            Nodo nodoV = nodos.get(i + 1);
            int weight = random.nextInt(maxWeight) + 1;
            Arista arista = new Arista(nodoU, nodoV, weight);
            agregarArista(arista);
        }

        // Agregar aristas adicionales según densidad
        for (int i = 0; i < nodos.size(); i++) {
            for (int j = i + 1; j < nodos.size(); j++) {
                if (j == i + 1) continue;
                if (random.nextDouble() < prob) {
                    Nodo nodoU = nodos.get(i);
                    Nodo nodoV = nodos.get(j);
                    int weight = random.nextInt(maxWeight) + 1;
                    Arista arista = new Arista(nodoU, nodoV, weight);
                    agregarArista(arista);
                }
            }
        }
        // Redraw to ensure proper rendering
        DibujarGrafo(canvasPane);
    }

    public void DibujarGrafo(Pane canvasPane) {
        canvasPane.getChildren().clear();
        // Redraw edges (lines and weight texts) first
        for (Arista arista : aristas) {
            canvasPane.getChildren().addAll(arista.getLine(), arista.getPesoText());
        }
        // Redraw nodes and labels on top
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