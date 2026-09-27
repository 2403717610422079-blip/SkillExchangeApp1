package com.skillexchange;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBConnection {

    private static volatile boolean tablesInitialized = false;

    private static String getEnv(String primaryKey, String fallbackKey, String defaultValue) {
        String val = System.getenv(primaryKey);
        if (val != null && !val.trim().isEmpty()) {
            return val.trim();
        }
        if (fallbackKey != null) {
            String fallbackVal = System.getenv(fallbackKey);
            if (fallbackVal != null && !fallbackVal.trim().isEmpty()) {
                return fallbackVal.trim();
            }
        }
        return defaultValue;
    }

    public static Connection getConnection() throws Exception {
        String host = getEnv("DB_HOST", "MYSQLHOST", "localhost");
        String port = getEnv("DB_PORT", "MYSQLPORT", "3306");
        String database = getEnv("DB_NAME", "MYSQLDATABASE", "skill_exchange");
        String user = getEnv("SKILL_DB_USER", "MYSQLUSER", null);
        String password = getEnv("SKILL_DB_PASSWORD", "MYSQLPASSWORD", null);

        if (user == null || password == null) {
            throw new Exception("Database credentials are not configured. Please set SKILL_DB_USER and SKILL_DB_PASSWORD (or MYSQLUSER and MYSQLPASSWORD).");
        }

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&autoReconnect=true";

        Class.forName("com.mysql.cj.jdbc.Driver");

        Connection con = DriverManager.getConnection(url, user, password);

        if (!tablesInitialized) {
            synchronized (DBConnection.class) {
                if (!tablesInitialized) {
                    initializeDatabase(con);
                    tablesInitialized = true;
                }
            }
        }

        return con;
    }

    private static void initializeDatabase(Connection con) {
        try (Statement stmt = con.createStatement()) {
            // Create users table if it does not already exist
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                "    user_id INT PRIMARY KEY AUTO_INCREMENT," +
                "    name VARCHAR(100) NOT NULL," +
                "    email VARCHAR(100) UNIQUE NOT NULL," +
                "    password VARCHAR(100) NOT NULL" +
                ")"
            );

            // Create skills table if it does not already exist
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS skills (" +
                "    skill_id INT PRIMARY KEY AUTO_INCREMENT," +
                "    user_id INT NOT NULL," +
                "    skill_name VARCHAR(100) NOT NULL," +
                "    skill_description VARCHAR(255)," +
                "    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ")"
            );

            // Create exchange_requests table if it does not already exist
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS exchange_requests (" +
                "    request_id INT PRIMARY KEY AUTO_INCREMENT," +
                "    sender_id INT NOT NULL," +
                "    receiver_id INT NOT NULL," +
                "    skill_id INT NOT NULL," +
                "    status VARCHAR(20) DEFAULT 'Pending'," +
                "    FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE," +
                "    FOREIGN KEY (receiver_id) REFERENCES users(user_id) ON DELETE CASCADE," +
                "    FOREIGN KEY (skill_id) REFERENCES skills(skill_id) ON DELETE CASCADE" +
                ")"
            );
        } catch (Exception e) {
            // Log but don't prevent connection if tables already exist or user lacks DDL privileges
            System.err.println("Database table initialization check: " + e.getMessage());
        }
    }
}