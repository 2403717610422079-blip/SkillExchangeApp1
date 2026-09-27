package com.skillexchange;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/users")
public class UsersServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Integer loggedUserId = (session != null) ? (Integer) session.getAttribute("user_id") : null;

        if (loggedUserId == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String sql = "SELECT user_id, name, email FROM users WHERE user_id != ? ORDER BY name ASC";

        out.println("<!DOCTYPE html>");
        out.println("<html><head><meta charset='UTF-8'><title>All Users - Skill Exchange</title>");
        out.println("<style>");
        out.println("* { box-sizing: border-box; }");
        out.println("body { margin: 0; font-family: Arial, sans-serif; background: linear-gradient(135deg, #667eea, #764ba2); min-height: 100vh; }");
        out.println(".navbar { background: white; padding: 15px 40px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 3px 15px rgba(0,0,0,0.15); flex-wrap: wrap; gap: 10px; }");
        out.println(".logo { font-size: 24px; font-weight: bold; color: #5b4bc4; text-decoration: none; }");
        out.println(".nav-links { display: flex; gap: 15px; align-items: center; flex-wrap: wrap; }");
        out.println(".nav-link { color: #555; text-decoration: none; font-weight: bold; font-size: 14px; padding: 6px 12px; border-radius: 15px; transition: 0.2s; }");
        out.println(".nav-link:hover, .nav-link.active { color: #5b4bc4; background: #f0efff; }");
        out.println(".logout { background: #ff5c5c; color: white !important; }");
        out.println(".logout:hover { background: #e04444 !important; }");
        out.println(".container { width: 90%; max-width: 1000px; margin: 40px auto; }");
        out.println(".heading { background: white; padding: 30px; border-radius: 20px; text-align: center; box-shadow: 0 8px 25px rgba(0,0,0,0.2); margin-bottom: 25px; }");
        out.println(".heading h1 { color: #4b3ca7; margin: 0 0 10px 0; }");
        out.println(".heading p { color: #777; margin: 0; }");
        out.println(".users { display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 20px; }");
        out.println(".user-card { background: white; padding: 25px; border-radius: 18px; box-shadow: 0 6px 20px rgba(0,0,0,0.15); transition: 0.3s; text-align: center; }");
        out.println(".user-card:hover { transform: translateY(-5px); box-shadow: 0 10px 25px rgba(0,0,0,0.25); }");
        out.println(".user-avatar { width: 60px; height: 60px; border-radius: 50%; background: linear-gradient(135deg, #667eea, #764ba2); color: white; display: flex; align-items: center; justify-content: center; font-size: 24px; font-weight: bold; margin: 0 auto 15px auto; }");
        out.println(".user-card h2 { color: #4b3ca7; margin: 0 0 5px 0; font-size: 20px; }");
        out.println(".user-card p { color: #666; font-size: 14px; margin-bottom: 20px; }");
        out.println(".profile-button { display: inline-block; padding: 10px 22px; background: linear-gradient(135deg, #667eea, #764ba2); color: white; text-decoration: none; border-radius: 20px; font-weight: bold; font-size: 14px; transition: 0.3s; }");
        out.println(".profile-button:hover { opacity: 0.9; transform: translateY(-2px); }");
        out.println(".empty { background: white; padding: 40px; border-radius: 18px; text-align: center; color: #777; box-shadow: 0 6px 20px rgba(0,0,0,0.15); }");
        out.println(".back { text-align: center; margin-top: 30px; }");
        out.println(".back a { color: white; text-decoration: none; font-weight: bold; padding: 10px 20px; background: rgba(255,255,255,0.2); border-radius: 20px; display: inline-block; }");
        out.println(".back a:hover { background: rgba(255,255,255,0.3); }");
        out.println("</style></head><body>");

        // Navbar
        out.println("<div class='navbar'>");
        out.println("<a class='logo' href='dashboard.html'>Skill Exchange</a>");
        out.println("<div class='nav-links'>");
        out.println("<a class='nav-link' href='dashboard.html'>Dashboard</a>");
        out.println("<a class='nav-link active' href='users'>All Users</a>");
        out.println("<a class='nav-link' href='profile'>My Profile</a>");
        out.println("<a class='nav-link' href='addskill.html'>Add Skill</a>");
        out.println("<a class='nav-link' href='searchskill.html'>Search</a>");
        out.println("<a class='nav-link' href='requests'>Requests</a>");
        out.println("<a class='nav-link logout' href='logout'>Logout</a>");
        out.println("</div></div>");

        out.println("<div class='container'>");
        out.println("<div class='heading'>");
        out.println("<h1>Community Members</h1>");
        out.println("<p>Explore other users, view their skills, and request skill exchanges.</p>");
        out.println("</div>");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, loggedUserId);

            try (ResultSet rs = ps.executeQuery()) {
                out.println("<div class='users'>");
                boolean found = false;

                while (rs.next()) {
                    found = true;
                    int userId = rs.getInt("user_id");
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String initial = (name != null && !name.isEmpty()) ? name.substring(0, 1).toUpperCase() : "?";

                    out.println("<div class='user-card'>");
                    out.println("<div class='user-avatar'>" + escapeHtml(initial) + "</div>");
                    out.println("<h2>" + escapeHtml(name) + "</h2>");
                    out.println("<p>" + escapeHtml(email) + "</p>");
                    out.println("<a class='profile-button' href='profile?user_id=" + userId + "'>View Profile</a>");
                    out.println("</div>");
                }

                out.println("</div>");

                if (!found) {
                    out.println("<div class='empty'>");
                    out.println("<h3>No Other Users Yet</h3>");
                    out.println("<p>You are the first registered user! When new users register, they will appear here.</p>");
                    out.println("</div>");
                }
            }

        } catch (Exception e) {
            out.println("<div class='empty'>");
            out.println("<h3>Error Loading Users</h3>");
            out.println("<p>" + escapeHtml(e.getMessage()) + "</p>");
            out.println("</div>");
        }

        out.println("<div class='back'><a href='dashboard.html'>← Back to Dashboard</a></div>");
        out.println("</div></body></html>");
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}