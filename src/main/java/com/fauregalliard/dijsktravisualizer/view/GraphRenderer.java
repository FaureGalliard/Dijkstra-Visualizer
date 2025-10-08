package com.fauregalliard.dijsktravisualizer.view;

import com.fauregalliard.dijsktravisualizer.model.Edge;
import com.fauregalliard.dijsktravisualizer.model.Graph;
import com.fauregalliard.dijsktravisualizer.model.Node;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphRenderer {
    private final List<NodeView> viewNodes;
    private final List<EdgeView> viewEdges;
    private final Map<Node, NodeView> nodeToViewMap;

    private int column;
    private int row;
    private final int columnSpace = 60;
    private final int rowSpace = 60;
    private final int cantityPerRow = 10;
    private final int startX = 220;
    private final int startY = 20;

    public GraphRenderer() {
        viewNodes = new ArrayList<>();
        viewEdges = new ArrayList<>();
        nodeToViewMap = new HashMap<>();
        column = 0;
        row = 0;
    }

    public void addNode(Node modelNode, NodeView viewNode) {
        viewNodes.add(viewNode);
        nodeToViewMap.put(modelNode, viewNode);
    }

    public void addEdge(NodeView from, NodeView to, int weight) {
        EdgeView edge = new EdgeView(from, to, weight);
        viewEdges.add(edge);
    }


    public void createNodes(List<Node> modelNodes) {
        for (Node modelNode : modelNodes) {

            if (nodeToViewMap.containsKey(modelNode)) {
                continue;
            }

            double x = startX + (column * columnSpace);
            double y = startY + (row * rowSpace);

            NodeView nodeView = new NodeView(x, y, String.valueOf(modelNode.getId()));

            addNode(modelNode, nodeView);

            column++;
            if (column >= cantityPerRow) {
                column = 0;
                row++;
            }
        }
    }

    public void createEdges(List<Edge> modelEdges) {
        viewEdges.clear();

        for (Edge edge : modelEdges) {
            Node fromNode = edge.getFrom();
            Node toNode = edge.getTo();

            NodeView fromView = nodeToViewMap.get(fromNode);
            NodeView toView = nodeToViewMap.get(toNode);

            if (fromView != null && toView != null) {
                addEdge(fromView, toView, edge.getWeight());
            }
        }
    }

    public void render(Pane canvasPane) {
        canvasPane.getChildren().clear();

        for (EdgeView edge : viewEdges) {
            canvasPane.getChildren().add(edge);
        }

        for (NodeView node : viewNodes) {
            canvasPane.getChildren().add(node);
        }
    }

    public void renderGraph(Graph graph, Pane canvasPane) {
        createNodes(graph.nodes);
        createEdges(graph.edges);
        render(canvasPane);
    }

    public void clear() {
        viewNodes.clear();
        viewEdges.clear();
        nodeToViewMap.clear();
        column = 0;
        row = 0;
    }

    // Getters
    public List<NodeView> getViewNodes() {
        return viewNodes;
    }

    public List<EdgeView> getViewEdges() {
        return viewEdges;
    }

    public NodeView getNodeView(Node modelNode) {
        return nodeToViewMap.get(modelNode);
    }
}