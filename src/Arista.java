import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.beans.binding.DoubleBinding;

class Arista {
    private final Nodo nodoU;
    private final Nodo nodoV;
    private final int peso;
    private final Line line;
    private final Text pesoText;
    private final Line arrowLine1; // Primera línea de la flecha
    private final Line arrowLine2; // Segunda línea de la flecha
    private final Group edgeGroup; // Grupo que contiene la línea principal y las líneas de la flecha

    public Arista(Nodo nodoU, Nodo nodoV, int peso) {
        this.nodoU = nodoU;
        this.nodoV = nodoV;
        this.peso = peso;
        this.line = createLine();
        this.arrowLine1 = createArrowLine(-15); // Ángulo de -15 grados (negado para abrir hacia nodoV)
        this.arrowLine2 = createArrowLine(15); // Ángulo de 15 grados (negado para abrir hacia nodoV)
        this.pesoText = createPesoText();
        this.edgeGroup = new Group(line, arrowLine1, arrowLine2); // Agrupar línea principal y flechas
    }

    private Line createLine() {
        Line line = new Line();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(2);
        // Vincular los extremos de la línea a las posiciones de los nodos
        line.startXProperty().bind(nodoU.centerXProperty());
        line.startYProperty().bind(nodoU.centerYProperty());
        line.endXProperty().bind(nodoV.centerXProperty());
        line.endYProperty().bind(nodoV.centerYProperty());
        return line;
    }

    private Line createArrowLine(double angleOffset) {
        double arrowLength = 10; // Longitud de las líneas de la flecha
        double offsetFromMid = 0.85; // Mover la flecha al 80% hacia nodoV (cerca del final)
        double nodeRadiusOffset = nodoV.getRadius(); // Offset para evitar solapamiento con el nodo

        Line arrow = new Line();
        arrow.setStroke(Color.BLACK);
        arrow.setStrokeWidth(2);

        // Calcular el punto de inicio de la flecha (a 80% de la arista, ajustado por el radio del nodo)
        DoubleBinding startX = new DoubleBinding() {
            {
                super.bind(nodoU.centerXProperty(), nodoV.centerXProperty());
            }
            @Override
            protected double computeValue() {
                double dx = nodoV.getCenterX() - nodoU.getCenterX();
                double dy = nodoV.getCenterY() - nodoU.getCenterY();
                double length = Math.sqrt(dx * dx + dy * dy);
                if (length == 0) return nodoU.getCenterX();
                // Ajustar la posición para que no se solape con el nodo
                double factor = (length - nodeRadiusOffset) * offsetFromMid / length;
                return nodoU.getCenterX() + dx * factor;
            }
        };

        DoubleBinding startY = new DoubleBinding() {
            {
                super.bind(nodoU.centerYProperty(), nodoV.centerYProperty());
            }
            @Override
            protected double computeValue() {
                double dx = nodoV.getCenterX() - nodoU.getCenterX();
                double dy = nodoV.getCenterY() - nodoU.getCenterY();
                double length = Math.sqrt(dx * dx + dy * dy);
                if (length == 0) return nodoU.getCenterY();
                // Ajustar la posición para que no se solape con el nodo
                double factor = (length - nodeRadiusOffset) * offsetFromMid / length;
                return nodoU.getCenterY() + dy * factor;
            }
        };

        // Calcular el punto final de la flecha basado en el ángulo y la dirección
        DoubleBinding endX = new DoubleBinding() {
            {
                super.bind(nodoU.centerXProperty(), nodoV.centerXProperty(), nodoU.centerYProperty(), nodoV.centerYProperty());
            }
            @Override
            protected double computeValue() {
                double dx = nodoV.getCenterX() - nodoU.getCenterX();
                double dy = nodoV.getCenterY() - nodoU.getCenterY();
                double length = Math.sqrt(dx * dx + dy * dy);
                if (length == 0) return startX.get();
                // Girar 180 grados para apuntar hacia nodoV y aplicar el ángulo offset
                double angle = Math.toRadians(Math.toDegrees(Math.atan2(dy, dx)) + 180 + angleOffset);
                return startX.get() + arrowLength * Math.cos(angle);
            }
        };

        DoubleBinding endY = new DoubleBinding() {
            {
                super.bind(nodoU.centerXProperty(), nodoV.centerXProperty(), nodoU.centerYProperty(), nodoV.centerYProperty());
            }
            @Override
            protected double computeValue() {
                double dx = nodoV.getCenterX() - nodoU.getCenterX();
                double dy = nodoV.getCenterY() - nodoU.getCenterY();
                double length = Math.sqrt(dx * dx + dy * dy);
                if (length == 0) return startY.get();
                // Girar 180 grados para apuntar hacia nodoV y aplicar el ángulo offset
                double angle = Math.toRadians(Math.toDegrees(Math.atan2(dy, dx)) + 180 + angleOffset);
                return startY.get() + arrowLength * Math.sin(angle);
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
        text.setFill(Color.rgb(57, 69, 79));
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