package com.skillexchange;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {

            renderPage(out, false, "Registration Failed", "All fields are required. Please fill in all fields.", "register.html", "Try Again");
            return;
        }

        name = name.trim();
        email = email.trim().toLowerCase();

        String sql = "INSERT INTO users(name, email, password) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);

            int result = ps.executeUpdate();

            if (result > 0) {
                renderSuccess(out, name);
            } else {
                renderPage(out, false, "Registration Failed", "Could not complete registration. Please try again.", "register.html", "Try Again");
            }

        } catch (SQLException e) {
            String message = e.getMessage();
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate")) {
                message = "An account with the email <b>" + escapeHtml(email) + "</b> already exists. Please login instead.";
            }
            renderPage(out, false, "Registration Failed", message, "register.html", "Try Again");
        } catch (Exception e) {
            renderPage(out, false, "Registration Error", e.getMessage(), "register.html", "Try Again");
        }
    }

    private void renderSuccess(PrintWriter out, String name) {
        out.println("<!DOCTYPE html>");
        out.println("<html><head><meta charset='UTF-8'><title>Registration Successful - Skill Exchange</title>");
        out.println("<style>");
        out.println("* { box-sizing: border-box; }");
        out.println("body { margin: 0; font-family: Arial, sans-serif; min-height: 100vh; display: flex; justify-content: center; align-items: center; background: linear-gradient(135deg, #667eea, #764ba2); }");
        out.println(".card { width: 90%; max-width: 450px; background: white; padding: 40px; border-radius: 22px; text-align: center; box-shadow: 0 10px 30px rgba(0,0,0,0.25); }");
        out.println(".logo { font-size: 28px; font-weight: bold; color: #5b4bc4; margin-bottom: 10px; }");
        out.println("h2 { color: #2ecc71; margin-bottom: 15px; }");
        out.println("p { color: #666; font-size: 16px; line-height: 1.6; margin-bottom: 25px; }");
        out.println(".button { display: inline-block; padding: 12px 28px; border-radius: 25px; background: linear-gradient(135deg, #667eea, #764ba2); color: white; text-decoration: none; font-weight: bold; margin: 6px; transition: 0.3s; }");
        out.println(".button:hover { opacity: 0.9; transform: translateY(-2px); }");
        out.println(".button-outline { background: white; color: #5b4bc4; border: 2px solid #667eea; }");
        out.println("</style></head><body>");
        out.println("<div class='card'>");
        out.println("<div class='logo'>Skill Exchange</div>");
        out.println("<h2>Registration Successful!</h2>");
        out.println("<p>Welcome, <b>" + escapeHtml(name) + "</b>! Your account has been created successfully. You can now login and start exchanging skills.</p>");
        out.println("<div><a class='button' href='login.html'>Login Now</a><a class='button button-outline' href='index.html'>Home</a></div>");
        out.println("</div></body></html>");
    }

    private void renderPage(PrintWriter out, boolean success, String title, String message, String actionLink, String actionText) {
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