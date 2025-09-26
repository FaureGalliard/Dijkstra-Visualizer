import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

class Arista {
    private Nodo nodoU;
    private Nodo nodoV;
    private int peso;
    private Line line;
    private Text pesoText;

    public Arista(Nodo nodoU, Nodo nodoV, int peso) {
        this.nodoU = nodoU;
        this.nodoV = nodoV;
        this.peso = peso;

        // Crear la línea gráfica
        line = new Line();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(2);

        // Vincular posiciones de la línea a los nodos
        line.startXProperty().bind(nodoU.centerXProperty());
        line.startYProperty().bind(nodoU.centerYProperty());
        line.endXProperty().bind(nodoV.centerXProperty());
        line.endYProperty().bind(nodoV.centerYProperty());
        // Crear el texto del peso
        pesoText = new Text(String.valueOf(peso));
        pesoText.setMouseTransparent(true); // Ignorar eventos del ratón

        pesoText.setStroke(Color.rgb(57, 69, 79));
        pesoText.setStrokeWidth(2);
        // Vincular posición del texto al medio de la línea
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

    public int getPeso() {
        return peso;
    }
}