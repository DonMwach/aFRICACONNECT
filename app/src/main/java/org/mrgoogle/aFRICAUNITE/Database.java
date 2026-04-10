/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.mrgoogle.aFRICAUNITE;

/**
 *
 * @author mRGoogle
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Database {

    private static final String URL = "jdbc:postgresql://localhost:5432/africa_connect";
    private static final String USER = "postgres";
    private static final String PASSWORD = "12345"; // <-- your PostgreSQL password

    public static Connection connect() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static String getPasswordHash(String username, String email) {
        try {
            Connection conn = connect();

            String sql = "SELECT password_hash FROM users WHERE username = ? OR email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, email);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("password_hash");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void saveUser(String username, String email, String passwordHash) {
        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";

        try (Connection conn = connect(); 
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, passwordHash);
            
            stmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
            // You might want to show an error dialog here if the save fails
        }
    }

    public static boolean userExists(String email) {
        try {
            Connection conn = connect();

            String sql = "SELECT 1 FROM users WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
