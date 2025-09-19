import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PresentationScene {

    private Scene scene; // variable de instancia correcta
    private final double BASE_WIDTH = 800;
    private final double BASE_HEIGHT = 450;

    public PresentationScene(Stage stage) {

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Dijkstra Visualizer");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");
        StackPane titleBox = new StackPane(title);
        titleBox.setPadding(new Insets(30, 0, 20, 0));
        root.setTop(titleBox);

        String[] crewMembers = {
                "Crispin Valdivia Angel Gabriel",
                "Chipoco Cordova Sergio Nicolas",
                "Flores Antezana Fabrizzio Anggelo",
                "Huarcaya Mejicano Angeles Lucero",
                "Flores Rios Juan Diego"
        };

        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        for (int i = 0; i < crewMembers.length; i++) {
            Label nombre = new Label(crewMembers[i]);
            int row = i / 3;
            int col = i % 3;
            grid.add(nombre, col, row);
        }

        // ==== Botón Next ====
        Button nextBtn = new Button("Comenzar");
        nextBtn.setPrefWidth(140);
        nextBtn.setOnAction(e -> {

             stage.setScene(new SetupScene(stage).getScene());
        });

        VBox centerBox = new VBox(30, nextBtn, grid);
        centerBox.setAlignment(Pos.CENTER);
        root.setCenter(centerBox);

        Label github = new Label("github.com/FaureGalliard");
        Label teacher = new Label("Profesor: Edgard Kenny Venegas Palacios");

        HBox footer = new HBox();
        footer.setPadding(new Insets(5));
        footer.setAlignment(Pos.CENTER);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        footer.getChildren().addAll(teacher, spacer, github);
        root.setBottom(footer);
        scene = new Scene(root, 800, 450);
    }

    public Scene getScene() {
        return scene;
    }
}
