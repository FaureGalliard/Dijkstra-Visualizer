import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.Random;

public class GraphEditorScene {
    private final Grafo grafo;
    private final Scene scene;
    private  double density;
    private  int maxWeight;
    private final Pane canvasPane;
    private final Stage stage;
    private static final String FONT_NAME = "Courier Prime";

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
        canvasPane.setStyle("-fx-background-color: #20232a; -fx-border-color: #2d2d35;");
        canvasPane.setPrefSize(500, 400);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #20232a;");
        root.setPadding(new Insets(20));

        VBox leftPanel = createLeftPanel();
        HBox mainBox = new HBox(10, leftPanel, canvasPane);
        mainBox.setAlignment(Pos.CENTER);
        root.setCenter(mainBox);
        root.setBottom(createFooter());

        grafo.render(canvasPane);

        this.scene = new Scene(root,  1024, 576);
        stage.setTitle("Graph Editor - Dijkstra Visualizer");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(15);
        leftPanel.setPrefWidth(250);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setStyle("-fx-background-color: #20232a;");

        TextField uField = new TextField();
        uField.setPrefWidth(50);
        uField.setPromptText("Node u");
        uField.setStyle("-fx-background-color: #2d2d35; -fx-text-fill: #ffffff; -fx-prompt-text-fill: #8f8f8f; -fx-font-family: '" + FONT_NAME + "'; -fx-font-size: 12;");

        TextField vField = new TextField();
        vField.setPrefWidth(50);
        vField.setPromptText("Node v");
        vField.setStyle("-fx-background-color: #2d2d35; -fx-text-fill: #ffffff; -fx-prompt-text-fill: #8f8f8f; -fx-font-family: '" + FONT_NAME + "'; -fx-font-size: 12;");

        TextField wField = new TextField();
        wField.setPrefWidth(50);
        wField.setPromptText("Weight");
        wField.setStyle("-fx-background-color: #2d2d35; -fx-text-fill: #ffffff; -fx-prompt-text-fill: #8f8f8f; -fx-font-family: '" + FONT_NAME + "'; -fx-font-size: 12;");

        Button addEdgeBtn = new Button("Add Edge");
        addEdgeBtn.setPrefWidth(100);
        addEdgeBtn.setStyle("-fx-background-color: #2d2d35; -fx-text-fill: #ffffff; -fx-font-family: '" + FONT_NAME + "'; -fx-font-size: 14;");
        addEdgeBtn.setOnAction(e -> handleAddEdge(uField, vField, wField));

        Label edgeLabel = new Label("Add Edge");
        edgeLabel.setTextFill(Color.web("#ff914d"));
        edgeLabel.setFont(Font.font(FONT_NAME, 14));

        Label nodeULabel = new Label("Node u:");
        nodeULabel.setTextFill(Color.web("#ff914d"));
        nodeULabel.setFont(Font.font(FONT_NAME, 12));

        Label nodeVLabel = new Label("Node v:");
        nodeVLabel.setTextFill(Color.web("#ff914d"));
        nodeVLabel.setFont(Font.font(FONT_NAME, 12));

        Label weightLabel = new Label("Weight w:");
        weightLabel.setTextFill(Color.web("#ff914d"));
        weightLabel.setFont(Font.font(FONT_NAME, 12));

        VBox edgeBox = new VBox(5, new HBox(5, nodeULabel, uField),
                new HBox(5, nodeVLabel, vField),
                new HBox(5, weightLabel, wField), addEdgeBtn);
        edgeBox.setAlignment(Pos.CENTER);

        TextField nField = new TextField();
        nField.setPrefWidth(50);
        nField.setPromptText("Number of nodes");
        nField.setStyle("-fx-background-color: #2d2d35; -fx-text-fill: #ffffff; -fx-prompt-text-fill: #8f8f8f; -fx-font-family: '" + FONT_NAME + "'; -fx-font-size: 12;");

        Button addNodeBtn = new Button("Add Node(s)");
        addNodeBtn.setPrefWidth(100);
        addNodeBtn.setStyle("-fx-background-color: #2d2d35; -fx-text-fill: #ffffff; -fx-font-family: '" + FONT_NAME + "'; -fx-font-size: 14;");
        addNodeBtn.setOnAction(e -> handleAddNodes(nField));

        Label nodeLabel = new Label("Add Node");
        nodeLabel.setTextFill(Color.web("#ff914d"));
        nodeLabel.setFont(Font.font(FONT_NAME, 14));

        Label numNodesLabel = new Label("Number of nodes:");
        numNodesLabel.setTextFill(Color.web("#ff914d"));
        numNodesLabel.setFont(Font.font(FONT_NAME, 12));

        VBox nodeBox = new VBox(5, new HBox(5, numNodesLabel, nField), addNodeBtn);
        nodeBox.setAlignment(Pos.CENTER);

        Button continueBtn = new Button("Continue");
        continueBtn.setPrefWidth(200);
        continueBtn.setStyle("-fx-background-color: #2d2d35; -fx-text-fill: #ffffff; -fx-font-family: '" + FONT_NAME + "'; -fx-font-size: 14;");
        continueBtn.setOnAction(e -> stage.setScene(new SourceTargetScene(stage, grafo).getScene()));

        leftPanel.getChildren().addAll(new VBox(10, edgeLabel, edgeBox, new Separator(), nodeLabel, nodeBox), continueBtn);
        return leftPanel;
    }

    private HBox createFooter() {
        Label github = new Label("github.com/FaureGalliard");
        github.setTextFill(Color.web("#8f8f8f"));
        github.setFont(Font.font(FONT_NAME, 12));

        Label teacher = new Label("Teacher: Edgard Kenny Venegas Palacios");
        teacher.setTextFill(Color.web("#8f8f8f"));
        teacher.setFont(Font.font(FONT_NAME, 12));

        HBox footer = new HBox(10, teacher, new Region(), github);
        footer.setPadding(new Insets(10));
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-background-color: #20232a;");
        HBox.setHgrow(footer.getChildren().get(1), Priority.ALWAYS);
        return footer;
    }

    private void handleAddNodes(TextField nField) {
        try {
            int nodesToAdd = nField.getText().trim().isEmpty() ? 1 : Integer.parseInt(nField.getText().trim());
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
            if (weight <= 0 || weight > 100) {
                showAlert("Error", "Weight must be between 1 and 100");
                return;
            }
            if (grafo.getAristas().stream().anyMatch(arista ->
                    (arista.getNodoU().getNodeId() == nodoU.getNodeId() && arista.getNodoV().getNodeId() == nodoV.getNodeId()) ||
                            (arista.getNodoU().getNodeId() == nodoV.getNodeId() && arista.getNodoV().getNodeId() == nodoU.getNodeId()))) {
                showAlert("Error", "Edge already exists between " + nodeUName + " and " + nodeVName);
                return;
            }

            grafo.addEdge(nodoU, nodoV, weight);
            grafo.render(canvasPane);
            uField.clear();
            vField.clear();
            wField.clear();
        } catch (NumberFormatException ex) {
            showAlert("Error", "Please enter a valid numeric value for weight");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #20232a; -fx-font-family: '" + FONT_NAME + "';");
        alert.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill: #ffffff; -fx-font-family: '" + FONT_NAME + "'; -fx-font-size: 12;");
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}