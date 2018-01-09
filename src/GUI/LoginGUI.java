package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class LoginGUI extends JFrame {

	private JPanel contentPane;
	private JTextField userName;
	private JPasswordField password;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginGUI frame = new LoginGUI();
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
	public LoginGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 399, 248);
		LoginGUI.this.setTitle("Camera Viewer - Login");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JLabel lblUsername = new JLabel();
		lblUsername.setText("Username:");
		lblUsername.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblUsername.setBounds(10, 11, 99, 40);
		panel.add(lblUsername);
		
		JLabel lblPassword = new JLabel();
		lblPassword.setText("Password:");
		lblPassword.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblPassword.setBounds(10, 86, 99, 40);
		panel.add(lblPassword);
		
		userName = new JTextField();
		userName.setText("alaa");
		userName.setFont(new Font("Tahoma", Font.BOLD, 18));
		userName.setBounds(119, 11, 238, 40);
		panel.add(userName);
		
		password = new JPasswordField();
		password.setBounds(119, 89, 238, 40);
		panel.add(password);
		
		JButton button = new JButton();
		button.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
		    	DataBaseManagement dbm = new DataBaseManagement();
		    	if (dbm.checkUserAndPass(userName.getText(), password.getText()))
		    	{
		    		if (dbm.getUserType(userName.getText()) == 1)
		    		{	
		    			AdminGUI adminGUI = new AdminGUI(userName.getText());
		    			LoginGUI.this.dispose();
		    			adminGUI.setVisible(true);
		    		}
		    		else
		    		{
		    			UsersGUI userGUI = new UsersGUI(userName.getText());
		    			LoginGUI.this.dispose();
		    			userGUI.setVisible(true);
		    		}
		    	}
			}
		});
		button.setText("Enter");
		button.setFont(new Font("Tahoma", Font.BOLD, 18));
		button.setBounds(10, 148, 109, 40);
		panel.add(button);
		
		JButton button_1 = new JButton();
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        userName.setText("");
		        password.setText("");
			}
		});
		button_1.setText("Reset");
		button_1.setFont(new Font("Tahoma", Font.BOLD, 18));
		button_1.setBounds(129, 148, 109, 40);
		panel.add(button_1);
		
		JButton button_2 = new JButton();
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LoginGUI.this.dispose();
			}
		});
		button_2.setText("Exit");
		button_2.setFont(new Font("Tahoma", Font.BOLD, 18));
		button_2.setBounds(248, 148, 109, 40);
		panel.add(button_2);
	}

}
