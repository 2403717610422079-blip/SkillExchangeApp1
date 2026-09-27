package com.skillexchange;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/addskill")
public class AddSkillServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("user_id") : null;

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        String skillName = request.getParameter("skill_name");
        String skillDescription = request.getParameter("skill_description");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (skillName == null || skillName.trim().isEmpty()) {
            renderMessage(out, false, "Invalid Skill", "Skill name cannot be empty.", "addskill.html", "Try Again");
            return;
        }

        skillName = skillName.trim();
        skillDescription = (skillDescription != null) ? skillDescription.trim() : "";

        String sql = "INSERT INTO skills(user_id, skill_name, skill_description) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, skillName);
            ps.setString(3, skillDescription);

            int result = ps.executeUpdate();

            if (result > 0) {
                renderSuccess(out, skillName, skillDescription);
            } else {
                renderMessage(out, false, "Failed to Add Skill", "Could not save skill. Please try again.", "addskill.html", "Try Again");
            }

        } catch (Exception e) {
            renderMessage(out, false, "Error Adding Skill", e.getMessage(), "addskill.html", "Try Again");
        }
    }

    private void renderSuccess(PrintWriter out, String name, String desc) {
        out.println("<!DOCTYPE html>");
        out.println("<html><head><meta charset='UTF-8'><title>Skill Added - Skill Exchange</title>");
        out.println("<style>");
        out.println("* { box-sizing: border-box; }");
        out.println("body { margin: 0; font-family: Arial, sans-serif; min-height: 100vh; display: flex; justify-content: center; align-items: center; background: linear-gradient(135deg, #667eea, #764ba2); }");
        out.println(".card { width: 90%; max-width: 480px; background: white; padding: 40px; border-radius: 22px; text-align: center; box-shadow: 0 10px 30px rgba(0,0,0,0.25); }");
        out.println(".logo { font-size: 28px; font-weight: bold; color: #5b4bc4; margin-bottom: 10px; }");
        out.println("h2 { color: #2ecc71; margin-bottom: 15px; }");
        out.println(".skill-box { background: #f7f7ff; border: 1px solid #e5e3ff; border-radius: 12px; padding: 15px; margin-bottom: 25px; text-align: left; }");
        out.println(".skill-box h3 { margin: 0 0 8px 0; color: #4b3ca7; }");
        out.println(".skill-box p { margin: 0; color: #666; font-size: 14px; line-height: 1.5; }");
        out.println(".button { display: inline-block; padding: 10px 22px; border-radius: 25px; background: linear-gradient(135deg, #667eea, #764ba2); color: white; text-decoration: none; font-weight: bold; margin: 5px; transition: 0.3s; font-size: 14px; }");
        out.println(".button:hover { opacity: 0.9; transform: translateY(-2px); }");
        out.println(".button-outline { background: white; color: #5b4bc4; border: 2px solid #667eea; }");
        out.println("</style></head><body>");
        out.println("<div class='card'>");
        out.println("<div class='logo'>Skill Exchange</div>");
        out.println("<h2>Skill Added Successfully!</h2>");
        out.println("<div class='skill-box'>");
        out.println("<h3>" + escapeHtml(name) + "</h3>");
        out.println("<p>" + escapeHtml(desc) + "</p>");
        out.println("</div>");
        out.println("<div>");
        out.println("<a class='button' href='addskill.html'>Add Another</a>");
        out.println("<a class='button' href='profile'>View Profile</a>");
        out.println("<a class='button button-outline' href='dashboard.html'>Dashboard</a>");
        out.println("</div>");
        out.println("</div></body></html>");
    }

    private void renderMessage(PrintWriter out, boolean success, String title, String message, String actionLink, String actionText) {
        out.println("<!DOCTYPE html>");
        out.println("<html><head><meta charset='UTF-8'><title>" + title + " - Skill Exchange</title>");
        out.println("<style>");
        out.println("* { box-sizing: border-box; }");
        out.println("body { margin: 0; font-family: Arial, sans-serif; min-height: 100vh; display: flex; justify-content: center; align-items: center; background: linear-gradient(135deg, #667eea, #764ba2); }");
        out.println(".card { width: 90%; max-width: 450px; background: white; padding: 40px; border-radius: 22px; text-align: center; box-shadow: 0 10px 30px rgba(0,0,0,0.25); }");
        out.println(".logo { font-size: 28px; font-weight: bold; color: #5b4bc4; margin-bottom: 10px; }");
        out.println("h2 { color: " + (success ? "#2ecc71" : "#e74c3c") + "; margin-bottom: 15px; }");
        out.println("p { color: #666; font-size: 15px; line-height: 1.6; margin-bottom: 25px; }");
        out.println(".button { display: inline-block; padding: 12px 28px; border-radius: 25px; background: linear-gradient(135deg, #667eea, #764ba2); color: white; text-decoration: none; font-weight: bold; margin: 6px; transition: 0.3s; }");
        out.println(".button:hover { opacity: 0.9; transform: translateY(-2px); }");
        out.println("</style></head><body>");
        out.println("<div class='card'>");
        out.println("<div class='logo'>Skill Exchange</div>");
        out.println("<h2>" + title + "</h2>");
        out.println("<p>" + message + "</p>");
        out.println("<a class='button' href='" + actionLink + "'>" + actionText + "</a>");
        out.println("</div></body></html>");
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}