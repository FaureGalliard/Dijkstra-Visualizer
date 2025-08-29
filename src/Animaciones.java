import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.control.Label;
public class Animaciones {

    public static void moverLabel(Label label,double x){

        TranslateTransition tt = new TranslateTransition(Duration.seconds(2),label);
        tt.setByY(x);
        tt.setCycleCount(TranslateTransition.INDEFINITE);
        tt.setAutoReverse(true);
        tt.play();


    }


}
