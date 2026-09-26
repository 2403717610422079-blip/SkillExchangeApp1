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

@WebServlet("/requests")
public class RequestServlet extends HttpServlet {

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

        int receiverId = userId;

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        try {

        	Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT r.request_id, r.sender_id, "
                  + "r.skill_id, s.skill_name, r.status "
                  + "FROM exchange_requests r "
                  + "JOIN skills s ON r.skill_id = s.skill_id "
                  + "WHERE r.receiver_id = ?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setInt(1, receiverId);

            ResultSet rs =
                    ps.executeQuery();

            out.println("<!DOCTYPE html>");
            out.println("<html>");

            out.println("<head>");

            out.println("<meta charset='UTF-8'>");

            out.println("<title>Exchange Requests - Skill Exchange</title>");

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
            out.println("max-width: 900px;");
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

            out.println(".requests {");
            out.println("display: grid;");
            out.println("gap: 20px;");
            out.println("}");

            out.println(".request-card {");
            out.println("background: white;");
            out.println("padding: 25px;");
            out.println("border-radius: 18px;");
            out.println("box-shadow: 0 6px 20px rgba(0,0,0,0.15);");
            out.println("}");

            out.println(".request-card h2 {");
            out.println("color: #4b3ca7;");
            out.println("margin-top: 0;");
            out.println("}");

            out.println(".request-card p {");
            out.println("color: #666;");
            out.println("line-height: 1.6;");
            out.println("}");

            out.println(".status {");
            out.println("display: inline-block;");
            out.println("padding: 7px 15px;");
            out.println("border-radius: 20px;");
            out.println("font-weight: bold;");
            out.println("margin: 5px 0 15px 0;");
            out.println("background: #f0efff;");
            out.println("color: #5b4bc4;");
            out.println("}");

            out.println(".buttons {");
            out.println("display: flex;");
            out.println("gap: 10px;");
            out.println("flex-wrap: wrap;");
            out.println("margin-top: 15px;");
            out.println("}");

            out.println(".button {");
            out.println("border: none;");
            out.println("padding: 10px 22px;");
            out.println("border-radius: 20px;");
            out.println("color: white;");
            out.println("font-weight: bold;");
            out.println("cursor: pointer;");
            out.println("}");

            out.println(".accept {");
            out.println("background: #4caf50;");
            out.println("}");

            out.println(".reject {");
            out.println("background: #e74c3c;");
            out.println("}");

            out.println(".button:hover {");
            out.println("opacity: 0.85;");
            out.println("transform: translateY(-2px);");
            out.println("}");

            out.println(".empty {");
            out.println("background: white;");
            out.println("padding: 30px;");
            out.println("border-radius: 18px;");
            out.println("text-align: center;");
            out.println("color: #777;");
            out.println("box-shadow: 0 6px 20px rgba(0,0,0,0.15);");
            out.println("}");

            out.println(".back {");
            out.println("text-align: center;");
            out.println("margin-top: 30px;");
            out.println("}");

            out.println(".back a {");
            out.println("display: inline-block;");
            out.println("padding: 10px 20px;");
            out.println("background: white;");
            out.println("color: #5b4bc4;");
            out.println("text-decoration: none;");
            out.println("border-radius: 20px;");
            out.println("font-weight: bold;");
            out.println("}");

            out.println(".back a:hover {");
            out.println("background: #f0efff;");
            out.println("}");

            out.println("</style>");

            out.println("</head>");

            out.println("<body>");

            out.println("<div class='navbar'>");
            out.println("<div class='logo'>Skill Exchange</div>");
            out.println("</div>");

            out.println("<div class='container'>");

            out.println("<div class='heading'>");

            out.println("<h1>Exchange Requests</h1>");

            out.println("<p>View and manage your incoming skill exchange requests.</p>");

            out.println("</div>");

            out.println("<div class='requests'>");

            boolean found = false;

            while (rs.next()) {

                found = true;

                int requestId =
                        rs.getInt("request_id");

                int senderId =
                        rs.getInt("sender_id");

                String skillName =
                        rs.getString("skill_name");

                String status =
                        rs.getString("status");

                out.println("<div class='request-card'>");

                out.println("<h2>Request #" +
                        requestId +
                        "</h2>");

                out.println("<p><b>Sender ID:</b> " +
                        senderId +
                        "</p>");

                out.println("<p><b>Skill:</b> " +
                        skillName +
                        "</p>");

                out.println("<div class='status'>" +
                        status +
                        "</div>");

                if ("Pending".equals(status)) {

                    out.println("<div class='buttons'>");

                    out.println(
                        "<form action='updaterequest' method='post'>"
                    );

                    out.println(
                        "<input type='hidden' " +
                        "name='request_id' value='" +
                        requestId + "'>"
                    );

                    out.println(
                        "<input type='hidden' " +
                        "name='status' value='Accepted'>"
                    );

                    out.println(
                        "<input class='button accept' " +
                        "type='submit' value='Accept'>"
                    );

                    out.println("</form>");

                    out.println(
                        "<form action='updaterequest' method='post'>"
                    );

                    out.println(
                        "<input type='hidden' " +
                        "name='request_id' value='" +
                        requestId + "'>"
                    );

                    out.println(
                        "<input type='hidden' " +
                        "name='status' value='Rejected'>"
                    );

                    out.println(
                        "<input class='button reject' " +
                        "type='submit' value='Reject'>"
                    );

                    out.println("</form>");

                    out.println("</div>");
                }

                out.println("</div>");
            }

            out.println("</div>");

            if (!found) {

                out.println("<div class='empty'>");

                out.println(
                    "<p>No exchange requests found.</p>"
                );

                out.println("</div>");
            }

            out.println("<div class='back'>");

            out.println(
                "<a href='dashboard.html'>Back to Dashboard</a>"
            );

            out.println("</div>");

            out.println("</div>");

            out.println("</body>");

            out.println("</html>");

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            out.println("<h2>Error Loading Requests</h2>");

            out.println("<p>" +
                    e.getMessage() +
                    "</p>");
        }
    }
}