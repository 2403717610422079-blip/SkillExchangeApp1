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

@WebServlet("/updaterequest")
public class UpdateRequestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        int requestId = Integer.parseInt(
                request.getParameter("request_id"));

        String status = request.getParameter("status");

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DBConnection.getConnection();

            String sql =
                    "UPDATE exchange_requests "
                  + "SET status = ? "
                  + "WHERE request_id = ?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, status);
            ps.setInt(2, requestId);

            int result = ps.executeUpdate();

            out.println("<!DOCTYPE html>");
            out.println("<html>");

            out.println("<head>");

            out.println("<meta charset='UTF-8'>");

            out.println("<title>Request Updated - Skill Exchange</title>");

            out.println("<style>");

            out.println("* {");
            out.println("box-sizing: border-box;");
            out.println("}");

            out.println("body {");
            out.println("margin: 0;");
            out.println("font-family: Arial, sans-serif;");
            out.println("background: linear-gradient(135deg, #667eea, #764ba2);");
            out.println("min-height: 100vh;");
            out.println("display: flex;");
            out.println("align-items: center;");
            out.println("justify-content: center;");
            out.println("}");

            out.println(".card {");
            out.println("width: 90%;");
            out.println("max-width: 500px;");
            out.println("background: white;");
            out.println("padding: 40px;");
            out.println("border-radius: 22px;");
            out.println("text-align: center;");
            out.println("box-shadow: 0 10px 30px rgba(0,0,0,0.2);");
            out.println("}");

            out.println(".card h1 {");
            out.println("color: #4b3ca7;");
            out.println("margin-bottom: 15px;");
            out.println("}");

            out.println(".card p {");
            out.println("color: #666;");
            out.println("font-size: 16px;");
            out.println("line-height: 1.6;");
            out.println("}");

            out.println(".status {");
            out.println("display: inline-block;");
            out.println("padding: 10px 22px;");
            out.println("margin: 15px 0;");
            out.println("border-radius: 25px;");
            out.println("background: #f0efff;");
            out.println("color: #5b4bc4;");
            out.println("font-weight: bold;");
            out.println("}");

            out.println(".button {");
            out.println("display: inline-block;");
            out.println("margin-top: 20px;");
            out.println("padding: 12px 25px;");
            out.println("background: linear-gradient(135deg, #667eea, #764ba2);");
            out.println("color: white;");
            out.println("text-decoration: none;");
            out.println("border-radius: 25px;");
            out.println("font-weight: bold;");
            out.println("}");

            out.println(".button:hover {");
            out.println("opacity: 0.85;");
            out.println("transform: translateY(-2px);");
            out.println("}");

            out.println(".error {");
            out.println("color: #e74c3c;");
            out.println("font-weight: bold;");
            out.println("}");

            out.println("</style>");

            out.println("</head>");

            out.println("<body>");

            out.println("<div class='card'>");

            if (result > 0) {

                out.println("<h1>Request Updated Successfully</h1>");

                out.println("<p>Your exchange request has been updated.</p>");

                out.println("<div class='status'>Status: "
                        + status +
                        "</div>");

                out.println("<br>");

                out.println("<a class='button' href='requests'>");
                out.println("Back to Requests");
                out.println("</a>");

            } else {

                out.println("<h1>Request Not Found</h1>");

                out.println("<p class='error'>");
                out.println("The requested exchange request could not be found.");
                out.println("</p>");

                out.println("<a class='button' href='requests'>");
                out.println("Back to Requests");
                out.println("</a>");
            }

            out.println("</div>");

            out.println("</body>");

            out.println("</html>");

            ps.close();
            con.close();

        } catch (Exception e) {

            out.println("<!DOCTYPE html>");
            out.println("<html>");

            out.println("<head>");
            out.println("<title>Error</title>");

            out.println("<style>");

            out.println("body {");
            out.println("margin: 0;");
            out.println("font-family: Arial, sans-serif;");
            out.println("background: linear-gradient(135deg, #667eea, #764ba2);");
            out.println("min-height: 100vh;");
            out.println("display: flex;");
            out.println("align-items: center;");
            out.println("justify-content: center;");
            out.println("}");

            out.println(".card {");
            out.println("background: white;");
            out.println("padding: 40px;");
            out.println("border-radius: 20px;");
            out.println("text-align: center;");
            out.println("max-width: 500px;");
            out.println("}");

            out.println("h1 {");
            out.println("color: #e74c3c;");
            out.println("}");

            out.println("p {");
            out.println("color: #666;");
            out.println("}");

            out.println("a {");
            out.println("display: inline-block;");
            out.println("margin-top: 20px;");
            out.println("padding: 10px 20px;");
            out.println("background: #5b4bc4;");
            out.println("color: white;");
            out.println("text-decoration: none;");
            out.println("border-radius: 20px;");
            out.println("}");

            out.println("</style>");
            out.println("</head>");

            out.println("<body>");

            out.println("<div class='card'>");

            out.println("<h1>Error Updating Request</h1>");

            out.println("<p>" +
                    e.getMessage() +
                    "</p>");

            out.println("<a href='requests'>Back to Requests</a>");

            out.println("</div>");

            out.println("</body>");

            out.println("</html>");
        }
    }
}