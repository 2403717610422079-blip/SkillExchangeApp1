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

@WebServlet("/updaterequest")
public class UpdateRequestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Integer loggedUserId = (session != null) ? (Integer) session.getAttribute("user_id") : null;

        if (loggedUserId == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        int requestId;
        String status = request.getParameter("status");

        try {
            requestId = Integer.parseInt(request.getParameter("request_id"));
        } catch (Exception e) {
            renderResponse(out, false, "Invalid Request", "The request ID provided is invalid.", "requests", "Back to Requests");
            return;
        }

        if (!"Accepted".equalsIgnoreCase(status) && !"Rejected".equalsIgnoreCase(status)) {
            renderResponse(out, false, "Invalid Status", "Status must be either Accepted or Rejected.", "requests", "Back to Requests");
            return;
        }

        // Format to standard casing: "Accepted" or "Rejected"
        status = "Accepted".equalsIgnoreCase(status) ? "Accepted" : "Rejected";

        // Ensure that the logged-in user is actually the receiver of this request
        String sql = "UPDATE exchange_requests SET status = ? WHERE request_id = ? AND receiver_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, requestId);
            ps.setInt(3, loggedUserId);

            int result = ps.executeUpdate();

            if (result > 0) {
                renderSuccess(out, requestId, status);
            } else {
                renderResponse(out, false, "Request Not Found", "The requested exchange request could not be found or you do not have permission to update it.", "requests", "Back to Requests");
            }

        } catch (Exception e) {
            renderResponse(out, false, "Error Updating Request", e.getMessage(), "requests", "Back to Requests");
        }
    }

    private void renderSuccess(PrintWriter out, int requestId, String status) {
        boolean isAccepted = "Accepted".equals(status);
        String badgeColor = isAccepted ? "#2ecc71" : "#e74c3c";
        String statusBg = isAccepted ? "#d4edda" : "#f8d7da";
        String statusColor = isAccepted ? "#155724" : "#721c24";

        out.println("<!DOCTYPE html>");
        out.println("<html><head><meta charset='UTF-8'><title>Request Updated - Skill Exchange</title>");
        out.println("<style>");
        out.println("* { box-sizing: border-box; }");
        out.println("body { margin: 0; font-family: Arial, sans-serif; background: linear-gradient(135deg, #667eea, #764ba2); min-height: 100vh; display: flex; align-items: center; justify-content: center; }");
        out.println(".card { width: 90%; max-width: 480px; background: white; padding: 40px; border-radius: 22px; text-align: center; box-shadow: 0 10px 30px rgba(0,0,0,0.2); }");
        out.println(".logo { font-size: 28px; font-weight: bold; color: #5b4bc4; margin-bottom: 10px; }");
        out.println("h2 { color: " + badgeColor + "; margin-bottom: 15px; }");
        out.println("p { color: #666; font-size: 15px; line-height: 1.6; margin-bottom: 20px; }");
        out.println(".status { display: inline-block; padding: 8px 20px; margin: 10px 0 25px 0; border-radius: 25px; background: " + statusBg + "; color: " + statusColor + "; font-weight: bold; font-size: 14px; }");
        out.println(".button { display: inline-block; padding: 11px 25px; background: linear-gradient(135deg, #667eea, #764ba2); color: white; text-decoration: none; border-radius: 25px; font-weight: bold; transition: 0.3s; font-size: 14px; margin: 5px; }");
        out.println(".button:hover { opacity: 0.85; transform: translateY(-2px); }");
        out.println(".button-outline { background: white; color: #5b4bc4; border: 2px solid #667eea; }");
        out.println("</style></head><body>");
        out.println("<div class='card'>");
        out.println("<div class='logo'>Skill Exchange</div>");
        out.println("<h2>Request Updated!</h2>");
        out.println("<p>Exchange request <b>#" + requestId + "</b> has been updated.</p>");
        out.println("<div class='status'>New Status: " + escapeHtml(status) + "</div><br>");
        out.println("<a class='button' href='requests'>Back to Requests</a>");
        out.println("<a class='button button-outline' href='dashboard.html'>Dashboard</a>");
        out.println("</div></body></html>");
    }

    private void renderResponse(PrintWriter out, boolean success, String title, String message, String actionLink, String actionText) {
        out.println("<!DOCTYPE html>");
        out.println("<html><head><meta charset='UTF-8'><title>" + title + " - Skill Exchange</title>");
        out.println("<style>");
        out.println("* { box-sizing: border-box; }");
        out.println("body { margin: 0; font-family: Arial, sans-serif; background: linear-gradient(135deg, #667eea, #764ba2); min-height: 100vh; display: flex; align-items: center; justify-content: center; }");
        out.println(".card { width: 90%; max-width: 480px; background: white; padding: 40px; border-radius: 22px; text-align: center; box-shadow: 0 10px 30px rgba(0,0,0,0.2); }");
        out.println(".logo { font-size: 28px; font-weight: bold; color: #5b4bc4; margin-bottom: 10px; }");
        out.println("h2 { color: " + (success ? "#2ecc71" : "#e74c3c") + "; margin-bottom: 15px; }");
        out.println("p { color: #666; font-size: 15px; line-height: 1.6; margin-bottom: 25px; }");
        out.println(".button { display: inline-block; padding: 12px 28px; background: linear-gradient(135deg, #667eea, #764ba2); color: white; text-decoration: none; border-radius: 25px; font-weight: bold; transition: 0.3s; }");
        out.println(".button:hover { opacity: 0.85; transform: translateY(-2px); }");
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