package org.example.wifiproject;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
// 데이터베이스, 테이블 초기화
@WebListener
public class DBInitializer implements ServletContextListener {
    private static final String URL = "jdbc:sqlite:/Users/jeon-yuno/Desktop/WifiProject/WIfiProject/wifi.db";

    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS wifi_info (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "X_SWIFI_MGR_NO TEXT NOT NULL, " +
            "X_SWIFI_WRDOFC TEXT, " +
            "X_SWIFI_MAIN_NM TEXT, " +
            "X_SWIFI_ADRES1 TEXT, " +
            "X_SWIFI_ADRES2 TEXT, " +
            "X_SWIFI_INSTL_FLOOR TEXT, " +
            "X_SWIFI_INSTL_TY TEXT, " +
            "X_SWIFI_INSTL_MBY TEXT, " +
            "X_SWIFI_SVC_SE TEXT, " +
            "X_SWIFI_CMCWR TEXT, " +
            "X_SWIFI_CNSTC_YEAR TEXT, " +
            "X_SWIFI_INOUT_DOOR TEXT, " +
            "X_SWIFI_REMARS3 TEXT, " +
            "LAT TEXT, " +
            "LNT TEXT, " +
            "WORK_DTTM TEXT" +
            ");";

    private static final String CREATE_HISTORY_TABLE_SQL = "CREATE TABLE IF NOT EXISTS wifi_history (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "LAT DOUBLE NOT NULL, " +
            "LNT DOUBLE NOT NULL, " +
            "query_date DATETIME DEFAULT CURRENT_TIMESTAMP " +
            ");";

    private static final String CREATE_BOOKMARK_SQL = "CREATE TABLE IF NOT EXISTS show_bookmark (" +
            "wifi_id INTEGER, " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL, " +
            "wifi_name TEXT NOT NULL, " +
            "create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (wifi_id) REFERENCES wifi_info(id) " +
            ");";

    private static final String CREATE_BOOKMARK_GROUP_SQL = "CREATE TABLE IF NOT EXISTS bookmark_group (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL, " +
            "orderNumber INTEGER NOT NULL, " +
            "create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
            ");";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = DriverManager.getConnection(URL);
                 Statement stmt = conn.createStatement()) {
                stmt.execute(CREATE_TABLE_SQL);
                stmt.execute(CREATE_HISTORY_TABLE_SQL);
                stmt.execute(CREATE_BOOKMARK_SQL);
                stmt.execute(CREATE_BOOKMARK_GROUP_SQL);
                System.out.println("Table created");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}