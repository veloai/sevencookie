package swingProject.service;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;

import static swingProject.setting.Color.CC_base;

public class MainFrame extends JFrame{
    private JPanel main_back;
    private JTabbedPane subTab;
    private JTabbedPane mainTab;
    private JPanel center;
    private JPanel local;
    private JButton testbt;

    public MainFrame() {
        __BackPane();

    }

    void __BackPane() {
        add(main_back);
        setSize(900,600);
        //main_back.setBackground(CC_base);
        setLocationRelativeTo(null);
    }

}
