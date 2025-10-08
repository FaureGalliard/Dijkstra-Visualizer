package com.fauregalliard.dijsktravisualizer.view;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class NodeView extends Group {

    public final Circle circle;
    private final Text label;
    private double offsetX;
    private double offsetY;
    private final String name;
    private static final double RADIUS = 10;

    public NodeView(double centerX, double centerY, String name) {
        this.name = name;

        circle = new Circle(centerX, centerY, RADIUS, Color.web("#8f8f8f")); // Default node color
        label = new Text(name);
        label.setMouseTransparent(true); // Prevent interference with drag
        label.setFill(Color.WHITE);
        label.setX(centerX - label.getLayoutBounds().getWidth() / 2);
        label.setY(centerY + label.getLayoutBounds().getHeight() / 4);

        circle.centerXProperty().addListener((obs, old, newVal) ->
                label.setX(newVal.doubleValue() - label.getLayoutBounds().getWidth() / 2));
        circle.centerYProperty().addListener((obs, old, newVal) ->
                label.setY(newVal.doubleValue() + label.getLayoutBounds().getHeight() / 4));

        enableDragging();

        getChildren().addAll(circle, label);
    }

    private void enableDragging() {
        circle.setOnMousePressed(e -> {
            offsetX = e.getSceneX() - circle.getCenterX();
            offsetY = e.getSceneY() - circle.getCenterY();
        });

        circle.setOnMouseDragged(e -> {
            circle.setCenterX(e.getSceneX() - offsetX);
            circle.setCenterY(e.getSceneY() - offsetY);
        });
    }

    public String getName() {
        return name;
    }

    public Circle getCircle() {
        return circle;
    }

    public Text getLabel() {
        return label;
    }
}