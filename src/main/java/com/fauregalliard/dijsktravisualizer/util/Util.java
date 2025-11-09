package com.fauregalliard.dijsktravisualizer.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;


public class Util {
    public static int SLIDER_WIDTH = 480, TEXT_FIELD_WIDTH = 60, BUTTON_WIDTH = 140;
    public static final double BASE_WIDTH = 1280, BASE_HEIGHT = 720;
    public static final String[] crewMembers = {
            "Angel Gabriel Crispin Valdivia",
            "Chipoco Cordova Sergio Nicolas",
            "Flores Antezana Fabrizzio Anggelo",
            "Huarcaya Mejicano Angeles Lucero",
            "Flores Rios Juan Diego"
    };

    public static HBox createFooter() {
        Label teacher = new Label("Teacher: Edgard Kenny Venegas Palacios");
        teacher.getStyleClass().add("secondary-label");

        Label github = new Label("github.com/FaureGalliard");
        github.getStyleClass().add("secondary-label");

        HBox footer = new HBox(10, teacher, new Region(), github);
        footer.getStyleClass().add("hbox");
        footer.setPadding(new Insets(10));
        footer.setAlignment(Pos.CENTER);
        HBox.setHgrow(footer.getChildren().get(1), Priority.ALWAYS);
        return footer;
    }

    public static Object[] createSliderBox(String labelText, double min, double max, double initial, java.util.function.Consumer<Integer> updateVar) {
        Label label = new Label(labelText);

        Slider slider = new Slider(min, max, initial);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit((max - min) / 4);
        slider.setMinorTickCount(4);
        slider.setPrefWidth(SLIDER_WIDTH);

        TextField field = new TextField(String.valueOf((int) initial));
        field.setPrefWidth(TEXT_FIELD_WIDTH);

        slider.valueProperty().addListener((obs, old, newVal) -> {
            int value = (int) Math.round(newVal.doubleValue());
            field.setText(String.valueOf(value));
            updateVar.accept(value);
        });

        field.textProperty().addListener((obs, old, newVal) -> {
            try {
                int value = Integer.parseInt(newVal);
                slider.setValue(value);
                updateVar.accept(value);
            } catch (NumberFormatException e) {
                field.setText(String.valueOf((int) slider.getValue()));
            }
        });

        return new Object[]{label, slider, field};
    }

    public static TextField createTextField(String prompt, double width) {
        TextField field = new TextField();
        field.setPrefWidth(width);
        field.setPromptText(prompt);
        return field;
    }

    public static Button createButton(String prompt, double width) {
        Button btn = new Button(prompt);
        btn.setPrefWidth(width);

        return btn;
    }

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
