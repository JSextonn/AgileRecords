package main.java.util.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Holds tools used for reading and writing to a specified database.
 */
public abstract class DatabaseUtil {

    /**
     * Use to obtain an sql connection.
     * NOTE: This function is temporary.
     * WARNING: MAY RETURN NULL
     *
     * @return The sql connection.
     */
    public static Connection getConnection(final String host,
                                           final String password,
                                           final String dbName) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("No MySQL JDBC Driver Detected!");
            e.printStackTrace();
        }

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(host, password, dbName);
        } catch (SQLException e) {
            System.err.println("Connection Failed!");
            e.printStackTrace();
        }
        return connection;
    }

    // TODO: Add remaining functions
}