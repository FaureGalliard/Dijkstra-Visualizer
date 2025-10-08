package com.fauregalliard.dijsktravisualizer.controller;

import com.fauregalliard.dijsktravisualizer.model.DijkstraAlgorithm;
import com.fauregalliard.dijsktravisualizer.model.Graph;
import com.fauregalliard.dijsktravisualizer.model.Node;
import com.fauregalliard.dijsktravisualizer.util.Util;
import com.fauregalliard.dijsktravisualizer.view.EdgeView;
import com.fauregalliard.dijsktravisualizer.view.GraphRenderer;
import com.fauregalliard.dijsktravisualizer.view.NodeView;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class SourceTargetScene {
    private final Scene scene;
    private final Stage stage;
    private final Graph grafo;
    private final GraphRenderer graphRenderer;
    private Node sourceNode, targetNode;
    private Label resultLabel;
    private TextField sourceField, targetField;
    private boolean nextIsSource = true;

    public SourceTargetScene(Stage stage, GraphRenderer graphRenderer, Graph grafo) {
        this.stage = stage;
        this.grafo = grafo;
        this.graphRenderer = graphRenderer;

        Pane canvasPane = new Pane();
        canvasPane.setPickOnBounds(false);
        graphRenderer.renderGraph(grafo, canvasPane);

        setupNodeSelection();

        BorderPane controlsPane = new BorderPane();
        controlsPane.setLeft(createLeftPanel());
        controlsPane.setBottom(Util.createFooter());

        scene = new Scene(new StackPane(controlsPane, canvasPane), Util.BASE_WIDTH, Util.BASE_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm()); // Add stylesheet
    }

    private VBox createLeftPanel() {
        VBox panel = new VBox(15);
        panel.setPrefWidth(200);
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setStyle("-fx-padding: 10;");

        sourceField = Util.createTextField("Source (e.g., 1)", 100);
        targetField = Util.createTextField("Target (e.g., 2)", 100);

        setupTextField(sourceField, true);
        setupTextField(targetField, false);

        Button runBtn = Util.createButton("Run Dijkstra", 150);
        runBtn.setOnAction(e -> runDijkstra());

        Button clearBtn = Util.createButton("Clear Selection", 150);
        clearBtn.setOnAction(e -> resetAll());

        Button backBtn = Util.createButton("Back to Editor", 150);
        backBtn.setOnAction(e -> stage.setScene(new GraphEditorScene(stage, grafo, graphRenderer).getScene()));

        resultLabel = new Label("Select source and target nodes.");
        resultLabel.setWrapText(true);
        resultLabel.setMaxWidth(180);

        Label sourceLabel = new Label("Source:");
        sourceLabel.getStyleClass().add("title2"); // Add title style class
        Label targetLabel = new Label("Target:");
        targetLabel.getStyleClass().add("title2"); // Add title style class

        panel.getChildren().addAll(
                sourceLabel, sourceField,
                targetLabel, targetField,
                runBtn, clearBtn, resultLabel, backBtn
        );
        return panel;
    }

    private void setupNodeSelection() {
        graphRenderer.getViewNodes().forEach(view ->
                view.getCircle().setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2) {
                        grafo.nodes.stream()
                                .filter(n -> graphRenderer.getNodeView(n) == view)
                                .findFirst()
                                .ifPresent(node -> selectNode(node, view, nextIsSource));
                    }
                })
        );
    }

    private void setupTextField(TextField field, boolean isSource) {
        field.textProperty().addListener((obs, old, val) -> {
            if (val == null || val.trim().isEmpty()) {
                updateNode(null, isSource);
                nextIsSource = sourceNode == null || targetNode != null;
                updateResultLabel();
                return;
            }

            try {
                Node node = grafo.getFromId(Integer.parseInt(val.trim()));
                NodeView view = graphRenderer.getNodeView(node);

                if (view != null) {
                    selectNode(node, view, isSource);
                } else {
                    resultLabel.setText("Invalid node ID");
                    updateNode(null, isSource);
                }
            } catch (NumberFormatException e) {
                resultLabel.setText("Enter a valid node ID");
                updateNode(null, isSource);
            }
        });
    }

    private void selectNode(Node node, NodeView view, boolean isSource) {
        if (node == (isSource ? targetNode : sourceNode)) {
            resultLabel.setText("Cannot select the same node twice.");
            return;
        }

        updateNode(node, isSource);
        view.getCircle().setFill(isSource ? Color.web("#8c52ff") : Color.web("#fd9e80")); // Source: #8c52ff, Target: #fd9e80
        (isSource ? sourceField : targetField).setText(String.valueOf(node.getId()));
        nextIsSource = !isSource;
        updateResultLabel();
    }

    private void updateNode(Node node, boolean isSource) {
        Node oldNode = isSource ? sourceNode : targetNode;
        if (oldNode != null) {
            NodeView oldView = graphRenderer.getNodeView(oldNode);
            if (oldView != null) oldView.getCircle().setFill(Color.web("#cb6ce6")); // Default node color
        }
        if (isSource) sourceNode = node;
        else targetNode = node;
    }

    private void resetAll() {
        graphRenderer.getViewNodes().forEach(v -> v.getCircle().setFill(Color.web("#8f8f8f"))); // Default node color
        graphRenderer.getViewEdges().forEach(edge ->
                edge.getChildren().stream()
                        .filter(Line.class::isInstance)
                        .map(Line.class::cast)
                        .forEach(l -> {
                            l.setStroke(Color.web("#f5f5f5")); // Default edge color
                            l.setStrokeWidth(2);
                        })
        );

        sourceNode = targetNode = null;
        sourceField.clear();
        targetField.clear();
        nextIsSource = true;
        updateResultLabel();
    }

    private void updateResultLabel() {
        if (sourceNode == null && targetNode == null) {
            resultLabel.setText("Select source and target nodes.");
        } else if (sourceNode == null || targetNode == null) {
            resultLabel.setText((sourceNode != null ? "Source: " + sourceNode.getId() :
                    "Target: " + targetNode.getId()));
        } else {
            resultLabel.setText("Source: " + sourceNode.getId() + ", Target: " + targetNode.getId());
        }
    }

    private void runDijkstra() {
        if (sourceNode == null || targetNode == null) {
            resultLabel.setText("Please select both nodes.");
            return;
        }

        DijkstraAlgorithm.PathResult result =
                DijkstraAlgorithm.calculateShortestPath(grafo, sourceNode, targetNode);

        if (result == null || result.getDistance() == Integer.MAX_VALUE) {
            resultLabel.setText("No path found.");
            return;
        }

        graphRenderer.getViewNodes().forEach(v -> v.getCircle().setFill(Color.web("#8f8f8f"))); // Default node color
        graphRenderer.getViewEdges().forEach(edge ->
                edge.getChildren().stream()
                        .filter(Line.class::isInstance)
                        .map(Line.class::cast)
                        .forEach(l -> {
                            l.setStroke(Color.web("#f5f5f5")); // Default edge color
                            l.setStrokeWidth(2);
                        })
        );

        result.getPath().forEach(n -> {
            NodeView view = graphRenderer.getNodeView(n);
            if (view != null) {
                view.getCircle().setFill(
                        n == sourceNode ? Color.web("#8c52ff") : // Source node color
                                n == targetNode ? Color.web("#fd9e80") : // Target node color
                                        Color.web("#ff914d") // Path node color
                );
            }
        });

        for (int i = 0; i < result.getPath().size() - 1; i++) {
            NodeView u = graphRenderer.getNodeView(result.getPath().get(i));
            NodeView v = graphRenderer.getNodeView(result.getPath().get(i + 1));

            graphRenderer.getViewEdges().stream()
                    .filter(e -> (e.nodoU == u && e.nodoV == v) || (e.nodoU == v && e.nodoV == u))
                    .findFirst()
                    .ifPresent(edge ->
                            edge.getChildren().stream()
                                    .filter(Line.class::isInstance)
                                    .map(Line.class::cast)
                                    .forEach(l -> {
                                        l.setStroke(Color.web("#ff914d")); // Path edge color
                                        l.setStrokeWidth(2);
                                    })
                    );
        }

        StringBuilder sb = new StringBuilder("Path: ");
        result.getPath().forEach(n -> sb.append(n.getId()).append(" → "));
        sb.setLength(sb.length() - 3);
        resultLabel.setText(sb.append("\nDistance: ").append(result.getDistance()).toString());
    }

    public Scene getScene() {
        return scene;
    }
}