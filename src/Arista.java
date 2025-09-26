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

        line = new Line();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(2);

        line.startXProperty().bind(nodoU.centerXProperty());
        line.startYProperty().bind(nodoU.centerYProperty());
        line.endXProperty().bind(nodoV.centerXProperty());
        line.endYProperty().bind(nodoV.centerYProperty());

        pesoText = new Text(String.valueOf(peso));
        pesoText.setMouseTransparent(true);
        pesoText.setFill(Color.rgb(57, 69, 79));

        pesoText.xProperty().bind(nodoU.centerXProperty().add(nodoV.centerXProperty()).divide(2).subtract(pesoText.boundsInLocalProperty().get().getWidth() / 2));
        pesoText.yProperty().bind(nodoU.centerYProperty().add(nodoV.centerYProperty()).divide(2).add(pesoText.boundsInLocalProperty().get().getHeight() / 4));
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

}