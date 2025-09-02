import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PresentationScene {

    private Scene scene;
    private final double BASE_WIDTH = 800;
    private final double BASE_HEIGHT = 450;

    public PresentationScene(Stage stage) {

        // 1️⃣ Layout principal
        BorderPane root = new BorderPane();

        // Título
        Label title = new Label("Dijkstra Visualizer");
        StackPane titleBox = new StackPane(title);
        titleBox.setPadding(new Insets(50, 0, 10, 0));

        // Grid de miembros
        String[] crewMembers = {
                "Crispin Valdivia Angel Gabriel",
                "Chipoco Cordova Sergio Nicolas",
                "Flores Antezana Fabrizzio Anggelo",
                "Huarcaya Mejicano Angeles Lucero",
                "Flores Rios Juan Diego"
        };

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(20);

        for (int i = 0; i < crewMembers.length; i++) {
            Label nombre = new Label(crewMembers[i]);
            StackPane tarjeta = new StackPane(nombre);
            tarjeta.setPrefSize(150, 30);

            int fila = i / 3;
            int col = i % 3;
            grid.add(tarjeta, col, fila);
        }

        // Botón Next
        Button nextBtn = new Button("Comenzar");
        nextBtn.setOnAction(e -> {
            SetupScene setupScene = new SetupScene(stage);
            stage.setScene(setupScene.getScene());
        });

        VBox centerBox = new VBox(40, nextBtn, grid);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(70, 0, 20, 0));

        // Footer
        Label github = new Label("github.com/FaureGalliard");
        Label teacher = new Label("Profesor: Edgard Kenny Venegas Palacios");
        BorderPane footer = new BorderPane();
        footer.setLeft(teacher);
        footer.setRight(github);
        BorderPane.setAlignment(teacher, Pos.CENTER_LEFT);
        BorderPane.setAlignment(github, Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10, 10, 10, 10));

        // Set layouts en root
        root.setTop(titleBox);
        root.setCenter(centerBox);
        root.setBottom(footer);

        // 2️⃣ Group para aplicar escala
        Group scalableGroup = new Group(root);

        // 3️⃣ Escena base
        scene = new Scene(scalableGroup, BASE_WIDTH, BASE_HEIGHT);

        // 4️⃣ Escalar todo según ventana
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            double scale = Math.min(newVal.doubleValue() / BASE_WIDTH, scene.getHeight() / BASE_HEIGHT);
            scalableGroup.setScaleX(scale);
            scalableGroup.setScaleY(scale);
        });

        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            double scale = Math.min(scene.getWidth() / BASE_WIDTH, newVal.doubleValue() / BASE_HEIGHT);
            scalableGroup.setScaleX(scale);
            scalableGroup.setScaleY(scale);
        });
    }

    public Scene getScene() {
        return scene;
    }
}
