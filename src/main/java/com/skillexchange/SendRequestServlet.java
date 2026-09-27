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

@WebServlet("/sendrequest")
public class SendRequestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Integer senderId = (session != null) ? (Integer) session.getAttribute("user_id") : null;

        if (senderId == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        int skillId;
        int receiverId;

        try {
            skillId = Integer.parseInt(request.getParameter("skill_id"));
            receiverId = Integer.parseInt(request.getParameter("receiver_id"));
        } catch (Exception e) {
            renderMessage(out, false, "Invalid Request", "Skill or user ID is invalid.", "users", "Browse Users");
            return;
        }

        if (senderId == receiverId) {
            renderMessage(out, false, "Invalid Request", "You cannot send a skill exchange request to yourself.", "users", "Browse Users");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            // Check if skill exists and belongs to receiver
            String checkSkillSql = "SELECT skill_name FROM skills WHERE skill_id = ? AND user_id = ?";
            String skillName = "";
            try (PreparedStatement checkPs = con.prepareStatement(checkSkillSql)) {
                checkPs.setInt(1, skillId);
                checkPs.setInt(2, receiverId);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (!rs.next()) {
                        renderMessage(out, false, "Skill Not Found", "The requested skill is no longer available.", "users", "Browse Users");
                        return;
                    }
                    skillName = rs.getString("skill_name");
                }
            }

            // Check for existing pending request
            String checkExistingSql = "SELECT request_id FROM exchange_requests WHERE sender_id = ? AND receiver_id = ? AND skill_id = ? AND status = 'Pending'";
            try (PreparedStatement checkPs = con.prepareStatement(checkExistingSql)) {
                checkPs.setInt(1, senderId);
                checkPs.setInt(2, receiverId);
                checkPs.setInt(3, skillId);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        renderMessage(out, true, "Request Already Sent", "You already have a pending exchange request for <b>" + escapeHtml(skillName) + "</b>. Please await the other user's response.", "requests", "View Requests");
                        return;
                    }
                }
            }

            // Insert exchange request
            String insertSql = "INSERT INTO exchange_requests (sender_id, receiver_id, skill_id, status) VALUES (?, ?, ?, 'Pending')";
            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setInt(1, senderId);
                ps.setInt(2, receiverId);
                ps.setInt(3, skillId);

                int result = ps.executeUpdate();

                if (result > 0) {
                    renderSuccess(out, skillName);
                } else {
                    renderMessage(out, false, "Request Failed", "Could not send the exchange request. Please try again.", "users", "Browse Users");
                }
            }

        } catch (Exception e) {
            renderMessage(out, false, "Request Error", e.getMessage(), "users", "Browse Users");
        }
    }

    private void renderSuccess(PrintWriter out, String skillName) {
        out.println("<!DOCTYPE html>");
        out.println("<html><head><meta charset='UTF-8'><title>Request Sent - Skill Exchange</title>");
        out.println("<style>");
        out.println("* { box-sizing: border-box; }");
        out.println("body { margin: 0; font-family: Arial, sans-serif; min-height: 100vh; display: flex; justify-content: center; align-items: center; background: linear-gradient(135deg, #667eea, #764ba2); }");
        out.println(".card { width: 90%; max-width: 480px; background: white; padding: 40px; border-radius: 22px; text-align: center; box-shadow: 0 10px 30px rgba(0,0,0,0.25); }");
        out.println(".logo { font-size: 28px; font-weight: bold; color: #5b4bc4; margin-bottom: 10px; }");
        out.println("h2 { color: #2ecc71; margin-bottom: 15px; }");
        out.println(".status { display: inline-block; padding: 6px 16px; background: #fff3cd; color: #856404; border-radius: 20px; font-weight: bold; font-size: 13px; margin: 10px 0 20px 0; }");
        out.println("p { color: #666; font-size: 15px; line-height: 1.6; margin-bottom: 25px; }");
        out.println(".button { display: inline-block; padding: 10px 22px; border-radius: 25px; background: linear-gradient(135deg, #667eea, #764ba2); color: white; text-decoration: none; font-weight: bold; margin: 5px; transition: 0.3s; font-size: 14px; }");
        out.println(".button:hover { opacity: 0.9; transform: translateY(-2px); }");
        out.println(".button-outline { background: white; color: #5b4bc4; border: 2px solid #667eea; }");
        out.println("</style></head><body>");
        out.println("<div class='card'>");
        out.println("<div class='logo'>Skill Exchange</div>");
        out.println("<h2>Request Sent Successfully!</h2>");
        out.println("<div class='status'>Status: Pending</div>");
        out.println("<p>Your exchange request for <b>" + escapeHtml(skillName) + "</b> has been delivered to the user. You will see updates when they accept or reject it.</p>");
        out.println("<div>");
        out.println("<a class='button' href='users'>Browse More Users</a>");
        out.println("<a class='button' href='requests'>My Requests</a>");
        out.println("<a class='button button-outline' href='dashboard.html'>Dashboard</a>");
        out.println("</div>");
        out.println("</div></body></html>");
    }

    private void renderMessage(PrintWriter out, boolean isNotice, String title, String message, String actionLink, String actionText) {
        out.println("<!DOCTYPE html>");
        out.println("<html><head><meta charset='UTF-8'><title>" + title + " - Skill Exchange</title>");
        out.println("<style>");
        out.println("* { box-sizing: border-box; }");
        out.println("body { margin: 0; font-family: Arial, sans-serif; min-height: 100vh; display: flex; justify-content: center; align-items: center; background: linear-gradient(135deg, #667eea, #764ba2); }");
        out.println(".card { width: 90%; max-width: 480px; background: white; padding: 40px; border-radius: 22px; text-align: center; box-shadow: 0 10px 30px rgba(0,0,0,0.25); }");
        out.println(".logo { font-size: 28px; font-weight: bold; color: #5b4bc4; margin-bottom: 10px; }");
        out.println("h2 { color: " + (isNotice ? "#f39c12" : "#e74c3c") + "; margin-bottom: 15px; }");
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