package com.fauregalliard.dijsktravisualizer.controller;
import com.fauregalliard.dijsktravisualizer.model.Graph;
import com.fauregalliard.dijsktravisualizer.view.GraphRenderer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import com.fauregalliard.dijsktravisualizer.util.Util;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class SelectCreationType {

    private final Scene scene;
    public SelectCreationType(Stage stage){
        BorderPane root = new BorderPane();

        Image imagen = new Image("Images/ExampleRandom.png");
        ImageView imagenView = new ImageView(imagen);
        imagenView.setFitWidth(360);
        imagenView.setPreserveRatio(true);
        imagenView.setSmooth(true);

        Label title = new Label("Select how to create your graph");
        title.getStyleClass().add("title2");
        StackPane titleBox = new StackPane(title);
        titleBox.setPadding(new Insets(30, 0, 20, 0));

        Button randomGraph = Util.createButton("Random Graph",140);
        randomGraph.setOnAction(e -> stage.setScene(new SetupScene(stage).getScene()));
        Button personalizedGraph = Util.createButton("Personalized Graph",140);
        personalizedGraph.setOnAction(e -> stage.setScene(new InputGraph(stage).getScene()));
        TextArea randomInfo = new TextArea("Create a graph based on random values.\n-Number of nodes\n-Max distances" +
                "\n-Density of the graph");
        randomInfo.getStyleClass().add("info-area");
        randomInfo.setEditable(false);
        randomInfo.setWrapText(true);
        randomInfo.setMaxWidth(360);
        randomInfo.setMaxHeight(100);

        TextArea personalizedInfo = new TextArea("Insert the graph like a competitive programming problem:"+
                "\n-----\nn e\na1 b1 w1\na2 b2 w2\na3 b3 w3\n...\n--------\nWhere n: number of nodes, e:number of edges, ai bi wi: a node of origin"
        + ", bi target node and wi the distance between those nodes\nExample:\n3 2\n1 2 3\n2 3 1\nThis will create a graph like the one shown below");
        personalizedInfo.getStyleClass().add("info-area");
        personalizedInfo.setEditable(false);
        personalizedInfo.setWrapText(true);
        personalizedInfo.setMaxWidth(360);
        personalizedInfo.setMaxHeight(160);



        Pane canvasPane = new Pane();
        Graph grafo = new Graph();
        grafo.createNodes(3);
        grafo.addEdge(grafo.nodes.get(0), grafo.nodes.get(1),3);
        grafo.addEdge(grafo.nodes.get(1), grafo.nodes.get(2),1);
        GraphRenderer graphRenderer = new GraphRenderer();
        graphRenderer.renderGraph(grafo,canvasPane);


        VBox randomContainer = new VBox(20,randomInfo,imagenView, randomGraph);
        VBox personalizedContainer = new VBox(20,personalizedInfo,canvasPane,personalizedGraph);
        randomContainer.setAlignment(Pos.CENTER);
        personalizedContainer.setAlignment(Pos.CENTER);
        HBox centerBox = new HBox(20,randomContainer,personalizedContainer);
        centerBox.setAlignment(Pos.CENTER);



        root.setTop(titleBox);
        root.setCenter(centerBox);
        root.setBottom(Util.createFooter());
        scene = new Scene(root, Util.BASE_WIDTH, Util.BASE_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
    }

    public Scene getScene() {
        return scene;
    }
}
