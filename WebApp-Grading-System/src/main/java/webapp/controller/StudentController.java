package webapp.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {"/student/*", "/viewGrades"})
public class StudentController extends BaseController {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Handle legacy /viewGrades URL
        if (request.getServletPath().equals("/viewGrades")) {
            // Redirect to the standardized URL pattern
            response.sendRedirect(request.getContextPath() + "/student/grades");
            return;
        }
        
        // Check authorization
        if (!checkAuthorization(request, response, "student")) {
            return;
        }
        
        // Process any session messages
        processSessionMessages(request);
        
        String username = getUsername(request);
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Default
            viewCourses(request, response, username);
        } else if (pathInfo.equals("/courses")) {
            viewCourses(request, response, username);
        } else if (pathInfo.equals("/grades")) {
            viewGrades(request, response, username);
        } else if (pathInfo.equals("/statistics")) {
            viewCourseStatistics(request, response, username);
        } else {
            // Invalid path, redirect to default
            response.sendRedirect(request.getContextPath() + "/student");
        }
    }
    
    /**
     * Displays the courses that the student is enrolled in
     */
    private void viewCourses(HttpServletRequest request, HttpServletResponse response, String username)
            throws ServletException, IOException {
        
        Map<String, String> courses = gradingDAO.getStudentCourses(username);
        request.setAttribute("courses", courses);
        request.setAttribute("operation", "viewCourses");
        
        request.getRequestDispatcher("/WEB-INF/views/student-dashboard.jsp").forward(request, response);
    }
    
    /**
     * Displays the grades for the student
     */
    private void viewGrades(HttpServletRequest request, HttpServletResponse response, String username)
            throws ServletException, IOException {
        
        Map<String, String> grades = gradingDAO.getStudentGrades(username);
        request.setAttribute("grades", grades);
        request.setAttribute("operation", "viewGrades");
        
        request.getRequestDispatcher("/WEB-INF/views/student-dashboard.jsp").forward(request, response);
    }
    
    /**
     * Displays the statistics for a specific course or shows course selection
     */
    private void viewCourseStatistics(HttpServletRequest request, HttpServletResponse response, String username)
            throws ServletException, IOException {
        
        String courseName = request.getParameter("course");
        
        if (courseName != null && !courseName.trim().isEmpty()) {
            // View statistics for specific course
            Map<String, String> statistics = gradingDAO.getCourseStatistics(courseName);
            request.setAttribute("selectedCourse", courseName);
            request.setAttribute("statistics", statistics);
            request.setAttribute("operation", "viewCourseStatistics");
        } else {
            // Show course selection
            Map<String, String> courses = gradingDAO.getStudentCourses(username);
            request.setAttribute("courses", courses);
            request.setAttribute("operation", "selectCourseForStatistics");
        }
        
        request.getRequestDispatcher("/WEB-INF/views/student-dashboard.jsp").forward(request, response);
    }
} 