import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.*;

public class SourceTargetScene {
    private final Grafo grafo;
    private final Scene scene;
    private final Stage stage;
    private Nodo sourceNode;
    private Nodo targetNode;
    private Label resultLabel;
    private final Pane canvasPane;

    public SourceTargetScene(Stage stage, Grafo grafo) {
        this.stage = stage;
        this.grafo = grafo;

        // Main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Canvas for rendering the graph
        canvasPane = new Pane();
        canvasPane.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: black;");
        canvasPane.setPrefSize(600, 500);

        // Render the graph
        grafo.render(canvasPane);

        // Enable node selection
        enableNodeSelection();

        // Left panel for input and results
        VBox leftPanel = createLeftPanel();

        // Back button
        Button backBtn = new Button("Back to Editor");
        backBtn.setPrefWidth(200);
        backBtn.setOnAction(e -> {
            GraphEditorScene editor = new GraphEditorScene(stage, grafo); // Pass the existing graph
            stage.setScene(editor.getScene());
        });

        // Layout assembly
        HBox mainBox = new HBox(10, leftPanel, canvasPane);
        mainBox.setAlignment(Pos.CENTER);
        root.setCenter(mainBox);
        root.setBottom(backBtn);
        BorderPane.setAlignment(backBtn, Pos.CENTER);
        BorderPane.setMargin(backBtn, new Insets(10));

        // Scene setup
        this.scene = new Scene(root, 800, 600);
        stage.setTitle("Source-Target Selector - Dijkstra Visualizer");
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(15);
        leftPanel.setPrefWidth(200);
        leftPanel.setAlignment(Pos.TOP_CENTER);

        // Source and Target input
        TextField sourceField = createTextField(100);
        TextField targetField = createTextField(100);
        Button runDijkstraBtn = new Button("Run Dijkstra");
        runDijkstraBtn.setPrefWidth(150);
        runDijkstraBtn.setOnAction(e -> runDijkstra(sourceField, targetField));

        // Result label
        resultLabel = new Label("Select source and target nodes.");
        resultLabel.setWrapText(true);

        leftPanel.getChildren().addAll(
                new Label("Source Node:"),
                sourceField,
                new Label("Target Node:"),
                targetField,
                runDijkstraBtn,
                new Label("Result:"),
                resultLabel
        );
        return leftPanel;
    }

    private TextField createTextField(double width) {
        TextField field = new TextField();
        field.setPrefWidth(width);
        return field;
    }

    private void enableNodeSelection() {
        for (Nodo node : grafo.getNodos()) {
            node.setOnMouseClicked(e -> {
                if (sourceNode == null) {
                    sourceNode = node;
                    node.setFill(Color.GREEN);
                } else if (targetNode == null && node != sourceNode) {
                    targetNode = node;
                    node.setFill(Color.RED);
                } else {
                    // Reset selection
                    resetNodeColors();
                    sourceNode = node;
                    node.setFill(Color.GREEN);
                    targetNode = null;
                }
            });
        }
    }

    private void resetNodeColors() {
        for (Nodo node : grafo.getNodos()) {
            node.setFill(Color.CORNFLOWERBLUE);
        }
        for (Arista arista : grafo.getAristas()) {
            arista.getLine().setStroke(Color.BLACK);
        }
    }

    private void runDijkstra(TextField sourceField, TextField targetField) {
        String sourceName = sourceField.getText().trim().toUpperCase();
        String targetName = targetField.getText().trim().toUpperCase();

        // If nodes were selected by clicking, use those; otherwise, use text input
        Nodo start = sourceNode != null ? sourceNode : grafo.getNodoPorNombre(sourceName);
        Nodo end = targetNode != null ? targetNode : grafo.getNodoPorNombre(targetName);

        if (start == null || end == null) {
            resultLabel.setText("Error: Invalid source or target node.");
            return;
        }

        // Run Dijkstra's algorithm
        Map<Nodo, Integer> distances = new HashMap<>();
        Map<Nodo, Nodo> previous = new HashMap<>();
        PriorityQueue<Nodo> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        Set<Nodo> visited = new HashSet<>();

        // Initialize distances
        for (Nodo node : grafo.getNodos()) {
            distances.put(node, Integer.MAX_VALUE);
            previous.put(node, null);
        }
        distances.put(start, 0);
        queue.offer(start);

        while (!queue.isEmpty()) {
            Nodo current = queue.poll();
            if (visited.contains(current)) continue;
            visited.add(current);

            // Find neighbors
            for (Arista arista : grafo.getAristas()) {
                Nodo neighbor = null;
                if (arista.getNodoU() == current) {
                    neighbor = arista.getNodoV();
                } else if (arista.getNodoV() == current) {
                    neighbor = arista.getNodoU();
                }
                if (neighbor != null && !visited.contains(neighbor)) {
                    int newDist = distances.get(current) + arista.getPeso();
                    if (newDist < distances.get(neighbor)) {
                        distances.put(neighbor, newDist);
                        previous.put(neighbor, current);
                        queue.offer(neighbor);
                    }
                }
            }
        }

        // Reconstruct path
        List<Nodo> path = new ArrayList<>();
        Nodo current = end;
        while (current != null) {
            path.add(current);
            current = previous.get(current);
        }
        Collections.reverse(path);

        // Display result
        if (distances.get(end) == Integer.MAX_VALUE) {
            resultLabel.setText("No path exists between " + start.getName() + " and " + end.getName());
        } else {
            StringBuilder pathStr = new StringBuilder();
            for (int i = 0; i < path.size(); i++) {
                pathStr.append(path.get(i).getName());
                if (i < path.size() - 1) pathStr.append(" -> ");
            }
            resultLabel.setText("Shortest path: " + pathStr + "\nTotal cost: " + distances.get(end));

            // Highlight path
            resetNodeColors();
            for (Nodo node : path) {
                node.setFill(Color.YELLOW);
            }
            for (int i = 0; i < path.size() - 1; i++) {
                Nodo u = path.get(i);
                Nodo v = path.get(i + 1);
                for (Arista arista : grafo.getAristas()) {
                    if ((arista.getNodoU() == u && arista.getNodoV() == v) ||
                            (arista.getNodoU() == v && arista.getNodoV() == u)) {
                        arista.getLine().setStroke(Color.RED);
                    }
                }
            }
        }
    }

    public Scene getScene() {
        return scene;
    }
}