package swingProject.front;

import swingProject.service.MainFrame;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static swingProject.setting.Color.CC_base;

public class DualLogin extends JFrame {
    private JFrame base_frame;
    private JPanel base_panel;
    private JTextField login_id;
    private JButton login_btn;
    private JPasswordField login_pw;
    private JLabel login_title;
    BufferedImage img = null;
    private boolean login_check;

    /*constructor*/
    public DualLogin() {
        __dual();
        base_frame.setVisible(true);
    }

    /*image call*/
    public class backgroundImage extends JPanel {
        public void paint(Graphics g){
            g.drawImage(img,0,0,null);
        }
        /*반투명 프레임*/
        //setUndecorated(true);
        //setBackground(new Color(0,0,0,122));

    }
    private void __background() {
        /*image IO*/
        try{
            img = ImageIO.read(new File("src/swingProject/img/base_login_sky.png"));
        }catch(IOException e){
            JOptionPane.showMessageDialog(null,"no image");
            System.exit(0);
        }

        /*image insert*/
        backgroundImage imagePanel = new backgroundImage();
        imagePanel.setSize(400,300);
        base_frame.add(imagePanel);

    }


    /*login id check*/
    public boolean isLogin() {
        return login_check;
    }
    public void loginCheck() {
        if (login_id.getText().equals("test") && new String(login_pw.getPassword()).equals("1234")) {
            login_check = true;

            // 로그인 성공이라면 매니져창 뛰우기
            if (isLogin()) {
                /*message*/
                JOptionPane.showMessageDialog(null, "Login Success");

                /*base_frame close*/
                base_frame.dispose();

                /*mainFrame 호출*/
                MainFrame main_frame = new MainFrame();
                main_frame.setVisible(true);
            }
        } else {
            //JOptionPane.showMessageDialog(null, "Faild");
            //DualLogin.dispose();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    MainFrame mainframe = new MainFrame();
                    mainframe.setVisible(true);
                }
            });
        }
    }

    /*login frame*/
    private void __dual() {
        base_frame = new JFrame();
        base_frame.setTitle("LEZ SERVICE TOOLS LOGIN");
        base_frame.setSize(400,300);
        base_frame.setLocationRelativeTo(null);
        base_frame.setResizable(false);



        /*layout setting*/
        //base_frame.setLayout(null);

        Container LoginContentPane = base_frame.getContentPane();
        LoginContentPane.add(base_panel);
        //base_panel.setBackground(CC_base);

        login_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginCheck();
            }
        });
    }

    /*add panel*/
    public void Mypanel() {
//        add(base_panel);
//        setTitle("myPanel");
//        setSize(100,100);
    }


}
