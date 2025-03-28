package webapp.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Check if logged in
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("/login");
            return;
        }

        // Get user role from session
        String userRole = (String) session.getAttribute("userRole");

        // Role-based routing
        switch (userRole) {
            case "student":
                request.getRequestDispatcher("/WEB-INF/views/student-dashboard.jsp")
                        .forward(request, response);
                break;
            case "instructor":
                request.getRequestDispatcher("/WEB-INF/views/instructor-dashboard.jsp")
                        .forward(request, response);
                break;
            case "admin":
                request.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp")
                        .forward(request, response);
                break;
            default:
                // Unauthorized access
                response.sendRedirect("/login");
        }
    }
}
