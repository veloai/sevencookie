package swing.main;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class MainController implements Initializable {

    //@FXML TextArea txtArea;

    @FXML private Button btnAdd;

    @FXML private Button btnDelete;

    @FXML private ListView<String> listBoxMain;

    @FXML private TextField txtAddItem;

    @FXML private ToggleGroup modGroup;

    @FXML private Button btnSave;

    @FXML private TextArea txtProp;

    private ObservableList<String> listItems;
    private Stage stage;

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

                }

            }
        });

    }

    public void textAreaResize() {
        /*txtArea.prefHeightProperty().bind(Bindings.createDoubleBinding(new Callable<Double>(){
            @Override
            public Double call() throws Exception {
                return txtArea.getBoundsInLocal().getHeight();
            }
        }, txtArea.boundsInLocalProperty()).add(10));*/
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
    }

    @FXML
    private void saveAction(ActionEvent action) {
        System.out.println("저장 ㅋ");
        try{
            FileWriter writer = null;
            writer = new FileWriter(propPath+File.separator+listBoxMain.getSelectionModel().getSelectedItem().toString());
            writer.write(txtProp.getText().replaceAll("\n", "\r\n"));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void listViewClick(MouseEvent arg0) {
        //System.out.println("clicked on " + listBoxMain.getSelectionModel().getSelectedItem());
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