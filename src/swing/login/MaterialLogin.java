/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package swing.login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import swing.demon.util.ExceptionConvert;

import java.io.File;
import java.net.URISyntaxException;

/**
 *
 * @author danml
 */
public class MaterialLogin extends Application {
    private static Logger logger = LoggerFactory.getLogger(MaterialLogin.class);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File absolutePath = null;
        String path;
        try {
            absolutePath = new File(Class.forName("swing.main.MainController").getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException | ClassNotFoundException e) {
            logger.info(ExceptionConvert.TraceAllError(e));
        }

        if(absolutePath != null && absolutePath.getPath().endsWith(".jar")){
            path = absolutePath.getParent();
        } else {
            path = "./";
        }

        PropertyConfigurator.configure(path + File.separator + "properties/log4j.properties");
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Class.forName("swing.login.MaterialLogin").getResource("MaterialLogin.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();

    }

}
