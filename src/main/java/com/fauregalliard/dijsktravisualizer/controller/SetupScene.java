package com.fauregalliard.dijsktravisualizer.controller;

import com.fauregalliard.dijsktravisualizer.util.Util;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class SetupScene {

    private final Scene scene;
    private int nodeCount = 20;
    private int density = 2;
    private int maxWeight = 100;

    public SetupScene(Stage stage) {
        BorderPane root = new BorderPane();

        Label title = new Label("Graph initial configuration");
        title.getStyleClass().add("title"); // Add title style class
        StackPane titleBox = new StackPane(title);
        titleBox.setPadding(new Insets(20, 0, 30, 0));

        GridPane centerGrid = new GridPane();
        centerGrid.setAlignment(Pos.CENTER);
        centerGrid.setHgap(10);
        centerGrid.setVgap(15);
        centerGrid.setPadding(new Insets(10));

        String[] labels = {
                "Number of nodes (20-100):",
                "Density (1-100%):",
                "Range of weight (1-200):"
        };
        double[] mins = {20, 1, 1};
        double[] maxes = {100, 100, 200};
        double[] initials = {20, 2, 100};
        Consumer<Integer>[] updaters = new Consumer[] {
                v -> nodeCount = (int) v,
                v -> density = (int) v,
                v -> maxWeight = (int) v
        };

        for (int i = 0; i < labels.length; i++) {
            Object[] components = Util.createSliderBox(labels[i], mins[i], maxes[i], initials[i], updaters[i]);
            centerGrid.add((Label) components[0], 0, i);
            centerGrid.add((Slider) components[1], 1, i);
            centerGrid.add((TextField) components[2], 2, i);
        }

        Button continueButton = Util.createButton("Continue", Util.BUTTON_WIDTH);
        continueButton.getStyleClass().add("main");
        continueButton.setOnAction(e -> {
            if (nodeCount < 20 || nodeCount > 100 || density < 0 || density > 100 || maxWeight < 1 || maxWeight > 200) {
                Util.showAlert("Error", "Out of range values");
                return;
            }
            stage.setScene(new GraphEditorScene(stage, nodeCount, "Manual", density, maxWeight).getScene());
        });

        centerGrid.add(continueButton, 0, 3, 3, 1);
        GridPane.setHalignment(continueButton, HPos.CENTER);

        root.setTop(titleBox);
        root.setBottom(Util.createFooter());
        root.setCenter(centerGrid);

        scene = new Scene(root, Util.BASE_WIDTH, Util.BASE_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm()); // Add stylesheet
    }

    public Scene getScene() {
        return scene;
    }
}