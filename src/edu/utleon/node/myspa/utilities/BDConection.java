package edu.utleon.node.myspa.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BDConection {

    private Connection conn;

    public BDConection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String user = "root";
            String pass = "root";
            String url = "jdbc:mysql://127.0.0.1:3306/myspa";
            
            conn = DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    public Connection getConexion() {
        return conn;
    }
    
    public void closeConetion() throws SQLException
    {
        conn.close();
    }
}
