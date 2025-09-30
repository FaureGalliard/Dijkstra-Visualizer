package com.fauregalliard.dijsktravisualizer.ui;

import com.fauregalliard.dijsktravisualizer.model.Grafo;
import com.fauregalliard.dijsktravisualizer.model.Nodo;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Random;

public class GraphEditorScene {
    private final Grafo grafo;
    private final Scene scene;
    private double density;
    private int maxWeight;
    private final Pane canvasPane;
    private final Stage stage;

    public GraphEditorScene(Stage stage, int nodeCount, String mode, double density, int maxWeight) {
        this(stage, new Grafo());
        this.density = density;
        this.maxWeight = maxWeight;
        grafo.createNodes(canvasPane, nodeCount);
        grafo.createEdges(canvasPane, density, maxWeight);
        grafo.render(canvasPane);
    }

    public GraphEditorScene(Stage stage, Grafo grafo) {
        this.stage = stage;
        this.grafo = grafo;
        this.density = 50.0;
        this.maxWeight = 10;
        this.canvasPane = new Pane();
        canvasPane.getStyleClass().add("canvas-pane");
        canvasPane.setPrefSize(500, 400);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setPadding(new Insets(20));

        VBox leftPanel = createLeftPanel();
        HBox mainBox = new HBox(10, leftPanel, canvasPane);
        mainBox.getStyleClass().add("hbox");
        mainBox.setAlignment(Pos.CENTER);
        root.setCenter(mainBox);
        root.setBottom(UIUtils.createFooter());

        grafo.render(canvasPane);

        this.scene = new Scene(root, 800, 450);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        stage.setTitle("Graph Editor - Dijkstra Visualizer");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(15);
        leftPanel.getStyleClass().add("vbox");
        leftPanel.setPrefWidth(250);
        leftPanel.setAlignment(Pos.TOP_CENTER);

        TextField[] fields = {
                createTextField("Node u", 50),
                createTextField("Node v", 50),
                createTextField("Weight", 50)
        };
        Button addEdgeBtn = new Button("Add Edge");
        addEdgeBtn.getStyleClass().add("button");
        addEdgeBtn.setPrefWidth(100);
        addEdgeBtn.setOnAction(e -> handleAddEdge(fields[0], fields[1], fields[2]));

        Label edgeLabel = new Label("Add Edge");
        edgeLabel.getStyleClass().add("subtitle-label");

        VBox edgeBox = new VBox(5);
        edgeBox.getStyleClass().add("vbox");
        edgeBox.setAlignment(Pos.CENTER);
        String[] labels = {"Node u:", "Node v:", "Weight w:"};
        for (int i = 0; i < 3; i++) {
            Label lbl = new Label(labels[i]);
            lbl.getStyleClass().add("normal-label");
            edgeBox.getChildren().add(new HBox(5, lbl, fields[i]));
        }
        edgeBox.getChildren().add(addEdgeBtn);

        TextField nField = createTextField("Number of nodes", 50);
        Button addNodeBtn = new Button("Add Node(s)");
        addNodeBtn.getStyleClass().add("button");
        addNodeBtn.setPrefWidth(100);
        addNodeBtn.setOnAction(e -> handleAddNodes(nField));

        Label nodeLabel = new Label("Add Node");
        nodeLabel.getStyleClass().add("subtitle-label");
        Label numNodesLabel = new Label("Number of nodes:");
        numNodesLabel.getStyleClass().add("normal-label");

        VBox nodeBox = new VBox(5, new HBox(5, numNodesLabel, nField), addNodeBtn);
        nodeBox.getStyleClass().add("vbox");
        nodeBox.setAlignment(Pos.CENTER);

        Button continueBtn = new Button("Continue");
        continueBtn.getStyleClass().add("button");
        continueBtn.setPrefWidth(200);
        continueBtn.setOnAction(e -> stage.setScene(new SourceTargetScene(stage, grafo).getScene()));

        leftPanel.getChildren().addAll(new VBox(10, edgeLabel, edgeBox, new Separator(), nodeLabel, nodeBox), continueBtn);
        return leftPanel;
    }

    private TextField createTextField(String prompt, double width) {
        TextField field = new TextField();
        field.getStyleClass().add("text-field");
        field.setPrefWidth(width);
        field.setPromptText(prompt);
        return field;
    }

    private void handleAddNodes(TextField nField) {
        try {
            int nodesToAdd = nField.getText().trim().isEmpty() ? 1 : Integer.parseInt(nField.getText().trim());
            if (nodesToAdd <= 0) {
                UIUtils.showAlert("Error", "Number of nodes must be positive");
                return;
            }

            Random random = new Random();
            int startId = grafo.getNodos().size();
            for (int i = 0; i < nodesToAdd; i++) {
                int nodeId = startId + i;
                grafo.addNode(50, 50, nodeId);
                if (nodeId > 0) {
                    Nodo lastNode = grafo.getNodoPorId(nodeId - 1);
                    Nodo newNode = grafo.getNodoPorId(nodeId);
                    if (lastNode != null && newNode != null) {
                        grafo.addEdge(lastNode, newNode, random.nextInt(maxWeight) + 1);
                    }
                }
            }
            grafo.render(canvasPane);
            nField.clear();
        } catch (NumberFormatException ex) {
            UIUtils.showAlert("Error", "Please enter a valid number of nodes");
        }
    }

    private void handleAddEdge(TextField uField, TextField vField, TextField wField) {
        try {
            String nodeUName = uField.getText().trim().toUpperCase();
            String nodeVName = vField.getText().trim().toUpperCase();
            if (nodeUName.isEmpty() || nodeVName.isEmpty()) {
                UIUtils.showAlert("Error", "Node names cannot be empty");
                return;
            }

            Nodo nodoU = grafo.getNodoPorNombre(nodeUName);
            Nodo nodoV = grafo.getNodoPorNombre(nodeVName);
            if (nodoU == null || nodoV == null) {
                UIUtils.showAlert("Error", "Invalid node name(s): " + nodeUName + " or " + nodeVName);
                return;
            }
            if (nodoU.getNodeId() == nodoV.getNodeId()) {
                UIUtils.showAlert("Error", "Cannot connect a node to itself");
                return;
            }

            int weight = Integer.parseInt(wField.getText().trim());
            if (weight <= 0 || weight > 100) {
                UIUtils.showAlert("Error", "Weight must be between 1 and 100");
                return;
            }

            if (grafo.getAristas().stream().anyMatch(arista ->
                    (arista.getNodoU().getNodeId() == nodoU.getNodeId() && arista.getNodoV().getNodeId() == nodoV.getNodeId()) ||
                            (arista.getNodoU().getNodeId() == nodoV.getNodeId() && arista.getNodoV().getNodeId() == nodoU.getNodeId()))) {
                UIUtils.showAlert("Error", "Edge already exists between " + nodeUName + " and " + nodeVName);
                return;
            }

            grafo.addEdge(nodoU, nodoV, weight);
            grafo.render(canvasPane);
            uField.clear();
            vField.clear();
            wField.clear();
        } catch (NumberFormatException ex) {
            UIUtils.showAlert("Error", "Please enter a valid numeric value for weight");
        }
    }

    public Scene getScene() {
        return scene;
    }
}