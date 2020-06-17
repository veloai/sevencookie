package swing.main;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

public class shooterAppender extends WriterAppender {
    private static TextArea textArea = null;

    public static void setTextArea(final TextArea textArea) {
        shooterAppender.textArea = textArea;
    }

    @Override
    public void append(final LoggingEvent loggingEvent) {
        final String message = this.layout.format(loggingEvent);
        try {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (textArea != null) {
                            if (textArea.getText().length() == 0) {
                                textArea.setText(message);
                            } else {
                                textArea.selectEnd();
                                textArea.insertText(textArea.getText().length(), message);
                            }
                        }
                    } catch (final Throwable t) {
                        System.out.println("Unable to append log to textarea:" + t.getMessage());
                    }
                }
            });
        } catch (final IllegalStateException e) {
        }
    }
}
