package org.ensah.dao;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;

public class DatabaseManager {
    private static volatile Connection connection;
    private static final Properties properties = new Properties();

    static {
        initializeDatabaseSystem();
    }

    private static synchronized void initializeDatabaseSystem() {
        try (InputStream input = DatabaseManager.class.getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (input == null) {
                throw new IllegalStateException("db.properties file not found in classpath");
            }

            properties.load(input);
            validateConfiguration();
            Class.forName("org.h2.Driver");

        } catch (Exception e) {
            throw new DatabaseInitializationException("Database initialization failed", e);
        }
    }

    private static void validateConfiguration() {
        if (!properties.containsKey("db.url") || !properties.containsKey("db.user")) {
            throw new IllegalStateException("Missing required database properties");
        }
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.user"),
                    properties.getProperty("db.password", "") // Default empty password
            );
            initializeDatabaseSchema();
        }
        return connection;
    }

    private static void initializeDatabaseSchema() {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction

            stmt = conn.createStatement();

            // Create players table (corrected missing comma)
            stmt.execute("CREATE TABLE IF NOT EXISTS players (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(100) NOT NULL, " + // Comma added here
                    "wins INT DEFAULT 0, " +
                    "losses INT DEFAULT 0, " +
                    "draws INT DEFAULT 0)");

            // Create games table (corrected FOREIGN KEY spelling)
            stmt.execute("CREATE TABLE IF NOT EXISTS games (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "player1_id INT NOT NULL, " +
                    "player2_id INT NOT NULL, " +
                    "winner_id INT, " +
                    "start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "end_time TIMESTAMP, " +
                    "FOREIGN KEY (player1_id) REFERENCES players(id), " + // Corrected spelling
                    "FOREIGN KEY (player2_id) REFERENCES players(id), " + // Corrected spelling
                    "FOREIGN KEY (winner_id) REFERENCES players(id))"); // Corrected spelling

            conn.commit(); // Commit transaction

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new DatabaseInitializationException("Failed to rollback transaction", ex);
                }
            }
            throw new DatabaseInitializationException("Database schema initialization failed", e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("Error closing statement: " + e.getMessage());
                }
            }
        }
    }

    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null; // Ensure reference is cleared
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    // Custom exception
    private static class DatabaseInitializationException extends RuntimeException {
        public DatabaseInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}