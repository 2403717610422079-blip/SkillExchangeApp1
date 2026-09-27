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

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

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

        String userIdText = request.getParameter("user_id");
        int profileUserId = loggedUserId;

        if (userIdText != null && !userIdText.trim().isEmpty()) {
            try {
                profileUserId = Integer.parseInt(userIdText.trim());
            } catch (NumberFormatException e) {
                renderError(out, "Invalid User ID", "The user ID requested is invalid.", "users", "Back to Users");
                return;
            }
        }

        boolean isOwnProfile = (profileUserId == loggedUserId);

        out.println("<!DOCTYPE html>");
        out.println("<html><head><meta charset='UTF-8'><title>" + (isOwnProfile ? "My Profile" : "User Profile") + " - Skill Exchange</title>");
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
        out.println(".container { width: 90%; max-width: 900px; margin: 40px auto; }");
        out.println(".profile-card { background: white; padding: 35px; border-radius: 20px; box-shadow: 0 8px 25px rgba(0,0,0,0.2); text-align: center; }");
        out.println(".avatar { width: 75px; height: 75px; border-radius: 50%; background: linear-gradient(135deg, #667eea, #764ba2); color: white; display: flex; align-items: center; justify-content: center; font-size: 32px; font-weight: bold; margin: 0 auto 15px auto; }");
        out.println(".profile-card h1 { color: #4b3ca7; margin-bottom: 5px; font-size: 26px; }");
        out.println(".badge { display: inline-block; padding: 4px 12px; background: #f0efff; color: #5b4bc4; border-radius: 15px; font-size: 12px; font-weight: bold; margin-bottom: 10px; }");
        out.println(".email { color: #777; margin-bottom: 25px; font-size: 15px; }");
        out.println(".section-title { color: #333; margin-top: 30px; margin-bottom: 20px; font-size: 20px; text-align: left; border-bottom: 2px solid #f0efff; padding-bottom: 10px; }");
        out.println(".skills { display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 20px; text-align: left; }");
        out.println(".skill-card { background: #f7f7ff; padding: 22px; border-radius: 16px; border: 1px solid #e5e3ff; transition: 0.3s; display: flex; flex-direction: column; justify-content: space-between; }");
        out.println(".skill-card:hover { transform: translateY(-4px); box-shadow: 0 6px 18px rgba(0,0,0,0.12); }");
        out.println(".skill-card h3 { color: #4b3ca7; margin-top: 0; margin-bottom: 8px; font-size: 18px; }");
        out.println(".skill-card p { color: #666; font-size: 14px; line-height: 1.5; margin-bottom: 18px; flex-grow: 1; }");
        out.println(".request-button { border: none; padding: 10px 20px; border-radius: 20px; background: linear-gradient(135deg, #667eea, #764ba2); color: white; font-weight: bold; cursor: pointer; transition: 0.3s; font-size: 14px; width: 100%; }");
        out.println(".request-button:hover { opacity: 0.9; transform: translateY(-2px); }");
        out.println(".no-skills { color: #777; padding: 30px; background: #f9f9f9; border-radius: 12px; text-align: center; }");
        out.println(".links { text-align: center; margin-top: 30px; display: flex; justify-content: center; gap: 10px; flex-wrap: wrap; }");
        out.println(".links a { display: inline-block; padding: 10px 20px; border-radius: 20px; background: white; color: #5b4bc4; text-decoration: none; font-weight: bold; font-size: 14px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }");
        out.println(".links a:hover { background: #f0efff; }");
        out.println("</style></head><body>");

        // Navbar
        out.println("<div class='navbar'>");
        out.println("<a class='logo' href='dashboard.html'>Skill Exchange</a>");
        out.println("<div class='nav-links'>");
        out.println("<a class='nav-link' href='dashboard.html'>Dashboard</a>");
        out.println("<a class='nav-link' href='users'>All Users</a>");
        out.println("<a class='nav-link " + (isOwnProfile ? "active" : "") + "' href='profile'>My Profile</a>");
        out.println("<a class='nav-link' href='addskill.html'>Add Skill</a>");
        out.println("<a class='nav-link' href='searchskill.html'>Search</a>");
        out.println("<a class='nav-link' href='requests'>Requests</a>");
        out.println("<a class='nav-link logout' href='logout'>Logout</a>");
        out.println("</div></div>");

        out.println("<div class='container'>");

        try (Connection con = DBConnection.getConnection()) {
            String userSql = "SELECT user_id, name, email FROM users WHERE user_id = ?";
            String userName = "";
            String email = "";

            try (PreparedStatement userPs = con.prepareStatement(userSql)) {
                userPs.setInt(1, profileUserId);
                try (ResultSet userRs = userPs.executeQuery()) {
                    if (!userRs.next()) {
                        renderError(out, "User Not Found", "The requested user profile does not exist.", "users", "Back to Users");
                        return;
                    }
                    userName = userRs.getString("name");
                    email = userRs.getString("email");
                }
            }

            String initial = (userName != null && !userName.isEmpty()) ? userName.substring(0, 1).toUpperCase() : "?";

            out.println("<div class='profile-card'>");
            out.println("<div class='avatar'>" + escapeHtml(initial) + "</div>");
            out.println("<h1>" + escapeHtml(userName) + "</h1>");
            out.println("<div class='badge'>" + (isOwnProfile ? "Your Profile" : "Community Member") + "</div>");
            out.println("<p class='email'>" + escapeHtml(email) + "</p>");

            out.println("<h2 class='section-title'>Skills Offered</h2>");

            String skillSql = "SELECT skill_id, skill_name, skill_description FROM skills WHERE user_id = ? ORDER BY skill_id DESC";
            try (PreparedStatement skillPs = con.prepareStatement(skillSql)) {
                skillPs.setInt(1, profileUserId);
                try (ResultSet skillRs = skillPs.executeQuery()) {
                    out.println("<div class='skills'>");
                    boolean hasSkills = false;

                    while (skillRs.next()) {
                        hasSkills = true;
                        int skillId = skillRs.getInt("skill_id");
                        String skillName = skillRs.getString("skill_name");
                        String skillDescription = skillRs.getString("skill_description");

                        out.println("<div class='skill-card'>");
                        out.println("<div>");
                        out.println("<h3>" + escapeHtml(skillName) + "</h3>");
                        out.println("<p>" + escapeHtml(skillDescription) + "</p>");
                        out.println("</div>");

                        if (!isOwnProfile) {
                            out.println("<form action='sendrequest' method='post'>");
                            out.println("<input type='hidden' name='skill_id' value='" + skillId + "'>");
                            out.println("<input type='hidden' name='receiver_id' value='" + profileUserId + "'>");
                            out.println("<input class='request-button' type='submit' value='Send Exchange Request'>");
                            out.println("</form>");
                        }
                        out.println("</div>");
                    }
                    out.println("</div>");

                    if (!hasSkills) {
                        out.println("<div class='no-skills'>");
                        out.println("<p>" + (isOwnProfile ? "You have not added any skills yet." : "This user has not added any skills yet.") + "</p>");
                        if (isOwnProfile) {
                            out.println("<a class='links' style='display:inline-block; margin-top:10px;' href='addskill.html'><span style='padding:8px 18px; background:linear-gradient(135deg, #667eea, #764ba2); color:white; border-radius:20px; text-decoration:none;'>+ Add Your First Skill</span></a>");
                        }
                        out.println("</div>");
                    }
                }
            }

            out.println("<div class='links'>");
            if (isOwnProfile) {
                out.println("<a href='addskill.html'>+ Add Another Skill</a>");
            }
            out.println("<a href='users'>View All Users</a>");
            out.println("<a href='dashboard.html'>Back to Dashboard</a>");
            out.println("</div>");

            out.println("</div>"); // profile-card

        } catch (Exception e) {
            out.println("<div class='profile-card'>");
            out.println("<h2 style='color:#e74c3c;'>Profile Error</h2>");
            out.println("<p>" + escapeHtml(e.getMessage()) + "</p>");
            out.println("<a href='users'>Back to Users</a>");
            out.println("</div>");
        }

        out.println("</div></body></html>");
    }

    private void renderError(PrintWriter out, String title, String message, String actionLink, String actionText) {
        out.println("<div style='max-width:500px; margin:50px auto; background:white; padding:40px; border-radius:20px; text-align:center;'>");
        out.println("<h2 style='color:#e74c3c;'>" + title + "</h2>");
        out.println("<p style='color:#666;'>" + message + "</p>");
        out.println("<a style='display:inline-block; padding:10px 22px; background:linear-gradient(135deg, #667eea, #764ba2); color:white; text-decoration:none; border-radius:20px;' href='" + actionLink + "'>" + actionText + "</a>");
        out.println("</div></body></html>");
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}