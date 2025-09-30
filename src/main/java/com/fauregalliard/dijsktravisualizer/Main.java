package com.fauregalliard.dijsktravisualizer;//com.fauregalliard.dijsktravisualizer.Main.java
import com.fauregalliard.dijsktravisualizer.ui.PresentationScene;
import com.fauregalliard.dijsktravisualizer.ui.SetupScene;
import javafx.application.Application;
import javafx.stage.Stage;

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
