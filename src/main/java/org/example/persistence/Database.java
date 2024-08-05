package org.example.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private final String host;
    private final String port;
    private final String dbName;
    private final String dbUser;
    private final String dbPassword;

    public Database(String host, String port, String dbName, String dbUser, String dbPassword) {
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    private Connection connection = null;

    public Connection openConnection() {
        var url = String.format("jdbc:mysql://%s:%s/%s", host, port, dbName);

        try {
            this.connection = DriverManager.getConnection(url, dbUser, dbPassword);
            System.out.println();
            System.out.println("Database connection established");
            System.out.println();
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: Couldn't connect to the database");
        }
        return this.connection;
    }

    public void closeConnection() {
        if (connection == null) return;
        try {
            if (connection.isClosed()) return;
            connection.close();
            System.out.println();
            System.out.println("Database connection closed");
            System.out.println();
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: A database error occurred");
        }
    }

    public void testConnection() {
        var con = openConnection();
        try (
             var statement = con.createStatement();
             var result = statement.executeQuery("SELECT 1 + 2 as result")) {
            result.next();
            System.out.println(result.getInt("result"));
        } catch (SQLException e) {
            System.out.println("Error testing database connection");
        } finally {
            closeConnection();
        }
    }
}
