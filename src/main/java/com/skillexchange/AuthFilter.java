package com.skillexchange;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());

        // Normalize path
        if (path.isEmpty() || path.equals("/")) {
            chain.doFilter(req, res);
            return;
        }

        // Allow public pages and resources
        boolean isPublic = path.equals("/index.html")
                || path.equals("/login.html")
                || path.equals("/login")
                || path.equals("/register.html")
                || path.equals("/register")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".png")
                || path.endsWith(".jpg")
                || path.endsWith(".jpeg")
                || path.endsWith(".svg")
                || path.endsWith(".ico");

        if (isPublic) {
            chain.doFilter(req, res);
            return;
        }

        // Check user session for protected routes
        HttpSession session = request.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("user_id") != null);

        if (!isLoggedIn) {
            response.sendRedirect(contextPath + "/login.html");
            return;
        }

        // Prevent browser caching of sensitive pages so back button after logout doesn't show old session data
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }
}
