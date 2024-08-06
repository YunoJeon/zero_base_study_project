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

@WebServlet("/showBookmark")
public class BookmarkListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<BookmarkList> bookmarkList = getBookmarkList();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();
        Gson gson = new Gson();
        String json = gson.toJson(bookmarkList);
        out.print(json);
        out.flush();
    }

    private List<BookmarkList> getBookmarkList() {
        List<BookmarkList> list = new ArrayList<>();
        String query = "SELECT * FROM show_bookmark";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                BookmarkList bookmarkList = new BookmarkList();
                bookmarkList.setWifi_id(rs.getInt("wifi_id"));
                bookmarkList.setID(rs.getInt("id"));
                bookmarkList.setName(rs.getString("name"));
                bookmarkList.setWifi_name(rs.getString("wifi_name"));
                bookmarkList.setCreateDate(rs.getTimestamp("create_date").toLocalDateTime());
                list.add(bookmarkList);

                System.out.println("ID: " + bookmarkList.getID());
                System.out.println("name: " + bookmarkList.getName());
                System.out.println("wifi_name: " + bookmarkList.getWifi_name());
                System.out.println("create_date: " + bookmarkList.getCreateDate());
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unknown error: " + e.getMessage());
        }
        return list;
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String idStr = req.getParameter("id");
        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                deleteBookmarkList(id);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write("{\"success\": true}");
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"success\": false, \"message\": \"Invalid id\"}");
            } catch (Exception e) {
                e.printStackTrace();
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"success\": false, \"message\": \"Server error\"}");
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"success\": false, \"message\": \"Missing parameters\"}");
        }
    }

    private void deleteBookmarkList(int id) {
        String query = "DELETE FROM show_bookmark WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
