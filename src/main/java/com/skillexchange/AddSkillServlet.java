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

@WebServlet("/addskill")
public class AddSkillServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String skillName = request.getParameter("skill_name");
        String skillDescription = request.getParameter("skill_description");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DBConnection.getConnection();

            String sql = "INSERT INTO skills(user_id, skill_name, skill_description) VALUES (?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);
            Integer userId =
                    (Integer) request.getSession().getAttribute("user_id");

            if (userId == null) {

                response.sendRedirect(
                        request.getContextPath() + "/login.html"
                );

                return;
            }

            ps.setInt(1, userId);
            ps.setString(2, skillName);
            ps.setString(3, skillDescription);

            int result = ps.executeUpdate();

            if (result > 0) {
                out.println("<h2>Skill Added Successfully!</h2>");
                out.println("<p>Skill: " + skillName + "</p>");
                out.println("<a href='dashboard.html'>Back to Dashboard</a>");
            }

            ps.close();
            con.close();

        } catch (Exception e) {

            out.println("<h2>Error Adding Skill</h2>");
            out.println("<p>" + e.getMessage() + "</p>");

        }
    }
}