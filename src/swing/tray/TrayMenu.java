package swing.tray;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TrayMenu extends Frame {
    private final Stage primaryStage;
    private final transient TrayIcon trayIcon;

    public TrayMenu(Stage stage, TrayIcon trayIcon) {
        primaryStage = stage;
        this.trayIcon = trayIcon;
        //primaryStage.setAlwaysOnTop(true);
    }

    public void addAppToTray() {
        try {
            if (!SystemTray.isSupported()) {
                System.out.println("Failed to display tray icon - no system tray support");
                return;
            }
            Toolkit.getDefaultToolkit();
            final SystemTray systemTray = SystemTray.getSystemTray();

            // Tray icon popup의 "About"의 이벤트 설정
            final MenuItem aboutItem = new MenuItem("About");
            aboutItem.addActionListener(e -> {
                System.out.println("Clicked about menu.");
                handleAlert();
            });

            // Tray icon popup의 "Open"의 이벤트 설정
            final MenuItem openItem = new MenuItem("Open");
            openItem.addActionListener(e -> {
                System.out.println("Clicked open menu.");
                handleShow();
            });

            // Tray icon popup의 "Exit"의 이벤트 설정
            final MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(e -> {
                System.out.println("Clicked exit menu");
                handleTerminate();
            });

            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        System.out.println("double click");
                        handleShow();
                    }
                }
            });

            final String systemName = "Lez Menu";
            final PopupMenu popup = new PopupMenu(systemName);
            popup.add(aboutItem);
            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);
            trayIcon.setToolTip(systemName);
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("Failed to display tray icon");
        }
    }

    private void handleAlert() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("통합 Tool");
            alert.setHeaderText(null);
            alert.setContentText("통합 툴입니다.\n문의사항은 010-1211-1234 연락주세요.");
            alert.getDialogPane().getButtonTypes().add(ButtonType.OK);

            alert.showAndWait();
        });
    }

    private void handleTerminate() {
        Platform.runLater(() -> {
            primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
            SystemTray.getSystemTray().remove(trayIcon);
            Platform.exit();
            System.exit(0);
        });
    }

    private void handleShow() {
        Platform.runLater(() -> {
            if(primaryStage != null) {
                primaryStage.show();
                primaryStage.toFront();
            }
        });
    }
}
