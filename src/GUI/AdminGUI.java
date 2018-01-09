package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;

import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import Infrastructure.AboutServer;
import InternetSecurity.SafeClient;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.SystemColor;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.GridLayout;

@SuppressWarnings("serial")
public class AdminGUI extends JFrame {
	private static  String userName;
	DataBaseManagement dbm = new DataBaseManagement();
		
	private JLabel cam1;
	private JLabel cam2;
	private JLabel cam3;
	private JLabel cam4;	
	private JLabel bigImageLbl;
	public static JToggleButton alarmTglBtn;
	
	private static int videoHight = 240, videoWidth = 320;
	private int resolutionSelection;
	private int fps = 5;
	private boolean camera1 = true;
	private boolean camera2 = true;
	private boolean camera3 = true;
	private boolean camera4 = true;
	
	SafeClient homeClient;
    private boolean connected = false;
    private boolean streaming = false;
    byte[] imageBytes = null;
    Runnable[] streamRunnable = new Runnable[5];
    Thread[] stremThread = new Thread[5];

    
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AdminGUI frame = new AdminGUI(userName);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public AdminGUI(String username) {
		userName = username;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		AdminGUI.this.setTitle("Camera Viewer - " + username);
		setBounds(20, 20, 630, 515);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JButton videoSettingsBtn = new JButton("Video Settings");
		videoSettingsBtn.addActionListener(new ActionListener() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public void actionPerformed(ActionEvent e) {
				JComboBox comboBox = new JComboBox();
				comboBox.setModel(new DefaultComboBoxModel(new String[] {"Please select resolution:", "1024x700 (4:3)", "800x600 (4:3)", "640x480 (4:3)", "480x360 (4:3)", "320x240 (4:3)", "240x180 (4:3)", "160x120 (4:3)", "1280x720 (16:9)", "800x450 (16:9)", "640x360 (16:9)", "480x270 (16:9)", "320x180 (16:9)", "160x90 (16:9)", "1280x800 (16:10)", "176x144"}));
				comboBox.setBounds(10, 21, 131, 20);
				JSpinner spinner = new JSpinner();
				spinner.setModel(new SpinnerNumberModel(5, 5, 30, 1));
				spinner.setBounds(151, 21, 83, 20);
				final JComponent[] inputs = new JComponent[] {
				        new JLabel("Resolution"),
				        comboBox,
				        new JLabel("Frequency"),
				        spinner,
				};
				int result = JOptionPane.showConfirmDialog(null, inputs, "Video Settings Dialog", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					resolutionSelection = comboBox.getSelectedIndex();
					setVideoResolution();
					bigImageLbl.setBounds(220, 10, videoWidth, videoHight);
					fps = (int)spinner.getValue();
				} 
			}
		});
		videoSettingsBtn.setBounds(10, 14, 179, 23);
		panel.add(videoSettingsBtn);
		
		JButton cameraSettingsBtn = new JButton("Camera Settings");
		cameraSettingsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				camera1 = dbm.getCameraPrivilege(username, 1);
				camera2 = dbm.getCameraPrivilege(username, 2);
				camera3 = dbm.getCameraPrivilege(username, 3);
				camera4 = dbm.getCameraPrivilege(username, 4);
				
				JTable table = new JTable();
				table.setBackground(SystemColor.control);
				table.setRowSelectionAllowed(true);
				table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				table.setBorder(null);
				table.setModel(new DefaultTableModel(
					new Object[][] {
						{"Camera 1", "Bed Room", "192.168.20.246", "10236", camera1},
						{"Camera 2", "Kitchen", "192.168.20.247", "10237", camera2},
						{"Camera 3", "Living Room", "192.168.20.248", "10238", camera3},
						{"Camera 4", "Entrance", "192.168.20.249", "10239", camera4},
					},
					new String[] {
						"Name", "Location", "IP Address", "Port no.", "View"
					}
				) {
					@SuppressWarnings("rawtypes")
					Class[] columnTypes = new Class[] {
						Object.class, Object.class, Object.class, Object.class, Boolean.class
					};
					@SuppressWarnings({ "unchecked", "rawtypes" })
					public Class getColumnClass(int columnIndex) {
						return columnTypes[columnIndex];
					}
				});
				table.getColumnModel().getColumn(0).setPreferredWidth(100);
				table.getColumnModel().getColumn(2).setPreferredWidth(100);
				table.getColumnModel().getColumn(3).setPreferredWidth(100);
				table.getColumnModel().getColumn(4).setPreferredWidth(100);
				
				table.setRowHeight(35);
				
				int result = JOptionPane.showConfirmDialog(null, table, "Cameras viewing settings Dialog", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION)
				{
					if ((Boolean)table.getValueAt(0, 4) != camera1)
						if ((Boolean)table.getValueAt(0, 4))
							dbm.setCameraPrivilegeToPositive(userName, 1);
						else
							dbm.setCameraPrivilegeToNegative(userName, 1);
					
					if ((Boolean)table.getValueAt(1, 4) != camera2)
						if ((Boolean)table.getValueAt(1, 4))
							dbm.setCameraPrivilegeToPositive(userName, 2);
						else
							dbm.setCameraPrivilegeToNegative(userName, 2);
					if ((Boolean)table.getValueAt(2, 4) != camera3)
						if ((Boolean)table.getValueAt(2, 4))
							dbm.setCameraPrivilegeToPositive(userName, 3);
						else
							dbm.setCameraPrivilegeToNegative(userName, 3);
					if ((Boolean)table.getValueAt(3, 4) != camera4)
						if ((Boolean)table.getValueAt(3, 4))
							dbm.setCameraPrivilegeToPositive(userName, 4);
						else
							dbm.setCameraPrivilegeToNegative(userName, 4);
					
					for (int i = 0; i < table.getRowCount(); i++)
						if ((Boolean)table.getValueAt(i, 4) == true)
						{
							switch(i+1)
							{
							case 1:
								cam1.setText("streaming");
								//imageStream("83.254.132.115", 10800, cam1.getName());
								camera1 = true;
								break;
							case 2:
								cam2.setText("streaming");
				                //imageStream("192.168.20.247", 5001, cam2.getName());								
								camera2 = true;
								break;
							case 3:
								cam3.setText("streaming");
				                //imageStream("192.168.20.248", 5001, cam3.getName());
								camera3 = true;
								break;
							case 4:
								cam4.setText("streaming");
				                //imageStream("192.168.20.249", 5001, cam4.getName());								
								camera4 = true;
								break;
							}
						}
						else
						{
							switch(i+1)
							{
							case 1:
								cam1.setText("CAM1");
								cam1.setIcon(null);
								camera1 = false;
								break;
							case 2:
								cam2.setText("CAM2");
								cam2.setIcon(null);
								camera2 = false;
								break;
							case 3:
								cam3.setText("CAM3");
								cam3.setIcon(null);
								camera3 = false;
								break;
							case 4:
								cam4.setText("CAM4");
								cam4.setIcon(null);
								camera4 = false;
								break;								
							}
						}


				}
			}
		});
		cameraSettingsBtn.setBounds(10, 48, 179, 23);
		panel.add(cameraSettingsBtn);
		
		JButton usersPrivilegesBtn = new JButton("Users Privileges");
		usersPrivilegesBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String userNames[] = dbm.getUserNames();
				
				JTable dataTable = new JTable();
				dataTable.setBackground(SystemColor.control);
				dataTable.setRowSelectionAllowed(true);
				dataTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				dataTable.setBorder(null);

				JTable tempTable = new JTable();
				tempTable.setModel(new DefaultTableModel(
						new Object[userNames.length][5],
						new String[] {
							"User Name", "Cam 1", "Cam 2", "Cam 3", "Cam 4"
						}
					) {
						@SuppressWarnings("rawtypes")
						Class[] columnTypes = new Class[] {
							Object.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class
						};
						@SuppressWarnings({ "unchecked", "rawtypes" })
						public Class getColumnClass(int columnIndex) {
							return columnTypes[columnIndex];
						}
					});
				
				dataTable.setModel(new DefaultTableModel(
					new Object[userNames.length][5],
					new String[] {
						"User Name", "Cam 1", "Cam 2", "Cam 3", "Cam 4"
					}
				) {
					@SuppressWarnings("rawtypes")
					Class[] columnTypes = new Class[] {
						Object.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class
					};
					@SuppressWarnings({ "unchecked", "rawtypes" })
					public Class getColumnClass(int columnIndex) {
						return columnTypes[columnIndex];
					}
				});
				dataTable.getColumnModel().getColumn(0).setPreferredWidth(150);
				dataTable.getColumnModel().getColumn(1).setPreferredWidth(100);				
				dataTable.getColumnModel().getColumn(2).setPreferredWidth(100);
				dataTable.getColumnModel().getColumn(3).setPreferredWidth(100);
				dataTable.setRowHeight(35);
				
				
				//filling the table with the cameras privileges
				for (int i = 0; i < userNames.length; i++)
				{
					dataTable.setValueAt(userNames[i], i, 0);
					for (int j = 1; j < 5; j++)
					{
						dataTable.setValueAt(dbm.getCameraPrivilege(userNames[i], j), i, j);
						tempTable.setValueAt(dbm.getCameraPrivilege(userNames[i], j), i, j);
					}
				}
				
				JScrollPane js = new JScrollPane(dataTable);
				js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				js.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				js.setPreferredSize(new Dimension(500, 200));
				js.setVisible(true);
				
				int result = JOptionPane.showConfirmDialog(null, js, "Cameras viewing settings Dialog", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (result == JOptionPane.OK_OPTION)
				{
					for (int row = 0; row < dataTable.getRowCount(); row++)
						for (int camIndex = 1; camIndex < 5; camIndex++)
						{
							if (dataTable.getValueAt(row, camIndex) != tempTable.getValueAt(row, camIndex))
								if ((boolean)tempTable.getValueAt(row, camIndex) == true)
									dbm.setCameraPrivilegeToNegative((String)dataTable.getValueAt(row, 0), camIndex);
								else
									dbm.setCameraPrivilegeToPositive((String)dataTable.getValueAt(row, 0), camIndex);
						}
				}
			}
		});
		usersPrivilegesBtn.setBounds(11, 116, 179, 23);
		panel.add(usersPrivilegesBtn);
		
		JButton logOutBtn = new JButton("Log out");
		logOutBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        if(connected){
		            connected = false;
		            bigImageLbl.setIcon(null);
		            
		            try {
		                while(streaming)
		                    Thread.sleep(100);
		                homeClient.writeEncString("disconnect");
		                String response = homeClient.readEncString();
		                System.out.println(response);
		                
		            } catch (Exception exc) {
		                System.out.println("Disconnection error");
		                System.out.println(exc.getMessage());
		            }
		        }
		        AdminGUI.this.dispose();
			}
		});
		logOutBtn.setBounds(10, 185, 179, 23);
		panel.add(logOutBtn);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Cameras", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_1.setBounds(252, 8, 340, 200);
		panel.add(panel_1);
		
		cam4 = new JLabel("streaming");
		cam4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (cam4.getText() == "streaming")
					//imageStream("192.168.20.249", 5004, bigImageLbl.getName());
				bigImageLbl.setText("CAM4 STREAMING NOW");
			else
				cam4.setText("CAM4 IS DISABLED");
			}
		});
		panel_1.setLayout(new GridLayout(2, 2, 0, 0));
		
		cam2 = new JLabel("streaming");
		cam2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (cam2.getText() == "streaming")
					//imageStream("192.168.20.247", 5002, bigImageLbl.getName());
					bigImageLbl.setText("CAM2 STREAMING NOW");
				else
					cam2.setText("CAM2 IS DISABLED");
			}
		});
		
		cam3 = new JLabel("streaming");
		cam3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (cam3.getText() == "streaming")
					bigImageLbl.setText("CAM3 STREAMING NOW");
					//imageStream("192.168.20.248", 5003, bigImageLbl.getName());
				else
					cam3.setText("CAM3 IS DISABLED");
			}
		});
		
		cam1 = new JLabel("streaming");
		cam1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (cam1.getText() == "streaming")
				{
					stremThread[0].interrupt();
					bigImageLbl.setText("CAM1 STREAMING NOW");
					imageStream("83.254.132.115", 10800, 4, bigImageLbl);
				}
				else
					cam1.setText("CAM1 IS DISABLED");
			}
		});
		cam1.setVerticalAlignment(SwingConstants.BOTTOM);
		cam1.setHorizontalAlignment(SwingConstants.RIGHT);
		cam1.setFont(new Font("SansSerif", Font.BOLD, 9));
		panel_1.add(cam1);
		cam3.setVerticalAlignment(SwingConstants.BOTTOM);
		cam3.setHorizontalAlignment(SwingConstants.RIGHT);
		cam3.setFont(new Font("SansSerif", Font.BOLD, 9));
		panel_1.add(cam3);
		cam2.setVerticalAlignment(SwingConstants.BOTTOM);
		cam2.setHorizontalAlignment(SwingConstants.RIGHT);
		cam2.setFont(new Font("SansSerif", Font.BOLD, 9));
		panel_1.add(cam2);
		cam4.setVerticalAlignment(SwingConstants.BOTTOM);
		cam4.setHorizontalAlignment(SwingConstants.RIGHT);
		cam4.setFont(new Font("SansSerif", Font.BOLD, 9));
		cam4.setBackground(SystemColor.controlDkShadow);
		panel_1.add(cam4);
		
		bigImageLbl = new JLabel("BIG IMAGE");
		bigImageLbl.setHorizontalAlignment(SwingConstants.CENTER);
		bigImageLbl.setBounds(262, 219, 320, 240);
		panel.add(bigImageLbl);
		
		JButton usersManagementBtn = new JButton("Users Management");
		usersManagementBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UserManagementGUI umgui = new UserManagementGUI();
				umgui.setVisible(true);
			}
		});
		usersManagementBtn.setBounds(10, 150, 179, 23);
		panel.add(usersManagementBtn);
		
		alarmTglBtn = new JToggleButton("Activate Motion Alarm");
		alarmTglBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Runnable r = new PromptAlarm();
				Thread t = new Thread(r);
				if (alarmTglBtn.isSelected())
				{
					t.start();
					alarmTglBtn.setText("Deactivate Motion Alarm");
					System.out.println("THREAD Status: " + String.valueOf(t.isAlive()));
				}
				else
				{
					t.interrupt();
					alarmTglBtn.setText("Activate Motion Alarm");				
					System.out.println(t.isAlive());
				}
			}
		});
		alarmTglBtn.setBounds(10, 82, 179, 23);
		panel.add(alarmTglBtn);
		
        if(!connected){
            System.out.println("TRYING TO CONNECT");
            
            AboutServer ca = new AboutServer("CA1","127.0.0.1", 10040);
            AboutServer home = new AboutServer(null,"127.0.0.1", 10041);
            this.homeClient = new SafeClient("ClientJanez",home,ca);
            bigImageLbl.setIcon(null);
            
            if(homeClient.isConnected()){
                connected = true;
                //imageStream("83.254.132.115", 10800, 0, cam1);
                //imageStream("83.254.132.115", 10800, 1, cam2);
                //imageStream("83.254.132.115", 10800, 2, cam3);
                //imageStream("83.254.132.115", 10800, 3, cam4);
                imageStream("83.254.132.115", 10800, 4, bigImageLbl);

                //imageStream("192.168.20.247", 5001, cam2.getName());
                //imageStream("192.168.20.248", 5001, cam3.getName());
                //imageStream("192.168.20.249", 5001, cam4.getName());
            }else{
                System.out.println("Can not connect to HOME server");
                connected = false;
            }
        }
	}

	private class PromptAlarm implements Runnable {
		@Override
		public void run() {
			try
			{
				long InitNumber = Files.list(Paths.get("C:\\Users\\user\\Downloads\\Final project\\images")).count();
				long currentNumber = 0;

				while (true)
				{	
					currentNumber = Files.list(Paths.get("C:\\Users\\user\\Downloads\\Final project\\images")).count();
					if (currentNumber > InitNumber)
					{
						JOptionPane.showMessageDialog(null, "Some motion have been detected!", "Motion detected", JOptionPane.WARNING_MESSAGE);
						Thread.sleep(1000);
						InitNumber = currentNumber;
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "The exception is: " + e);
			}
		}

	}
	
	public void setVideoResolution()
	{
		switch (resolutionSelection) {
		case 1:
			//Res = "1024x768";
			videoWidth = 1024;
			videoHight = 700;
/*			bigImageLbl.setBounds(220, 220, videoWidth, videoHight);
			stremThread[4].interrupt();
            imageStream("83.254.132.115", 10800, 4, bigImageLbl);*/
			this.setBounds(20, 20, 630 + videoWidth, 65 + videoHight);
			this.contentPane.setBounds(0 , 0, 240 + videoWidth, 30 + videoHight);
			break;
		case 2:
			//Res = "800x600";
			videoWidth = 800;
			videoHight = 600;
			this.setBounds(20, 20, 630 + videoWidth, 65 + videoHight);
			this.contentPane.setBounds(0 , 0, 240 + videoWidth, 30 + videoHight);
			break;
		case 3:
			//Res = "640x480";
			videoWidth = 640;
			videoHight = 480;
			this.setBounds(20, 20, 630 + videoWidth, 65 + videoHight);
			this.contentPane.setBounds(0 , 0, 240 + videoWidth, 30 + videoHight);
			break;
		case 4:
			//Res = "480x360";
			videoWidth = 480;
			videoHight = 360;
			this.setBounds(20, 20, 630 + videoWidth, 65 + videoHight);
			this.contentPane.setBounds(0 , 0, 240 + videoWidth, 30 + videoHight);
			break;
		case 5:
			//Res = "320x240";
			videoWidth = 320;
			videoHight = 240;
			this.setBounds(20, 20, 630 + videoWidth, 413);
			this.contentPane.setBounds(0, 0, 240 + videoWidth, 378);
			break;
		case 6:
			//Res = "240x180";
			videoWidth = 240;
			videoHight = 180;
			this.setBounds(20, 20, 630 + videoWidth, 413);
			this.contentPane.setBounds(0, 0, 240 + videoWidth, 378);
			break;
		case 7:
			//Res = "160x120";
			videoWidth = 160;
			videoHight = 120;
			this.setBounds(20, 20, 630 + videoWidth, 413);
			this.contentPane.setBounds(0, 0, 240 + videoWidth, 378);
			break;
		case 8:
			//Res = "1280x720";
			videoWidth = 1280;
			videoHight = 720;
			this.setBounds(20, 20, 630 + videoWidth, 65 + videoHight);
			this.contentPane.setBounds(20 , 20, 240 + videoWidth, 30 + videoHight);
			break;
		case 9:
			//Res = "800x450";
			videoWidth = 800;
			videoHight = 450;
			this.setBounds(20, 20, 630 + videoWidth, 65 + videoHight);
			this.contentPane.setBounds(0 , 0, 240 + videoWidth, 30 + videoHight);
			break;
		case 10:
			//Res = "640x360";
			videoWidth = 640;
			videoHight = 360;
			this.setBounds(2, 20, 630 + videoWidth, 65 + videoHight);
			this.contentPane.setBounds(0 , 0, 240 + videoWidth, 30 + videoHight);
			break;
		case 11:
			//Res = "480x270";
			videoWidth = 480;
			videoHight = 270;
			this.setBounds(20, 20, 630 + videoWidth, 413);
			this.contentPane.setBounds(0, 0, 240 + videoWidth, 378);
			break;
		case 12:
			//Res = "320x180";
			videoWidth = 320;
			videoHight = 180;
			this.setBounds(20, 20, 630 + videoWidth, 413);
			this.contentPane.setBounds(0, 0, 240 + videoWidth, 378);
			break;
		case 13:
			//Res = "160x90";
			videoWidth = 160;
			videoHight = 90;
			this.setBounds(20, 20, 630 + videoWidth, 413);
			this.contentPane.setBounds(0, 0, 240 + videoWidth, 378);
			break;
		case 14:
			videoWidth = 1280;
			videoHight = 800;
			this.setBounds(20, 20, 630 + videoWidth, 65 + videoHight);
			this.contentPane.setBounds(0 , 0, 240 + videoWidth, 30 + videoHight);
			break;
		case 15:
			videoWidth = 176;
			videoHight = 144;
			this.setBounds(20, 20, 630 + videoWidth, 413);
			this.contentPane.setBounds(0, 0, 240 + videoWidth, 378);
			break;
		}
	}



    private void imageStream(String IPAddress, int portNum, int index, JLabel viewLbl){
        //send command first
        this.homeClient.writeEncString("stream");
        
        this.homeClient.writeEncString(IPAddress.toString());
        this.homeClient.writeEncInt(Integer.valueOf(portNum));
        
        String response = this.homeClient.readEncString();
        if(!response.equals("Cammera connected")){
            System.out.println(response);
        }
        
        this.homeClient.writeEncInt(videoWidth);
        this.homeClient.writeEncInt(videoHight);
        this.homeClient.writeEncInt(fps);
        //System.out.println("IP" + IPAddress + " and port number = ");
        streamRunnable[index] = new Streamer(this, viewLbl); //init Runnable, and pass arg to thread 1 by constructor
        stremThread[index] = new Thread(streamRunnable[index]);
        stremThread[index].start();
    }
    
    class Streamer implements Runnable{
        private AdminGUI gui;
        private JLabel view;
        public Streamer(AdminGUI gui, JLabel viewLbl){
            this.gui=gui;
            this.view = viewLbl;
        }

        @Override
        public void run() {
            try {
                gui.streaming = true;
                while(gui.connected){
                    gui.homeClient.writeEncString("continue");
                    gui.imageBytes =  gui.homeClient.readEnc();
                    this.view.setIcon(new ImageIcon(gui.imageBytes));
                }
                gui.homeClient.writeEncString("end");
                System.out.println("closing stream");
                gui.streaming = false;
                this.view.setIcon(null);
            } catch (Exception e) {
                System.out.println("Can't read image stream");
                System.out.println(e.getMessage());
            }
            
        }
    }
}
