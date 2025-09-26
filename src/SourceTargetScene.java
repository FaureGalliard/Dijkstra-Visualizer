import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SourceTargetScene {
    private final Grafo grafo;
    private final Scene scene;
    private final Stage stage;

    public SourceTargetScene(Stage stage, Grafo grafo) {
        this.stage = stage;
        this.grafo = grafo;

        // Main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Canvas for rendering the graph
        Pane canvasPane = new Pane();
        canvasPane.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: black;");
        canvasPane.setPrefSize(600, 500);

        // Render the graph
        grafo.render(canvasPane);

        // Back button to return to editor
        Button backBtn = new Button("Back to Editor");
        backBtn.setPrefWidth(200);
        backBtn.setOnAction(e -> {
            GraphEditorScene editor = new GraphEditorScene(stage, grafo.getNodos().size(), "manual", 50.0, 10);
            stage.setScene(editor.getScene());
        });

        // Layout assembly
        root.setCenter(canvasPane);
        root.setBottom(backBtn);
        BorderPane.setAlignment(backBtn, Pos.CENTER);
        BorderPane.setMargin(backBtn, new Insets(10));

        // Scene setup
        this.scene = new Scene(root, 800, 600);
        stage.setTitle("Source-Target Selector - Dijkstra Visualizer");
    }

    public Scene getScene() {
        return scene;
    }
}