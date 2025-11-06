package com.fauregalliard.dijsktravisualizer.controller;

import com.fauregalliard.dijsktravisualizer.model.Graph;
import com.fauregalliard.dijsktravisualizer.util.Util;
import com.fauregalliard.dijsktravisualizer.view.GraphRenderer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class InputGraph
{
    private final Scene scene;

    public InputGraph(Stage stage){

        Label title = new Label("Insert the graph");
        title.getStyleClass().add("title2");
        StackPane titleBox = new StackPane(title);
        titleBox.setPadding(new Insets(30, 0, 20, 0));

        TextArea input = new TextArea();
        input.setMaxWidth(460);
        input.setMaxHeight(280);
        input.getStyleClass().add("info-area");


        TextArea personalizedInfo = new TextArea("Insert the graph like a competitive programming problem:"+
                "\n-----\nn e\na1 b1 w1\na2 b2 w2\na3 b3 w3\n...\n--------\nWhere n: number of nodes, e:number of edges, ai bi wi: a node of origin"
                + ", bi target node and wi the distance between those nodes\nExample:\n3 2\n1 2 3\n2 3 1\nThis will create a graph like the one shown below");
        personalizedInfo.getStyleClass().add("info-area");
        personalizedInfo.setEditable(false);
        personalizedInfo.setWrapText(true);
        personalizedInfo.setMinWidth(260);
        personalizedInfo.setMaxWidth(260);

        personalizedInfo.setMaxHeight(200);

        Pane canvasPane = new Pane();
        Graph grafo = new Graph();
        grafo.createNodes(3);
        grafo.addEdge(grafo.nodes.get(0), grafo.nodes.get(1),3);
        grafo.addEdge(grafo.nodes.get(1), grafo.nodes.get(2),1);
        GraphRenderer graphRenderer = new GraphRenderer();
        graphRenderer.renderGraph(grafo,canvasPane);
        Button nextBtn = Util.createButton("Continue",150);

        VBox vcontainer = new VBox(20,personalizedInfo,canvasPane ,nextBtn);
        vcontainer.setAlignment(Pos.CENTER);
        vcontainer.setPadding(new Insets(0, 0, 0, 20));


        nextBtn.setOnAction(e -> {
            String inputText = input.getText();
            Graph inputGraph = CreateInputGraph(inputText);
            GraphRenderer graphRenderer1 = new GraphRenderer();
            stage.setScene(new GraphEditorScene(stage, inputGraph,  graphRenderer1 ).getScene());

        });

        BorderPane root = new BorderPane();
        root.setCenter(input);
        root.setLeft(vcontainer);
        root.setBottom(Util.createFooter());
        root.setTop(titleBox);
        scene = new Scene(root, Util.BASE_WIDTH, Util.BASE_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

    }

    public Graph CreateInputGraph(String inputText){
        Graph inputGraph = new Graph();
        String[] lines = inputText.strip().split("\\r?\\n");
        if (lines.length < 1) return inputGraph;
        String[] firstLine = lines[0].trim().split("\\s+");
        int n = Integer.parseInt(firstLine[0]);
        int e = Integer.parseInt(firstLine[1]);
        inputGraph.createNodes(n);
        for (int i = 1; i <= e && i < lines.length; i++) {
            String[] parts = lines[i].trim().split("\\s+");
            if (parts.length < 3) continue; // ignorar líneas inválidas
            int from = Integer.parseInt(parts[0]);
            int to = Integer.parseInt(parts[1]);
            int weight = Integer.parseInt(parts[2]);

            inputGraph.addEdge(inputGraph.getFromId(from), inputGraph.getFromId(to), weight);
        }


        return inputGraph;
    }

    public Scene getScene() {
        return scene;
    }

}
