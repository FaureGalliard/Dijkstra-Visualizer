import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class UIUtils {
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

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().getStyleClass().add("dialog-pane");
        alert.showAndWait();
    }
}