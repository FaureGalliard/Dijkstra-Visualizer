import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;

class Nodo extends Circle {
    private double offsetX;
    private double offsetY;
    private int nodeId;

    public Nodo(double radius, double centerX, double centerY) {
        super(centerX, centerY, radius, Color.CORNFLOWERBLUE);
        setOnMousePressed((MouseEvent e) -> {
            offsetX = e.getSceneX() - getCenterX();
            offsetY = e.getSceneY() - getCenterY();
        });
        setOnMouseDragged((MouseEvent e) -> {
            setCenterX(e.getSceneX() - offsetX);
            setCenterY(e.getSceneY() - offsetY);
        });
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getNodeId() {
        return nodeId;
    }
}