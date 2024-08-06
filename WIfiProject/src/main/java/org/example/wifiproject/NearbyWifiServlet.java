package org.example.wifiproject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/nearbyWifi")
public class NearbyWifiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String latStr = req.getParameter("lat");
        String lntStr = req.getParameter("lnt");

        if (latStr == null || lntStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"missing parameters\"}");
            return;
        }

        double lat = Double.parseDouble(latStr);
        double lnt = Double.parseDouble(lntStr);

        System.out.println("Received lat: " + lat + ", lnt: " + lnt);

        List<WifiInfo> wifiList = getNearbyWifi(lat, lnt);

        Gson gson = new Gson();
        String json = gson.toJson(wifiList, new TypeToken<List<WifiInfo>>() {}.getType());

        PrintWriter out = resp.getWriter();
        out.print(json);
        out.flush();
    }

    private List<WifiInfo> getNearbyWifi(double lat, double lnt) {
        List<WifiInfo> wifiList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();

            String sql = "SELECT DISTINCT *, " +
                    "       (6371 * sqrt(" +
                    "           pow(sin((? - lat) * pi() / 180 / 2), 2) + " +
                    "           cos(? * pi() / 180) * cos(lat * pi() / 180) * " +
                    "           pow(sin((? - lnt) * pi() / 180 / 2), 2)" +
                    "       )) AS distance " +
                    "FROM wifi_info " +
                    "ORDER BY distance " +
                    "LIMIT 20;";

            ps = conn.prepareStatement(sql);
            ps.setDouble(1, lat);
            ps.setDouble(2, lat);
            ps.setDouble(3, lnt);

            rs = ps.executeQuery();

            while (rs.next()) {
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

                wifiList.add(wifi);
            }

            System.out.println("Query result size: " + wifiList.size());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return wifiList;
    }
}
