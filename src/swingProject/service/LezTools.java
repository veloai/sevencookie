package swingProject.service;

import net.miginfocom.swing.MigLayout;
import swingProject.setting.Color;

import javax.swing.*;
import java.awt.*;

public class LezTools extends JFrame{
    JFrame testFrame;
    JPanel testPanel;
    JLabel testTitle;

    public LezTools() {
        component__tools();
    }

    private void component__tools() {
        testFrame = new JFrame();
        /*getContentPane().setBackground(Color.CC_base);
        setBounds(100, 100, 1084, 631);
        setTitle("LEZ service tools");*/

        Container mltest = testFrame.getContentPane();
        mltest.setLayout(new MigLayout(
                "insets 0,hidemode 3",
                "[20,fill]",
                "[]"
        ));
        /*LoginContentPane.setLayout(new MigLayout(
                "insets 0,hidemode 3",
                // columns
                "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[20,fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[fill]",
                // rows
                "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[fill]" +
                        "[]"));*/









        /*Container frameContentPane = testFrame.getContentPane();
        testTitle.setText("Lez Service Tools");
        testTitle.setFont(new Font("Lucida Grande", Font.BOLD, 26));
        frameContentPane.add(testTitle,"cell 3 1 4 1,align center center,grow 0 0");

        frameContentPane.add(testPanel);*/
    }
}
