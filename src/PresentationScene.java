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
        root.setStyle("-fx-background-color: #20232a;");
        root.setPadding(new Insets(20));

        Label title = new Label("Dijkstra Visualizer");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #ff914d; -fx-font-family: '" + FONT_NAME + "';");
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
            nombre.setTextFill(Color.web("#ffffff"));
            nombre.setFont(Font.font(FONT_NAME, 14));
            int row = i / 3;
            int col = i % 3;
            grid.add(nombre, col, row);
        }

        Button nextBtn = new Button("Comenzar");
        nextBtn.setPrefWidth(140);
        nextBtn.setStyle("-fx-background-color: #2d2d35; -fx-text-fill: #ffffff; -fx-font-family: '" + FONT_NAME + "'; -fx-font-size: 14;");
        nextBtn.setOnAction(e -> stage.setScene(new SetupScene(stage).getScene()));

        VBox centerBox = new VBox(30, nextBtn, grid);
        centerBox.setAlignment(Pos.CENTER);
        root.setCenter(centerBox);

        Label github = new Label("github.com/FaureGalliard");
        github.setTextFill(Color.web("#8f8f8f"));
        github.setFont(Font.font(FONT_NAME, 12));

        Label teacher = new Label("Profesor: Edgard Kenny Venegas Palacios");
        teacher.setTextFill(Color.web("#8f8f8f"));
        teacher.setFont(Font.font(FONT_NAME, 12));

        HBox footer = new HBox();
        footer.setPadding(new Insets(5));
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-background-color: #20232a;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        footer.getChildren().addAll(teacher, spacer, github);
        root.setBottom(footer);
        scene = new Scene(root, 1024, 576);
    }

    public Scene getScene() {
        return scene;
    }
}