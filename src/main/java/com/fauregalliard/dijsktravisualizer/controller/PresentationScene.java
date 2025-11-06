package com.fauregalliard.dijsktravisualizer.controller;

import com.fauregalliard.dijsktravisualizer.util.Util;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PresentationScene {

    private final Scene scene;

    public PresentationScene(Stage stage) {
        BorderPane root = new BorderPane();

        Label title = new Label("Dijkstra Visualizer");
        title.getStyleClass().add("title"); // Add title style class

        StackPane titleBox = new StackPane(title);
        titleBox.setPadding(new Insets(30, 0, 20, 0));

        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);
        for (int i = 0; i < Util.crewMembers.length; i++) {
            Label nombre = new Label(Util.crewMembers[i]);
            grid.add(nombre, i % 3, i / 3);
        }

        Button nextBtn = Util.createButton("Continue", 140);
        nextBtn.getStyleClass().add("main");
        nextBtn.setOnAction(e -> stage.setScene(new SelectCreationType(stage).getScene()));

        VBox centerBox = new VBox(30, nextBtn, grid);
        centerBox.setAlignment(Pos.CENTER);

        root.setTop(titleBox);
        root.setCenter(centerBox);
        root.setBottom(Util.createFooter());

        scene = new Scene(root, Util.BASE_WIDTH, Util.BASE_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm()); // Add stylesheet
    }

    public Scene getScene() {
        return scene;
    }
}