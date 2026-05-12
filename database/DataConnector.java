package database;

import java.sql.*;

public class DataConnector {

    private String url      = "jdbc:mysql://localhost:3306/users";
    private String username = "root";
    private String password = "new_password";
    private Connection con;

    public DataConnector() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    // Returns true if login is valid
    public boolean checkLogin(String email, String password) {
        try {
            String query = "SELECT * FROM user WHERE email = ? AND password = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (Exception ex) {
            System.out.println("Login error: " + ex.getMessage());
            return false;
        }
    }

    // Returns true if email exists in the database
    public boolean emailExists(String email) {
        try {
            String query = "SELECT * FROM user WHERE email = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();   // true = found, false = not found
        } catch (Exception ex) {
            System.out.println("Email check error: " + ex.getMessage());
            return false;
        }
    }

    // Returns "success", "exists", or "error"
    public String registerUser(String email, String password) {
        try {
            String checkQuery = "SELECT * FROM user WHERE email = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                return "exists";
            }

            String insertQuery = "INSERT INTO user (email, password) VALUES (?, ?)";
            PreparedStatement pstmt = con.prepareStatement(insertQuery);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return "success";

        } catch (Exception ex) {
            System.out.println("Registration error: " + ex.getMessage());
            return "error";
        }
    }

    // Returns true if email exists and password was updated
    public boolean resetPassword(String email, String newPassword) {
        try {
            String query = "UPDATE user SET password = ? WHERE email = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, newPassword);
            pstmt.setString(2, email);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (Exception ex) {
            System.out.println("Reset error: " + ex.getMessage());
            return false;
        }
    }

    public void closeConnection() {
        try {
            if (con != null && !con.isClosed()) con.close();
        } catch (SQLException e) {
            System.out.println("Error closing: " + e.getMessage());
        }
    }
}