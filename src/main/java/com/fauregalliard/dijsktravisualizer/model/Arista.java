package com.fauregalliard.dijsktravisualizer.model;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Arista {
    private final Nodo nodoU;
    private final Nodo nodoV;
    private final int peso;
    private final Line line;
    private final Text pesoText;
    private final Group edgeGroup;
    private static final String FONT_NAME = "Courier Prime";

    public Arista(Nodo nodoU, Nodo nodoV, int peso) {
        this.nodoU = nodoU;
        this.nodoV = nodoV;
        this.peso = peso;
        this.line = createLine();
        this.pesoText = createPesoText();
        this.edgeGroup = new Group(line, createArrowLine(-15), createArrowLine(15));
    }

    private Line createLine() {
        Line line = new Line();
        line.setStroke(Color.web("#f5f5f5"));
        line.setStrokeWidth(2);
        line.startXProperty().bind(nodoU.centerXProperty());
        line.startYProperty().bind(nodoU.centerYProperty());
        line.endXProperty().bind(nodoV.centerXProperty());
        line.endYProperty().bind(nodoV.centerYProperty());
        return line;
    }

    private Line createArrowLine(double angleOffset) {
        Line arrow = new Line();
        arrow.setStroke(Color.web("#f5f5f5"));
        arrow.setStrokeWidth(2);

        DoubleBinding startX = new DoubleBinding() {
            { bind(nodoU.centerXProperty(), nodoV.centerXProperty(), nodoU.centerYProperty(), nodoV.centerYProperty()); }
            @Override
            protected double computeValue() {
                double dx = nodoV.getCenterX() - nodoU.getCenterX();
                double dy = nodoV.getCenterY() - nodoU.getCenterY();
                double length = Math.sqrt(dx * dx + dy * dy);
                return length == 0 ? nodoU.getCenterX() :
                        nodoU.getCenterX() + dx * (length - nodoV.getRadius()) * 0.85 / length;
            }
        };

        DoubleBinding startY = new DoubleBinding() {
            { bind(nodoU.centerYProperty(), nodoV.centerYProperty(), nodoU.centerXProperty(), nodoV.centerXProperty()); }
            @Override
            protected double computeValue() {
                double dx = nodoV.getCenterX() - nodoU.getCenterX();
                double dy = nodoV.getCenterY() - nodoU.getCenterY();
                double length = Math.sqrt(dx * dx + dy * dy);
                return length == 0 ? nodoU.getCenterY() :
                        nodoU.getCenterY() + dy * (length - nodoV.getRadius()) * 0.85 / length;
            }
        };

        DoubleBinding endX = new DoubleBinding() {
            { bind(nodoU.centerXProperty(), nodoV.centerXProperty(), nodoU.centerYProperty(), nodoV.centerYProperty()); }
            @Override
            protected double computeValue() {
                double dx = nodoV.getCenterX() - nodoU.getCenterX();
                double dy = nodoV.getCenterY() - nodoU.getCenterY();
                double length = Math.sqrt(dx * dx + dy * dy);
                if (length == 0) return startX.get();
                double angle = Math.toRadians(Math.toDegrees(Math.atan2(dy, dx)) + 180 + angleOffset);
                return startX.get() + 10 * Math.cos(angle);
            }
        };

        DoubleBinding endY = new DoubleBinding() {
            { bind(nodoU.centerXProperty(), nodoV.centerXProperty(), nodoU.centerYProperty(), nodoV.centerYProperty()); }
            @Override
            protected double computeValue() {
                double dx = nodoV.getCenterX() - nodoU.getCenterX();
                double dy = nodoV.getCenterY() - nodoU.getCenterY();
                double length = Math.sqrt(dx * dx + dy * dy);
                if (length == 0) return startY.get();
                double angle = Math.toRadians(Math.toDegrees(Math.atan2(dy, dx)) + 180 + angleOffset);
                return startY.get() + 10 * Math.sin(angle);
            }
        };

        arrow.startXProperty().bind(startX);
        arrow.startYProperty().bind(startY);
        arrow.endXProperty().bind(endX);
        arrow.endYProperty().bind(endY);
        return arrow;
    }

    private Text createPesoText() {
        Text text = new Text(String.valueOf(peso));
        text.setMouseTransparent(true);
        text.setFill(Color.web("#ffffff"));
        text.setFont(Font.font(FONT_NAME, 12));
        text.xProperty().bind(nodoU.centerXProperty().add(nodoV.centerXProperty()).divide(2).subtract(text.boundsInLocalProperty().get().getWidth() / 2));
        text.yProperty().bind(nodoU.centerYProperty().add(nodoV.centerYProperty()).divide(2).add(text.boundsInLocalProperty().get().getHeight() / 4));
        return text;
    }

    public Group getEdgeGroup() {
        return edgeGroup;
    }

    public Line getLine() {
        return line;
    }

    public Text getPesoText() {
        return pesoText;
    }

    public Nodo getNodoU() {
        return nodoU;
    }

    public Nodo getNodoV() {
        return nodoV;
    }

    public int getPeso() {
        return peso;
    }
}