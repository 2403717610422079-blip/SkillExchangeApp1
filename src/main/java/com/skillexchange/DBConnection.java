package com.skillexchange;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String HOST =
            System.getenv().getOrDefault("DB_HOST", "localhost");

    private static final String PORT =
            System.getenv().getOrDefault("DB_PORT", "3306");

    private static final String DATABASE =
            System.getenv().getOrDefault("DB_NAME", "skill_exchange");

    private static final String USER =
            System.getenv("SKILL_DB_USER");

    private static final String PASSWORD =
            System.getenv("SKILL_DB_PASSWORD");

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    public static Connection getConnection() throws Exception {

        if (USER == null || PASSWORD == null) {
            throw new Exception("Database credentials are not configured.");
        }

        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}