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
    private final double density;
    private final int maxWeight;
    private final Pane canvasPane;
    private final Stage stage;

    // Existing constructor
    public GraphEditorScene(Stage stage, int nodeCount, String mode, double density, int maxWeight) {
        this.stage = stage;
        this.grafo = new Grafo();
        this.density = density;
        this.maxWeight = maxWeight;
        this.canvasPane = createCanvasPane();

        // Main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Left panel
        VBox leftPanel = createLeftPanel();

        // Main layout assembly
        HBox mainBox = new HBox(10, leftPanel, canvasPane);
        mainBox.setAlignment(Pos.CENTER);
        root.setCenter(mainBox);

        // Footer
        root.setBottom(createFooter());

        // Initialize graph
        grafo.createNodes(canvasPane, nodeCount);
        grafo.createEdges(canvasPane, density, maxWeight);
        grafo.render(canvasPane);

        // Scene setup
        this.scene = new Scene(root, 800, 450);
        stage.setTitle("Graph Editor - Dijkstra Visualizer");
        stage.setScene(scene);
        stage.show();
    }

    // New constructor to reuse existing graph
    public GraphEditorScene(Stage stage, Grafo grafo) {
        this.stage = stage;
        this.grafo = grafo;
        this.density = 50.0; // Default value, adjust if needed
        this.maxWeight = 10; // Default value, adjust if needed
        this.canvasPane = createCanvasPane();

        // Main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Left panel
        VBox leftPanel = createLeftPanel();

        // Main layout assembly
        HBox mainBox = new HBox(10, leftPanel, canvasPane);
        mainBox.setAlignment(Pos.CENTER);
        root.setCenter(mainBox);

        // Footer
        root.setBottom(createFooter());

        // Render existing graph
        grafo.render(canvasPane);

        // Scene setup
        this.scene = new Scene(root, 800, 450);
        stage.setTitle("Graph Editor - Dijkstra Visualizer");
        stage.setScene(scene);
        stage.show();
    }

    private Pane createCanvasPane() {
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: black;");
        pane.setPrefSize(500, 400);
        return pane;
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(15);
        leftPanel.setPrefWidth(250);
        leftPanel.setAlignment(Pos.TOP_CENTER);

        // Edge input panel
        TextField uField = createTextField(50);
        TextField vField = createTextField(50);
        TextField wField = createTextField(50);
        Button addEdgeBtn = new Button("Add Edge");
        addEdgeBtn.setOnAction(e -> handleAddEdge(uField, vField, wField));

        VBox edgeBox = new VBox(5,
                new HBox(5, new Label("Node u:"), uField),
                new HBox(5, new Label("Node v:"), vField),
                new HBox(5, new Label("Weight w:"), wField),
                addEdgeBtn);
        edgeBox.setAlignment(Pos.CENTER);

        // Node input panel
        TextField nField = createTextField(50);
        Button addNodeBtn = new Button("Add Node(s)");
        addNodeBtn.setOnAction(e -> handleAddNodes(nField));

        VBox nodeBox = new VBox(5, new HBox(5, new Label("Number of nodes:"), nField), addNodeBtn);
        nodeBox.setAlignment(Pos.CENTER);

        // Continue button
        Button continueBtn = new Button("Continue");
        continueBtn.setPrefWidth(200);
        continueBtn.setOnAction(e -> {
            SourceTargetScene sourceTarget = new SourceTargetScene(stage, grafo);
            stage.setScene(sourceTarget.getScene());
        });

        leftPanel.getChildren().addAll(
                new VBox(10, new Label("Add Edge"), edgeBox, new Separator(), new Label("Add Node"), nodeBox),
                continueBtn);
        return leftPanel;
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

    private TextField createTextField(double width) {
        TextField field = new TextField();
        field.setPrefWidth(width);
        return field;
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

            Nodo nodoU = grafo.getNodoPorNombre(nodeUName);
            Nodo nodoV = grafo.getNodoPorNombre(nodeVName);
            if (nodoU == null || nodoV == null) {
                showAlert("Error", "Invalid node name(s): " + nodeUName + " or " + nodeVName);
                return;
            }
            if (nodoU.getNodeId() == nodoV.getNodeId()) {
                showAlert("Error", "Cannot connect a node to itself");
                return;
            }
            if (weight <= 0 || weight >  100) {
                showAlert("Error", "Weight must be between 1 and " + 100);
                return;
            }
            if (edgeExists(nodoU, nodoV)) {
                showAlert("Error", "Edge already exists between " + nodeUName + " and " + nodeVName);
                return;
            }

            grafo.addEdge(nodoU, nodoV, weight);
            grafo.render(canvasPane);
            System.out.println("Arista añadida: " + nodeUName + " -> " + nodeVName + " (peso=" + weight + ")");
            uField.clear();
            vField.clear();
            wField.clear();
        } catch (NumberFormatException ex) {
            showAlert("Error", "Please enter a valid numeric value for weight");
        }
    }

    private boolean edgeExists(Nodo nodoU, Nodo nodoV) {
        return grafo.getAristas().stream().anyMatch(arista ->
                (arista.getNodoU().getNodeId() == nodoU.getNodeId() && arista.getNodoV().getNodeId() == nodoV.getNodeId()) ||
                        (arista.getNodoU().getNodeId() == nodoV.getNodeId() && arista.getNodoV().getNodeId() == nodoU.getNodeId()));
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

    public Grafo getGrafo() {
        return grafo;
    }
}