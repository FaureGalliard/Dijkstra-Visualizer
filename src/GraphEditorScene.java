import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

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
                if (input.isEmpty()) {
                    nodesToAdd = 1;
                    grafo.AddNode(canvasPane, 50, 50, grafo.getNodos().size());
                } else {
                    nodesToAdd = Integer.parseInt(input);
                    if (nodesToAdd <= 0) {
                        showAlert("Error", "Number of nodes must be positive");
                        return;
                    }
                    for (int i = 0; i < nodesToAdd; i++) {
                        grafo.AddNode(canvasPane, 50, 50, grafo.getNodos().size());
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
                int nodeUId = Integer.parseInt(uField.getText());
                int nodeVId = Integer.parseInt(vField.getText());
                int weight = Integer.parseInt(wField.getText());
                int currentNodeCount = grafo.getNodos().size();
                if (nodeUId < 0 || nodeUId >= currentNodeCount || nodeVId < 0 || nodeVId >= currentNodeCount) {
                    showAlert("Error", "Nodes u and v must be between 0 and " + (currentNodeCount - 1));
                    return;
                }
                if (nodeUId == nodeVId) {
                    showAlert("Error", "Nodes u and v cannot be the same");
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
                Arista arista = new Arista(nodoU, nodoV, weight);
                grafo.agregarArista(arista);
                // Agregar elementos gráficos al canvas
                canvasPane.getChildren().add(arista.getLine());
                canvasPane.getChildren().add(arista.getPesoText());
                uField.clear();
                vField.clear();
                wField.clear();
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter valid numeric values for u, v, and w");
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