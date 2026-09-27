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

@WebServlet("/searchskill")
public class SearchSkillServlet extends HttpServlet {

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

        String skillQuery = request.getParameter("skill_name");
        if (skillQuery == null) {
            skillQuery = "";
        }
        skillQuery = skillQuery.trim();

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html><head><meta charset='UTF-8'><title>Search Results - Skill Exchange</title>");
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
        out.println(".container { width: 90%; max-width: 950px; margin: 40px auto; }");
        out.println(".heading { background: white; padding: 30px; border-radius: 20px; text-align: center; box-shadow: 0 8px 25px rgba(0,0,0,0.2); margin-bottom: 25px; }");
        out.println(".heading h1 { color: #4b3ca7; margin: 0 0 10px 0; }");
        out.println(".heading p { color: #777; margin: 0; }");
        out.println(".search-bar { margin-top: 15px; display: flex; justify-content: center; gap: 10px; flex-wrap: wrap; }");
        out.println(".search-input { padding: 10px 18px; border: 1px solid #ddd; border-radius: 20px; font-size: 14px; width: 280px; outline: none; }");
        out.println(".search-input:focus { border-color: #667eea; }");
        out.println(".search-btn { padding: 10px 22px; background: linear-gradient(135deg, #667eea, #764ba2); color: white; border: none; border-radius: 20px; font-weight: bold; cursor: pointer; }");
        out.println(".results-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 20px; }");
        out.println(".skill-card { background: white; padding: 25px; border-radius: 18px; box-shadow: 0 6px 20px rgba(0,0,0,0.15); display: flex; flex-direction: column; justify-content: space-between; transition: 0.3s; }");
        out.println(".skill-card:hover { transform: translateY(-4px); box-shadow: 0 10px 25px rgba(0,0,0,0.25); }");
        out.println(".skill-card h3 { color: #4b3ca7; margin-top: 0; margin-bottom: 8px; font-size: 19px; }");
        out.println(".skill-card p { color: #666; font-size: 14px; line-height: 1.5; margin-bottom: 15px; flex-grow: 1; }");
        out.println(".teacher-info { font-size: 13px; color: #888; border-top: 1px solid #f0efff; padding-top: 12px; margin-bottom: 15px; }");
        out.println(".teacher-info a { color: #5b4bc4; font-weight: bold; text-decoration: none; }");
        out.println(".teacher-info a:hover { text-decoration: underline; }");
        out.println(".your-skill-badge { display: inline-block; padding: 6px 14px; background: #e8e6ff; color: #5b4bc4; border-radius: 15px; font-size: 12px; font-weight: bold; }");
        out.println(".request-button { border: none; padding: 10px 20px; border-radius: 20px; background: linear-gradient(135deg, #667eea, #764ba2); color: white; font-weight: bold; cursor: pointer; transition: 0.3s; font-size: 14px; width: 100%; }");
        out.println(".request-button:hover { opacity: 0.9; transform: translateY(-2px); }");
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
        out.println("<a class='nav-link' href='users'>All Users</a>");
        out.println("<a class='nav-link' href='profile'>My Profile</a>");
        out.println("<a class='nav-link' href='addskill.html'>Add Skill</a>");
        out.println("<a class='nav-link active' href='searchskill.html'>Search</a>");
        out.println("<a class='nav-link' href='requests'>Requests</a>");
        out.println("<a class='nav-link logout' href='logout'>Logout</a>");
        out.println("</div></div>");

        out.println("<div class='container'>");
        out.println("<div class='heading'>");
        out.println("<h1>Search Results</h1>");
        out.println("<p>Showing results for: <b>\"" + escapeHtml(skillQuery) + "\"</b></p>");
        out.println("<form class='search-bar' action='searchskill' method='get'>");
        out.println("<input class='search-input' type='text' name='skill_name' value='" + escapeHtml(skillQuery) + "' placeholder='Search skills...' required>");
        out.println("<input class='search-btn' type='submit' value='Search Again'>");
        out.println("</form>");
        out.println("</div>");

        String sql = "SELECT s.skill_id, s.user_id, s.skill_name, s.skill_description, u.name AS user_name, u.email AS user_email "
                   + "FROM skills s "
                   + "JOIN users u ON s.user_id = u.user_id "
                   + "WHERE s.skill_name LIKE ? OR s.skill_description LIKE ? "
                   + "ORDER BY s.skill_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String term = "%" + skillQuery + "%";
            ps.setString(1, term);
            ps.setString(2, term);

            try (ResultSet rs = ps.executeQuery()) {
                out.println("<div class='results-grid'>");
                boolean found = false;

                while (rs.next()) {
                    found = true;
                    int skillId = rs.getInt("skill_id");
                    int teacherId = rs.getInt("user_id");
                    String skillName = rs.getString("skill_name");
                    String skillDesc = rs.getString("skill_description");
                    String teacherName = rs.getString("user_name");

                    out.println("<div class='skill-card'>");
                    out.println("<div>");
                    out.println("<h3>" + escapeHtml(skillName) + "</h3>");
                    out.println("<p>" + escapeHtml(skillDesc) + "</p>");
                    out.println("</div>");

                    out.println("<div class='teacher-info'>");
                    out.println("Offered by: <a href='profile?user_id=" + teacherId + "'>" + escapeHtml(teacherName) + "</a>");
                    out.println("</div>");

                    if (teacherId == loggedUserId) {
                        out.println("<div style='text-align:center;'><span class='your-skill-badge'>Your Skill</span></div>");
                    } else {
                        out.println("<form action='sendrequest' method='post'>");
                        out.println("<input type='hidden' name='skill_id' value='" + skillId + "'>");
                        out.println("<input type='hidden' name='receiver_id' value='" + teacherId + "'>");
                        out.println("<input class='request-button' type='submit' value='Send Request'>");
                        out.println("</form>");
                    }

                    out.println("</div>");
                }
                out.println("</div>");

                if (!found) {
                    out.println("<div class='empty'>");
                    out.println("<h3>No Skills Found</h3>");
                    out.println("<p>No skills match <b>\"" + escapeHtml(skillQuery) + "\"</b>. Try searching for other terms like 'Java', 'Python', 'Design', etc.</p>");
                    out.println("<a class='search-btn' style='display:inline-block; margin-top:15px; text-decoration:none;' href='searchskill.html'>New Search</a>");
                    out.println("</div>");
                }
            }

        } catch (Exception e) {
            out.println("<div class='empty'>");
            out.println("<h3 style='color:#e74c3c;'>Search Error</h3>");
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