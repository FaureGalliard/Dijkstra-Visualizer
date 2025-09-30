package com.fauregalliard.dijsktravisualizer.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SetupScene {
    private static final int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 450, SLIDER_WIDTH = 300, TEXT_FIELD_WIDTH = 60, BUTTON_WIDTH = 140;
    private static final double SPACING = 20.0, PADDING = 20.0;

    private int nodeCount = 20;
    private double density = 50;
    private int maxWeight = 100;
    private final Scene scene;

    public SetupScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        root.setPadding(new Insets(PADDING));

        root.setTop(createTitlePane());
        root.setCenter(createCenterPane(stage));
        root.setBottom(UIUtils.createFooter());

        scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
    }

    private Pane createTitlePane() {
        Label title = new Label("Graph initial configuration");
        title.getStyleClass().add("title-label");
        StackPane titleBox = new StackPane(title);
        titleBox.setPadding(new Insets(PADDING, 0, PADDING + 10, 0));
        return titleBox;
    }

    private VBox createCenterPane(Stage stage) {
        VBox centerBox = new VBox(SPACING);
        centerBox.getStyleClass().add("vbox");
        centerBox.setAlignment(Pos.CENTER);

        HBox[] sliderBoxes = {
                createSliderBox("Number of nodes (20-100):", 20, 100, 20, v -> nodeCount = v),
                createSliderBox("Density (1-100%):", 1, 100, 50, v -> density = v),
                createSliderBox("Range of weight (1-200):", 1, 200, 100, v -> maxWeight = v)
        };

        Button continueButton = new Button("Continue");
        continueButton.getStyleClass().add("button");
        continueButton.setPrefWidth(BUTTON_WIDTH);
        continueButton.setOnAction(e -> {
            if (nodeCount < 20 || nodeCount > 100 || density < 0 || density > 100 || maxWeight < 1 || maxWeight > 200) {
                UIUtils.showAlert("Error", "out of range values");
                return;
            }
            stage.setScene(new GraphEditorScene(stage, nodeCount, "Manual", density, maxWeight).getScene());
        });

        centerBox.getChildren().addAll(sliderBoxes);
        centerBox.getChildren().add(continueButton);
        return centerBox;
    }

    private HBox createSliderBox(String labelText, double min, double max, double initial, java.util.function.Consumer<Integer> updateVar) {
        Label label = new Label(labelText);
        label.getStyleClass().add("normal-label");

        Slider slider = new Slider(min, max, initial);
        slider.getStyleClass().add("slider");
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit((max - min) / 4);
        slider.setMinorTickCount(4);
        slider.setPrefWidth(SLIDER_WIDTH);

        TextField field = new TextField(String.valueOf((int) initial));
        field.getStyleClass().add("text-field");
        field.setPrefWidth(TEXT_FIELD_WIDTH);

        slider.valueProperty().addListener((obs, old, newVal) -> {
            int value = (int) Math.round(newVal.doubleValue());
            field.setText(String.valueOf(value));
            updateVar.accept(value);
        });

        field.textProperty().addListener((obs, old, newVal) -> {
            try {
                int value = Integer.parseInt(newVal);
                slider.setValue(value);
                updateVar.accept(value);
            } catch (NumberFormatException e) {
                field.setText(String.valueOf((int) slider.getValue()));
            }
        });

        HBox box = new HBox(SPACING, label, slider, field);
        box.getStyleClass().add("hbox");
        box.setAlignment(Pos.CENTER);
        return box;
    }

    public Scene getScene() {
        return scene;
    }
}