package swingProject.front;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import net.miginfocom.swing.*;
import swingProject.service.MainFrame;

public class Login {

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - swj
    private JFrame Login;
    private JLabel title;
    private JLabel label1;
    private JTextField textField1;
    private JLabel label2;
    private JPasswordField passwordField1;
    private JPanel panel1;
    private JButton regist_btn;
    private JButton login_btn;
    private boolean bLoginCheck;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public Login() {
        initComponents();
        Login.setVisible(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - swj
        Login = new JFrame();
        title = new JLabel();
        label1 = new JLabel();
        textField1 = new JTextField();
        label2 = new JLabel();
        passwordField1 = new JPasswordField();
        panel1 = new JPanel();
        regist_btn = new JButton();
        login_btn = new JButton();

        //======== Login ========
        {
            Container LoginContentPane = Login.getContentPane();
            LoginContentPane.setLayout(new MigLayout(
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
                            "[]"));

            //---- title ----
            title.setText("Lez Service Tools");
            title.setFont(new Font("Lucida Grande", Font.BOLD, 26));
            LoginContentPane.add(title, "cell 3 1 4 1,align center center,grow 0 0");

            //---- label1 ----
            label1.setText("USER ID");
            LoginContentPane.add(label1, "cell 3 4 2 1");
            LoginContentPane.add(textField1, "cell 5 4 2 1");

            //---- label2 ----
            label2.setText("PASSWD");
            LoginContentPane.add(label2, "cell 3 6");
            LoginContentPane.add(passwordField1, "cell 5 6 2 1");

            //======== panel1 ========
            {
                panel1.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new javax.swing.border
                        .EmptyBorder(0, 0, 0, 0), "test version 0.1.0", javax.swing.border.TitledBorder.CENTER, javax
                        .swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dia\u006cog", java.awt.Font.BOLD,
                        12), java.awt.Color.red), panel1.getBorder()));
                panel1.addPropertyChangeListener(new java.beans
                        .PropertyChangeListener() {
                    @Override
                    public void propertyChange(java.beans.PropertyChangeEvent e) {
                        if ("\u0062ord\u0065r".equals(e.
                                getPropertyName())) throw new RuntimeException();
                    }
                });
                panel1.setLayout(new MigLayout(
                        "hidemode 3",
                        // columns
                        "[fill]" +
                                "[106,fill]",
                        // rows
                        "[]" +
                                "[]" +
                                "[]"));

                //---- regist_btn ----
                regist_btn.setText("REGIST");
                panel1.add(regist_btn, "cell 1 1");

                //---- login_btn ----
                login_btn.setText("LOGIN");
                panel1.add(login_btn, "cell 1 1");
                login_btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        isLoginCheck();
                    }
                });
            }
            LoginContentPane.add(panel1, "cell 6 9");
            Login.pack();
            Login.setLocationRelativeTo(Login.getOwner());
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    public void isLoginCheck() {
        if (textField1.getText().equals("test") && new String(passwordField1.getPassword()).equals("1234")) {
            JOptionPane.showMessageDialog(null, "Success");
            bLoginCheck = true;

            // 로그인 성공이라면 매니져창 뛰우기
            if (isLogin()) {
                Login.dispose();

                /*Main frame = new Main();
                frame.setVisible(true);*/
                /*LezTools lezTools = new LezTools();
                lezTools.setVisible(true);*/

            }
        } else {
            //JOptionPane.showMessageDialog(null, "Faild");

            Login.dispose();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    MainFrame mainframe = new MainFrame();
                    mainframe.setVisible(true);
                }
            });
        }
    }

    public boolean isLogin() {
        return bLoginCheck;
    }
}
