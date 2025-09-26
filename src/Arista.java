import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

class Arista {
    private final Nodo nodoU;
    private final Nodo nodoV;
    private final int peso;
    private final Line line;
    private final Text pesoText;

    public Arista(Nodo nodoU, Nodo nodoV, int peso) {
        this.nodoU = nodoU;
        this.nodoV = nodoV;
        this.peso = peso;
        this.line = createLine();
        this.pesoText = createPesoText();
    }

    private Line createLine() {
        Line line = new Line();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(2);
        line.startXProperty().bind(nodoU.centerXProperty());
        line.startYProperty().bind(nodoU.centerYProperty());
        line.endXProperty().bind(nodoV.centerXProperty());
        line.endYProperty().bind(nodoV.centerYProperty());
        return line;
    }

    private Text createPesoText() {
        Text text = new Text(String.valueOf(peso));
        text.setMouseTransparent(true);
        text.setFill(Color.rgb(57, 69, 79));
        text.xProperty().bind(nodoU.centerXProperty().add(nodoV.centerXProperty()).divide(2).subtract(text.boundsInLocalProperty().get().getWidth() / 2));
        text.yProperty().bind(nodoU.centerYProperty().add(nodoV.centerYProperty()).divide(2).add(text.boundsInLocalProperty().get().getHeight() / 4));
        return text;
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