package org.example.wifiproject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/wifiDetail")
public class WifiDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String latStr = req.getParameter("lat");
        String lntStr = req.getParameter("lnt");

        System.out.println("Requested ID: " + id);
        System.out.println("Latitude: " + latStr);
        System.out.println("Longitude: " + lntStr);

        if (id == null || latStr == null || lntStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid or missing parameters");
            return;
        }
        try {
            double lat = Double.parseDouble(latStr);
            double lnt = Double.parseDouble(lntStr);

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT *, " +
                        "       (6371 * sqrt(" +
                        "           pow(sin((? - LAT) * pi() / 180 / 2), 2) + " +
                        "           cos(? * pi() / 180) * cos(LAT * pi() / 180) * " +
                        "           pow(sin((? - LNT) * pi() / 180 / 2), 2)" +
                        "       )) AS DISTANCE " +
                        "FROM wifi_info " +
                        "WHERE id = ?";

                System.out.println("Executing SQL query with parameters: " +
                        "lat=" + lat + ", lnt=" + lnt + ", id=" + id);

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setDouble(1, lat);
                    pstmt.setDouble(2, lat);
                    pstmt.setDouble(3, lnt);
                    pstmt.setInt(4, Integer.parseInt(id));

                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            WifiInfo wifi = new WifiInfo();
                            wifi.setId(rs.getInt("id"));
                            wifi.setDISTANCE(rs.getDouble("DISTANCE"));
                            wifi.setX_SWIFI_MGR_NO(rs.getString("X_SWIFI_MGR_NO"));
                            wifi.setX_SWIFI_WRDOFC(rs.getString("X_SWIFI_WRDOFC"));
                            wifi.setX_SWIFI_MAIN_NM(rs.getString("X_SWIFI_MAIN_NM"));
                            wifi.setX_SWIFI_ADRES1(rs.getString("X_SWIFI_ADRES1"));
                            wifi.setX_SWIFI_ADRES2(rs.getString("X_SWIFI_ADRES2"));
                            wifi.setX_SWIFI_INSTL_FLOOR(rs.getString("X_SWIFI_INSTL_FLOOR"));
                            wifi.setX_SWIFI_INSTL_TY(rs.getString("X_SWIFI_INSTL_TY"));
                            wifi.setX_SWIFI_INSTL_MBY(rs.getString("X_SWIFI_INSTL_MBY"));
                            wifi.setX_SWIFI_SVC_SE(rs.getString("X_SWIFI_SVC_SE"));
                            wifi.setX_SWIFI_CMCWR(rs.getString("X_SWIFI_CMCWR"));
                            wifi.setX_SWIFI_CNSTC_YEAR(rs.getString("X_SWIFI_CNSTC_YEAR"));
                            wifi.setX_SWIFI_INOUT_DOOR(rs.getString("X_SWIFI_INOUT_DOOR"));
                            wifi.setX_SWIFI_REMARS3(rs.getString("X_SWIFI_REMARS3"));
                            wifi.setLAT(rs.getDouble("LAT"));
                            wifi.setLNT(rs.getDouble("LNT"));
                            wifi.setWORK_DTTM(rs.getString("WORK_DTTM"));

                            DecimalFormat df = new DecimalFormat("0.####");
                            String formattedDistance = df.format(wifi.getDISTANCE());

                            System.out.println("WiFi Info: ");
                            System.out.println("ID: " + wifi.getId());
                            System.out.println("Distance: " + formattedDistance);
                            System.out.println("MGR_NO: " + wifi.getX_SWIFI_MGR_NO());
                            System.out.println("WRDOFC: " + wifi.getX_SWIFI_WRDOFC());
                            System.out.println("MAIN_NM: " + wifi.getX_SWIFI_MAIN_NM());
                            System.out.println("ADRES1: " + wifi.getX_SWIFI_ADRES1());
                            System.out.println("ADRES2: " + wifi.getX_SWIFI_ADRES2());

                            wifi.setFormattedDistance(formattedDistance);
                            req.setAttribute("wifi", wifi);

                            String groupSql = "SELECT name FROM bookmark_group ORDER BY orderNumber";
                            List<String> bookmarkGroups = new ArrayList<>();
                            try (Statement stmt = conn.createStatement();
                            ResultSet rsGroups = stmt.executeQuery(groupSql)) {
                                while (rsGroups.next()) {
                                    bookmarkGroups.add(rsGroups.getString("name"));
                                }
                            }

                            req.setAttribute("bookmarkGroups", bookmarkGroups);
                            req.getRequestDispatcher("/wifiDetail.jsp").forward(req, resp);
                        } else {
                            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No data found for the given ID");
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid lat/lnt parameters");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing the request");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String wifiId = req.getParameter("wifiId");
        String groupName = req.getParameter("groupName");
        String wifiName = req.getParameter("wifiName");

        if (groupName == null || wifiName == null) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{ \"error\": \"Invalid input\" }");
            return;
        }

        try {
            createTable();
            addBookmark(wifiId, groupName, wifiName);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"success\": true}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{ \"success\": false} }");
        }
    }
    private void addBookmark(String wifiId, String groupName, String wifiName) throws SQLException {
        String query = "INSERT INTO show_bookmark (wifi_id, name, wifi_name, create_date) " +
                "VALUES (?, ?, ?, datetime('now', '+9 hours'))";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            int id = Integer.parseInt(wifiId);
            pstmt.setInt(1, id);
            pstmt.setString(2, groupName);
            pstmt.setString(3, wifiName);
            pstmt.executeUpdate();
            System.out.println("wifiId: " + wifiId + " groupName: " + groupName + " wifiName: " + wifiName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTable() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite driver not found");
        }
        String createTableSQL = "CREATE TABLE IF NOT EXISTS show_bookmark (" +
                "wifi_id INTEGER, " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL, " +
                "wifi_name TEXT NOT NULL, " +
                "create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (wifi_id) REFERENCES wifi_info(id) " +
                ");";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
            System.out.println("Table created or already exists");
        } catch (SQLException e) {
            System.out.println("Table creation failed: " + e.getMessage());
            throw e;
        }
    }
}
