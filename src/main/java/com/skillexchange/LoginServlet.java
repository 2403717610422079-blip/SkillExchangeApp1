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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {

            renderError(out, "Invalid Input", "Please enter both your email address and password.");
            return;
        }

        email = email.trim().toLowerCase();

        String sql = "SELECT user_id, name, email FROM users WHERE email = ? AND password = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String userName = rs.getString("name");
                    String userEmail = rs.getString("email");

                    HttpSession session = request.getSession(true);
                    session.setAttribute("user_id", userId);
                    session.setAttribute("user_name", userName);
                    session.setAttribute("user_email", userEmail);

                    response.sendRedirect(request.getContextPath() + "/dashboard.html");
                    return;
                } else {
                    renderError(out, "Login Failed", "Invalid email or password. Please verify your credentials and try again.");
                }
            }

        } catch (Exception e) {
            renderError(out, "Login Error", "Unable to connect or process login: " + e.getMessage());
        }
    }

    private void renderError(PrintWriter out, String title, String message) {
        out.println("<!DOCTYPE html>");
        out.println("<html><head><meta charset='UTF-8'><title>" + title + " - Skill Exchange</title>");
        out.println("<style>");
        out.println("* { box-sizing: border-box; }");
        out.println("body { margin: 0; font-family: Arial, sans-serif; min-height: 100vh; display: flex; justify-content: center; align-items: center; background: linear-gradient(135deg, #667eea, #764ba2); }");
        out.println(".card { width: 90%; max-width: 420px; background: white; padding: 40px; border-radius: 22px; text-align: center; box-shadow: 0 10px 30px rgba(0,0,0,0.25); }");
        out.println(".logo { font-size: 28px; font-weight: bold; color: #5b4bc4; margin-bottom: 10px; }");
        out.println("h2 { color: #e74c3c; margin-bottom: 15px; }");
        out.println("p { color: #666; font-size: 15px; line-height: 1.6; margin-bottom: 25px; }");
        out.println(".button { display: inline-block; padding: 12px 28px; border-radius: 25px; background: linear-gradient(135deg, #667eea, #764ba2); color: white; text-decoration: none; font-weight: bold; margin: 6px; transition: 0.3s; }");
        out.println(".button:hover { opacity: 0.9; transform: translateY(-2px); }");
        out.println(".button-outline { background: white; color: #5b4bc4; border: 2px solid #667eea; }");
        out.println("</style></head><body>");
        out.println("<div class='card'>");
        out.println("<div class='logo'>Skill Exchange</div>");
        out.println("<h2>" + title + "</h2>");
        out.println("<p>" + message + "</p>");
        out.println("<div><a class='button' href='login.html'>Try Again</a><a class='button button-outline' href='register.html'>Register</a></div>");
        out.println("</div></body></html>");
    }
}