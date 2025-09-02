import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SetupScene {
    private Scene scene;

    public SetupScene(Stage stage) {

        Label label = new Label("Ingresa el numero de nodos:");
        TextField enternumber = new TextField();
        enternumber.setPrefSize(200, 30);

        CheckBox numerorand = new CheckBox("Usar número aleatorio");

        numerorand.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            enternumber.setDisable(isSelected);
        });

        Pane root = new Pane(); // 10px de espacio entre elementos
        root.getChildren().addAll(label, enternumber, numerorand);

        scene = new Scene(root, 800, 450);
    }

    public Scene getScene() {
        return scene;
    }
}
