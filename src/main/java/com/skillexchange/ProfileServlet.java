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

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        Integer loggedUserId =
                (Integer) request.getSession().getAttribute("user_id");

        if (loggedUserId == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login.html"
            );

            return;
        }

        String userIdText = request.getParameter("user_id");

        int profileUserId = loggedUserId;

        if (userIdText != null && !userIdText.trim().isEmpty()) {

            try {

                profileUserId =
                        Integer.parseInt(userIdText.trim());

            } catch (NumberFormatException e) {

                out.println("<h2>Invalid User ID</h2>");
                out.println("<a href='users'>Back to Users</a>");

                return;
            }
        }

        try {

        	Connection con = DBConnection.getConnection();
            String userSql =
                    "SELECT user_id, name, email " +
                    "FROM users WHERE user_id = ?";

            PreparedStatement userPs =
                    con.prepareStatement(userSql);

            userPs.setInt(1, profileUserId);

            ResultSet userRs =
                    userPs.executeQuery();

            if (!userRs.next()) {

                out.println("<h2>User Not Found</h2>");
                out.println("<a href='users'>Back to Users</a>");

                userRs.close();
                userPs.close();
                con.close();

                return;
            }

            String userName =
                    userRs.getString("name");

            String email =
                    userRs.getString("email");

            out.println("<!DOCTYPE html>");
            out.println("<html>");

            out.println("<head>");

            out.println("<meta charset='UTF-8'>");

            out.println("<title>Profile - Skill Exchange</title>");

            out.println("<style>");

            out.println("* {");
            out.println("box-sizing: border-box;");
            out.println("}");

            out.println("body {");
            out.println("margin: 0;");
            out.println("font-family: Arial, sans-serif;");
            out.println("background: linear-gradient(135deg, #667eea, #764ba2);");
            out.println("min-height: 100vh;");
            out.println("}");

            out.println(".navbar {");
            out.println("background: white;");
            out.println("padding: 18px 40px;");
            out.println("box-shadow: 0 3px 15px rgba(0,0,0,0.15);");
            out.println("}");

            out.println(".logo {");
            out.println("font-size: 24px;");
            out.println("font-weight: bold;");
            out.println("color: #5b4bc4;");
            out.println("}");

            out.println(".container {");
            out.println("width: 90%;");
            out.println("max-width: 850px;");
            out.println("margin: 40px auto;");
            out.println("}");

            out.println(".profile-card {");
            out.println("background: white;");
            out.println("padding: 35px;");
            out.println("border-radius: 20px;");
            out.println("box-shadow: 0 8px 25px rgba(0,0,0,0.2);");
            out.println("text-align: center;");
            out.println("}");

            out.println(".profile-card h1 {");
            out.println("color: #4b3ca7;");
            out.println("margin-bottom: 10px;");
            out.println("}");

            out.println(".email {");
            out.println("color: #777;");
            out.println("margin-bottom: 25px;");
            out.println("}");

            out.println(".section-title {");
            out.println("color: #333;");
            out.println("margin-top: 30px;");
            out.println("margin-bottom: 20px;");
            out.println("}");

            out.println(".skills {");
            out.println("display: grid;");
            out.println("grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));");
            out.println("gap: 20px;");
            out.println("text-align: left;");
            out.println("}");

            out.println(".skill-card {");
            out.println("background: #f7f7ff;");
            out.println("padding: 25px;");
            out.println("border-radius: 16px;");
            out.println("border: 1px solid #e5e3ff;");
            out.println("transition: 0.3s;");
            out.println("}");

            out.println(".skill-card:hover {");
            out.println("transform: translateY(-4px);");
            out.println("box-shadow: 0 6px 18px rgba(0,0,0,0.12);");
            out.println("}");

            out.println(".skill-card h3 {");
            out.println("color: #4b3ca7;");
            out.println("margin-top: 0;");
            out.println("}");

            out.println(".skill-card p {");
            out.println("color: #666;");
            out.println("line-height: 1.5;");
            out.println("}");

            out.println(".request-button {");
            out.println("border: none;");
            out.println("padding: 10px 20px;");
            out.println("border-radius: 20px;");
            out.println("background: linear-gradient(135deg, #667eea, #764ba2);");
            out.println("color: white;");
            out.println("font-weight: bold;");
            out.println("cursor: pointer;");
            out.println("}");

            out.println(".request-button:hover {");
            out.println("opacity: 0.85;");
            out.println("}");

            out.println(".no-skills {");
            out.println("color: #777;");
            out.println("padding: 20px;");
            out.println("background: #f7f7f7;");
            out.println("border-radius: 12px;");
            out.println("}");

            out.println(".links {");
            out.println("text-align: center;");
            out.println("margin-top: 30px;");
            out.println("}");

            out.println(".links a {");
            out.println("display: inline-block;");
            out.println("margin: 5px;");
            out.println("padding: 10px 20px;");
            out.println("border-radius: 20px;");
            out.println("background: white;");
            out.println("color: #5b4bc4;");
            out.println("text-decoration: none;");
            out.println("font-weight: bold;");
            out.println("}");

            out.println(".links a:hover {");
            out.println("background: #f0efff;");
            out.println("}");

            out.println("</style>");

            out.println("</head>");

            out.println("<body>");

            out.println("<div class='navbar'>");
            out.println("<div class='logo'>Skill Exchange</div>");
            out.println("</div>");

            out.println("<div class='container'>");

            out.println("<div class='profile-card'>");

            out.println("<h1>" + userName + "</h1>");

            out.println("<p class='email'>"
                    + email
                    + "</p>");

            out.println("<h2 class='section-title'>Skills I Can Teach</h2>");

            String skillSql =
                    "SELECT skill_id, skill_name, skill_description " +
                    "FROM skills WHERE user_id = ?";

            PreparedStatement skillPs =
                    con.prepareStatement(skillSql);

            skillPs.setInt(1, profileUserId);

            ResultSet skillRs =
                    skillPs.executeQuery();

            out.println("<div class='skills'>");

            boolean hasSkills = false;

            while (skillRs.next()) {

                hasSkills = true;

                int skillId =
                        skillRs.getInt("skill_id");

                String skillName =
                        skillRs.getString("skill_name");

                String skillDescription =
                        skillRs.getString("skill_description");

                out.println("<div class='skill-card'>");

                out.println("<h3>"
                        + skillName
                        + "</h3>");

                out.println("<p>"
                        + skillDescription
                        + "</p>");

                if (loggedUserId != profileUserId) {

                    out.println(
                        "<form action='sendrequest' method='post'>"
                    );

                    out.println(
                        "<input type='hidden' " +
                        "name='skill_id' value='" +
                        skillId + "'>"
                    );

                    out.println(
                        "<input type='hidden' " +
                        "name='receiver_id' value='" +
                        profileUserId + "'>"
                    );

                    out.println(
                        "<input class='request-button' " +
                        "type='submit' " +
                        "value='Send Request'>"
                    );

                    out.println("</form>");
                }

                out.println("</div>");
            }

            out.println("</div>");

            if (!hasSkills) {

                out.println(
                    "<div class='no-skills'>" +
                    "This user has not added any skills yet." +
                    "</div>"
                );
            }

            out.println("<div class='links'>");

            out.println(
                "<a href='users'>View All Users</a>"
            );

            out.println(
                "<a href='dashboard.html'>Back to Dashboard</a>"
            );

            out.println("</div>");

            out.println("</div>");

            out.println("</div>");

            out.println("</body>");

            out.println("</html>");

            skillRs.close();
            skillPs.close();

            userRs.close();
            userPs.close();

            con.close();

        } catch (Exception e) {

            out.println("<h2>Profile Error</h2>");

            out.println("<p>"
                    + e.getMessage()
                    + "</p>");
        }
    }
}