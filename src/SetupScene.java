import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SetupScene {
    private static final int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 450, SLIDER_WIDTH = 300, TEXT_FIELD_WIDTH = 60, BUTTON_WIDTH = 140;
    private static final double SPACING = 20.0, PADDING = 20.0;
    private static final String FONT_NAME = "Courier Prime";

    private int nodeCount = 20;
    private double density = 50;
    private int maxWeight = 100;
    private final Scene scene;

    public SetupScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #20232a;");
        root.setPadding(new Insets(PADDING));

        root.setTop(createTitlePane());
        root.setCenter(createCenterPane(stage));
        root.setBottom(createFooterPane());

        scene = new Scene(root,  1024, 576);
    }

    private Pane createTitlePane() {
        Label title = new Label("Configuración Inicial del Grafo");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #ff914d; -fx-font-family: '" + FONT_NAME + "';");
        StackPane titleBox = new StackPane(title);
        titleBox.setPadding(new Insets(PADDING, 0, PADDING + 10, 0));
        return titleBox;
    }

    private VBox createCenterPane(Stage stage) {
        VBox centerBox = new VBox(SPACING);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setStyle("-fx-background-color: #20232a;");

        HBox nodeBox = createSliderBox("Número de nodos (20-100):", 20, 100, 20, v -> nodeCount = v);
        HBox densityBox = createSliderBox("Densidad (1-100%):", 1, 100, 50, v -> density = v);
        HBox weightBox = createSliderBox("Rango de pesos (1-200):", 1, 200, 100, v -> maxWeight = v);

        Button continueButton = new Button("Continuar");
        continueButton.setPrefWidth(BUTTON_WIDTH);
        continueButton.setStyle("-fx-background-color: #2d2d35; -fx-text-fill: #ffffff; -fx-font-family: '" + FONT_NAME + "'; -fx-font-size: 14;");
        continueButton.setOnAction(e -> {
            if (nodeCount < 20 || nodeCount > 100 || density < 0 || density > 100 || maxWeight < 1 || maxWeight > 200) {
                showAlert("Error", "Valores fuera de rango");
                return;
            }
            stage.setScene(new GraphEditorScene(stage, nodeCount, "Manual", density, maxWeight).getScene());
        });

        centerBox.getChildren().addAll(nodeBox, densityBox, weightBox, continueButton);
        return centerBox;
    }

    private HBox createFooterPane() {
        Label teacher = new Label("Teacher: Edgard Kenny Venegas Palacios");
        teacher.setTextFill(Color.web("#8f8f8f"));
        teacher.setFont(Font.font(FONT_NAME, 12));

        Label github = new Label("github.com/FaureGalliard");
        github.setTextFill(Color.web("#8f8f8f"));
        github.setFont(Font.font(FONT_NAME, 12));

        HBox footer = new HBox(SPACING, teacher, new Region(), github);
        footer.setPadding(new Insets(PADDING));
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-background-color: #20232a;");
        HBox.setHgrow(footer.getChildren().get(1), Priority.ALWAYS);
        return footer;
    }

    private HBox createSliderBox(String labelText, double min, double max, double initial, java.util.function.Consumer<Integer> updateVar) {
        Label label = new Label(labelText);
        label.setTextFill(Color.web("#ff914d"));
        label.setFont(Font.font(FONT_NAME, 12));

        HBox box = new HBox(SPACING, label);
        box.setAlignment(Pos.CENTER);

        Slider slider = new Slider(min, max, initial);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit((max - min) / 4);
        slider.setMinorTickCount(4);
        slider.setPrefWidth(SLIDER_WIDTH);
        slider.setStyle("-fx-font-family: '" + FONT_NAME + "';");

        TextField field = new TextField(String.valueOf((int) initial));
        field.setPrefWidth(TEXT_FIELD_WIDTH);
        field.setStyle("-fx-background-color: #2d2d35; -fx-text-fill: #ffffff; -fx-prompt-text-fill: #8f8f8f; -fx-font-family: '" + FONT_NAME + "'; -fx-font-size: 12;");

        slider.valueProperty().addListener((obs, old, newVal) -> {
            int value = (int) Math.round(newVal.doubleValue());
            field.setText(String.valueOf(value));
            updateVar.accept(value);
        });

        field.textProperty().addListener((obs, old, newVal) -> {
            try {
                int value = Integer.parseInt(newVal);
                slider.setValue(value);
                updateVar.accept(value);
            } catch (NumberFormatException e) {
                field.setText(String.valueOf((int) slider.getValue()));
            }
        });

        box.getChildren().addAll(slider, field);
        return box;
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