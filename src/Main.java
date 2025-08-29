import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label("¡Hola JavaFX!");
        Button boton = new Button("Click aquí");
        Slider slider = new Slider(0, 100, 50);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(25);
        slider.setMinorTickCount(4);
        slider.setBlockIncrement(5);

        VBox root = new VBox(20, label, boton, slider);
        root.setStyle("-fx-padding: 20;"); // padding general

        Scene scene = new Scene(root, 400, 300);

        // Integrar el archivo CSS externo
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        stage.setTitle("JavaFX con CSS");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
