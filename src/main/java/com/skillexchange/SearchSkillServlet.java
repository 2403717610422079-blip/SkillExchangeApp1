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

@WebServlet("/searchskill")
public class SearchSkillServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        Integer userId =
                (Integer) request.getSession().getAttribute("user_id");

        if (userId == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login.html"
            );

            return;
        }

        String skillName = request.getParameter("skill_name");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {

        	Connection con = DBConnection.getConnection();

            String sql = "SELECT skill_id, user_id, skill_name, "
                       + "skill_description FROM skills "
                       + "WHERE skill_name LIKE ?";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, "%" + skillName + "%");

            ResultSet rs = ps.executeQuery();

            out.println("<h1>Search Results</h1>");

            boolean found = false;

            while (rs.next()) {

                found = true;

                int skillId = rs.getInt("skill_id");
                int receiverId = rs.getInt("user_id");

                out.println("<h3>Skill: "
                        + rs.getString("skill_name") + "</h3>");

                out.println("<p>Description: "
                        + rs.getString("skill_description") + "</p>");

                out.println("<form action='sendrequest' method='post'>");

                out.println("<input type='hidden' name='skill_id' value='"
                        + skillId + "'>");

                out.println("<input type='hidden' name='receiver_id' value='"
                        + receiverId + "'>");

                out.println("<input type='submit' value='Send Request'>");

                out.println("</form>");

                out.println("<hr>");
            }

            if (!found) {
                out.println("<p>No skills found.</p>");
            }

            out.println("<br>");
            out.println("<a href='searchskill.html'>Search Again</a>");
            out.println("<br>");
            out.println("<a href='dashboard.html'>Back to Dashboard</a>");

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            out.println("<h2>Search Error</h2>");
            out.println("<p>" + e.getMessage() + "</p>");
        }
    }
}