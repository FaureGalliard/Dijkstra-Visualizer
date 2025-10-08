package com.fauregalliard.dijsktravisualizer;
import com.fauregalliard.dijsktravisualizer.controller.PresentationScene;
import javafx.application.Application;
import javafx.stage.Stage;
import com.fauregalliard.dijsktravisualizer.model.*;

import javax.print.attribute.PrintRequestAttributeSet;

public class Main extends Application {

    @Override
    public void start(Stage stage){

        PresentationScene presentationScene = new PresentationScene(stage);

        stage.setScene(presentationScene.getScene());
        stage.setTitle("FaureGalliard - Dijsktra Visualizer");
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}
