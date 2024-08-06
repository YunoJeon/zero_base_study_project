package org.example.wifiproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// 데이터베이스 연결 관리
public class DBConnection {

    private static final String URL = "jdbc:sqlite:/Users/jeon-yuno/Desktop/WifiProject/WIfiProject/wifi.db";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite driver not found");
        }
        return DriverManager.getConnection(URL);
    }
}
