import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class PresentationScene {
    private Scene scene;
    private final double BASE_WIDTH = 800;
    private final double BASE_HEIGHT = 450;
    private static final String FONT_NAME = "Courier Prime";

    public PresentationScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setPadding(new Insets(20));

        Label title = new Label("Dijkstra Visualizer");
        title.getStyleClass().add("title-label");
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
            nombre.getStyleClass().add("normal-label");
            int row = i / 3;
            int col = i % 3;
            grid.add(nombre, col, row);
        }

        Button nextBtn = new Button("Comenzar");
        nextBtn.getStyleClass().add("button");
        nextBtn.setPrefWidth(140);
        nextBtn.setOnAction(e -> stage.setScene(new SetupScene(stage).getScene()));

        VBox centerBox = new VBox(30, nextBtn, grid);
        centerBox.setAlignment(Pos.CENTER);
        root.setCenter(centerBox);

        Label github = new Label("github.com/FaureGalliard");
        github.getStyleClass().add("secondary-label");

        Label teacher = new Label("Profesor: Edgard Kenny Venegas Palacios");
        teacher.getStyleClass().add("secondary-label");

        HBox footer = new HBox();
        footer.getStyleClass().add("hbox");
        footer.setPadding(new Insets(5));
        footer.setAlignment(Pos.CENTER);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        footer.getChildren().addAll(teacher, spacer, github);
        root.setBottom(footer);

        scene = new Scene(root, 800, 450);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    }

    public Scene getScene() {
        return scene;
    }
}