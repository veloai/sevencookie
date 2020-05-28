package swing.main;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import swing.demon.cmrctl.CmrctlMain;
import swing.demon.receiver.RcvMain;
import swing.demon.sender.SndMain;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class MainController implements Initializable {

    @FXML private ListView<String> listBoxMain;

    @FXML private ToggleGroup modGroup;

    @FXML private TextField txtAddItem;

    @FXML private TextArea txtProp;

    @FXML private Button snd_start;
    @FXML private Button snd_stop;
    @FXML private Button rcv_start;
    @FXML private Button rcv_stop;
    @FXML private Button sht_start;
    @FXML private Button sht_stop;
    @FXML private Button kpa_start;
    @FXML private Button kpa_stop;
    @FXML private Button cctl_start;
    @FXML private Button cctl_stop;

    @FXML private TextFlow console_box;

    @FXML private Label console;

    private ObservableList<String> listItems;

    RadioButton rdBtn;
    Pattern pattern = Pattern.compile("[ !@#$%^&*(),?\":{}|<>]");


    StringBuilder sb;
    String propPath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
                    sb.append("./").append(rdBtn.getUserData().toString()).append(File.separator);
                    propPath = sb.toString();
                    File path = new File(sb.toString());
                    File[] fileList = path.listFiles();

                    int len = fileList.length;
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
    private void addAction(ActionEvent action) {
        String getTxt = txtAddItem.getText();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        BufferedWriter fw;
        if("".equals(getTxt) || pattern.matcher(getTxt).find()) {
            alert.setContentText("공백 또는 특수문자 입력하지 마세요");
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
                e.printStackTrace();
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
        //System.out.println("저장 ㅋ");
        try{
            if(propPath != null && listBoxMain.getSelectionModel().getSelectedItem() != null) {
                FileWriter writer = null;
                writer = new FileWriter(propPath + File.separator + listBoxMain.getSelectionModel().getSelectedItem().toString());
                writer.write(txtProp.getText().replaceAll("\n", "\r\n"));
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void listViewClick(MouseEvent arg0) {
        //System.out.println("clicked on " + listBoxMain.getSelectionModel().getSelectedItem());
        if(propPath != null && listBoxMain.getSelectionModel().getSelectedItem() != null) {
            BufferedReader reader = null;
            //3번째 textarea에다가 txt내용 표시
            txtProp.clear();
            File file = new File(propPath+File.separator+listBoxMain.getSelectionModel().getSelectedItem().toString());

            try {
                reader = new BufferedReader(new FileReader(file));

                String line = "";
                while((line = reader.readLine()) != null){
                    txtProp.appendText(line+"\r\n");
                }
                reader.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void onDemon(ActionEvent event) {
        String log = "start!!!";
        String time_log = "\n>>> [Sample time log] passed >>>" + log;
        Text system_text = new Text();
        system_text.setStyle("-fx-fill: #4F8A10;-fx-font-weight:bold;");
        //setStyle("-fx-fill: RED;-fx-font-weight:normal;");
        system_text.setText(time_log);
        console_box.getChildren().add(system_text);

//        cc.test("teset");
        /*Text t1 = new Text();
        Text text1=new Text("Some Text");
        text1.setStyle("-fx-font-weight: bold");
        console_log.getChildren().add(cc);*/

        //System.out.println(((Control)event.getSource()).getId());
        /*String getId = ((Control)event.getSource()).getId();
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
                e.printStackTrace();
            }
        }else{
            console.setText("옵션을 선택해 주세요.");
        }*/
    }

    public String controlGetid (String getid, String mode, String filePath){
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
            if (mode.equals("start")){
                opt2 = "시작";
            }else{
                opt2 = "정지";
            }
        }else if(getid.equals("kpa")){
            opt1 = "KeepAlive";
            if (mode.equals("start")){
                opt2 = "시작";
            }else{
                opt2 = "정지";
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
}
