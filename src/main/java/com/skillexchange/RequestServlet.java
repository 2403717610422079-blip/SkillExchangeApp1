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

@WebServlet("/requests")
public class RequestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("user_id") : null;

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html><head><meta charset='UTF-8'><title>Exchange Requests - Skill Exchange</title>");
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
        out.println(".section-title { color: white; font-size: 22px; margin: 30px 0 15px 0; font-weight: bold; }");
        out.println(".requests-grid { display: grid; gap: 20px; }");
        out.println(".request-card { background: white; padding: 25px; border-radius: 18px; box-shadow: 0 6px 20px rgba(0,0,0,0.15); transition: 0.3s; }");
        out.println(".card-header { display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #f0efff; padding-bottom: 12px; margin-bottom: 15px; flex-wrap: wrap; gap: 10px; }");
        out.println(".request-id { font-size: 18px; font-weight: bold; color: #4b3ca7; }");
        out.println(".badge { display: inline-block; padding: 6px 14px; border-radius: 20px; font-weight: bold; font-size: 13px; }");
        out.println(".badge-pending { background: #fff3cd; color: #856404; }");
        out.println(".badge-accepted { background: #d4edda; color: #155724; }");
        out.println(".badge-rejected { background: #f8d7da; color: #721c24; }");
        out.println(".info-row { margin: 8px 0; color: #555; font-size: 15px; }");
        out.println(".info-row strong { color: #333; }");
        out.println(".buttons { display: flex; gap: 10px; margin-top: 18px; flex-wrap: wrap; }");
        out.println(".button { border: none; padding: 10px 22px; border-radius: 20px; color: white; font-weight: bold; cursor: pointer; transition: 0.3s; font-size: 14px; text-decoration: none; display: inline-block; }");
        out.println(".button:hover { opacity: 0.9; transform: translateY(-2px); }");
        out.println(".accept { background: #2ecc71; }");
        out.println(".reject { background: #e74c3c; }");
        out.println(".view-profile { background: #5b4bc4; }");
        out.println(".empty { background: white; padding: 30px; border-radius: 18px; text-align: center; color: #777; box-shadow: 0 6px 20px rgba(0,0,0,0.15); }");
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
        out.println("<a class='nav-link' href='searchskill.html'>Search</a>");
        out.println("<a class='nav-link active' href='requests'>Requests</a>");
        out.println("<a class='nav-link logout' href='logout'>Logout</a>");
        out.println("</div></div>");

        out.println("<div class='container'>");
        out.println("<div class='heading'>");
        out.println("<h1>Exchange Requests</h1>");
        out.println("<p>Manage incoming requests from other learners and track requests you sent.</p>");
        out.println("</div>");

        try (Connection con = DBConnection.getConnection()) {

            // Section 1: Received Requests (User is receiver)
            out.println("<div class='section-title'>Incoming Requests (Received)</div>");
            out.println("<div class='requests-grid'>");

            String receivedSql =
                    "SELECT r.request_id, r.sender_id, u.name AS sender_name, u.email AS sender_email, "
                  + "r.skill_id, s.skill_name, s.skill_description, r.status "
                  + "FROM exchange_requests r "
                  + "JOIN skills s ON r.skill_id = s.skill_id "
                  + "JOIN users u ON r.sender_id = u.user_id "
                  + "WHERE r.receiver_id = ? "
                  + "ORDER BY r.request_id DESC";

            boolean hasReceived = false;

            try (PreparedStatement ps = con.prepareStatement(receivedSql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        hasReceived = true;
                        int requestId = rs.getInt("request_id");
                        int senderId = rs.getInt("sender_id");
                        String senderName = rs.getString("sender_name");
                        String senderEmail = rs.getString("sender_email");
                        String skillName = rs.getString("skill_name");
                        String skillDesc = rs.getString("skill_description");
                        String status = rs.getString("status");

                        String badgeClass = "badge-pending";
                        if ("Accepted".equalsIgnoreCase(status)) badgeClass = "badge-accepted";
                        else if ("Rejected".equalsIgnoreCase(status)) badgeClass = "badge-rejected";

                        out.println("<div class='request-card'>");
                        out.println("<div class='card-header'>");
                        out.println("<span class='request-id'>Request #" + requestId + "</span>");
                        out.println("<span class='badge " + badgeClass + "'>" + escapeHtml(status) + "</span>");
                        out.println("</div>");

                        out.println("<div class='info-row'><strong>Sender:</strong> " + escapeHtml(senderName) + " (" + escapeHtml(senderEmail) + ")</div>");
                        out.println("<div class='info-row'><strong>Requested Skill:</strong> " + escapeHtml(skillName) + "</div>");
                        if (skillDesc != null && !skillDesc.trim().isEmpty()) {
                            out.println("<div class='info-row'><strong>Details:</strong> " + escapeHtml(skillDesc) + "</div>");
                        }

                        out.println("<div class='buttons'>");
                        out.println("<a class='button view-profile' href='profile?user_id=" + senderId + "'>View Sender Profile</a>");

                        if ("Pending".equalsIgnoreCase(status)) {
                            // Accept form
                            out.println("<form action='updaterequest' method='post' style='margin:0;'>");
                            out.println("<input type='hidden' name='request_id' value='" + requestId + "'>");
                            out.println("<input type='hidden' name='status' value='Accepted'>");
                            out.println("<input class='button accept' type='submit' value='Accept Request'>");
                            out.println("</form>");

                            // Reject form
                            out.println("<form action='updaterequest' method='post' style='margin:0;'>");
                            out.println("<input type='hidden' name='request_id' value='" + requestId + "'>");
                            out.println("<input type='hidden' name='status' value='Rejected'>");
                            out.println("<input class='button reject' type='submit' value='Reject Request'>");
                            out.println("</form>");
                        }
                        out.println("</div>"); // buttons
                        out.println("</div>"); // request-card
                    }
                }
            }

            if (!hasReceived) {
                out.println("<div class='empty'>");
                out.println("<p>No incoming exchange requests at this time.</p>");
                out.println("</div>");
            }
            out.println("</div>"); // requests-grid

            // Section 2: Sent Requests (User is sender)
            out.println("<div class='section-title' style='margin-top:40px;'>Outgoing Requests (Sent by You)</div>");
            out.println("<div class='requests-grid'>");

            String sentSql =
                    "SELECT r.request_id, r.receiver_id, u.name AS receiver_name, u.email AS receiver_email, "
                  + "r.skill_id, s.skill_name, s.skill_description, r.status "
                  + "FROM exchange_requests r "
                  + "JOIN skills s ON r.skill_id = s.skill_id "
                  + "JOIN users u ON r.receiver_id = u.user_id "
                  + "WHERE r.sender_id = ? "
                  + "ORDER BY r.request_id DESC";

            boolean hasSent = false;

            try (PreparedStatement ps = con.prepareStatement(sentSql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        hasSent = true;
                        int requestId = rs.getInt("request_id");
                        int receiverId = rs.getInt("receiver_id");
                        String receiverName = rs.getString("receiver_name");
                        String receiverEmail = rs.getString("receiver_email");
                        String skillName = rs.getString("skill_name");
                        String status = rs.getString("status");

                        String badgeClass = "badge-pending";
                        if ("Accepted".equalsIgnoreCase(status)) badgeClass = "badge-accepted";
                        else if ("Rejected".equalsIgnoreCase(status)) badgeClass = "badge-rejected";

                        out.println("<div class='request-card'>");
                        out.println("<div class='card-header'>");
                        out.println("<span class='request-id'>Request #" + requestId + "</span>");
                        out.println("<span class='badge " + badgeClass + "'>" + escapeHtml(status) + "</span>");
                        out.println("</div>");

                        out.println("<div class='info-row'><strong>Sent to:</strong> " + escapeHtml(receiverName) + " (" + escapeHtml(receiverEmail) + ")</div>");
                        out.println("<div class='info-row'><strong>Skill:</strong> " + escapeHtml(skillName) + "</div>");
                        out.println("<div class='buttons'>");
                        out.println("<a class='button view-profile' href='profile?user_id=" + receiverId + "'>View Teacher Profile</a>");
                        out.println("</div>");
                        out.println("</div>");
                    }
                }
            }

            if (!hasSent) {
                out.println("<div class='empty'>");
                out.println("<p>You haven't sent any skill exchange requests yet.</p>");
                out.println("<a class='button view-profile' style='display:inline-block; margin-top:10px;' href='users'>Browse Users &amp; Request Skills</a>");
                out.println("</div>");
            }
            out.println("</div>"); // requests-grid

        } catch (Exception e) {
            out.println("<div class='empty'>");
            out.println("<h3 style='color:#e74c3c;'>Error Loading Requests</h3>");
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