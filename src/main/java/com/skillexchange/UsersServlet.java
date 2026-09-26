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

@WebServlet("/users")
public class UsersServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        Integer loggedUserId =
                (Integer) request.getSession().getAttribute("user_id");

        if (loggedUserId == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login.html"
            );

            return;
        }

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        try {

        	Connection con = DBConnection.getConnection();
            String sql =
                    "SELECT user_id, name, email " +
                    "FROM users " +
                    "WHERE user_id != ?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setInt(1, loggedUserId);

            ResultSet rs =
                    ps.executeQuery();

            out.println("<!DOCTYPE html>");
            out.println("<html>");

            out.println("<head>");

            out.println("<meta charset='UTF-8'>");

            out.println("<title>All Users - Skill Exchange</title>");

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
            out.println("display: flex;");
            out.println("justify-content: space-between;");
            out.println("align-items: center;");
            out.println("box-shadow: 0 3px 15px rgba(0,0,0,0.15);");
            out.println("}");

            out.println(".logo {");
            out.println("font-size: 24px;");
            out.println("font-weight: bold;");
            out.println("color: #5b4bc4;");
            out.println("}");

            out.println(".container {");
            out.println("width: 90%;");
            out.println("max-width: 1000px;");
            out.println("margin: 40px auto;");
            out.println("}");

            out.println(".heading {");
            out.println("background: white;");
            out.println("padding: 30px;");
            out.println("border-radius: 20px;");
            out.println("text-align: center;");
            out.println("box-shadow: 0 8px 25px rgba(0,0,0,0.2);");
            out.println("margin-bottom: 25px;");
            out.println("}");

            out.println(".heading h1 {");
            out.println("color: #4b3ca7;");
            out.println("margin: 0 0 10px 0;");
            out.println("}");

            out.println(".heading p {");
            out.println("color: #777;");
            out.println("margin: 0;");
            out.println("}");

            out.println(".users {");
            out.println("display: grid;");
            out.println("grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));");
            out.println("gap: 20px;");
            out.println("}");

            out.println(".user-card {");
            out.println("background: white;");
            out.println("padding: 25px;");
            out.println("border-radius: 18px;");
            out.println("box-shadow: 0 6px 20px rgba(0,0,0,0.15);");
            out.println("transition: 0.3s;");
            out.println("}");

            out.println(".user-card:hover {");
            out.println("transform: translateY(-5px);");
            out.println("box-shadow: 0 10px 25px rgba(0,0,0,0.25);");
            out.println("}");

            out.println(".user-card h2 {");
            out.println("color: #4b3ca7;");
            out.println("margin-top: 0;");
            out.println("}");

            out.println(".user-card p {");
            out.println("color: #666;");
            out.println("margin-bottom: 20px;");
            out.println("}");

            out.println(".profile-button {");
            out.println("display: inline-block;");
            out.println("padding: 10px 20px;");
            out.println("background: linear-gradient(135deg, #667eea, #764ba2);");
            out.println("color: white;");
            out.println("text-decoration: none;");
            out.println("border-radius: 20px;");
            out.println("font-weight: bold;");
            out.println("}");

            out.println(".profile-button:hover {");
            out.println("opacity: 0.85;");
            out.println("}");

            out.println(".empty {");
            out.println("background: white;");
            out.println("padding: 30px;");
            out.println("border-radius: 18px;");
            out.println("text-align: center;");
            out.println("color: #777;");
            out.println("}");

            out.println(".back {");
            out.println("text-align: center;");
            out.println("margin-top: 30px;");
            out.println("}");

            out.println(".back a {");
            out.println("color: white;");
            out.println("text-decoration: none;");
            out.println("font-weight: bold;");
            out.println("}");

            out.println("</style>");

            out.println("</head>");

            out.println("<body>");

            out.println("<div class='navbar'>");
            out.println("<div class='logo'>Skill Exchange</div>");
            out.println("</div>");

            out.println("<div class='container'>");

            out.println("<div class='heading'>");

            out.println("<h1>All Skill Exchange Users</h1>");

            out.println("<p>Explore other users and view the skills they can teach.</p>");

            out.println("</div>");

            out.println("<div class='users'>");

            boolean found = false;

            while (rs.next()) {

                found = true;

                int userId =
                        rs.getInt("user_id");

                String name =
                        rs.getString("name");

                String email =
                        rs.getString("email");

                out.println("<div class='user-card'>");

                out.println("<h2>"
                        + name
                        + "</h2>");

                out.println("<p>Email: "
                        + email
                        + "</p>");

                out.println("<a class='profile-button' href='profile?user_id="
                        + userId
                        + "'>View Profile</a>");

                out.println("</div>");
            }

            out.println("</div>");

            if (!found) {

                out.println("<div class='empty'>");

                out.println("<p>No other users found.</p>");

                out.println("</div>");
            }

            out.println("<div class='back'>");

            out.println("<a href='dashboard.html'>Back to Dashboard</a>");

            out.println("</div>");

            out.println("</div>");

            out.println("</body>");

            out.println("</html>");

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            out.println("<h2>Error Loading Users</h2>");

            out.println("<p>"
                    + e.getMessage()
                    + "</p>");
        }
    }
}