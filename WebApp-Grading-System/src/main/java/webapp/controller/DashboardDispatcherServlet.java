package webapp.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardDispatcherServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Check if logged in
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String userRole = (String) session.getAttribute("userRole");
        
        // Get operation parameter
        String operation = request.getParameter("operation");
        
        // Build the appropriate redirect URL based on role and operation
        String redirectUrl = buildRedirectUrl(request, userRole, operation);
        
        response.sendRedirect(redirectUrl);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // forward to doGet
        doGet(request, response);
    }
    
    /**
     * Builds the appropriate redirect URL based on user role and requested operation
     */
    private String buildRedirectUrl(HttpServletRequest request, String userRole, String operation) {
        String contextPath = request.getContextPath();
        
        // Default URLs for each role
        if (operation == null) {
            if ("STUDENT".equalsIgnoreCase(userRole)) {
                return contextPath + "/student";
            } else if ("INSTRUCTOR".equalsIgnoreCase(userRole)) {
                return contextPath + "/instructor";
            } else if ("ADMIN".equalsIgnoreCase(userRole)) {
                return contextPath + "/admin";
            } else {
                return contextPath + "/login";
            }
        }
        
        // Handle legacy operations for each role
        if ("STUDENT".equalsIgnoreCase(userRole)) {
            switch (operation) {
                case "viewCourses":
                    return contextPath + "/student/courses";
                case "viewGrades":
                    return contextPath + "/student/grades";
                default:
                    return contextPath + "/student";
            }
        } else if ("INSTRUCTOR".equalsIgnoreCase(userRole)) {
            String course = request.getParameter("course");
            String student = request.getParameter("student");
            
            switch (operation) {
                case "viewCourses":
                    return contextPath + "/instructor/courses";
                case "viewCourseGrades":
                    if (course != null && !course.trim().isEmpty()) {
                        return contextPath + "/instructor/courseGrades?course=" + course;
                    } else {
                        return contextPath + "/instructor/courseGrades";
                    }
                case "editStudentGrade":
                    String editUrl = contextPath + "/instructor/editGrade";
                    if (course != null && !course.trim().isEmpty()) {
                        editUrl += "?course=" + course;
                        if (student != null && !student.trim().isEmpty()) {
                            editUrl += "&student=" + student;
                        }
                    }
                    return editUrl;
                default:
                    return contextPath + "/instructor";
            }
        } else if ("ADMIN".equalsIgnoreCase(userRole)) {
            switch (operation) {
                case "viewAllUsers":
                    return contextPath + "/admin/users";
                case "viewAllCourses":
                    return contextPath + "/admin/courses";
                case "addStudent":
                    return contextPath + "/admin/addStudent";
                case "addInstructor":
                    return contextPath + "/admin/addInstructor";
                case "addCourse":
                    return contextPath + "/admin/addCourse";
                case "deleteStudent":
                    String studentUsername = request.getParameter("username");
                    if (studentUsername != null && !studentUsername.trim().isEmpty()) {
                        return contextPath + "/admin/deleteStudent?username=" + studentUsername;
                    } else {
                        return contextPath + "/admin/deleteStudent";
                    }
                case "deleteInstructor":
                    String instructorUsername = request.getParameter("username");
                    if (instructorUsername != null && !instructorUsername.trim().isEmpty()) {
                        return contextPath + "/admin/deleteInstructor?username=" + instructorUsername;
                    } else {
                        return contextPath + "/admin/deleteInstructor";
                    }
                default:
                    return contextPath + "/admin";
            }
        } else {
            // Unknown role, redirect to login page
            return contextPath + "/login";
        }
    }
} 