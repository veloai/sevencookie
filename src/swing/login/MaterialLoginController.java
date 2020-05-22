/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package swing.login;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import swing.tray.TrayMenu;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
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

    private boolean firstTime;
    private TrayIcon trayIcon;

    Stage primaryStage = null;
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
        if (primaryStage == null) {
            primaryStage = new Stage();
        }
        closeAndTrayIcon(primaryStage);
        firstTime = true;
        Platform.setImplicitExit(false);
        primaryStage.setScene(scene);
        primaryStage.setTitle("통합 Tool");
        //primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setResizable(false);

        if(firstTime == true) {
            try {
                final InputStream resource = this.getClass().getResourceAsStream("/swing/tray/favicon-16.png");
                TrayIcon trayIcon = new TrayIcon(ImageIO.read(resource));

                TrayMenu menu = new TrayMenu(primaryStage, trayIcon);
                menu.addAppToTray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        primaryStage.show();
    }

    private void closeAndTrayIcon(Stage stage) {
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                hide(stage);
            }
        });
    }

    public void showProgramIsMinimizedMsg() {
        if (firstTime) {
            firstTime = false;
        }
    }

    private void hide(final Stage stage) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (SystemTray.isSupported()) {
                    stage.hide();
                    showProgramIsMinimizedMsg();
                } else {
                    System.exit(0);
                }
            }
        });
    }
}
