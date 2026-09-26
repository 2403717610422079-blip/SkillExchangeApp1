package com.skillexchange;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/skill_exchange";

    private static final String USER =
    		 System.getenv("SKILL_DB_USER");

    private static final String PASSWORD =
    		System.getenv("SKILL_DB_PASSWORD");

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
