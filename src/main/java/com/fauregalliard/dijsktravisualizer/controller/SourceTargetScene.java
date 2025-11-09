package com.fauregalliard.dijsktravisualizer.controller;

import com.fauregalliard.dijsktravisualizer.model.DijkstraAlgorithm;
import com.fauregalliard.dijsktravisualizer.model.Graph;
import com.fauregalliard.dijsktravisualizer.model.Node;
import com.fauregalliard.dijsktravisualizer.util.Util;
import com.fauregalliard.dijsktravisualizer.view.EdgeView;
import com.fauregalliard.dijsktravisualizer.view.GraphRenderer;
import com.fauregalliard.dijsktravisualizer.view.NodeView;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class SourceTargetScene {
    private final Scene scene;
    private final Stage stage;
    private final Graph grafo;
    private final GraphRenderer graphRenderer;
    private Node sourceNode, targetNode;
    private Label resultLabel;
    private TextField sourceField, targetField;
    private boolean nextIsSource = true;
    private Slider speedSlider;
    private Button runBtn, pauseBtn;
    private boolean isPaused = false;
    private boolean isRunning = false;

    private static final Color COLOR_DEFAULT = Color.web("#8f8f8f");
    private static final Color COLOR_SOURCE = Color.web("#8c52ff");
    private static final Color COLOR_TARGET = Color.web("#fd9e80");
    private static final Color COLOR_VISITING = Color.web("#00ff00");
    private static final Color COLOR_CHECKING = Color.web("#ffff00");
    private static final Color COLOR_UPDATED = Color.web("#00ccff");
    private static final Color COLOR_PATH = Color.web("#ff914d");
    private static final Color COLOR_VISITED = Color.web("#666666");

    private static final Color COLOR_EDGE_DEFAULT = Color.web("#f5f5f5");
    private static final Color COLOR_EDGE_CHECKING = Color.web("#ffff00");
    private static final Color COLOR_EDGE_UPDATED = Color.web("#00ccff");
    private static final Color COLOR_EDGE_PATH = Color.web("#ff914d");

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
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
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

        runBtn = Util.createButton("Run Animated", 150);
        runBtn.setOnAction(e -> runDijkstraAnimated());

        pauseBtn = Util.createButton("Pause", 150);
        pauseBtn.setDisable(true);
        pauseBtn.setOnAction(e -> togglePause());

        Label speedLabel = new Label("Speed:");
        speedLabel.getStyleClass().add("title2");

        speedSlider = new Slider(100, 2000, 500);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(500);
        speedSlider.setBlockIncrement(100);
        speedSlider.setPrefWidth(150);

        Label speedValueLabel = new Label("500ms");
        speedSlider.valueProperty().addListener((obs, old, val) ->
                speedValueLabel.setText(String.format("%.0fms", val.doubleValue()))
        );

        Button clearBtn = Util.createButton("Clear Selection", 150);
        clearBtn.setOnAction(e -> resetAll());

        Button backBtn = Util.createButton("Back to Editor", 150);
        backBtn.setOnAction(e -> stage.setScene(new GraphEditorScene(stage, grafo, graphRenderer).getScene()));

        resultLabel = new Label("Select source and target nodes.");
        resultLabel.setWrapText(true);
        resultLabel.setMaxWidth(180);
        resultLabel.setStyle("-fx-font-size: 11px;");

        Label sourceLabel = new Label("Source:");
        sourceLabel.getStyleClass().add("title2");
        Label targetLabel = new Label("Target:");
        targetLabel.getStyleClass().add("title2");

        VBox legend = createLegend();

        panel.getChildren().addAll(
                sourceLabel, sourceField,
                targetLabel, targetField,
                runBtn, pauseBtn,
                speedLabel, speedSlider, speedValueLabel,
                clearBtn, resultLabel,
                legend, backBtn
        );
        return panel;
    }

    private VBox createLegend() {
        VBox legend = new VBox(3);
        legend.setStyle("-fx-padding: 5; -fx-border-color: #444; -fx-border-width: 1;");

        Label title = new Label("Legend:");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");

        legend.getChildren().addAll(
                title,
                createLegendItem("Visiting", COLOR_VISITING),
                createLegendItem("Checking", COLOR_CHECKING),
                createLegendItem("Updated", COLOR_UPDATED),
                createLegendItem("Visited", COLOR_VISITED),
                createLegendItem("Final path", COLOR_PATH)
        );

        return legend;
    }

    private HBox createLegendItem(String text, Color color) {
        HBox item = new HBox(5);
        item.setAlignment(Pos.CENTER_LEFT);

        Region colorBox = new Region();
        colorBox.setPrefSize(12, 12);
        colorBox.setStyle("-fx-background-color: " + toHexString(color) + ";");

        Label label = new Label(text);
        label.setStyle("-fx-font-size: 9px;");

        item.getChildren().addAll(colorBox, label);
        return item;
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255));
    }

    private void togglePause() {
        isPaused = !isPaused;
        pauseBtn.setText(isPaused ? "Resume" : "Pause");
    }

    private void setupNodeSelection() {
        graphRenderer.getViewNodes().forEach(view ->
                view.getCircle().setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2 && !isRunning) {
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
            if (isRunning) return;

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
        view.getCircle().setFill(isSource ? COLOR_SOURCE : COLOR_TARGET);
        (isSource ? sourceField : targetField).setText(String.valueOf(node.getId()));
        nextIsSource = !isSource;
        updateResultLabel();
    }

    private void updateNode(Node node, boolean isSource) {
        Node oldNode = isSource ? sourceNode : targetNode;
        if (oldNode != null) {
            NodeView oldView = graphRenderer.getNodeView(oldNode);
            if (oldView != null) oldView.getCircle().setFill(COLOR_DEFAULT);
        }
        if (isSource) sourceNode = node;
        else targetNode = node;
    }

    private void resetAll() {
        if (isRunning) return;

        graphRenderer.getViewNodes().forEach(v -> v.getCircle().setFill(COLOR_DEFAULT));
        graphRenderer.getViewEdges().forEach(edge ->
                edge.getChildren().stream()
                        .filter(Line.class::isInstance)
                        .map(Line.class::cast)
                        .forEach(l -> {
                            l.setStroke(COLOR_EDGE_DEFAULT);
                            l.setStrokeWidth(2);
                        })
        );

        sourceNode = targetNode = null;
        sourceField.clear();
        targetField.clear();
        nextIsSource = true;
        isPaused = false;
        pauseBtn.setText("Pause");
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

    private void runDijkstraAnimated() {
        if (sourceNode == null || targetNode == null) {
            resultLabel.setText("Please select both nodes.");
            return;
        }

        isRunning = true;
        isPaused = false;
        runBtn.setDisable(true);
        pauseBtn.setDisable(false);
        pauseBtn.setText("Pause");

        graphRenderer.getViewNodes().forEach(v -> {
            if (graphRenderer.getNodeView(sourceNode) != v && graphRenderer.getNodeView(targetNode) != v) {
                v.getCircle().setFill(COLOR_DEFAULT);
            }
        });
        graphRenderer.getViewEdges().forEach(edge ->
                edge.getChildren().stream()
                        .filter(Line.class::isInstance)
                        .map(Line.class::cast)
                        .forEach(l -> {
                            l.setStroke(COLOR_EDGE_DEFAULT);
                            l.setStrokeWidth(2);
                        })
        );

        DijkstraAlgorithm.AnimatedResult result =
                DijkstraAlgorithm.calculateShortestPathAnimated(grafo, sourceNode, targetNode);

        animateSteps(result, 0);
    }

    private void animateSteps(DijkstraAlgorithm.AnimatedResult result, int stepIndex) {
        if (stepIndex >= result.getSteps().size()) {
            showFinalPath(result.getFinalResult());
            return;
        }

        if (isPaused) {
            PauseTransition pause = new PauseTransition(Duration.millis(100));
            pause.setOnFinished(e -> animateSteps(result, stepIndex));
            pause.play();
            return;
        }

        DijkstraAlgorithm.StepInfo step = result.getSteps().get(stepIndex);

        Platform.runLater(() -> {
            resultLabel.setText(step.getDescription());
            visualizeStep(step);
        });

        double delay = speedSlider.getValue();
        PauseTransition pause = new PauseTransition(Duration.millis(delay));
        pause.setOnFinished(e -> animateSteps(result, stepIndex + 1));
        pause.play();
    }

    private void visualizeStep(DijkstraAlgorithm.StepInfo step) {
        NodeView currentView = step.getCurrentNode() != null ?
                graphRenderer.getNodeView(step.getCurrentNode()) : null;
        NodeView neighborView = step.getNeighborNode() != null ?
                graphRenderer.getNodeView(step.getNeighborNode()) : null;

        switch (step.getType()) {
            case INITIALIZE:
                if (currentView != null) {
                    currentView.getCircle().setFill(COLOR_SOURCE);
                }
                break;

            case VISIT_NODE:
                if (currentView != null) {
                    if (step.getCurrentNode() != sourceNode && step.getCurrentNode() != targetNode) {
                        currentView.getCircle().setFill(COLOR_VISITING);
                    }
                }
                break;

            case CHECK_NEIGHBOR:
                if (neighborView != null && step.getNeighborNode() != targetNode) {
                    neighborView.getCircle().setFill(COLOR_CHECKING);
                }
                if (currentView != null && neighborView != null) {
                    highlightEdge(currentView, neighborView, COLOR_EDGE_CHECKING, 3);
                }
                break;

            case UPDATE_DISTANCE:
                if (neighborView != null && step.getNeighborNode() != targetNode) {
                    neighborView.getCircle().setFill(COLOR_UPDATED);
                }
                if (currentView != null && neighborView != null) {
                    highlightEdge(currentView, neighborView, COLOR_EDGE_UPDATED, 3);
                }
                if (currentView != null && step.getCurrentNode() != sourceNode &&
                        step.getCurrentNode() != targetNode) {
                    currentView.getCircle().setFill(COLOR_VISITED);
                }
                break;
        }
    }

    private void highlightEdge(NodeView from, NodeView to, Color color, double width) {
        graphRenderer.getViewEdges().stream()
                .filter(e -> (e.nodoU == from && e.nodoV == to) || (e.nodoU == to && e.nodoV == from))
                .findFirst()
                .ifPresent(edge ->
                        edge.getChildren().stream()
                                .filter(Line.class::isInstance)
                                .map(Line.class::cast)
                                .forEach(l -> {
                                    l.setStroke(color);
                                    l.setStrokeWidth(width);
                                })
                );
    }

    private void showFinalPath(DijkstraAlgorithm.PathResult pathResult) {
        if (pathResult == null || pathResult.getDistance() == Integer.MAX_VALUE) {
            resultLabel.setText("No path found.");
            isRunning = false;
            runBtn.setDisable(false);
            pauseBtn.setDisable(true);
            return;
        }

        pathResult.getPath().forEach(n -> {
            NodeView view = graphRenderer.getNodeView(n);
            if (view != null) {
                view.getCircle().setFill(
                        n == sourceNode ? COLOR_SOURCE :
                                n == targetNode ? COLOR_TARGET :
                                        COLOR_PATH
                );
            }
        });

        for (int i = 0; i < pathResult.getPath().size() - 1; i++) {
            NodeView u = graphRenderer.getNodeView(pathResult.getPath().get(i));
            NodeView v = graphRenderer.getNodeView(pathResult.getPath().get(i + 1));
            highlightEdge(u, v, COLOR_EDGE_PATH, 4);
        }

        StringBuilder sb = new StringBuilder("Path: ");
        pathResult.getPath().forEach(n -> sb.append(n.getId()).append(" → "));
        sb.setLength(sb.length() - 3);
        resultLabel.setText(sb.append("\nDistance: ").append(pathResult.getDistance()).toString());

        isRunning = false;
        runBtn.setDisable(false);
        pauseBtn.setDisable(true);
    }

    public Scene getScene() {
        return scene;
    }
}