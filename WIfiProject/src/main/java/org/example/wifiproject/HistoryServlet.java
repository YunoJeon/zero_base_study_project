package org.example.wifiproject;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/history")
public class HistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sql = "SELECT id, LAT, LNT, query_date FROM wifi_history ORDER BY id DESC";

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery()) {

            List<LocalHistory> historyList = new ArrayList<>();
            while (rs.next()) {
                LocalHistory history = new LocalHistory();
                history.setId(rs.getInt("id"));
                history.setLat(rs.getString("LAT"));
                history.setLnt(rs.getString("LNT"));
                history.setQuery_date(rs.getString("query_date"));
                historyList.add(history);
            }

            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            new Gson().toJson(historyList, out);
            out.flush();

        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
    private static class LocalHistory {
        private int id;
        private String lat;
        private String lnt;
        private String query_date;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLnt() {
            return lnt;
        }

        public void setLnt(String lnt) {
            this.lnt = lnt;
        }

        public String getQuery_date() {
            return query_date;
        }

        public void setQuery_date(String query_date) {
            this.query_date = query_date;
        }
    }
}
