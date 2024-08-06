package org.example.wifiproject;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jdk.nashorn.internal.ir.IfNode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@WebServlet("/fetchData")
public class FetchWifiDataServlet extends HttpServlet {

    private static int PAGE_SIZE = 1000;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String lat = req.getParameter("lat");
        String lnt = req.getParameter("lnt");

        if (lat == null) lat = "0";
        if (lnt == null) lnt = "0";

        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();

        int totalRecords = 0;
        try {
            int pageNo = 1;
            boolean hasMorePages = true;

            while (hasMorePages) {
                String apiURL = String.format("http://openapi.seoul.go.kr:8088/676b4756647a657233304341775774/json/TbPublicWifiInfo/%d/%d/", pageNo, pageNo + PAGE_SIZE - 1);
                String jsonResponse = fetchWifiData(apiURL, client);

                JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                JsonObject wifiInfo = jsonObject.getAsJsonObject("TbPublicWifiInfo");
                int totalCnt = wifiInfo.get("list_total_count").getAsInt();
                JsonArray rows = wifiInfo.getAsJsonArray("row");

                if (rows.size() > 0) {
                    int num = saveWifiData(jsonResponse);
                    totalRecords += num;
                    pageNo += PAGE_SIZE;
                } else {
                    hasMorePages = false;
                }

                if (pageNo > totalCnt) {
                    hasMorePages = false;
                }
            }

            req.setAttribute("message",  totalRecords + "개의 WIFI 정보를 정상적으로 저장하였습니다.");
            req.getRequestDispatcher("/result.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    private String fetchWifiData(String apiUrl, OkHttpClient client) throws IOException {
        Request request = new Request.Builder().url(apiUrl).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    private int saveWifiData(String jsonData) throws SQLException {
        JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
        JsonArray rows = jsonObject.getAsJsonObject("TbPublicWifiInfo").getAsJsonArray("row");

        String sql = "INSERT OR REPLACE INTO wifi_info (X_SWIFI_MGR_NO, X_SWIFI_WRDOFC, X_SWIFI_MAIN_NM, X_SWIFI_ADRES1, X_SWIFI_ADRES2, X_SWIFI_INSTL_FLOOR, X_SWIFI_INSTL_TY, X_SWIFI_INSTL_MBY, X_SWIFI_SVC_SE, X_SWIFI_CMCWR, X_SWIFI_CNSTC_YEAR, X_SWIFI_INOUT_DOOR, X_SWIFI_REMARS3, LAT, LNT, WORK_DTTM) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int cnt = 0;

        try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

            System.out.println("Starting to save data");

            for (com.google.gson.JsonElement element : rows) {
                JsonObject wifi = element.getAsJsonObject();
                if (!recordExists(conn, wifi.get("X_SWIFI_MGR_NO").getAsString())) {
                    pstmt.setString(1, wifi.get("X_SWIFI_MGR_NO").getAsString());
                    pstmt.setString(2, wifi.get("X_SWIFI_WRDOFC").getAsString());
                    pstmt.setString(3, wifi.get("X_SWIFI_MAIN_NM").getAsString());
                    pstmt.setString(4, wifi.get("X_SWIFI_ADRES1").getAsString());
                    pstmt.setString(5, wifi.get("X_SWIFI_ADRES2").getAsString());
                    pstmt.setString(6, wifi.get("X_SWIFI_INSTL_FLOOR").getAsString());
                    pstmt.setString(7, wifi.get("X_SWIFI_INSTL_TY").getAsString());
                    pstmt.setString(8, wifi.get("X_SWIFI_INSTL_MBY").getAsString());
                    pstmt.setString(9, wifi.get("X_SWIFI_SVC_SE").getAsString());
                    pstmt.setString(10, wifi.get("X_SWIFI_CMCWR").getAsString());
                    pstmt.setString(11, wifi.get("X_SWIFI_CNSTC_YEAR").getAsString());
                    pstmt.setString(12, wifi.get("X_SWIFI_INOUT_DOOR").getAsString());
                    pstmt.setString(13, wifi.get("X_SWIFI_REMARS3").getAsString());
                    pstmt.setString(14, wifi.get("LAT").getAsString());
                    pstmt.setString(15, wifi.get("LNT").getAsString());
                    pstmt.setString(16, wifi.get("WORK_DTTM").getAsString());

                    pstmt.addBatch();
                    cnt++;
                }
            }
            pstmt.executeBatch();

            System.out.println("Data saved successfully");
        }
        return cnt;
    }
    private boolean recordExists(Connection conn, String mgrNo) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM wifi_info WHERE X_SWIFI_MGR_NO = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setString(1, mgrNo);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.getInt(1) > 0;
            }
        }
    }
}