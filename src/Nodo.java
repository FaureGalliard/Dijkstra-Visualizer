import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;

class Nodo extends Circle {
    private double offsetX;
    private double offsetY;
    private int nodeId;
    private String name;

    public Nodo(double radius, double centerX, double centerY, int nodeId, String name) {
        super(centerX, centerY, radius, Color.CORNFLOWERBLUE);
        this.nodeId = nodeId;
        this.name = name.toUpperCase();
        enableDragging();
    }

    private void enableDragging() {
        setOnMousePressed(e -> {
            offsetX = e.getSceneX() - getCenterX();
            offsetY = e.getSceneY() - getCenterY();
        });
        setOnMouseDragged(e -> {
            setCenterX(e.getSceneX() - offsetX);
            setCenterY(e.getSceneY() - offsetY);
        });
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.toUpperCase();
    }
}