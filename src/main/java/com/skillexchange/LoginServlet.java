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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {

        	Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                int userId = rs.getInt("user_id");
                String userName = rs.getString("name");

                request.getSession().setAttribute("user_id", userId);
                request.getSession().setAttribute("user_name", userName);

                response.sendRedirect(
                        request.getContextPath() + "/dashboard.html"
                );

            } else {

                out.println("<h2>Login Failed</h2>");
                out.println("<p>Invalid email or password.</p>");
                out.println("<a href='login.html'>Try Again</a>");

            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            out.println("<h2>Login Error</h2>");
            out.println("<p>" + e.getMessage() + "</p>");

        }
    }
}