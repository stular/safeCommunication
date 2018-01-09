package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.JSeparator;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

@SuppressWarnings("serial")
public class CamerasManagementGUI extends JFrame {

	private JPanel contentPane;
	private JTable usersTable;
	private JButton deleteUserBtn;
	private JButton addNewUserBtn;
	private JButton updateUserPasswordBtn;
	private JButton exitBtn;
	private JPanel tablePanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CamerasManagementGUI frame = new CamerasManagementGUI();
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
	@SuppressWarnings("serial")
	public CamerasManagementGUI() {
		DataBaseManagement dbm = new DataBaseManagement();
		String userNames[] = dbm .getUserNames();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		CamerasManagementGUI.this.setTitle("Cameras Management Window");
		setBounds(100, 100, 537, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		deleteUserBtn = new JButton("Delete User");
		deleteUserBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (usersTable.getSelectedRow() == -1)
					JOptionPane.showMessageDialog(null, "Select one of users by clicking on the row");
				else
				{
					String userNameToDelete = String.valueOf(usersTable.getValueAt(usersTable.getSelectedRow(), 0));
					dbm.deleteUser(userNameToDelete);
					updateTable();
				}
			}
		});
		deleteUserBtn.setBounds(296, 217, 106, 23);
		panel.add(deleteUserBtn);
		
		addNewUserBtn = new JButton("Add User");
		addNewUserBtn.addActionListener(new ActionListener() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public void actionPerformed(ActionEvent e) {
				JTextField userName = new JTextField(15);
				JTextField password = new JTextField(15);
				JComboBox comboBox = new JComboBox();
				comboBox.setModel(new DefaultComboBoxModel(new String[] { "User", "Admin" }));
				comboBox.setBounds(111, 0, 40, 20);

				JPanel myPanel = new JPanel();
				myPanel.add(new JLabel("Enter User Name:"));
				myPanel.add(userName);
				myPanel.add(new JSeparator());
				myPanel.add(new JLabel("Enter Password:"));
				myPanel.add(password);
				myPanel.add(new JSeparator());
				myPanel.add(new JLabel("Enter user privilege:"));
				myPanel.add(comboBox);
				
				int result = JOptionPane.showConfirmDialog(null, myPanel, "Add new user", JOptionPane.CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION)
				{
					if (comboBox.getSelectedItem().equals("Admin"))
						dbm.addUser(userName.getText(), password.getText(), 1);
					else
						dbm.addUser(userName.getText(), password.getText(), 0);
					updateTable();
				}
			}
		});
		addNewUserBtn.setBounds(10, 217, 90, 23);
		panel.add(addNewUserBtn);
		
		updateUserPasswordBtn = new JButton("Update User Password");
		updateUserPasswordBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (usersTable.getSelectedRow() == -1)
					JOptionPane.showMessageDialog(null, "Select one of users by clicking on the row");
				else {
					JTextField password = new JTextField(15);
					JPanel myPanel = new JPanel();
					myPanel.add(new JLabel("Enter Password:"));
					myPanel.add(password);
					String userNameToUpdate = String.valueOf(usersTable.getValueAt(usersTable.getSelectedRow(), 0));
					boolean flag = true;
					int result;
					while (flag) {
						password.setText("");
						result = JOptionPane.showConfirmDialog(null, myPanel, "Update " + userNameToUpdate + " password",
								JOptionPane.CANCEL_OPTION);
						if (result == JOptionPane.OK_OPTION)
							if (password.getText().length() < 5)
								JOptionPane.showMessageDialog(null, "Password should be at least 5 characters!");
							else {
								flag = false;
								dbm.updatePasswords(userNameToUpdate, password.getText());
								updateTable();
							}
						else
							flag = false;
					}
				}
			}
		});
		updateUserPasswordBtn.setBounds(110, 217, 176, 23);
		panel.add(updateUserPasswordBtn);
		
		exitBtn = new JButton("Close");
		exitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CamerasManagementGUI.this.dispose();
			}
		});
		exitBtn.setBounds(412, 217, 89, 23);
		panel.add(exitBtn);
		
		tablePanel = new JPanel();
		tablePanel.setBorder(new TitledBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Users", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), "Cameras", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		tablePanel.setBounds(10, 11, 436, 195);
		panel.add(tablePanel);
		tablePanel.setLayout(null);
		
		usersTable = new JTable();
		usersTable.setBounds(15, 22, 400, 150);
		usersTable.setModel(new DefaultTableModel(
				new Object[userNames.length][2],
				new String[] {
					"User Name", "Password"
				}
			) {
				@SuppressWarnings("rawtypes")
				Class[] columnTypes = new Class[] {
					Object.class, Object.class
				};
				@SuppressWarnings({ "unchecked", "rawtypes" })
				public Class getColumnClass(int columnIndex) {
					return columnTypes[columnIndex];
				}
			});
		
		for (int i = 0; i < userNames.length; i++)
		{
			usersTable.setValueAt(userNames[i], i, 0);
			usersTable.setValueAt(dbm.getPassword(userNames[i]), i, 1);
		}
		usersTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		usersTable.getColumnModel().getColumn(1).setPreferredWidth(200);	
		
		tablePanel.add(usersTable);
			}
	
	@SuppressWarnings("serial")
	public void updateTable()
	{		
		DataBaseManagement dbm = new DataBaseManagement();
		String userNames[] = dbm .getUserNames();
		
		usersTable.setModel(new DefaultTableModel(
				new Object[userNames.length][2],
				new String[] {
					"User Name", "Password"
				}
			) {
				@SuppressWarnings("rawtypes")
				Class[] columnTypes = new Class[] {
					Object.class, Object.class
				};
				@SuppressWarnings({ "unchecked", "rawtypes" })
				public Class getColumnClass(int columnIndex) {
					return columnTypes[columnIndex];
				}
			});

		for (int i = 0; i < userNames.length; i++)
		{
			usersTable.setValueAt(userNames[i], i, 0);
			usersTable.setValueAt(dbm.getPassword(userNames[i]), i, 1);
		}
	}
}