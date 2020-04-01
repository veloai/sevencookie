package swingProject.service.verif;

import javax.swing.*;
import java.math.BigDecimal;

public class InputVerifier extends javax.swing.InputVerifier {
    @Override
    public boolean verify(JComponent input) {
        String text = ((JTextField) input).getText();
        try {
            BigDecimal value = new BigDecimal(text);
            return (value.scale() <= Math.abs(4));
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
