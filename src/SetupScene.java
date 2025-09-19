import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SetupScene {
    private Scene scene;
    private int n;                // Número de nodos
    private String mode;           // "Manual" o "Aleatorio"
    private double density;        // Grafo aleatorio
    private int maxWeight;

    public SetupScene(Stage stage) {

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Configuración Inicial del Grafo");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        StackPane titleBox = new StackPane(title);
        titleBox.setPadding(new Insets(20, 0, 30, 0));
        root.setTop(titleBox);

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);

        HBox nBox = new HBox(10);
        nBox.setAlignment(Pos.CENTER_LEFT);
        Label nLabel = new Label("Número de nodos (8-16):");
        TextField nField = new TextField();
        nField.setPrefWidth(60);
        nBox.getChildren().addAll(nLabel, nField);

        // RadioButtons Manual / Aleatorio
        ToggleGroup modeGroup = new ToggleGroup();
        RadioButton manualBtn = new RadioButton("Manual");
        RadioButton randomBtn = new RadioButton("Aleatorio");
        manualBtn.setToggleGroup(modeGroup);
        randomBtn.setToggleGroup(modeGroup);
        manualBtn.setSelected(true);

        HBox modeBox = new HBox(20, manualBtn, randomBtn);
        modeBox.setAlignment(Pos.CENTER);

        // Opcional: sliders para densidad y rango de pesos
        Label densityLabel = new Label("Densidad (%):");
        Slider densitySlider = new Slider(0, 100, 50);
        densitySlider.setShowTickLabels(true);
        densitySlider.setShowTickMarks(true);
        densitySlider.setMajorTickUnit(25);
        densitySlider.setMinorTickCount(4);

        Label weightLabel = new Label("Rango de pesos (1-10):");
        Slider weightSlider = new Slider(1, 10, 5);
        weightSlider.setShowTickLabels(true);
        weightSlider.setShowTickMarks(true);
        weightSlider.setMajorTickUnit(1);
        weightSlider.setMinorTickCount(0);

        VBox randomOptions = new VBox(15, densityLabel, densitySlider, weightLabel, weightSlider);
        randomOptions.setAlignment(Pos.CENTER);
        randomOptions.visibleProperty().bind(randomBtn.selectedProperty());
        randomOptions.managedProperty().bind(randomBtn.selectedProperty());

        // Botón continuar
        Button continueBtn = new Button("Continuar");
        continueBtn.setPrefWidth(140);
        continueBtn.setOnAction(e -> {
            String nText = nField.getText();
            try {
                n = Integer.parseInt(nField.getText());
                if (n < 8 || n > 16) throw new Exception();

                mode = manualBtn.isSelected() ? "Manual" : "Aleatorio";
                density = densitySlider.getValue();
                maxWeight = (int) weightSlider.getValue();

                // Pasar al graph editor
                GraphEditorScene graphScene = new GraphEditorScene(stage, n, mode, density, maxWeight);
                stage.setScene(graphScene.getScene());

            } catch (Exception a) {
                showAlert("Error", "Ingrese valores válidos");
            }
        });

        centerBox.getChildren().addAll(nBox, modeBox, randomOptions, continueBtn);
        root.setCenter(centerBox);

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

        // ==== Escena ====
         scene = new Scene(root, 800, 450);
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
