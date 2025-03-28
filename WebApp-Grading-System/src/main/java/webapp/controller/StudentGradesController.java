package webapp.controller;

import webapp.data.GradingSystemDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@WebServlet("/viewGrades")
public class StudentGradesController extends HttpServlet {
    private GradingSystemDAO gradingDAO;

    public void init() {
        gradingDAO = new GradingSystemDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Check if user is logged in
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Check if user is a student
        String userRole = (String) session.getAttribute("userRole");
        if (!"student".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }
        
        // Get username from session
        String username = (String) session.getAttribute("username");
        
        // Fetch student grades from DAO
        Map<String, String> grades = gradingDAO.getStudentGrades(username);
        
        // Set grades as request attribute
        request.setAttribute("grades", grades);
        
        // Forward to student dashboard
        request.getRequestDispatcher("/WEB-INF/views/student-dashboard.jsp")
                .forward(request, response);
    }
} 