import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class GraphEditorScene {

    private int n;
    private String mode;
    private double density;
    private int maxWeight;
    private Scene scene;
    private ObservableList<Edge> edges = FXCollections.observableArrayList();

    public GraphEditorScene(Stage stage, int n, String mode, double density, int maxWeight) {
        this.n = n;
        this.mode = mode;
        this.density = density;
        this.maxWeight = maxWeight;

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // ==== Panel izquierdo ====
        VBox leftPanel = new VBox(15);
        leftPanel.setPrefWidth(250);
        leftPanel.setAlignment(Pos.TOP_CENTER);

        ToggleGroup modeGroup = new ToggleGroup();
        RadioButton manualBtn = new RadioButton("Manual");
        RadioButton randomBtn = new RadioButton("Aleatorio");
        manualBtn.setToggleGroup(modeGroup);
        randomBtn.setToggleGroup(modeGroup);
        manualBtn.setSelected(mode.equals("Manual"));

        leftPanel.getChildren().addAll(manualBtn, randomBtn);

        // ----- Panel manual -----
        Label uLabel = new Label("Nodo u:");
        TextField uField = new TextField(); uField.setPrefWidth(60);
        Label vLabel = new Label("Nodo v:");
        TextField vField = new TextField(); vField.setPrefWidth(60);
        Label wLabel = new Label("Peso w:");
        TextField wField = new TextField(); wField.setPrefWidth(60);
        Button addEdgeBtn = new Button("Agregar arista");

        HBox edgeInput = new HBox(5, uLabel, uField, vLabel, vField, wLabel, wField, addEdgeBtn);
        edgeInput.setAlignment(Pos.CENTER);
        VBox manualBox = new VBox(10, edgeInput);

        TableView<Edge> table = new TableView<>(edges);
        table.setPrefHeight(200);
        TableColumn<Edge, String> fromCol = new TableColumn<>("u");
        fromCol.setCellValueFactory(data -> data.getValue().uProperty());
        TableColumn<Edge, String> toCol = new TableColumn<>("v");
        toCol.setCellValueFactory(data -> data.getValue().vProperty());
        TableColumn<Edge, String> weightCol = new TableColumn<>("w");
        weightCol.setCellValueFactory(data -> data.getValue().wProperty());
        table.getColumns().addAll(fromCol, toCol, weightCol);

        Button deleteEdgeBtn = new Button("Eliminar seleccionada");

        VBox manualPanel = new VBox(10, manualBox, table, deleteEdgeBtn);
        manualPanel.visibleProperty().bind(manualBtn.selectedProperty());
        manualPanel.managedProperty().bind(manualBtn.selectedProperty());

        // ----- Panel aleatorio -----
        Label densityLabel = new Label("Densidad (%)");
        Slider densitySlider = new Slider(0, 100, density);
        densitySlider.setShowTickLabels(true);
        densitySlider.setShowTickMarks(true);
        densitySlider.setMajorTickUnit(25);

        Label weightRangeLabel = new Label("Rango de pesos (1-" + maxWeight + ")");
        Slider weightSlider = new Slider(1, maxWeight, maxWeight);
        weightSlider.setShowTickLabels(true);
        weightSlider.setShowTickMarks(true);
        weightSlider.setMajorTickUnit(1);

        Button generateBtn = new Button("Generar grafo");
        VBox randomPanel = new VBox(10, densityLabel, densitySlider, weightRangeLabel, weightSlider, generateBtn);
        randomPanel.visibleProperty().bind(randomBtn.selectedProperty());
        randomPanel.managedProperty().bind(randomBtn.selectedProperty());

        // Botón validar y continuar
        Button validateBtn = new Button("Validar y continuar");
        validateBtn.setPrefWidth(200);
        validateBtn.setOnAction(e -> {
            System.out.println("Validar y continuar...");
            // Aquí se puede pasar a SourceTargetScene
        });

        leftPanel.getChildren().addAll(manualPanel, randomPanel, validateBtn);

        // ==== Panel central (placeholder) ====
        Pane canvasPane = new Pane();
        canvasPane.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: black;");
        canvasPane.setPrefSize(500, 400);

        // ==== Layout principal ====
        HBox mainBox = new HBox(10, leftPanel, canvasPane);
        mainBox.setAlignment(Pos.CENTER);
        root.setCenter(mainBox);

        // ==== Footer ====
        Label github = new Label("github.com/FaureGalliard");
        Label teacher = new Label("Profesor: Edgard Kenny Venegas Palacios");
        HBox footer = new HBox();
        footer.setPadding(new Insets(10));
        footer.setAlignment(Pos.CENTER);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        footer.getChildren().addAll(teacher, spacer, github);
        root.setBottom(footer);

        scene = new Scene(root, 800, 450);
        stage.setTitle("Graph Editor - Dijkstra Visualizer");
        stage.setScene(scene);
        stage.show();

        // ===== EVENTOS =====
        addEdgeBtn.setOnAction(e -> {
            try {
                int u = Integer.parseInt(uField.getText());
                int v = Integer.parseInt(vField.getText());
                int w = Integer.parseInt(wField.getText());
                if (w <= 0) throw new Exception("Peso debe ser > 0");
                edges.add(new Edge(u, v, w));
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        deleteEdgeBtn.setOnAction(e -> {
            Edge selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                edges.remove(selected);
            }
        });

        generateBtn.setOnAction(e -> {
            System.out.println("Generar grafo aleatorio...");
        });
    }

    private void showAlert(String title, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Clase simple para aristas
    public static class Edge {
        private final javafx.beans.property.SimpleStringProperty u;
        private final javafx.beans.property.SimpleStringProperty v;
        private final javafx.beans.property.SimpleStringProperty w;

        public Edge(int u, int v, int w){
            this.u = new javafx.beans.property.SimpleStringProperty(String.valueOf(u));
            this.v = new javafx.beans.property.SimpleStringProperty(String.valueOf(v));
            this.w = new javafx.beans.property.SimpleStringProperty(String.valueOf(w));
        }

        public int getU() { return Integer.parseInt(u.get()); }
        public int getV() { return Integer.parseInt(v.get()); }
        public int getW() { return Integer.parseInt(w.get()); }

        public javafx.beans.property.StringProperty uProperty() { return u; }
        public javafx.beans.property.StringProperty vProperty() { return v; }
        public javafx.beans.property.StringProperty wProperty() { return w; }
    }

    public Scene getScene(){ return scene; }
}
