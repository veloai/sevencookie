package swing.main;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javafx.stage.Stage;
import swing.demon.cmrctl.CmrctlMain;
import swing.demon.kpAlv.kpAlvMain;
import swing.demon.receiver.RcvMain;
import swing.demon.sender.SndMain;
import swing.demon.shooter.ShooterMain;
import swing.demon.util.ExceptionConvert;
import swing.demon.util.FileLog;
import swing.demon.util.LogShow;

import java.awt.*;
import java.io.*;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public class MainController implements Initializable {

    @FXML private ListView<String> listBoxMain;

    @FXML private ToggleGroup modGroup;

    @FXML private TextField txtAddItem;

    @FXML private TextArea txtProp;

    @FXML private Label console;

    private ObservableList<String> listItems;

    RadioButton rdBtn;
    static final Pattern specialPattern = Pattern.compile("[ !@#$%^&*(),?\":{}|<>]");
    static final Pattern hanglePattern = Pattern.compile("^[ㄱ-ㅎ|ㅏ-ㅣ|가-힣\\s]*$");
    StringBuilder sb;
    String propPath;


    String logPath;
    static File absolutePath;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            absolutePath = new File(Class.forName("swing.main.MainController").getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException | ClassNotFoundException e) {
            LogShow.logMessage(true, ExceptionConvert.TraceAllError(e));
        }


        //listView 초기 부분
        listItems = FXCollections.observableArrayList();
        listBoxMain.setItems(listItems);
        modGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {

                if(modGroup.getSelectedToggle() != null) {
                    listBoxMain.getItems().clear();
                    rdBtn = (RadioButton) modGroup.getSelectedToggle();

                    sb = new StringBuilder();
                    File path = null;

                    if(absolutePath.getPath().endsWith(".jar")){
                        sb.append(absolutePath.getParent());
                    } else {
//                        sb.append("./total/src");
                        sb.append("./");
                    }

                    sb.append(File.separator).append(rdBtn.getUserData().toString());
                    path = new File(sb.toString());
                    propPath = sb.append(File.separator).toString();
                    File[] fileList = path.listFiles();

                    int len = fileList != null ? fileList.length : 0;
                    if(len > 0) {
                        for (int i = 0; i < len; i++) {
                            listItems.add(fileList[i].getName());
                        }
                    }
                   // }

                    //txtProp.clear();

                }

            }
        });

    }

    @FXML
    private void addAction(ActionEvent action) {
        String getTxt = txtAddItem.getText();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        BufferedWriter fw;
//        if("".equals(getTxt) || pattern.matcher(getTxt).find()) {
        if("".equals(getTxt) || specialPattern.matcher(getTxt).find()) {
            alert.setContentText("공백 또는 특수문자 입력하지 마세요");
            alert.showAndWait();
        } else if (hanglePattern.matcher(getTxt).find()) {
            alert.setContentText("한글 입력하지 마세요.");
            alert.showAndWait();
        } else {
            listItems.add(txtAddItem.getText());
            //System.out.println("addAction");
            listBoxMain.setItems(listItems);

            try {
                fw = new BufferedWriter(new FileWriter(propPath+getTxt, true));

                fw.write("");
                fw.flush();

                fw.close();
                txtAddItem.clear();
            } catch (IOException e) {
                LogShow.logMessage(true, ExceptionConvert.TraceAllError(e));
            }

        }

    }

    @FXML
    private void deleteAction(ActionEvent action) {
        int selectedItem = listBoxMain.getSelectionModel().getSelectedIndex();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        if(listBoxMain.getItems().size() > 1) {
            if(selectedItem > -1){

                File file = new File(propPath+File.separator+listBoxMain.getSelectionModel().getSelectedItem().toString());

                if(file.exists()) {
                    if(file.delete()) {
                        System.out.println("파일 삭제 성공");
                    }
                } else {
                    System.out.println("파일이 존재 하지 않습니다.");
                }

                listItems.remove(selectedItem);

            }

        } else {
            alert.setContentText("1개 이하로 삭제 할수 없습니다.");
            alert.showAndWait();
        }
    }

    @FXML
    private void saveAction(ActionEvent action) {
        try{
            if(propPath != null && listBoxMain.getSelectionModel().getSelectedItem() != null) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(propPath + File.separator + listBoxMain.getSelectionModel().getSelectedItem().toString()), StandardCharsets.UTF_8));
                writer.write(txtProp.getText().replaceAll("\n", "\r\n"));
                writer.close();
            }
        } catch (IOException e) {
            LogShow.logMessage(true, ExceptionConvert.TraceAllError(e));
        }

    }

    @FXML
    public void listViewClick(MouseEvent arg0) {
        if(propPath != null && listBoxMain.getSelectionModel().getSelectedItem() != null) {
            BufferedReader reader = null;
            //3번째 textarea에다가 txt내용 표시
            txtProp.clear();
            File file = new File(propPath+File.separator+listBoxMain.getSelectionModel().getSelectedItem().toString());

            textAreaLineInsert(file, txtProp);
        }

    }

    public void onDemon(ActionEvent event) {
        String getId = ((Control)event.getSource()).getId();
        String[] options = getId.split("_");

        String item = listBoxMain.getSelectionModel().getSelectedItem();
        if (item!=null){
            String str = propPath+listBoxMain.getSelectionModel().getSelectedItem();
            String option_text = null;
            try {
                option_text = controlGetid(options[0],options[1],str);
                console.setText(option_text);
            } catch (Exception e) {
                console.setText("옵션 설정이 잘못 되었습니다.");
                LogShow.logMessage(true, ExceptionConvert.TraceAllError(e));
            }
        }else{
            console.setText("옵션을 선택해 주세요.");
        }
    }

    public String controlGetid (String getid, String mode, String filePath) {
        String option_String="";
        String opt1 = "";
        String opt2 = "";
        if (getid.equals("snd")){
            opt1 = "Sender";
            SndMain demon = new SndMain();
            if (mode.equals("start")){
                opt2 = "시작";
                demon.sndStart(filePath);
            }else{
                opt2 = "정지";
                demon.sndStop();
            }
        }else if(getid.equals("rcv")){
            opt1 = "Receiver";
            RcvMain demon = new RcvMain();
            if (mode.equals("start")){
                opt2 = "시작";
                demon.rcvStart(filePath);
            }else{
                opt2 = "정지";
                demon.rcvStop();
            }
        }else if(getid.equals("sht")){
            opt1 = "Shooter";
            ShooterMain demon = new ShooterMain();
            if (mode.equals("start")){
                opt2 = "시작";
                demon.shooterStart(filePath);
            }else{
                opt2 = "정지";
                demon.shooterStop();
            }
        }else if(getid.equals("kpa")){
            opt1 = "KeepAlive";
            kpAlvMain demon = new kpAlvMain();
            if (mode.equals("start")){
                opt2 = "시작";
                demon.kpStart(filePath);
            }else{
                opt2 = "정지";
                demon.kpStop();
            }
        }else if(getid.equals("cctl")){
            opt1 = "CameraController";
            CmrctlMain demon = new CmrctlMain();
            if (mode.equals("start")){
                opt2 = "시작";
                demon.cmrctlStart(filePath);
            }else{
                opt2 = "정지";
                demon.cmrctlStop();
            }
        }

        option_String = opt1+opt2;
        return option_String;
    }

    @FXML
    public void showLog(ActionEvent event) throws IOException, ClassNotFoundException {
        //System.out.println("클릭");
        String getId = ((Control)event.getSource()).getId();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        FileLog fileLog = new FileLog();
        if(logPath != null && !"".equals(logPath)) {

            File logFile = fileLog.getFile(logPath, getId);

            if(logFile.exists()){

                FXMLLoader loader = new FXMLLoader(Class.forName("swing.main.MainController").getResource("LogView.fxml"));
                LogViewController controller = new LogViewController();
                Stage stage = new Stage();
                controller.setType(getId);
                loader.setController(controller);
                controller.setNowStage(stage);
                controller.setLogPath(logPath);
                controller.setFile(logFile);
                Parent root =loader.load();

                Scene scene = new Scene(root);
                stage.setTitle(getId);
                stage.setScene(scene);
                stage.show();

            } else {
                alert.setContentText(logFile.getAbsolutePath() + " 경로 확인 해주세요.");
                alert.showAndWait();
            }


        } else {
            alert.setContentText("2번쩨 리스트뷰의 properties를 선택해 주세요.");
            alert.showAndWait();
        }

    }

    private void textAreaLineInsert(File file, TextArea textArea) {
        BufferedReader reader;
        try {
            //reader = new BufferedReader(new FileReader(file));
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF8"));

            String line = "";
            while((line = reader.readLine()) != null){
                textArea.appendText(line+"\r\n");
                if((line.indexOf("log.file.path") > -1)) {
                    logPath = line.split("=")[1];
                }
            }
            reader.close();

        } catch (FileNotFoundException e) {
            LogShow.logMessage(true, ExceptionConvert.TraceAllError(e));
        } catch (IOException e) {
            LogShow.logMessage(true, ExceptionConvert.TraceAllError(e));
        }
    }


}
