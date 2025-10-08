package com.fauregalliard.dijsktravisualizer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Graph {

    public List<Node> nodes;
    public List<Edge> edges;

    public Graph() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void addEdge(Node from, Node to, int weight) {
        edges.add(new Edge(from, to, weight));
    }

    private boolean edgeExists(Node a, Node b) {
        return edges.stream().anyMatch(e ->
                (e.getFrom() == a && e.getTo() == b) || (e.getFrom() == b && e.getTo() == a));
    }

    public void createNodes(int num) {
        for (int i = 0; i < num; i++) {
            Node node = new Node(nodes.size() + 1);
            addNode(node);
        }
    }

    public void createEdges(int maxWeight, int density) {
        Random random = new Random();
        edges.clear();
        for (int i = 0; i < nodes.size() - 1; i++) {
            addEdge(nodes.get(i), nodes.get(i + 1), random.nextInt(maxWeight) + 1);
        }
        double prob = density / 100.0;
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 2; j < nodes.size(); j++) {
                if (random.nextDouble() < prob) {
                    addEdge(nodes.get(i), nodes.get(j), random.nextInt(maxWeight) + 1);
                }
            }
        }
    }

    public Node getFromId(int id){

        for(int i = 0;i < nodes.size();i++){
            if(nodes.get(i).getId()== id) return nodes.get(i);
        }
        System.out.println("no se encontro el nodo");
        return nodes.get(0);
    }
}
