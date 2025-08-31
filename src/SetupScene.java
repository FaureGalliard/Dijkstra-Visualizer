import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SetupScene {
    private Scene scene;

    public SetupScene(Stage stage){

        Label nose = new Label("aca es el setupxd");
        StackPane root = new StackPane(nose);
        scene = new Scene(root,600,600);
    }
    public Scene getScene() {
        return scene;
    }
}
