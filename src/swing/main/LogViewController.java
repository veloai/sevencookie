package swing.main;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;


public class LogViewController implements Initializable {

    @FXML TextArea logArea;

    String type;
    Stage nowStage;
    String logPath;
    File file;

    //private ScheduledExecutorService eService = Executors.newSingleThreadScheduledExecutor();
    //private ScheduledExecutorService eService = Executors.newScheduledThreadPool(1);
    StringBuffer sf = new StringBuffer();
    private static Logger logger = LoggerFactory.getLogger(LogViewController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        nowStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                logger.info("log Thread 종료");
                //eService.shutdown();

            }
        });

        logArea.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                logArea.setScrollTop(Double.MAX_VALUE); //this will scroll to the top
            }
        });

        //log 시작
        /*Runnable task = () -> {
            fileRead(file);
            logArea.setText(sf.toString());
            logArea.appendText("");
            logArea.setScrollTop(Double.MAX_VALUE);
        };
        eService.scheduleWithFixedDelay(task, 1000, 3000, TimeUnit.MILLISECONDS);*/

    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNowStage(Stage stage) {
        this.nowStage = stage;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public TextArea getLogTextArea() {
        return logArea;
    }

    /*private StringBuffer fileRead(File file) {
        sf.setLength(0);
        String line;
        try {
            //BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            while ((line = br.readLine()) != null) {
                sf.append(line).append("\n");
            }
            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return sf;
    }*/

    /*private void fileRead2(File file){
        //final File file = //some file
        try (final FileChannel fileChannel = new RandomAccessFile(file, "r").getChannel()) {
            //final StringBuilder sb = new StringBuilder();
            final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            final CharsetDecoder charsetDecoder = Charset.forName("UTF-8").newDecoder();
            while (fileChannel.read(byteBuffer) > 0) {
                byteBuffer.flip();
                sf.append(charsetDecoder.decode(byteBuffer));
                byteBuffer.clear();
            }
        } catch (CharacterCodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
