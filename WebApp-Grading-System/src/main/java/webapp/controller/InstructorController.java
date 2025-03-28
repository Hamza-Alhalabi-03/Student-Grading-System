package webapp.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/instructor/*")
public class InstructorController extends BaseController {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!checkAuthorization(request, response, "instructor")) {
            return;
        }
        
        processSessionMessages(request);
        
        String username = getUsername(request);
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            viewCourses(request, response, username);
        } else if (pathInfo.equals("/courses")) {
            viewCourses(request, response, username);
        } else if (pathInfo.equals("/courseGrades")) {
            viewCourseGrades(request, response, username);
        } else if (pathInfo.equals("/editGrade")) {
            editStudentGrade(request, response, username);
        } else if (pathInfo.equals("/statistics")) {
            viewCourseStatistics(request, response, username);
        } else {
            // Invalid path, redirect to default
            response.sendRedirect(request.getContextPath() + "/instructor");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!checkAuthorization(request, response, "instructor")) {
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.equals("/updateGrade")) {
            updateStudentGrade(request, response);
        } else {
            // Unknown operation, redirect to default
            response.sendRedirect(request.getContextPath() + "/instructor");
        }
    }
    
    /**
     * Displays the courses that the instructor teaches
     */
    private void viewCourses(HttpServletRequest request, HttpServletResponse response, String username)
            throws ServletException, IOException {
        
        List<String> courses = gradingDAO.getInstructorCourses(username);
        request.setAttribute("courses", courses);
        request.setAttribute("operation", "viewCourses");
        
        request.getRequestDispatcher("/WEB-INF/views/instructor-dashboard.jsp").forward(request, response);
    }
    
    /**
     * Displays grades for a specific course or shows course selection
     */
    private void viewCourseGrades(HttpServletRequest request, HttpServletResponse response, String username)
            throws ServletException, IOException {
        
        String courseName = request.getParameter("course");
        
        if (courseName != null && !courseName.trim().isEmpty()) {

            Map<String, String> courseGrades = gradingDAO.getCourseGrades(courseName);
            request.setAttribute("selectedCourse", courseName);
            request.setAttribute("courseGrades", courseGrades);
            request.setAttribute("operation", "viewCourseGrades");
        } else {

            List<String> instructorCourses = gradingDAO.getInstructorCourses(username);
            request.setAttribute("courses", instructorCourses);
            request.setAttribute("operation", "selectCourseForGrades");
        }
        
        request.getRequestDispatcher("/WEB-INF/views/instructor-dashboard.jsp").forward(request, response);
    }
    
    /**
     * Displays form to edit a student's grade
     */
    private void editStudentGrade(HttpServletRequest request, HttpServletResponse response, String username)
            throws ServletException, IOException {
        
        String courseName = request.getParameter("course");
        String studentName = request.getParameter("student");
        
        if (courseName != null && studentName != null && 
            !courseName.trim().isEmpty() && !studentName.trim().isEmpty()) {
            // Show edit form for specific student
            request.setAttribute("selectedCourse", courseName);
            request.setAttribute("selectedStudent", studentName);
            request.setAttribute("operation", "editGradeForm");
        } else if (courseName != null && !courseName.trim().isEmpty()) {
            // Show student selection for specific course
            Map<String, String> courseGrades = gradingDAO.getCourseGrades(courseName);
            request.setAttribute("selectedCourse", courseName);
            request.setAttribute("courseGrades", courseGrades);
            request.setAttribute("operation", "selectStudentForGradeEdit");
        } else {
            // Show course selection
            List<String> instructorCourses = gradingDAO.getInstructorCourses(username);
            request.setAttribute("courses", instructorCourses);
            request.setAttribute("operation", "selectCourseForGradeEdit");
        }
        
        // Forward to instructor dashboard
        request.getRequestDispatcher("/WEB-INF/views/instructor-dashboard.jsp").forward(request, response);
    }
    
    /**
     * Updates a student's grade
     */
    private void updateStudentGrade(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get parameters
        String courseName = request.getParameter("course");
        String studentName = request.getParameter("student");
        String newGrade = request.getParameter("newGrade");
        
        // Validate parameters
        if (courseName == null || studentName == null || newGrade == null ||
                courseName.trim().isEmpty() || studentName.trim().isEmpty() || newGrade.trim().isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "All fields are required");
            response.sendRedirect(request.getContextPath() + "/instructor/editGrade");
            return;
        }
        
        // Update grade
        boolean success = gradingDAO.updateStudentGrade(courseName, studentName, newGrade);
        
        // Set success/error message in session
        HttpSession session = request.getSession();
        if (success) {
            session.setAttribute("message", "Grade updated successfully");
        } else {
            session.setAttribute("errorMessage", "Failed to update grade");
        }
        
        // Redirect back to view course grades
        response.sendRedirect(request.getContextPath() + 
                "/instructor/courseGrades?course=" + courseName);
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
            List<String> instructorCourses = gradingDAO.getInstructorCourses(username);
            request.setAttribute("courses", instructorCourses);
            request.setAttribute("operation", "selectCourseForStatistics");
        }
        
        request.getRequestDispatcher("/WEB-INF/views/instructor-dashboard.jsp").forward(request, response);
    }
} 