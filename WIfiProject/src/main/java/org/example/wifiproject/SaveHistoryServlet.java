package org.example.wifiproject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/saveHistory")
public class SaveHistoryServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String lat = req.getParameter("LAT");
        String lnt = req.getParameter("LNT");

        if (lat == null || lnt == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "위치 정보가 제공되지 않았습니다.");
            return;
        }

        String sql = "INSERT INTO wifi_history (LAT, LNT, query_date) VALUES (?, ?, datetime('now', '+9 hours'))";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lat);
            pstmt.setString(2, lnt);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}
