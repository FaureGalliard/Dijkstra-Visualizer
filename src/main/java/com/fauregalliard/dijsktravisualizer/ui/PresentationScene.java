package com.fauregalliard.dijsktravisualizer.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PresentationScene {
    private final Scene scene;
    private static final double BASE_WIDTH = 800, BASE_HEIGHT = 450;

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
            grid.add(nombre, i % 3, i / 3);
        }

        Button nextBtn = new Button("Start");
        nextBtn.getStyleClass().add("button");
        nextBtn.setPrefWidth(140);
        nextBtn.setOnAction(e -> stage.setScene(new SetupScene(stage).getScene()));

        VBox centerBox = new VBox(30, nextBtn, grid);
        centerBox.setAlignment(Pos.CENTER);
        root.setCenter(centerBox);

        root.setBottom(UIUtils.createFooter());

        scene = new Scene(root, BASE_WIDTH, BASE_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
    }

    public Scene getScene() {
        return scene;
    }
}