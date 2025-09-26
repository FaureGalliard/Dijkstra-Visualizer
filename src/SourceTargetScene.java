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
import java.util.ArrayList;

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
            GraphEditorScene editor = new GraphEditorScene(stage, grafo);
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
        sourceField = createTextField(100);
        sourceField.setPromptText("Source node (e.g., A)");
        targetField = createTextField(100);
        targetField.setPromptText("Target node (e.g., B)");

        // Add listeners for text changes
        addTextChangeListener(sourceField, true);
        addTextChangeListener(targetField, false);

        Button runDijkstraBtn = new Button("Run Dijkstra");
        runDijkstraBtn.setPrefWidth(150);
        // runDijkstraBtn.setOnAction(e -> runDijkstra()); // Disabled as per request

        Button clearBtn = new Button("Clear Selection");
        clearBtn.setPrefWidth(150);
        clearBtn.setOnAction(e -> resetAllSelection());

        // Result label
        resultLabel = new Label("Select source and target nodes.");
        resultLabel.setWrapText(true);

        leftPanel.getChildren().addAll(
                new Label("Source:"),
                sourceField,
                new Label("Target:"),
                targetField,
                runDijkstraBtn,
                clearBtn,
                resultLabel
        );
        return leftPanel;
    }

    private TextField createTextField(double width) {
        TextField field = new TextField();
        field.setPrefWidth(width);
        return field;
    }

    /** --- Selección centralizada --- */
    private void selectNode(Nodo node, boolean isSource) {
        Nodo other = isSource ? targetNode : sourceNode;

        // Evitar que source y target sean el mismo nodo
        if (node == other) {
            resultLabel.setText("Cannot select the same node for source and target.");
            return;
        }

        // Resetear el anterior
        if (isSource && sourceNode != null) {
            sourceNode.setFill(Color.CORNFLOWERBLUE);
        }
        if (!isSource && targetNode != null) {
            targetNode.setFill(Color.CORNFLOWERBLUE);
        }

        // Guardar y pintar el nuevo
        if (isSource) {
            sourceNode = node;
            sourceField.setText(node.getName());
            node.setFill(Color.GREEN);
        } else {
            targetNode = node;
            targetField.setText(node.getName());
            node.setFill(Color.RED);
        }

        nextIsSource = !isSource;
        updateResultLabel();
    }

    /** --- Listeners en los TextFields --- */
    private void addTextChangeListener(TextField field, boolean isSource) {
        field.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                resetSelection(isSource);
                updateNextAfterReset();
                updateResultLabel();
                return;
            }

            String nodeName = newValue.trim().toUpperCase();
            Nodo node = grafo.getNodoPorNombre(nodeName);

            if (node != null) {
                selectNode(node, isSource); // Usar lógica centralizada
            } else {
                resultLabel.setText("Invalid node name: " + nodeName);
                resetSelection(isSource);
                updateNextAfterReset();
            }
        });
    }

    /** --- Listener en los nodos (doble click) --- */
    private void enableNodeSelection() {
        for (Nodo node : new ArrayList<>(grafo.getNodos())) {
            node.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    selectNode(node, nextIsSource);
                }
            });
        }
    }

    /** --- Helpers --- */
    private void resetSelection(boolean isSource) {
        if (isSource && sourceNode != null) {
            sourceNode.setFill(Color.CORNFLOWERBLUE);
            sourceNode = null;
            sourceField.clear();
        } else if (!isSource && targetNode != null) {
            targetNode.setFill(Color.CORNFLOWERBLUE);
            targetNode = null;
            targetField.clear();
        }
    }

    private void resetAllSelection() {
        for (Nodo node : new ArrayList<>(grafo.getNodos())) {
            node.setFill(Color.CORNFLOWERBLUE);
        }
        for (Arista arista : new ArrayList<>(grafo.getAristas())) {
            arista.getLine().setStroke(Color.BLACK);
        }
        sourceNode = null;
        targetNode = null;
        sourceField.clear();
        targetField.clear();
        nextIsSource = true;
        updateResultLabel();
    }

    private void updateNextAfterReset() {
        if (sourceNode == null) {
            nextIsSource = true;
        } else if (targetNode == null) {
            nextIsSource = false;
        } else {
            nextIsSource = true;
        }
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

    public Scene getScene() {
        return scene;
    }
}