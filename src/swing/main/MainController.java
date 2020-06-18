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
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import swing.demon.cmrctl.CmrctlMain;
import swing.demon.kpAlv.kpAlvMain;
import swing.demon.receiver.RcvMain;
import swing.demon.sender.SndMain;
import swing.demon.shooter.ShooterMain;
import swing.demon.util.Const;
import swing.demon.util.ExceptionConvert;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
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

    static String rPath;
    private static Logger logger = LoggerFactory.getLogger(MainController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File absolutePath = null;
        try {
            absolutePath = new File(Class.forName("swing.main.MainController").getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException | ClassNotFoundException e) {
            logger.info(ExceptionConvert.TraceAllError(e));
        }

        if(absolutePath != null && absolutePath.getPath().endsWith(".jar")){
            rPath = absolutePath.getParent();
        } else {
            rPath = "./";
        }

        //listView 초기 부분
        listItems = FXCollections.observableArrayList();
        listBoxMain.setItems(listItems);
        //objectLineInsert(new File(rPath + File.separator +"properties/logPath.txt"), logPath, Const.TEXTFIELD);
        modGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {

                if(modGroup.getSelectedToggle() != null) {
                    listBoxMain.getItems().clear();
                    rdBtn = (RadioButton) modGroup.getSelectedToggle();

                    sb = new StringBuilder();
                    File path;

                    sb.append(rPath).append(File.separator).append(rdBtn.getUserData().toString());
                    path = new File(sb.toString());
                    propPath = sb.append(File.separator).toString();
                    File[] fileList = path.listFiles();

                    int len = fileList != null ? fileList.length : 0;
                    if(len > 0) {
                        for (int i = 0; i < len; i++) {
                            listItems.add(fileList[i].getName());
                        }
                    }

                    txtProp.clear();

                }

            }
        });

    }

    @FXML
    private void addAction(ActionEvent event) {
        String getTxt = txtAddItem.getText();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        BufferedWriter fw;
        if("".equals(getTxt) || specialPattern.matcher(getTxt).find()) {
            alert.setContentText("공백 또는 특수문자 입력하지 마세요");
            alert.showAndWait();
        } else if (hanglePattern.matcher(getTxt).find()) {
            alert.setContentText("한글 입력하지 마세요.");
            alert.showAndWait();
        } else {
            listItems.add(txtAddItem.getText());
            listBoxMain.setItems(listItems);

            try {
                fw = new BufferedWriter(new FileWriter(propPath+getTxt, true));

                fw.write("");
                fw.flush();

                fw.close();
                txtAddItem.clear();
            } catch (IOException e) {
                logger.info(ExceptionConvert.TraceAllError(e));
            }

        }

    }

    @FXML
    private void deleteAction(ActionEvent event) {
        int selectedItem = listBoxMain.getSelectionModel().getSelectedIndex();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        if(listBoxMain.getItems().size() > 1) {
            if(selectedItem > -1){

                File file = new File(propPath+File.separator+listBoxMain.getSelectionModel().getSelectedItem().toString());

                if(file.exists()) {
                    if(file.delete()) {
                        logger.info("파일 삭제 성공");
                    }
                } else {
                    logger.info("파일이 존재 하지 않습니다.");
                }

                listItems.remove(selectedItem);

            }

        } else {
            alert.setContentText("1개 이하로 삭제 할수 없습니다.");
            alert.showAndWait();
        }
    }

    @FXML
    private void saveAction(ActionEvent event) {
        BufferedWriter writer = null;
        try{
            if(propPath != null && listBoxMain.getSelectionModel().getSelectedItem() != null) {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(propPath + File.separator + listBoxMain.getSelectionModel().getSelectedItem().toString()), StandardCharsets.UTF_8));
                writer.write(txtProp.getText().replaceAll("\n", "\r\n"));
            }
            if(writer != null) writer.close();
        } catch (IOException e) {
            logger.info(ExceptionConvert.TraceAllError(e));
        }

    }

    @FXML
    public void listViewClick(MouseEvent arg0) {
        if(propPath != null && listBoxMain.getSelectionModel().getSelectedItem() != null) {
            //3번째 textarea에다가 txt내용 표시
            txtProp.clear();
            File file = new File(propPath+File.separator+listBoxMain.getSelectionModel().getSelectedItem().toString());

            objectLineInsert(file, txtProp, Const.TEXTAREA);
        }

    }

    public void onDemon(ActionEvent event) {
        String getId = ((Control)event.getSource()).getId();
        String[] options = getId.split("_");

        String item = listBoxMain.getSelectionModel().getSelectedItem();
        if (item!=null){
            String str = propPath+listBoxMain.getSelectionModel().getSelectedItem();
            String option_text;
            try {
                option_text = controlGetid(options[0],options[1],str);
                console.setText(option_text);
            } catch (Exception e) {
                console.setText("옵션 설정이 잘못 되었습니다.");
                logger.info(ExceptionConvert.TraceAllError(e));
            }
        }else{
            console.setText("옵션을 선택해 주세요.");
        }
    }

    public String controlGetid (String getid, String mode, String filePath) {
        String option_String="";
        String opt1 = "";
        String opt2 = "";
        switch (getid) {
            case "snd": {
                opt1 = "Sender";
                SndMain demon = new SndMain();
                if (mode.equals("start")) {
                    opt2 = "시작";
                    demon.sndStart(filePath);
                } else {
                    opt2 = "정지";
                    demon.sndStop();
                }
                break;
            }
            case "rcv": {
                opt1 = "Receiver";
                RcvMain demon = new RcvMain();
                if (mode.equals("start")) {
                    opt2 = "시작";
                    demon.rcvStart(filePath);
                } else {
                    opt2 = "정지";
                    demon.rcvStop();
                }
                break;
            }
            case "sht": {
                opt1 = "Shooter";
                ShooterMain demon = new ShooterMain();
                if (mode.equals("start")) {
                    opt2 = "시작";
                    demon.shooterStart(filePath);
                } else {
                    opt2 = "정지";
                    demon.shooterStop();
                }
                break;
            }
            case "kpa": {
                opt1 = "KeepAlive";
                kpAlvMain demon = new kpAlvMain();
                if (mode.equals("start")) {
                    opt2 = "시작";
                    demon.kpStart(filePath);
                } else {
                    opt2 = "정지";
                    demon.kpStop();
                }
                break;
            }
            case "cctl": {
                opt1 = "CameraController";
                CmrctlMain demon = new CmrctlMain();
                if (mode.equals("start")) {
                    opt2 = "시작";
                    demon.cmrctlStart(filePath);
                } else {
                    opt2 = "정지";
                    demon.cmrctlStop();
                }
                break;
            }
        }

        option_String = opt1+opt2;
        return option_String;
    }

    @FXML
    public void showLog(ActionEvent event) throws IOException, ClassNotFoundException {
        String getId = ((Control)event.getSource()).getId();

        FXMLLoader loader = new FXMLLoader(Class.forName("swing.main.LogViewController").getResource("LogView.fxml"));
        LogViewController controller = new LogViewController();
        Stage stage = new Stage();
        controller.setType(getId);
        loader.setController(controller);
        controller.setNowStage(stage);
        Parent root =loader.load();

        Scene scene = new Scene(root);
        stage.setTitle(getId);
        stage.setScene(scene);
        stage.show();

        switch (getId) {
            case "sender": {
                senderAppender.setTextArea(controller.getLogTextArea());
                break;
            }
            case "receiver": {
                receiverAppender.setTextArea(controller.getLogTextArea());
                break;
            }
            case "shooter": {
                shooterAppender.setTextArea(controller.getLogTextArea());
                break;
            }
            case "kpAlive": {
                kpAliveAppender.setTextArea(controller.getLogTextArea());
                break;
            }
            case "cmrCtrl": {
                cmrCtlAppender.setTextArea(controller.getLogTextArea());
                break;
            }
        }

    }

    private void objectLineInsert(File file, Object obj, int order) {
        TextArea tempArea= null;
        TextField tempField = null;
        if(order == Const.TEXTAREA) {
            tempArea = ((TextArea) obj);
        } else if (order == Const.TEXTFIELD) {
            tempField = ((TextField) obj);
        }
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

            String line = "";
            while((line = reader.readLine()) != null){
                if(order == Const.TEXTAREA) tempArea.appendText(line+"\r\n");
                else if(order == Const.TEXTFIELD) tempField.setText(line);
            }
            reader.close();

        } catch (FileNotFoundException e) {
            logger.info(ExceptionConvert.TraceAllError(e));
        } catch (IOException e) {
            logger.info(ExceptionConvert.TraceAllError(e));
        }
    }

}
