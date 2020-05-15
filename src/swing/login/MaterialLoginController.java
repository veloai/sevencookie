/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package swing.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 * @author danml
 */
public class MaterialLoginController implements Initializable {
    
    @FXML
    private Label label;

    @FXML
    private TextField userId;

    @FXML
    private TextField userPw;
    
    @FXML
    private void ButtonAction(ActionEvent event) throws IOException {
        //System.out.println("You clicked me!");
        //label.setText("Hello World!");
        if(userId.getText().equals("user") && userPw.getText().equals("pass")){
            label.setText("Login Success");

        }else{
            label.setText("Login Failed");
            login();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        System.out.println("Initialize FXML");
    }

    public void login() throws IOException {
        /* login window close */
        Stage stage = (Stage) label.getScene().getWindow();
        stage.close();

        /* new window */
        Parent root = FXMLLoader.load(getClass().getResource("../main/Main.fxml"));
        Scene scene = new Scene(root);

        Stage primaryStage = new Stage();
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.show();
    }
}
