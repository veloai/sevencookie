package swingProject.service;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import com.util.Const;
import com.util.props.Props;
import com.util.props.PropsException;
import swingProject.front.Login;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import java.awt.Color;

import javax.swing.table.DefaultTableModel;
import javax.swing.JLayeredPane;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import java.awt.SystemColor;
import javax.swing.ButtonGroup;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;

public class Main extends JFrame implements ActionListener, ItemListener {

	JMenuBar menuBar;
	JMenu mn1, mn2;
	JMenuItem menuItem1, menuItem2, menuItem3, menuItem4;
	private JLabel lblPath;
	private JPanel listPanel;
	private JPanel configPane;
	private JTable table;
	private final ButtonGroup buttonGroup = new ButtonGroup();

	JRadioButton rdbtnLezKeepAlive, rdbtnLezSimpleSender, rdbtnLezReceiver, rdbtnLezShooter, rdbtnLezCameraControl;
	JRadioButton radio[] = new JRadioButton[5];
	private JToggleButton tglbtnLezKeepAlive, tglbtnLezSimpleSender, tglbtnLezReceiver, tglbtnLezShooter, tglbtnLezCameraControl;

	Props props;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Login();
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		getContentPane().setBackground(Color.WHITE);
		setTitle("LEZ 노후경유차");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1084, 631);

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mn1 = new JMenu("지자체");
		mn1.setHorizontalAlignment(SwingConstants.CENTER);
		menuBar.add(mn1);

		menuItem1 = new JMenuItem("WEB");
		menuItem1.addActionListener(this);
		menuItem1.setHorizontalAlignment(SwingConstants.CENTER);
		mn1.add(menuItem1);

		menuItem2 = new JMenuItem("WAS");
		menuItem2.addActionListener(this);
		mn1.add(menuItem2);

		mn2 = new JMenu("도청");
		menuBar.add(mn2);

		menuItem3 = new JMenuItem("WEB");
		menuItem3.addActionListener(this);
		mn2.add(menuItem3);

		menuItem4 = new JMenuItem("WAS");
		menuItem4.setSelected(true);
		menuItem4.addActionListener(this);
		mn2.add(menuItem4);
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("461px:grow"),
				ColumnSpec.decode("485px:grow"),
				ColumnSpec.decode("max(39dlu;default)"),},
			new RowSpec[] {
				FormSpecs.DEFAULT_ROWSPEC,
				RowSpec.decode("249px:grow"),
				RowSpec.decode("299px:grow"),}));

		lblPath = new JLabel("");
		lblPath.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(lblPath, "3, 1, fill, fill");

		listPanel = new JPanel();
		listPanel.setBackground(SystemColor.control);
		getContentPane().add(listPanel, "1, 2, default, fill");
		listPanel.setLayout(null);

		rdbtnLezKeepAlive = new JRadioButton("lezKeepAlive");
		buttonGroup.add(rdbtnLezKeepAlive);
		rdbtnLezKeepAlive.addActionListener(this);
		rdbtnLezKeepAlive.setBounds(54, 29, 109, 23);
		radio[0] = rdbtnLezKeepAlive;
		listPanel.add(rdbtnLezKeepAlive);

		rdbtnLezSimpleSender = new JRadioButton("lezSimpleSender");
		buttonGroup.add(rdbtnLezSimpleSender);
		rdbtnLezSimpleSender.addActionListener(this);
		rdbtnLezSimpleSender.setBounds(54, 71, 141, 23);
		radio[1] = rdbtnLezSimpleSender;
		listPanel.add(rdbtnLezSimpleSender);

		rdbtnLezReceiver = new JRadioButton("lezReceiver");
		buttonGroup.add(rdbtnLezReceiver);
		rdbtnLezReceiver.addActionListener(this);
		rdbtnLezReceiver.setBounds(54, 116, 109, 23);
		radio[2] = rdbtnLezReceiver;
		listPanel.add(rdbtnLezReceiver);

		rdbtnLezShooter = new JRadioButton("lezShooter");
		buttonGroup.add(rdbtnLezShooter);
		rdbtnLezShooter.addActionListener(this);
		rdbtnLezShooter.setBounds(54, 162, 109, 23);
		radio[3] = rdbtnLezShooter;
		listPanel.add(rdbtnLezShooter);

		rdbtnLezCameraControl = new JRadioButton("lezCameraControl");
		buttonGroup.add(rdbtnLezCameraControl);
		rdbtnLezCameraControl.addActionListener(this);
		rdbtnLezCameraControl.setBounds(54, 207, 141, 23);
		radio[4] = rdbtnLezCameraControl;
		listPanel.add(rdbtnLezCameraControl);

		JLabel lblList = new JLabel("목록");
		lblList.setBounds(92, 7, 34, 15);
		listPanel.add(lblList);

		JLabel lblStatus = new JLabel("실행 상태");
		lblStatus.setBounds(252, 7, 58, 15);
		listPanel.add(lblStatus);

		tglbtnLezKeepAlive = new JToggleButton(Const.STOP);
		tglbtnLezKeepAlive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startStopAction(e, tglbtnLezKeepAlive, Const.lezKeepAlive);
			}
		});

		tglbtnLezKeepAlive.setBounds(216, 29, 135, 23);
		listPanel.add(tglbtnLezKeepAlive);

		tglbtnLezSimpleSender = new JToggleButton(Const.STOP);
		tglbtnLezSimpleSender.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startStopAction(e, tglbtnLezSimpleSender, Const.lezSimpleSender);
			}
		});
		tglbtnLezSimpleSender.setBounds(216, 71, 135, 23);
		listPanel.add(tglbtnLezSimpleSender);

		tglbtnLezReceiver = new JToggleButton(Const.STOP);
		tglbtnLezReceiver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startStopAction(e, tglbtnLezReceiver, Const.lezReceiver);
			}
		});
		tglbtnLezReceiver.setBounds(216, 116, 135, 23);
		listPanel.add(tglbtnLezReceiver);

		tglbtnLezShooter = new JToggleButton(Const.STOP);
		tglbtnLezShooter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startStopAction(e, tglbtnLezShooter, Const.lezShooter);
			}
		});
		tglbtnLezShooter.setBounds(216, 162, 135, 23);
		listPanel.add(tglbtnLezShooter);

		tglbtnLezCameraControl = new JToggleButton(Const.STOP);
		tglbtnLezCameraControl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startStopAction(e, tglbtnLezCameraControl, Const.lezCameraControl);
			}
		});
		tglbtnLezCameraControl.setBounds(216, 207, 135, 23);
		listPanel.add(tglbtnLezCameraControl);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, "2, 2, 1, 2, fill, fill");

		JLayeredPane dirPanel = new JLayeredPane();
		tabbedPane.addTab("directory", null, dirPanel, null);

		JTree tree = new JTree();
		tree.setBounds(0, 0, 506, 513);
		dirPanel.add(tree);

		JLayeredPane logPanel = new JLayeredPane();
		tabbedPane.addTab("log", null, logPanel, null);

		JTextArea textArea = new JTextArea();
		textArea.setBounds(0, 0, 506, 513);
		logPanel.add(textArea);

		configPane = new JPanel();
		configPane.setBackground(SystemColor.control);
		getContentPane().add(configPane, "1, 3, fill, fill");
		configPane.setLayout(null);

		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{"path", "/data/mecar"},
				{"file.transuni", "/data/meca/dse"},
				{"delay", 100},
				{null, null},
			},
			new String[] {
				"\uC18D\uC131 \uC774\uB984", "\uAC12"
			}
		));
		table.getColumnModel().getColumn(0).setPreferredWidth(130);
		table.getColumnModel().getColumn(1).setPreferredWidth(125);
		table.setBorder(new LineBorder(new Color(0, 0, 0)));
		table.setBounds(12, 10, 489, 231);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setLocation(7, 0);
		scrollPane.setSize(473, 242);
		configPane.add(scrollPane);

		JButton btnConfig = new JButton("저 장");
		btnConfig.setBounds(370, 256, 97, 23);
		configPane.add(btnConfig);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == menuItem1 || e.getSource() == menuItem2) {
			lblPath.setText("지자체 - " + e.getActionCommand());
		} else if (e.getSource() == menuItem3 || e.getSource() == menuItem4) {
			lblPath.setText("도청 - " + e.getActionCommand());
		}
		int size = radio.length;
		for (int i = 0; i < size; i++) {
			if(e.getSource() == radio[i]) {
				DefaultTableModel m = (DefaultTableModel)table.getModel();
				m.setNumRows(0); // 초기화

				//데이터 properties 입력
				props = getProps(radio[i].getText());
				if(props != null) {
					List<Object[]> list = props.getPropList();
					int s = list.size();
					for (int j = 0; j < s; j++) {
						m.insertRow(j, list.get(j));
					}
				}
				break;
			}
		}
	}

	//시작 or 정지
	private void startStopAction(ActionEvent e, JToggleButton tglBtn, int num) {

		boolean isStarted = false;
		if(e.getActionCommand().equals(Const.STOP)) {
			tglBtn.setText(Const.START);
			isStarted = true;
		}else {
			tglBtn.setText(Const.STOP);
			isStarted = false;
		}

		try {

			Runtime rt = Runtime.getRuntime();
			if(num == Const.lezKeepAlive) {

				if(isStarted) {
					//Process p = rt.exec("./kpAlvSim.sh start "+getPropPath()+"lezKeepAlive.properties");
					System.out.println("진짜 실행됨?");
				}else {
					rt.exec("./kpAlvSim.sh stop");
					System.out.println("멈춤");
				}

			} else if (num == Const.lezSimpleSender) {

			} else if (num == Const.lezReceiver) {

			} else if (num == Const.lezShooter) {

			} else if (num == Const.lezCameraControl) {

			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	private Props getProps(String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(getPropPath()).append(File.separator).append(name).append(".properties");
		try {
			//JOptionPane.showMessageDialog(listPanel, getPropPath(name));
			//return new Props(getPropPath(name));
			return new Props(sb.toString());
		} catch (PropsException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {

		/*
		 * if (e.getSource() == cbMenuItem1) { System.out.println("check box item 1");
		 * }else (e.getSource() == cbMenuItem2) {
		 * System.out.println("check box item 2"); }
		 */

	}

	private String getPropPath() {
		StringBuilder sb = new StringBuilder();
		sb.append(System.getProperty("user.dir")).append(Paths.get(File.separator,"src","com","properties").toString());
		//sb.append(Paths.get("/","com","credif","properties").toString());

//		  Path file = null; try { file = Files.createTempFile(null, ".properties"); try
//		  (InputStream stream =
//		  this.getClass().getResourceAsStream("/com/properties/"+name+
//		  ".properties")) { Files.copy(stream, file,
//		  StandardCopyOption.REPLACE_EXISTING); } return file.toString(); } catch
//		  (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }

//		Path file = Files.createFile("/com/properties/", attrs)(null, ".txt");
//		try (InputStream stream = this.getClass().getResourceAsStream("/key.txt")) {
//		    Files.copy(stream, file, StandardCopyOption.REPLACE_EXISTING);
//		}
		return sb.toString();
	}


}
	