package Infrastructure;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import InternetSecurity.User;

/**
 *
 * @author jaisha
 */
public class LoginDatabase {

    /**
     * Connect to the UserDB.db database
     *
     * @return the Connection object
     */
    public Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:C:\\Users\\jaish\\OneDrive\\Documents\\NetBeansProjects\\connectedProject\\safeCommunication_refactored.zip (Unzipped Files)\\safeCommunication\\src\\Infrastructure\\UserDB.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("An error has been occured: " + e.getMessage());
        }
        return conn;
    }

    /**
     * select all rows in the UserDetails table
     */
    public boolean userValidation(String name1, String password1) {
        boolean bLogin = false;
        User secureUserCred = new User(name1, password1, true);

        String sql = "SELECT UserName, Password FROM UserDetails WHERE UserName=\"" + secureUserCred.getUserName() + "\" AND Password =\"" + secureUserCred.getPasswordDigestHexString() + "\"";

        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                bLogin = true;
                conn.close();
            } 
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }

        return (bLogin);
    }

//    To list out all users
    public void readUserDetails() {
        String sql = "SELECT UserName, Password FROM UserDetails";

        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString("UserName") + "\t" + rs.getString("Password"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Insert a new row into the UserDetails table
     *
     * @param UserName
     * @param Password
     */
    public void insertUserDetail(String name1, String password1) {
        String sql = "INSERT INTO UserDetails(UserName, Password) VALUES(?,?)";
// to check if a user already exists in DB
        String sql2 = "SELECT UserName, Password FROM UserDetails WHERE " + "UserName =\"" + name1 + "\"";

        User secureUserCred = new User(name1, password1, true);

//        name1 =   secureUserCred.getUserName();
//        password1 = secureUserCred.getPasswordDigestHexString() ; 
////        
//Statement stmt = conn.createStatement()
//              ResultSet rs = stmt.executeQuery(sql2))
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, secureUserCred.getUserName());
            pstmt.setString(2, secureUserCred.getPasswordDigestHexString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

//    // Delete a row from the UserDetails table
//    public void deleteUserDetail(String Name1) {
//        String sql = "DELETE FROM UserDetails WHERE UserName = ?";
//
//        try (Connection conn = this.connect();
//                PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            // set the corresponding param
//            pstmt.setString(1, Name1);
//            // execute the delete statement
//            pstmt.executeUpdate();
//
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LoginDatabase app = new LoginDatabase();

// Read all records from  the table
//        app.readUserDetails();
//        app.insertUserDetail("superuser", "abcd");
//        app.insertUserDetail("nuser1", "qwerty");
//        app.insertUserDetail("nuser2", "qwert");
////
//        // Read all records from  the table again
        app.readUserDetails();

        if (app.userValidation("superuser", "abcd2")) {
            System.out.println("+++++++++++LOGIN SUCCESS +++++++++++++");
        } else {
            System.out.println("+++++++++++ LOGIN FAILED +++++++++++++");
        }
//
        // Delete records from the table
//        app.deleteUserDetail("LG1");
//        app.deleteUserDetail("LG2");
//        System.out.println ("JAISH+++++++++++++++++++++++++++++++++++++++");
//        app.readUserDetails();
// 
    }

}
