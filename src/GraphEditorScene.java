import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class GraphEditorScene {
    private int nodeCount;
    private String mode;
    private double density;
    private int maxWeight;
    private Scene scene;

    private void CreateNodes(Pane canvasPane){

        for(int i = 0;i< nodeCount;i++){
            Nodo noden = new Nodo(20, 100, 100);
            canvasPane.getChildren().addAll(noden);
        }

    }

    public GraphEditorScene(Stage stage, int nodeCount, String mode, double density, int maxWeight) {

        this.nodeCount = nodeCount;
        this.mode = mode;
        this.density = density;
        this.maxWeight = maxWeight;

        // ==== Main layout ====
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // ==== Left panel ====
        VBox leftPanel = new VBox(15);
        leftPanel.setPrefWidth(250);
        leftPanel.setAlignment(Pos.TOP_CENTER);

        ToggleGroup modeGroup = new ToggleGroup();
        RadioButton manualBtn = new RadioButton("Manual");
        RadioButton randomBtn = new RadioButton("Random");
        manualBtn.setToggleGroup(modeGroup);
        randomBtn.setToggleGroup(modeGroup);
        manualBtn.setSelected(mode.equals("Manual"));

        // ==== Manual input panel ====
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
        Button deleteEdgeBtn = new Button("Delete Selected");

        // ==== Random graph panel ====
        Label densityLabel = new Label("Density (%)");
        Slider densitySlider = new Slider(0, 100, density);
        densitySlider.setShowTickLabels(true);
        densitySlider.setShowTickMarks(true);
        densitySlider.setMajorTickUnit(25);
        Label weightRangeLabel = new Label("Weight Range (1-" + maxWeight + ")");
        Slider weightSlider = new Slider(1, maxWeight, maxWeight);
        weightSlider.setShowTickLabels(true);
        weightSlider.setShowTickMarks(true);
        weightSlider.setMajorTickUnit(1);
        Button generateBtn = new Button("Generate Graph");
        VBox randomPanel = new VBox(10, densityLabel, densitySlider, weightRangeLabel, weightSlider, generateBtn);
        randomPanel.visibleProperty().bind(randomBtn.selectedProperty());
        randomPanel.managedProperty().bind(randomBtn.selectedProperty());

        Button validateBtn = new Button("Validate and Continue");
        validateBtn.setPrefWidth(200);
        validateBtn.setOnAction(e -> {

        });

        leftPanel.getChildren().addAll(manualBtn, randomBtn, manualBox, deleteEdgeBtn, randomPanel, validateBtn);

        // ==== Central canvas ====
        Pane canvasPane = new Pane();
        canvasPane.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: black;");
        canvasPane.setPrefSize(500, 400);
        CreateNodes(canvasPane);

        // ==== Main layout assembly ====
        HBox mainBox = new HBox(10, leftPanel, canvasPane);
        mainBox.setAlignment(Pos.CENTER);
        root.setCenter(mainBox);

        // ==== Footer ====
        Label github = new Label("github.com/FaureGalliard");
        Label teacher = new Label("Teacher: Edgard Kenny Venegas Palacios");
        HBox footer = new HBox();
        footer.setPadding(new Insets(10));
        footer.setAlignment(Pos.CENTER);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        footer.getChildren().addAll(teacher, spacer, github);
        root.setBottom(footer);

        // ==== Scene setup ====
        scene = new Scene(root, 800, 450);
        stage.setTitle("Graph Editor - Dijkstra Visualizer");
        stage.setScene(scene);
        stage.show();

        // ==== Event handlers ====
        generateBtn.setOnAction(e -> {
            System.out.println("Generating random graph...");
        });

        addEdgeBtn.setOnAction(e->{
            Nodo noden = new Nodo(40, 100, 100);
            canvasPane.getChildren().addAll(noden);
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