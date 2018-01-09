package GUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import InternetSecurity.User;

public class DataBaseManagement {

	private Connection con;
	private PreparedStatement pst;
	private ResultSet rs;
	public static void main(String[] args) {

	}
	public DataBaseManagement()
	{
	    try {
			con = DriverManager.getConnection("jdbc:mysql://localhost/usersdb", "root","");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    pst = null;
	    rs = null;
	}

	public  boolean checkUserAndPass(String userName, String password)
	{
		User user = new User(userName, password, true);
	       String sql = "select * from usersinfo where username=? and password=?";
	       try{
	           pst = con.prepareStatement(sql);
	           pst.setString(1, user.getUserName());
	          
	           pst.setString(2, user.getPasswordDigestHexString());
	           rs= pst.executeQuery();
	           
	           if(rs.next()){
	        	   return true; 
	           }else
	        	   {
	        		   JOptionPane.showMessageDialog(null, "Error in Username or Password");
	        		   return false;
	        	   }
	           }
	       catch(Exception ex){
	           JOptionPane.showMessageDialog(null, ex);
	       }
		return false;
	}
	
	public int getUserType(String userName)
	{
	       String sql = "select UserType from usersinfo where username=?";
	       try{
	    	   
	           pst = con.prepareStatement(sql);
	           pst.setString(1, userName);
	           rs= pst.executeQuery();
	           
	           if(rs.next()){
	        	   return rs.getInt("UserType"); 
	           }
	           else
	        	   return 0;
	           }
	       catch(Exception ex){
	           JOptionPane.showMessageDialog(null, ex);
	       }
	       return 0;
	}
	
	public boolean getCameraPrivilege (String userName, int cameraNumber)
	{
	       String sql = "select * from usersinfo where username=?";
	       try{
	           pst = con.prepareStatement(sql);
	           pst.setString(1, userName);
	           rs= pst.executeQuery();
	           String cameraName = "Cam" + String.valueOf(cameraNumber);
	           
	           if(rs.next()){
	        	   if (rs.getInt(cameraName) == 1)
	        		   return true;
	        	   else 
	        		   return false;
	        	   }
	           else
	        	   return true;
	           }
	       catch(Exception ex){
	           JOptionPane.showMessageDialog(null, "THe Exception is :    " + ex);
	       }
		return true;
	}

	public void setCameraPrivilegeToNegative (String userName, int cameraNumber)
	{
		String cameraName = "Cam" + String.valueOf(cameraNumber);
		String sql = "update usersinfo set " + cameraName + " = ? where username = ?";
	       try{
	           pst = con.prepareStatement(sql);

	           pst.setInt(1, 0);
	           pst.setString(2, userName);
	           
	           pst.executeUpdate();
	       }
	       catch(Exception ex){
	           JOptionPane.showMessageDialog(null, "THe Exception is :    " + ex);
	       }
	}
	
	public void setCameraPrivilegeToPositive (String userName, int cameraNumber)
	{
		String cameraName = "Cam" + String.valueOf(cameraNumber);
		String sql = "update usersinfo set " + cameraName + " = ? where username = ?";
	       try{
	           pst = con.prepareStatement(sql);

	           pst.setInt(1, 1);
	           pst.setString(2, userName);
	           
	           pst.executeUpdate();
	       }
	       catch(Exception ex){
	           JOptionPane.showMessageDialog(null, "THe Exception is :    " + ex);
	       }
	}

	public String[] getUserNames()
	{
		String[] result = null;
	       String sql = "select username from usersinfo";
	       try{
	    	   
	           pst = con.prepareStatement(sql);
	           rs= pst.executeQuery();

	           ArrayList<String> list= new ArrayList<String>();
	           while (rs.next()) {
	               list.add(rs.getString("UserName"));   
	           } 

	           result = new String[list.size()];
	           result = list.toArray(result);
	       }
	       catch(Exception ex){
	           JOptionPane.showMessageDialog(null, ex);
	       }
		return result;
	}

	public String getPassword(String userName)
	{
		String result = null;
	       String sql = "select password from usersinfo where username=?";
	       try{  
	           pst = con.prepareStatement(sql);
	           pst.setString(1, userName);
	           rs= pst.executeQuery();

	           if (rs.next()) 
	               result = rs.getString("password");
	       }
	       catch(Exception ex){
	           JOptionPane.showMessageDialog(null, ex);
	       }
		return result;
	}
	
	public boolean getCameraPrivilegeForSingleUser (String userName, int cameraNumber)
	{
	       String sql = "select * from userprivilege where username=?";
	       try{
	           pst = con.prepareStatement(sql);
	           pst.setString(1, userName);
	           rs= pst.executeQuery();
	           String cameraName = "Cam" + String.valueOf(cameraNumber);
	           
	           if(rs.next()){
	        	   if (rs.getInt(cameraName) == 1)
	        		   return true;
	        	   else 
	        		   return false;
	        	   }
	           else
	        	   return true;
	           }
	       catch(Exception ex){
	           JOptionPane.showMessageDialog(null, "THe Exception is :    " + ex);
	       }
		return true;
	}

	public void setCameraPrivilegeToNegativeForSingleUser (String userName, int cameraNumber)
	{
		String cameraName = "Cam" + String.valueOf(cameraNumber);
		String sql = "update userprivilege set " + cameraName + " = ? where username = ?";
	       try{
	           pst = con.prepareStatement(sql);

	           pst.setInt(1, 0);
	           pst.setString(2, userName);
	           
	           pst.executeUpdate();
	       }
	       catch(Exception ex){
	           JOptionPane.showMessageDialog(null, "THe Exception is :    " + ex);
	       }
	}
	
	public void setCameraPrivilegeToPositiveForSingleUser (String userName, int cameraNumber)
	{
		String cameraName = "Cam" + String.valueOf(cameraNumber);
		String sql = "update userprivilege set " + cameraName + " = ? where username = ?";
	       try{
	           pst = con.prepareStatement(sql);

	           pst.setInt(1, 1);
	           pst.setString(2, userName);
	           
	           pst.executeUpdate();
	       }
	       catch(Exception ex){
	           JOptionPane.showMessageDialog(null, "THe Exception is :    " + ex);
	       }
	}

	public void addUser(String userName, String password, int privilege) {
		boolean exist = false;
		String userNames[] = this.getUserNames();
		for (int i = 0; i < userNames.length; i++)
			if (userName.equals(userNames[i]))
				exist = true;
		if (exist)
			JOptionPane.showMessageDialog(null, "This user name is already registered, enter another one!");
		else {
			User user = new User(userName, password, true);
			String sql1 = "insert into usersinfo (userName, password, userType) values (?, ?, ?)";
			String sql2 = "insert into userprivilege (userName) values (?)";
			try {
				if (privilege == 0) {
					pst = con.prepareStatement(sql1);
					pst.setString(1, user.getUserName());
					pst.setString(2, user.getPasswordDigestHexString());
					pst.setInt(3, 0);
					pst.execute();

					pst.clearParameters();

					pst = con.prepareStatement(sql2);
					pst.setString(1, user.getUserName());
					pst.execute();
				}

				if (privilege == 1) 
				{
					pst = con.prepareStatement(sql1);
					pst.setString(1, user.getUserName());
					pst.setString(2, user.getPasswordDigestHexString());
					pst.setInt(3, 1);
					pst.execute();
				}
				JOptionPane.showMessageDialog(null,
						"Please note that new users have privilege to all cameras by default!");
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, ex);
			}
		}
	}

	public void updatePasswords(String userName, String password)
	{
		User user = new User(userName, password, true);
		String sql = "update usersinfo set password = ? where username = ?";
	       try{
	           pst = con.prepareStatement(sql);

	           pst.setString(1, user.getPasswordDigestHexString());
	           pst.setString(2, user.getUserName());
	           
	           pst.executeUpdate();
	           JOptionPane.showMessageDialog(null, "Password Updated!");
	       }
	       catch(Exception ex){
	           JOptionPane.showMessageDialog(null, "THe Exception is :    " + ex);
	       
	}
	}

	public void deleteUser(String userName) {
		String sql1 = "delete from usersinfo where username = ?";
		String sql2 = "delete from userprivilege where username = ?";
		try {
			int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + userName + "\'s account ?", "Delete user", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (confirm == JOptionPane.OK_OPTION) {
				pst = con.prepareStatement(sql1);
				pst.setString(1, userName);
				pst.execute();
				
				pst.clearParameters();
				
				pst = con.prepareStatement(sql2);
				pst.setString(1, userName);
				pst.execute();				
		
				JOptionPane.showMessageDialog(null, "User deleted!");
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "THe Exception is :    " + ex);

		}
	}

/*	public String[][] getCameras()
	{
		String[][] result = null;
	       String sql = "select * from cameras";
	       try{
	    	   
	           pst = con.prepareStatement(sql);
	           rs= pst.executeQuery();

	           ArrayList<String> list= new ArrayList<String>();
	           while (rs.next()) {
	               list.add(rs.getString("UserName"));   
	           } 
	           for int row = 0; row < 5; row++)
				{
					result[row][0] = rs.getString("IPAddress");
					for (int col = 1; col < 3; col++)
						result[row][col] = 
				}
	           result = new String[list.size()];
	           result = list.toArray(result);
	       }
	       catch(Exception ex){
	           JOptionPane.showMessageDialog(null, ex);
	       }
		return result;
	}*/
	
	public void addCamera(String cameraName, String ipAddress, int portNumber)
	{
		
	}
	
	public void updateCamera(String cameraName, String ipAddress, int portNumber)
	{
		
	}
	
	public void deleteCamera(String ipAddress)
	{
		
	}
}
