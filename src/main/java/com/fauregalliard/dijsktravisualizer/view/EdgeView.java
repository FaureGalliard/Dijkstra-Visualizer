package com.fauregalliard.dijsktravisualizer.view;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class EdgeView extends Group {
    public final NodeView nodoU;
    public final NodeView nodoV;
    private final int weight;
    private final Line mainLine;
    private final Line arrowLeft;
    private final Line arrowRight;
    private final Text label;

    public EdgeView(NodeView nodoU, NodeView nodoV, int weight) {
        this.nodoU = nodoU;
        this.nodoV = nodoV;
        this.weight = weight;

        mainLine = createMainLine();
        arrowLeft = createArrowLine(-25);
        arrowRight = createArrowLine(25);
        label = createLabel();

        getChildren().addAll(mainLine, arrowLeft, arrowRight, label);
    }

    private Line createMainLine() {
        Line line = new Line();
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(2);
        line.startXProperty().bind(nodoU.getCircle().centerXProperty());
        line.startYProperty().bind(nodoU.getCircle().centerYProperty());
        line.endXProperty().bind(nodoV.getCircle().centerXProperty());
        line.endYProperty().bind(nodoV.getCircle().centerYProperty());
        return line;
    }

    private Text createLabel() {
        Text text = new Text(String.valueOf(weight));
        text.setFill(Color.WHITE);
        text.setMouseTransparent(true);
        text.setStyle("-fx-font-size: 15px;");
        DoubleBinding midX = nodoU.getCircle().centerXProperty()
                .add(nodoV.getCircle().centerXProperty()).divide(2);
        DoubleBinding midY = nodoU.getCircle().centerYProperty()
                .add(nodoV.getCircle().centerYProperty()).divide(2);

        text.xProperty().bind(midX.subtract(text.layoutBoundsProperty().get().getWidth() / 2));
        text.yProperty().bind(midY);
        return text;
    }

    private Line createArrowLine(double angleOffset) {
        Line arrow = new Line();
        arrow.setStroke(Color.WHITE);
        arrow.setStrokeWidth(2);

        DoubleProperty ux = nodoU.getCircle().centerXProperty();
        DoubleProperty uy = nodoU.getCircle().centerYProperty();
        DoubleProperty vx = nodoV.getCircle().centerXProperty();
        DoubleProperty vy = nodoV.getCircle().centerYProperty();

        DoubleBinding dx = vx.subtract(ux);
        DoubleBinding dy = vy.subtract(uy);
        DoubleBinding length = Bindings.createDoubleBinding(
                () -> Math.sqrt(dx.get() * dx.get() + dy.get() * dy.get()),
                dx, dy
        );

        DoubleBinding arrowStartX = Bindings.createDoubleBinding(() -> {
            double len = length.get();
            return len == 0 ? ux.get() :
                    ux.get() + dx.get() * (len - nodoV.getCircle().getRadius()) * 0.97 / len;
        }, ux, vx, uy, vy);

        DoubleBinding arrowStartY = Bindings.createDoubleBinding(() -> {
            double len = length.get();
            return len == 0 ? uy.get() :
                    uy.get() + dy.get() * (len - nodoV.getCircle().getRadius()) * 0.97 / len;
        }, ux, vx, uy, vy);

        DoubleBinding arrowEndX = Bindings.createDoubleBinding(() -> {
            if (length.get() == 0) return arrowStartX.get();
            double angle = Math.atan2(dy.get(), dx.get()) + Math.toRadians(180 + angleOffset);
            return arrowStartX.get() + 10 * Math.cos(angle);
        }, arrowStartX, dx, dy, length);

        DoubleBinding arrowEndY = Bindings.createDoubleBinding(() -> {
            if (length.get() == 0) return arrowStartY.get();
            double angle = Math.atan2(dy.get(), dx.get()) + Math.toRadians(180 + angleOffset);
            return arrowStartY.get() + 10 * Math.sin(angle);
        }, arrowStartY, dx, dy, length);

        arrow.startXProperty().bind(arrowStartX);
        arrow.startYProperty().bind(arrowStartY);
        arrow.endXProperty().bind(arrowEndX);
        arrow.endYProperty().bind(arrowEndY);

        return arrow;
    }
}