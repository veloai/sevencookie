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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import swing.demon.util.ExceptionConvert;
import swing.demon.util.FileLog;
import swing.demon.util.LogShow;
import swing.tray.TrayMenu;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
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

    static final String ID = "lez";
    static final String PW = "lez";

    @FXML
    private void ButtonAction(ActionEvent event) throws IOException, ClassNotFoundException {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        if(ID.equalsIgnoreCase(userId.getText()) && PW.equalsIgnoreCase(userPw.getText())){
            label.setText("Login Success");
            login();
        }else{
            label.setText("Login Failed");
            alert.setContentText("아이디와 비밀번호 확인해주세요.");
            alert.showAndWait();

        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            File absolutePath = new File(Class.forName("swing.main.MainController").getProtectionDomain().getCodeSource().getLocation().toURI());
            if(absolutePath.getPath().endsWith(".jar")){
                FileLog fileLog = new FileLog();
                fileLog.setFileLog(absolutePath.getParent(), "MainLog");
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        userPw.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent ke)
            {
                if (ke.getCode().equals(KeyCode.ENTER))
                {
                    try {
                        ButtonAction(null);
                    } catch (IOException e) {
                        LogShow.logMessage(true, ExceptionConvert.TraceAllError(e));
                    } catch (ClassNotFoundException e) {
                        LogShow.logMessage(true, ExceptionConvert.TraceAllError(e));
                    }
                }
            }
        });
    }

    public void login() throws IOException, ClassNotFoundException {
        /* login window close */
        Stage stage = (Stage) label.getScene().getWindow();
        stage.close();


        /* new window */
        Parent root = FXMLLoader.load(Class.forName("swing.main.MainController").getResource("Main.fxml"));
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

        if(firstTime) {
            try {
                final InputStream resource = Class.forName("swing.login.MaterialLoginController").getResourceAsStream("/swing/tray/favicon-16.png");
                TrayIcon trayIcon = new TrayIcon(ImageIO.read(resource));

                TrayMenu menu = new TrayMenu(primaryStage, trayIcon);
                menu.addAppToTray();
            } catch (IOException e) {
                LogShow.logMessage(true, ExceptionConvert.TraceAllError(e));
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
        String os = System.getProperty("os.name").toLowerCase();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (SystemTray.isSupported()) {
                    if(os.contains("nix") || os.contains("nux") || os.contains("aix")) { //리눅스 계열은 종료.. (리눅스에 tray 쓰는게 없다) 쓰려면 확장기능 설치 해야 함..
                        System.exit(0);
                    }
                    stage.hide();
                    showProgramIsMinimizedMsg();
                } else {
                    System.exit(0);
                }
            }
        });
    }
}
