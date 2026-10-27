package com.foodwise.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection provides JDBC database connectivity to MySQL.
 * Configured for MySQL standard port 3306.
 */
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/foodwise_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Update with your MySQL password if configured

    private static boolean driverLoaded = false;
    private static boolean mysqlAvailable = true;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            driverLoaded = true;
        } catch (ClassNotFoundException e) {
            try {
                // Legacy driver fallback
                Class.forName("com.mysql.jdbc.Driver");
                driverLoaded = true;
            } catch (ClassNotFoundException ex) {
                System.out.println("[DBConnection] Note: MySQL JDBC Driver not found in classpath.");
                System.out.println("[DBConnection] FOODWISE will run in memory-backed mode with sample data.");
                driverLoaded = false;
                mysqlAvailable = false;
            }
        }
    }

    public static Connection getConnection() {
        if (!driverLoaded || !mysqlAvailable) {
            return null;
        }
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            if (mysqlAvailable) {
                System.out.println("[DBConnection] MySQL server not reachable on localhost:3306 (" + e.getMessage() + ")");
                System.out.println("[DBConnection] Seamlessly utilizing built-in active state matching presentation slides.");
                mysqlAvailable = false;
            }
            return null;
        }
    }

    public static boolean isConnectedToMySQL() {
        return mysqlAvailable;
    }
}
