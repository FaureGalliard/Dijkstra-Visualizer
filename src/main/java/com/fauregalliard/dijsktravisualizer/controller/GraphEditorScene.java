package com.fauregalliard.dijsktravisualizer.controller;

import com.fauregalliard.dijsktravisualizer.model.Edge;
import com.fauregalliard.dijsktravisualizer.model.Graph;
import com.fauregalliard.dijsktravisualizer.model.Node;
import com.fauregalliard.dijsktravisualizer.util.Util;
import com.fauregalliard.dijsktravisualizer.view.GraphRenderer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class GraphEditorScene {
    private final Scene scene;
    private double density;
    private int maxWeight;
    public Graph grafo;
    private final Pane canvasPane;
    public GraphRenderer graphRenderer;
    private Stage stage;

    public GraphEditorScene(Stage stage, int nodeCount, String mode, int density, int maxWeight) {
        this.density = density;
        this.maxWeight = maxWeight;
        this.grafo = new Graph();
        this.stage = stage;
        this.graphRenderer = new GraphRenderer();

        canvasPane = new Pane();
        canvasPane.setPickOnBounds(false);

        grafo.createNodes(nodeCount);
        grafo.createEdges(maxWeight, density);
        graphRenderer.renderGraph(grafo, canvasPane);

        scene = createScene();
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm()); // Add stylesheet
    }

    public GraphEditorScene(Stage stage, Graph grafo, GraphRenderer graphRenderer) {
        this.stage = stage;
        this.grafo = grafo;
        this.graphRenderer = graphRenderer;

        canvasPane = new Pane();
        canvasPane.setPickOnBounds(false);

        graphRenderer.renderGraph(grafo, canvasPane);

        scene = createScene();
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm()); // Add stylesheet
    }

    private Scene createScene() {
        BorderPane controlsPane = new BorderPane();

        VBox leftPanel = createLeftPanel();
        controlsPane.setLeft(leftPanel);

        HBox footer = Util.createFooter();
        controlsPane.setBottom(footer);

        StackPane root = new StackPane();
        root.getChildren().addAll(controlsPane, canvasPane);

        return new Scene(root, Util.BASE_WIDTH, Util.BASE_HEIGHT);
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(15);
        leftPanel.setPrefWidth(250);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setStyle("-fx-padding: 10;");

        TextField[] fields = {
                Util.createTextField("Node u", 60),
                Util.createTextField("Node v", 60),
                Util.createTextField("Weight", 60)
        };
        Button addEdgeBtn = Util.createButton("Add Edge", 100);
        addEdgeBtn.setOnAction(e -> handleAddEdge(fields[0], fields[1], fields[2]));

        Label edgeLabel = new Label("Add Edge");
        edgeLabel.getStyleClass().add("title2"); // Add title style class
        VBox edgeBox = new VBox(5);
        edgeBox.setAlignment(Pos.CENTER);
        String[] labels = {"Node u:", "Node v:", "Weight w:"};
        for (int i = 0; i < 3; i++) {
            Label lbl = new Label(labels[i]);
            edgeBox.getChildren().add(new HBox(5, lbl, fields[i]));
        }
        edgeBox.getChildren().add(addEdgeBtn);

        TextField nField = Util.createTextField("Number of nodes", 60);
        Button addNodeBtn = Util.createButton("Add Node(s)", 120);
        addNodeBtn.setOnAction(e -> handleAddNodes(nField));

        Label nodeLabel = new Label("Add Node");
        nodeLabel.getStyleClass().add("title2"); // Add title style class
        Label numNodesLabel = new Label("Number of nodes:");
        VBox nodeBox = new VBox(5, new HBox(5, numNodesLabel, nField), addNodeBtn);
        nodeBox.setAlignment(Pos.CENTER);

        Button continueBtn = Util.createButton("Continue", 100);
        continueBtn.setOnAction(e -> stage.setScene(new SourceTargetScene(stage, graphRenderer, grafo).getScene()));

        leftPanel.getChildren().addAll(
                new VBox(10, edgeLabel, edgeBox, new Separator(), nodeLabel, nodeBox),
                continueBtn
        );

        return leftPanel;
    }

    private void handleAddEdge(TextField uField, TextField vField, TextField wField) {
        try {
            String uText = uField.getText().trim();
            String vText = vField.getText().trim();
            String wText = wField.getText().trim();

            if (uText.isEmpty() || vText.isEmpty() || wText.isEmpty()) {
                Util.showAlert("Error", "All fields must be filled");
                return;
            }

            int u = Integer.parseInt(uText);
            int v = Integer.parseInt(vText);
            int weight = Integer.parseInt(wText);

            Node nodoU = grafo.getFromId(u);
            Node nodoV = grafo.getFromId(v);

            if (nodoU == null || nodoV == null || nodoU.getId() == nodoV.getId()) {
                Util.showAlert("Error", nodoU == null || nodoV == null ?
                        "Invalid node name(s): " + u + " or " + v :
                        "Cannot connect a node to itself");
                return;
            }

            if (weight <= 0 || weight > 100) {
                Util.showAlert("Error", "Weight must be between 1 and 100");
                return;
            }

            if (grafo.edges.stream().anyMatch(e ->
                    (e.getFrom().getId() == u && e.getTo().getId() == v) ||
                            (e.getFrom().getId() == v && e.getTo().getId() == u))) {
                Util.showAlert("Error", "Edge already exists between " + u + " and " + v);
                return;
            }

            grafo.addEdge(nodoU, nodoV, weight);
            uField.clear();
            vField.clear();
            wField.clear();

            graphRenderer.renderGraph(grafo, canvasPane);

            System.out.println("Ahora hay: " + grafo.edges.size() + " aristas");
            for (Edge e : grafo.edges) {
                System.out.printf("Nodo %d -> Nodo %d [peso=%d]%n",
                        e.getFrom().getId(), e.getTo().getId(), e.getWeight());
            }

        } catch (NumberFormatException ex) {
            Util.showAlert("Error", "Please enter valid numeric values");
        }
    }

    private void handleAddNodes(TextField nField) {
        try {
            int nodesToAdd = nField.getText().trim().isEmpty() ? 1 : Integer.parseInt(nField.getText().trim());
            if (nodesToAdd <= 0) {
                Util.showAlert("Error", "Number of nodes must be positive");
                return;
            }

            grafo.createNodes(nodesToAdd);
            nField.clear();

            graphRenderer.renderGraph(grafo, canvasPane);

            System.out.println("Ahora hay " + grafo.nodes.size() + " nodos");
            for (Node node : grafo.nodes) {
                System.out.printf("Nodo %d%n", node.getId());
            }
        } catch (NumberFormatException ex) {
            Util.showAlert("Error", "Please enter a valid number of nodes");
        }
    }

    public Scene getScene() {
        return scene;
    }
}