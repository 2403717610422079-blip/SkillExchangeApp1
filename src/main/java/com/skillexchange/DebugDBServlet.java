package com.skillexchange;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/debug-db")
public class DebugDBServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><body><h1>Database Schema Inspector</h1>");

        try (Connection con = DBConnection.getConnection()) {
            DatabaseMetaData meta = con.getMetaData();
            String catalog = con.getCatalog();
            out.println("<h2>Connected Catalog: " + catalog + "</h2>");

            String[] tableNames = {"users", "skills", "exchange_requests"};

            for (String tableName : tableNames) {
                out.println("<h3>Table: " + tableName + "</h3>");
                try (ResultSet rs = meta.getColumns(catalog, null, tableName, null)) {
                    out.println("<ul>");
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        String colName = rs.getString("COLUMN_NAME");
                        String colType = rs.getString("TYPE_NAME");
                        String isNullable = rs.getString("IS_NULLABLE");
                        String isAutoInc = "";
                        try {
                            isAutoInc = rs.getString("IS_AUTOINCREMENT");
                        } catch (Exception ignore) {}
                        out.println("<li><b>" + colName + "</b>: " + colType + " (Nullable: " + isNullable + ", AutoInc: " + isAutoInc + ")</li>");
                    }
                    if (!found) {
                        out.println("<li><i>Table does not exist</i></li>");
                    }
                    out.println("</ul>");
                }

                // Show row count
                try (Statement stmt = con.createStatement();
                     ResultSet countRs = stmt.executeQuery("SELECT count(*) FROM `" + tableName + "`")) {
                    if (countRs.next()) {
                        out.println("<p>Row count: " + countRs.getInt(1) + "</p>");
                    }
                } catch (Exception e) {
                    out.println("<p>Count query error: " + e.getMessage() + "</p>");
                }
            }

        } catch (Exception e) {
            out.println("<h2 style='color:red;'>Error: " + e.getMessage() + "</h2>");
            e.printStackTrace(out);
        }

        out.println("</body></html>");
    }
}
