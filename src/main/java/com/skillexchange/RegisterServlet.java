package com.skillexchange;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {

            
        	Connection con = DBConnection.getConnection();
            
            String sql = "INSERT INTO users(name, email, password) VALUES (?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);

            int result = ps.executeUpdate();

            if (result > 0) {
                out.println("<h2>Registration Successful!</h2>");
                out.println("<p>Welcome, " + name + "!</p>");
                out.println("<a href='index.html'>Go to Home</a>");
            }

            ps.close();
            con.close();

        } catch (Exception e) {

            out.println("<h2>Registration Failed</h2>");
            out.println("<p>" + e.getMessage() + "</p>");

        }
    }
}