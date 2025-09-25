import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class GraphEditorScene {
    private int nodeCount;
    private double density;
    private int maxWeight;
    private Scene scene;

    private String getNodeName(int index) {
        // Genera nombres alfabéticos: A, B, ..., Z, AA, AB, ..., AZ, BA, ..., ZA
        StringBuilder name = new StringBuilder();
        while (index >= 0) {
            name.insert(0, (char) ('A' + (index % 26)));
            index = (index / 26) - 1;
            if (index < 0) break;
        }
        return name.toString();
    }

    private void CreateNodes(Pane canvasPane) {
        double nodeDiameter = 20;
        double spacing = 5;
        double nodeSizeWithSpacing = nodeDiameter + spacing;
        double canvasWidth = canvasPane.getPrefWidth();
        int nodesPerRow = (int) (canvasWidth / nodeSizeWithSpacing);
        double startX = 10;
        double startY = 10;

        for (int i = 0; i < nodeCount; i++) {
            int row = i / nodesPerRow;
            int col = i % nodesPerRow;
            double x = startX + col * nodeSizeWithSpacing + nodeDiameter / 2;
            double y = startY + row * nodeSizeWithSpacing + nodeDiameter / 2;

            Nodo node = new Nodo(nodeDiameter / 2, x, y); // Radio = diámetro / 2
            node.setNodeId(i);

            // Crear etiqueta con el nombre alfabético
            Text label = new Text(getNodeName(i));
            label.setTextAlignment(TextAlignment.CENTER);
            label.setMouseTransparent(true); // Ignorar eventos del ratón en la etiqueta
            // Centrar la etiqueta en el nodo
            label.setX(x - label.getBoundsInLocal().getWidth() / 2);
            label.setY(y + label.getBoundsInLocal().getHeight() / 4);
            // Vincular la posición del label al nodo al arrastrar
            node.centerXProperty().addListener((obs, old, newVal) ->
                    label.setX(newVal.doubleValue() - label.getBoundsInLocal().getWidth() / 2));
            node.centerYProperty().addListener((obs, old, newVal) ->
                    label.setY(newVal.doubleValue() + label.getBoundsInLocal().getHeight() / 4));

            canvasPane.getChildren().addAll(node, label);
        }
    }

    public GraphEditorScene(Stage stage, int nodeCount, String mode, double density, int maxWeight) {
        this.nodeCount = nodeCount;
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
        uField.setPrefWidth(60);
        Label vLabel = new Label("Node v:");
        TextField vField = new TextField();
        vField.setPrefWidth(60);
        Label wLabel = new Label("Weight w:");
        TextField wField = new TextField();
        wField.setPrefWidth(60);
        Button addEdgeBtn = new Button("Add Edge");
        HBox edgeInput = new HBox(5, uLabel, uField, vLabel, vField, wLabel, wField, addEdgeBtn);
        edgeInput.setAlignment(Pos.CENTER);
        VBox manualBox = new VBox(10, edgeInput);

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
        CreateNodes(canvasPane);

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

        // Event handler for addEdgeBtn
        addEdgeBtn.setOnAction(e -> {
            try {
                int nodeU = Integer.parseInt(uField.getText());
                int nodeV = Integer.parseInt(vField.getText());
                int weight = Integer.parseInt(wField.getText());

                if (nodeU < 0 || nodeU >= nodeCount || nodeV < 0 || nodeV >= nodeCount) {
                    showAlert("Error", "Nodes u and v must be between 0 and " + (nodeCount - 1));
                    return;
                }
                if (nodeU == nodeV) {
                    showAlert("Error", "Nodes u and v cannot be the same");
                    return;
                }
                if (weight <= 0 || weight > maxWeight) {
                    showAlert("Error", "Weight must be between 1 and " + maxWeight);
                    return;
                }

                Nodo nodoU = null, nodoV = null;
                for (var child : canvasPane.getChildren()) {
                    if (child instanceof Nodo) {
                        Nodo nodo = (Nodo) child;
                        if (nodo.getNodeId() == nodeU) nodoU = nodo;
                        if (nodo.getNodeId() == nodeV) nodoV = nodo;
                    }
                }

                if (nodoU == null || nodoV == null) {
                    showAlert("Error", "One or both nodes do not exist on the canvas");
                    return;
                }

                Line edge = new Line(
                        nodoU.getCenterX(), nodoU.getCenterY(),
                        nodoV.getCenterX(), nodoV.getCenterY()
                );
                edge.setStroke(Color.BLACK);
                edge.setStrokeWidth(2);
                canvasPane.getChildren().add(edge);

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