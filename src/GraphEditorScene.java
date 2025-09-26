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

    public GraphEditorScene(Stage stage, int nodeCount, String mode, double density, int maxWeight) {
        this.density = density;
        this.maxWeight = maxWeight;

        // Main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Left panel
        VBox leftPanel = new VBox(15);
        leftPanel.setPrefWidth(250);
        leftPanel.setAlignment(Pos.TOP_CENTER);

        // Manual input panel
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

        // Node input panel
        Label nLabel = new Label("Number of nodes:");
        TextField nField = new TextField();
        nField.setPrefWidth(50);
        HBox nBox = new HBox(5, nLabel, nField);
        nBox.setAlignment(Pos.CENTER);

        Button addNodeBtn = new Button("Add Node(s)");

        VBox manualBox = new VBox(10, new Label("Add Edge"), new VBox(5, uBox, vBox, wBox, addEdgeBtn),
                new Separator(), new Label("Add Node"), new VBox(5, nBox, addNodeBtn));
        manualBox.setAlignment(Pos.CENTER);

        Button continueBtn = new Button("Continue");
        continueBtn.setPrefWidth(200);
        continueBtn.setOnAction(e -> {
            System.out.println("Continuing to next scene...");
        });

        leftPanel.getChildren().addAll(manualBox, continueBtn);

        // Central canvas
        Pane canvasPane = new Pane();
        canvasPane.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: black;");
        canvasPane.setPrefSize(500, 400);
        grafo.CreateNodes(canvasPane, nodeCount);
        grafo.CreateAristas(canvasPane,density,maxWeight);
        grafo.DibujarGrafo(canvasPane);
        // Main layout assembly
        HBox mainBox = new HBox(10, leftPanel, canvasPane);
        mainBox.setAlignment(Pos.CENTER);
        root.setCenter(mainBox);

        // Footer
        Label github = new Label("github.com/FaureGalliard");
        Label teacher = new Label("Teacher: Edgard Kenny Venegas Palacios");
        HBox footer = new HBox(10, teacher, new Region(), github);
        footer.setPadding(new Insets(10));
        footer.setAlignment(Pos.CENTER);
        HBox.setHgrow(footer.getChildren().get(1), Priority.ALWAYS);
        root.setBottom(footer);

        // Scene setup
        scene = new Scene(root, 800, 450);
        stage.setTitle("Graph Editor - Dijkstra Visualizer");
        stage.setScene(scene);
        stage.show();

        // Event handler for addNodeBtn
        addNodeBtn.setOnAction(e -> {
            try {
                String input = nField.getText().trim();
                int nodesToAdd;
                Random random = new Random(); // For generating random weights
                if (input.isEmpty()) {
                    nodesToAdd = 1;
                    int lastNodeId = grafo.getNodos().size() - 1;
                    grafo.AddNode(canvasPane, 50, 50, grafo.getNodos().size());
                    // Add edge to the last node if it exists
                    if (lastNodeId >= 0) {
                        Nodo lastNode = grafo.getNodoPorId(lastNodeId);
                        Nodo newNode = grafo.getNodoPorId(lastNodeId + 1);
                        if (lastNode != null && newNode != null) {
                            int weight = random.nextInt(maxWeight) + 1;
                            Arista arista = new Arista(lastNode, newNode, weight);
                            grafo.agregarArista(arista);

                        }
                    }
                } else {
                    nodesToAdd = Integer.parseInt(input);
                    if (nodesToAdd <= 0) {
                        showAlert("Error", "Number of nodes must be positive");
                        return;
                    }
                    for (int i = 0; i < nodesToAdd; i++) {
                        int lastNodeId = grafo.getNodos().size() - 1;
                        grafo.AddNode(canvasPane, 50, 50, grafo.getNodos().size());
                        // Add edge to the last node if it exists
                        if (lastNodeId >= 0) {
                            Nodo lastNode = grafo.getNodoPorId(lastNodeId);
                            Nodo newNode = grafo.getNodoPorId(lastNodeId + 1);
                            if (lastNode != null && newNode != null) {
                                int weight = random.nextInt(maxWeight) + 1;
                                Arista arista = new Arista(lastNode, newNode, weight);
                                grafo.agregarArista(arista);

                            }
                        }
                    }
                }
                grafo.DibujarGrafo(canvasPane); // Redraw the entire graph
                nField.clear();
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid number of nodes");
            }
        });

        // Event handler for addEdgeBtn
        addEdgeBtn.setOnAction(e -> {
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

                // Revisar duplicados
                for (Arista existing : grafo.getAristas()) {
                    if ((existing.getNodoU().getNodeId() == nodeUId && existing.getNodoV().getNodeId() == nodeVId) ||
                            (existing.getNodoU().getNodeId() == nodeVId && existing.getNodoV().getNodeId() == nodeUId)) {
                        showAlert("Error", "Edge already exists between " + nodeUName + " and " + nodeVName);
                        return;
                    }
                }

                // Crear y dibujar arista
                Arista arista = new Arista(nodoU, nodoV, weight);
                grafo.agregarArista(arista);
                canvasPane.getChildren().addAll(arista.getLine(), arista.getPesoText()); // 🔹 DIBUJAR DIRECTO

                System.out.println("Arista añadida: " + nodeUName + " -> " + nodeVName + " (peso=" + weight + ")");

                uField.clear();
                vField.clear();
                wField.clear();
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid numeric value for weight");
            }
        });
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