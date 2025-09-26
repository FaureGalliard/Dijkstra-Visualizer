import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Random;

public class GraphEditorScene {
    private final double density;
    private final int maxWeight;
    private final Scene scene;
    private final Grafo grafo = new Grafo();
    private Pane canvasPane;

    public GraphEditorScene(Stage stage, int nodeCount, String mode, double density, int maxWeight) {
        this.density = density;
        this.maxWeight = maxWeight;

        // Main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Left panel
        VBox leftPanel = createLeftPanel();

        // Central canvas
        canvasPane = createCanvasPane(nodeCount, mode);

        // Main layout assembly
        HBox mainBox = new HBox(10, leftPanel, canvasPane);
        mainBox.setAlignment(Pos.CENTER);
        root.setCenter(mainBox);

        // Footer
        HBox footer = createFooter();
        root.setBottom(footer);

        // Scene setup
        scene = new Scene(root, 800, 450);
        stage.setTitle("Graph Editor - Dijkstra Visualizer");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(15);
        leftPanel.setPrefWidth(250);
        leftPanel.setAlignment(Pos.TOP_CENTER);

        // Manual input panel for edges
        Label uLabel = new Label("Node u:");
        TextField uField = new TextField();
        uField.setPrefWidth(50);
        HBox uBox = new HBox(5, uLabel, uField);
        uBox.setAlignment(Pos.CENTER);

        Label vLabel = new Label("Node v:");
        TextField vField = new TextField();
        vField.setPrefWidth(50);
        HBox vBox = new HBox(5, vLabel, vField);
        vBox.setAlignment(Pos.CENTER);

        Label wLabel = new Label("Weight w:");
        TextField wField = new TextField();
        wField.setPrefWidth(50);
        HBox wBox = new HBox(5, wLabel, wField);
        wBox.setAlignment(Pos.CENTER);

        Button addEdgeBtn = new Button("Add Edge");
        addEdgeBtn.setOnAction(e -> handleAddEdge(uField, vField, wField));

        // Node input panel
        Label nLabel = new Label("Number of nodes:");
        TextField nField = new TextField();
        nField.setPrefWidth(50);
        HBox nBox = new HBox(5, nLabel, nField);
        nBox.setAlignment(Pos.CENTER);

        Button addNodeBtn = new Button("Add Node(s)");
        addNodeBtn.setOnAction(e -> handleAddNodes(nField));

        VBox manualBox = new VBox(10, new Label("Add Edge"), new VBox(5, uBox, vBox, wBox, addEdgeBtn),
                new Separator(), new Label("Add Node"), new VBox(5, nBox, addNodeBtn));
        manualBox.setAlignment(Pos.CENTER);

        Button continueBtn = new Button("Continue");
        continueBtn.setPrefWidth(200);
        continueBtn.setOnAction(e -> {
            System.out.println("Continuing to next scene...");
        });

        leftPanel.getChildren().addAll(manualBox, continueBtn);
        return leftPanel;
    }

    private Pane createCanvasPane(int nodeCount, String mode) {
        Pane canvasPane = new Pane();
        canvasPane.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: black;");
        canvasPane.setPrefSize(500, 400);
        grafo.createNodes(canvasPane, nodeCount);
        if ("random".equalsIgnoreCase(mode)) {
            grafo.createAristas(canvasPane, density, maxWeight);
        }
        return canvasPane;
    }

    private HBox createFooter() {
        Label github = new Label("github.com/FaureGalliard");
        Label teacher = new Label("Teacher: Edgard Kenny Venegas Palacios");
        HBox footer = new HBox(10, teacher, new Region(), github);
        footer.setPadding(new Insets(10));
        footer.setAlignment(Pos.CENTER);
        HBox.setHgrow(footer.getChildren().get(1), Priority.ALWAYS);
        return footer;
    }

    private void handleAddNodes(TextField nField) {
        try {
            String input = nField.getText().trim();
            int nodesToAdd = input.isEmpty() ? 1 : Integer.parseInt(input);
            if (nodesToAdd <= 0) {
                showAlert("Error", "Number of nodes must be positive");
                return;
            }
            Random random = new Random();
            for (int i = 0; i < nodesToAdd; i++) {
                int nextNodeId = grafo.getNodos().size();
                double x = 50 + random.nextDouble() * (canvasPane.getPrefWidth() - 100);
                double y = 50 + random.nextDouble() * (canvasPane.getPrefHeight() - 100);
                grafo.addNode(canvasPane, x, y, nextNodeId);

                // Add edge to the previous node if it exists
                int prevNodeId = nextNodeId - 1;
                if (prevNodeId >= 0) {
                    Nodo prevNode = grafo.getNodoPorId(prevNodeId);
                    Nodo newNode = grafo.getNodoPorId(nextNodeId);
                    if (prevNode != null && newNode != null) {
                        int weight = random.nextInt(maxWeight) + 1;
                        Arista arista = new Arista(prevNode, newNode, weight);
                        grafo.agregarArista(arista);
                        canvasPane.getChildren().addAll(arista.getLine(), arista.getPesoText());
                    }
                }
            }
            nField.clear();
        } catch (NumberFormatException ex) {
            showAlert("Error", "Please enter a valid number of nodes");
        }
    }

    private void handleAddEdge(TextField uField, TextField vField, TextField wField) {
        try {
            String nodeUName = uField.getText().trim().toUpperCase();
            String nodeVName = vField.getText().trim().toUpperCase();
            int weight = Integer.parseInt(wField.getText().trim());

            if (nodeUName.isEmpty() || nodeVName.isEmpty()) {
                showAlert("Error", "Node names cannot be empty");
                return;
            }

            Integer nodeUId = grafo.getNodeIdByName(nodeUName);
            Integer nodeVId = grafo.getNodeIdByName(nodeVName);
            if (nodeUId == null || nodeVId == null) {
                showAlert("Error", "Invalid node name(s): " + nodeUName + " or " + nodeVName);
                return;
            }
            if (nodeUId.equals(nodeVId)) {
                showAlert("Error", "Cannot connect a node to itself");
                return;
            }
            if (weight <= 0 || weight > maxWeight) {
                showAlert("Error", "Weight must be between 1 and " + maxWeight);
                return;
            }

            Nodo nodoU = grafo.getNodoPorId(nodeUId);
            Nodo nodoV = grafo.getNodoPorId(nodeVId);

            if (nodoU == null || nodoV == null) {
                showAlert("Error", "One or both nodes do not exist");
                return;
            }

            // Check for duplicates
            for (Arista existing : grafo.getAristas()) {
                if ((existing.getNodoU().getNodeId() == nodeUId && existing.getNodoV().getNodeId() == nodeVId) ||
                        (existing.getNodoU().getNodeId() == nodeVId && existing.getNodoV().getNodeId() == nodeUId)) {
                    showAlert("Error", "Edge already exists between " + nodeUName + " and " + nodeVName);
                    return;
                }
            }

            // Create and add edge
            Arista arista = new Arista(nodoU, nodoV, weight);
            grafo.agregarArista(arista);
            canvasPane.getChildren().addAll(arista.getLine(), arista.getPesoText());

            System.out.println("Arista añadida: " + nodeUName + " -> " + nodeVName + " (peso=" + weight + ")");

            uField.clear();
            vField.clear();
            wField.clear();
        } catch (NumberFormatException ex) {
            showAlert("Error", "Please enter a valid numeric value for weight");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}