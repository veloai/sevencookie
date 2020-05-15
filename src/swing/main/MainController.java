package swing.main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import swing.demon.sender.SenderMain;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {


    @FXML
    private Label label;

    @FXML
    private void start() {
        label.setText("Start!!");
        SenderMain sndMain = new SenderMain();


    }

    @FXML
    private void stop() {
        label.setText("Stop!!");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Main initialize");
    }
}
