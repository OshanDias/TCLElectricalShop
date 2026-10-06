package com.tclelectrical.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton Design Pattern - one shared DB connection for the whole app.
 */
public class DBConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/tcl_electrical_shop_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Root@1234";   // <= change this to ur password

    private static DBConnection instance;   // the single instance
    private Connection connection;

    private DBConnection() {                // private constructor = no external 'new'
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found. Check the connector jar.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed: " + e.getMessage(), e);
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not re-open connection: " + e.getMessage(), e);
        }
        return connection;
    }
}