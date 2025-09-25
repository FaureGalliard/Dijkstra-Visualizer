import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SetupScene {
    private Scene scene;
    private int n = 20; // Número de nodos
    private double density = 50; // Densidad del grafo
    private int maxWeight = 100; // Rango máximo de pesos

    public SetupScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Título
        Label title = new Label("Configuración Inicial del Grafo");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        StackPane titleBox = new StackPane(title);
        titleBox.setPadding(new Insets(20, 0, 30, 0));
        root.setTop(titleBox);

        // Panel central
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);

        // Crear sliders y campos de texto
        HBox nBox = createSliderBox("Número de nodos (20-100):", 20, 100, 20, value -> n = value);
        HBox densityBox = createSliderBox("Densidad (0-100%):", 0, 100, 50, value -> density = value);
        HBox weightBox = createSliderBox("Rango de pesos (1-200):", 1, 200, 100, value -> maxWeight = value);

        // Botón continuar
        Button continueBtn = new Button("Continuar");
        continueBtn.setPrefWidth(140);
        continueBtn.setOnAction(e -> {
            if (n < 20 || n > 100 || density < 0 || density > 100 || maxWeight < 1 || maxWeight > 200) {
                showAlert("Error", "Valores fuera de rango");
                return;
            }
            GraphEditorScene graphScene = new GraphEditorScene(stage, n, "Manual", density, maxWeight);
            stage.setScene(graphScene.getScene());
        });

        centerBox.getChildren().addAll(nBox, densityBox, weightBox, continueBtn);
        root.setCenter(centerBox);

        // Footer
        Label github = new Label("github.com/FaureGalliard");
        Label teacher = new Label("Teacher: Edgard Kenny Venegas Palacios");
        HBox footer = new HBox(10, teacher, new Region(), github);
        footer.setPadding(new Insets(10));
        footer.setAlignment(Pos.CENTER);
        HBox.setHgrow(footer.getChildren().get(1), Priority.ALWAYS);
        root.setBottom(footer);

        // Escena
        scene = new Scene(root, 800, 450);
    }

    private HBox createSliderBox(String labelText, double min, double max, double initial, java.util.function.Consumer<Integer> updateVar) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER); // Centrar los elementos horizontalmente

        Label label = new Label(labelText);
        Slider slider = new Slider(min, max, initial);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit((max - min) / 4);
        slider.setMinorTickCount(4);
        slider.setSnapToTicks(false);
        slider.setPrefWidth(300); // Ancho fijo para sliders
        TextField field = new TextField(String.valueOf((int) initial));
        field.setPrefWidth(60);

        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int value = (int) Math.round(newVal.doubleValue());
            field.setText(String.valueOf(value));
            updateVar.accept(value);
        });
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            //por si un gracioso pone algo que no es entero
            try {
                int value = Integer.parseInt(newVal);
                if (value >= min && value <= max) {
                    slider.setValue(value);
                    updateVar.accept(value);
                } else {//en vez de crashear ignora el valor y pone el que ya estaba
                    field.setText(String.valueOf((int) slider.getValue()));
                }
            } catch (NumberFormatException e) {
                field.setText(String.valueOf((int) slider.getValue()));
            }
        });

        box.getChildren().addAll(label, slider, field);
        return box;
    }

    //verificar si los valores estan dentro de los rangos
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