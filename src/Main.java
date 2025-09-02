import javafx.application.Application;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Set;

public class Main extends Application {

    @Override
    public void start(Stage stage){

        PresentationScene presentation = new PresentationScene(stage);
        SetupScene setupScene = new SetupScene(stage);
        stage.setScene(presentation.getScene());
        stage.setTitle("FaureGalliard - Dijkstra Visualizer");
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}
