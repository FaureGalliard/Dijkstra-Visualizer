package com.fauregalliard.dijsktravisualizer.ui;

import com.fauregalliard.dijsktravisualizer.model.Arista;
import com.fauregalliard.dijsktravisualizer.model.Grafo;
import com.fauregalliard.dijsktravisualizer.model.Nodo;
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
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SourceTargetScene {
    private final Grafo grafo;
    private final Scene scene;
    private final Stage stage;
    private Nodo sourceNode;
    private Nodo targetNode;
    private Label resultLabel;
    private final Pane canvasPane;
    private TextField sourceField;
    private TextField targetField;
    private boolean nextIsSource = true;
    private static final String FONT_NAME = "Courier Prime";

    public SourceTargetScene(Stage stage, Grafo grafo) {
        this.stage = stage;
        this.grafo = grafo;
        this.canvasPane = new Pane();
        canvasPane.setStyle("-fx-background-color: #20232a; -fx-border-color: #2d2d35;");
        canvasPane.setPrefSize(600, 500);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #20232a;");
        root.setPadding(new Insets(20));

        grafo.render(canvasPane);
        enableNodeSelection();

        VBox leftPanel = createLeftPanel();
        Button backBtn = new Button("Back to Editor");
        backBtn.setStyle("-fx-background-color: #2d2d35; -fx-text-fill: #ffffff; -fx-font-family: '" + FONT_NAME + "'; -fx-font-size: 14;");
        backBtn.setPrefWidth(200);
        backBtn.setOnAction(e -> stage.setScene(new GraphEditorScene(stage, grafo).getScene()));

        HBox mainBox = new HBox(10, leftPanel, canvasPane);
        mainBox.setAlignment(Pos.CENTER);
        root.setCenter(mainBox);
        root.setBottom(backBtn);
        BorderPane.setAlignment(backBtn, Pos.CENTER);
        BorderPane.setMargin(backBtn, new Insets(10));

        this.scene = new Scene(root,  1024, 576);
        stage.setTitle("Source-Target Selector - Dijkstra Visualizer");
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(15);
        leftPanel.setPrefWidth(200);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setStyle("-fx-background-color: #20232a;");

        sourceField = new TextField();
        sourceField.setPrefWidth(100);
        sourceField.setPromptText("Source node (e.g., A)");
        sourceField.setStyle("-fx-background-color: #2d2d35; -fx-text-fill: #ffffff; -fx-prompt-text-fill: #8f8f8f; -fx-font-family: '" + FONT_NAME + "'; -fx-font-size: 12;");

        targetField = new TextField();
        targetField.setPrefWidth(100);
        targetField.setPromptText("Target node (e.g., B)");
        targetField.setStyle("-fx-background-color: #2d2d35; -fx-text-fill: #ffffff; -fx-prompt-text-fill: #8f8f8f; -fx-font-family: '" + FONT_NAME + "'; -fx-font-size: 12;");

        addTextChangeListener(sourceField, true);
        addTextChangeListener(targetField, false);

        Button runDijkstraBtn = new Button("Run Dijkstra");
        runDijkstraBtn.setPrefWidth(150);
        runDijkstraBtn.setStyle("-fx-background-color: #2d2d35; -fx-text-fill: #ffffff; -fx-font-family: '" + FONT_NAME + "'; -fx-font-size: 14;");

        Button clearBtn = new Button("Clear Selection");
        clearBtn.setPrefWidth(150);
        clearBtn.setStyle("-fx-background-color: #2d2d35; -fx-text-fill: #ffffff; -fx-font-family: '" + FONT_NAME + "'; -fx-font-size: 14;");

        runDijkstraBtn.setOnAction(e -> runDijkstra());
        clearBtn.setOnAction(e -> resetAllSelection());

        resultLabel = new Label("Select source and target nodes.");
        resultLabel.setTextFill(Color.web("#ffffff"));
        resultLabel.setFont(Font.font(FONT_NAME, 12));
        resultLabel.setWrapText(true);

        Label sourceLabel = new Label("Source:");
        sourceLabel.setTextFill(Color.web("#ff914d"));
        sourceLabel.setFont(Font.font(FONT_NAME, 14));

        Label targetLabel = new Label("Target:");
        targetLabel.setTextFill(Color.web("#ff914d"));
        targetLabel.setFont(Font.font(FONT_NAME, 14));

        leftPanel.getChildren().addAll(sourceLabel, sourceField, targetLabel, targetField, runDijkstraBtn, clearBtn, resultLabel);
        return leftPanel;
    }

    private void selectNode(Nodo node, boolean isSource) {
        Nodo other = isSource ? targetNode : sourceNode;
        if (node == other) {
            resultLabel.setText("Cannot select the same node for source and target.");
            return;
        }

        if (isSource && sourceNode != null) {
            sourceNode.setFill(Color.web("#8f8f8f"));
        }
        if (!isSource && targetNode != null) {
            targetNode.setFill(Color.web("#8f8f8f"));
        }

        if (isSource) {
            sourceNode = node;
            sourceField.setText(node.getName());
            node.setFill(Color.web("#8c52ff"));
        } else {
            targetNode = node;
            targetField.setText(node.getName());
            node.setFill(Color.web("#cb6ce6"));
        }

        nextIsSource = !isSource;
        updateResultLabel();
    }

    private void addTextChangeListener(TextField field, boolean isSource) {
        field.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                resetSelection(isSource);
                updateNextAfterReset();
                updateResultLabel();
                return;
            }

            Nodo node = grafo.getNodoPorNombre(newValue.trim().toUpperCase());
            if (node != null) {
                selectNode(node, isSource);
            } else {
                resultLabel.setText("Invalid node name: " + newValue.trim().toUpperCase());
                resetSelection(isSource);
                updateNextAfterReset();
            }
        });
    }

    private void enableNodeSelection() {
        grafo.getNodos().forEach(node -> node.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                selectNode(node, nextIsSource);
            }
        }));
    }

    private void resetSelection(boolean isSource) {
        if (isSource && sourceNode != null) {
            sourceNode.setFill(Color.web("#8f8f8f"));
            sourceNode = null;
            sourceField.clear();
        } else if (!isSource && targetNode != null) {
            targetNode.setFill(Color.web("#8f8f8f"));
            targetNode = null;
            targetField.clear();
        }
    }

    private void resetHighlights() {
        grafo.getNodos().forEach(node -> node.setFill(Color.web("#8f8f8f")));
        grafo.getAristas().forEach(arista -> {
            arista.getLine().setStroke(Color.web("#f5f5f5"));
            arista.getLine().setStrokeWidth(2);
            arista.getEdgeGroup().getChildren().stream()
                    .filter(n -> n instanceof Line)
                    .forEach(n -> ((Line) n).setStroke(Color.web("#f5f5f5")));
        });
    }

    private void resetAllSelection() {
        resetHighlights();
        sourceNode = null;
        targetNode = null;
        sourceField.clear();
        targetField.clear();
        nextIsSource = true;
        updateResultLabel();
    }

    private void updateNextAfterReset() {
        nextIsSource = sourceNode == null || targetNode != null;
    }

    private void updateResultLabel() {
        if (sourceNode == null && targetNode == null) {
            resultLabel.setText("Select source and target nodes.");
        } else if (sourceNode != null && targetNode == null) {
            resultLabel.setText("Source: " + sourceNode.getName());
        } else if (sourceNode == null && targetNode != null) {
            resultLabel.setText("Target: " + targetNode.getName());
        } else {
            resultLabel.setText("Source: " + sourceNode.getName() + ", Target: " + targetNode.getName());
        }
    }

    private void runDijkstra() {
        if (sourceNode == null || targetNode == null) {
            resultLabel.setText("Please select source and target nodes.");
            return;
        }

        Grafo.PathResult result = grafo.getShortestPath(sourceNode, targetNode);
        if (result == null) {
            resultLabel.setText("No path found from source to target.");
            return;
        }

        resetHighlights();
        for (Nodo n : result.path) {
            n.setFill(n == sourceNode ? Color.web("#8c52ff") : n == targetNode ? Color.web("#cb6ce6") : Color.web("#fd9e80"));
        }

        for (int i = 0; i < result.path.size() - 1; i++) {
            Nodo u = result.path.get(i);
            Nodo v = result.path.get(i + 1);
            Arista e = grafo.getEdgeBetween(u, v);
            if (e != null) {
                e.getLine().setStroke(Color.web("#ff914d"));
                e.getLine().setStrokeWidth(4);
                e.getEdgeGroup().getChildren().stream()
                        .filter(n -> n instanceof Line)
                        .forEach(n -> {
                            ((Line) n).setStroke(Color.web("#ff914d"));
                            ((Line) n).setStrokeWidth(4);
                        });
            }
        }

        StringBuilder sb = new StringBuilder("Shortest Path: ");
        result.path.forEach(n -> sb.append(n.getName()).append(" -> "));
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 4);
        }
        sb.append("\nDistance: ").append(result.distance);
        resultLabel.setText(sb.toString());
    }

    public Scene getScene() {
        return scene;
    }
}