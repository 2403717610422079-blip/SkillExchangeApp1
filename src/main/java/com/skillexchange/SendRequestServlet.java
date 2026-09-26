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

@WebServlet("/sendrequest")
public class SendRequestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        int skillId = Integer.parseInt(
                request.getParameter("skill_id"));

        int receiverId = Integer.parseInt(
                request.getParameter("receiver_id"));

        int senderId = (Integer) request.getSession().getAttribute("user_id");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {

        	Connection con = DBConnection.getConnection();

            String sql = "INSERT INTO exchange_requests "
                       + "(sender_id, receiver_id, skill_id, status) "
                       + "VALUES (?, ?, ?, 'Pending')";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.setInt(3, skillId);

            int result = ps.executeUpdate();

            if (result > 0) {

                out.println("<h2>Exchange Request Sent Successfully!</h2>");
                out.println("<p>Status: Pending</p>");
                out.println("<br>");
                out.println("<a href='dashboard.html'>Back to Dashboard</a>");

            }

            ps.close();
            con.close();

        } catch (Exception e) {

            out.println("<h2>Request Failed</h2>");
            out.println("<p>" + e.getMessage() + "</p>");

        }
    }
}