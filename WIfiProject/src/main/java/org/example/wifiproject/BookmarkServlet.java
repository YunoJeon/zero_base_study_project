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

@WebServlet("/bookmarkGroup")
public class BookmarkServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("list".equals(action)) {
            List<BookmarkGroup> group = getBookmarkGroup();
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            PrintWriter out = resp.getWriter();
            Gson gson = new Gson();
            String json = gson.toJson(group);

            out.print(json);
            out.flush();
        } else if ("edit".equals(action)) {
            String idStr = req.getParameter("id");
            if (idStr != null) {
                try {
                    int id = Integer.parseInt(idStr);
                    BookmarkGroup group = getBookmarkGroupById(id);
                    if (group != null) {
                        resp.setContentType("application/json");
                        resp.setCharacterEncoding("UTF-8");

                        PrintWriter out = resp.getWriter();
                        Gson gson = new Gson();
                        String json = gson.toJson(group);

                        out.print(json);
                        out.flush();
                    } else {
                        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Group not found");
                    }
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID");
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
            }
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        try {
            if ("add".equals(action)) {
                addGroup(req, resp);
            } else if ("edit".equals(action)) {
                editGroup(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error" + e.getMessage());
        }
    }

    private void createTable() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS bookmark_group (" +
                "wifi_id INTEGER, " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL, " +
                "orderNumber INTEGER NOT NULL, " +
                "create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
            System.out.println("Table created or already exists");
        } catch (SQLException e) {
            System.out.println("Table creation failed:" + e.getMessage());
            throw e;
        }
    }

    private void addGroup(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("group-name");
        String order = req.getParameter("group-order");

        System.out.println("Received parameters - Name: " + name + ", Order: " + order);

        if (name == null || order == null) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"success\": false, \"message\": \"Invalid input\"}");
            return;
        }

        try {
            int orderNumber = Integer.parseInt(order);
            createTable();
            addBookmarkGroup(name, orderNumber); // 실제로 데이터를 추가하는 메서드 호출
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"success\": true}");
            resp.sendRedirect(req.getContextPath() + "/bookmarkGroup?action=list");
        } catch (NumberFormatException e) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"success\": false, \"message\": \"Invalid order number\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"success\": false, \"message\": \"Server error\"}");
        }
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String idStr = req.getParameter("id");
        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                deleteBookmarkGroup(id);
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

    private List<BookmarkGroup> getBookmarkGroup() {
        List<BookmarkGroup> groups = new ArrayList<>();
        String query = "SELECT * FROM bookmark_group";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                BookmarkGroup group = new BookmarkGroup();
                group.setID(rs.getInt("id"));
                group.setName(rs.getString("name"));
                group.setOrderNumber(rs.getInt("orderNumber"));
                group.setCreateDate(rs.getTimestamp("create_date").toLocalDateTime());
                group.setModifiedDate(rs.getTimestamp("modified_date") != null ?
                        rs.getTimestamp("modified_date").toLocalDateTime() : null);
                groups.add(group);
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unknown error: " + e.getMessage());
        }
        System.out.println("Fetched groups: " + groups);
        return groups;
    }

    private void addBookmarkGroup(String name, int orderNumber) {
        String query = "INSERT INTO bookmark_group (name, orderNumber, create_date, modified_date) VALUES (?, ?, datetime('now', '+9 hours'), NULL)";
        System.out.println("Inserting group - Name: " + name + ", OrderNumber: " + orderNumber);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, orderNumber);
            pstmt.executeUpdate();
            System.out.println("Group inserted successfully");
        } catch (Exception e) {
            System.out.println("Exception occurred while inserting group: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateBookmarkGroup(int id, String name, int orderNumber) throws SQLException {
        String query = "UPDATE bookmark_group SET name = ?, orderNumber = ?, modified_date = datetime('now', '+9 hours') WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, orderNumber);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    protected BookmarkGroup getBookmarkGroupById(int id) {
        BookmarkGroup group = null;
        String query = "SELECT * FROM bookmark_group WHERE id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                group = new BookmarkGroup();
                group.setID(rs.getInt("id"));
                group.setName(rs.getString("name"));
                group.setOrderNumber(rs.getInt("orderNumber"));
                group.setCreateDate(rs.getTimestamp("create_date").toLocalDateTime());
                group.setModifiedDate(rs.getTimestamp("modified_date") != null ?
                        rs.getTimestamp("modified_date").toLocalDateTime() : null);
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unknown error: " + e.getMessage());
        }
        return group;
    }

    private void editGroup(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("group-id");
        String name = req.getParameter("group-name");
        String orderStr = req.getParameter("group-order");

        if (idStr == null || name== null || orderStr == null) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"success\": false}, \"message\": \"Invalid input\"}");
            return;
        }
        try {
            int id = Integer.parseInt(idStr);
            int orderNumber = Integer.parseInt(orderStr);

            updateBookmarkGroup(id, name, orderNumber);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"success\": true}");
            resp.sendRedirect(req.getContextPath() + "/bookmarkGroup?action=list");
        } catch (NumberFormatException e) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"success\": false, \"message\": \"Invalid input\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"success\": false, \"message\": \"Server error\"}");
        }
    }

    private void deleteBookmarkGroup(int id) {
        String query = "DELETE FROM bookmark_group WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
