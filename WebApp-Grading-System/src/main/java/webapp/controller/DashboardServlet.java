package webapp.controller;

import webapp.data.GradingSystemDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import webapp.model.Role;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private GradingSystemDAO gradingDAO;
    
    public void init() {
        gradingDAO = new GradingSystemDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Check if logged in
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Get user role and username from session
        String userRole = (String) session.getAttribute("userRole");
        String username = (String) session.getAttribute("username");
        
        // Get operation (if any)
        String operation = request.getParameter("operation");
        
        // If no operation specified, default to appropriate view based on role
        if (operation == null) {
            switch (userRole.toUpperCase()) {
                case "STUDENT":
                    operation = "viewCourses";
                    break;
                case "INSTRUCTOR":
                    operation = "viewCourses";
                    break;
                case "ADMIN":
                    operation = "viewAllUsers";
                    break;
            }
        }
        
        // Handle specific operations based on role
        if ("STUDENT".equals(userRole.toUpperCase())) {
            handleStudentOperations(request, response, username, operation);
        } else if ("INSTRUCTOR".equals(userRole.toUpperCase())) {
            handleInstructorOperations(request, response, username, operation);
        } else if ("ADMIN".equals(userRole.toUpperCase())) {
            handleAdminOperations(request, response, username, operation);
        } else {
            // Unauthorized access
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle form submissions
        HttpSession session = request.getSession(false);
        
        // Check if logged in
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Get user role
        String userRole = (String) session.getAttribute("userRole");
        String operation = request.getParameter("operation");
        
        if (operation == null) {
            doGet(request, response);
            return;
        }
        
        // Route based on role and operation
        if ("ADMIN".equals(userRole.toUpperCase())) {
            handleAdminPostOperations(request, response, operation);
        } else if ("INSTRUCTOR".equals(userRole.toUpperCase())) {
            handleInstructorPostOperations(request, response, operation);
        } else {
            // No POST operations for students
            doGet(request, response);
        }
    }
    
    private void handleStudentOperations(HttpServletRequest request, HttpServletResponse response, 
                                       String username, String operation) 
            throws ServletException, IOException {
        
        switch (operation) {
            case "viewCourses":
                // Fetch student courses
                Map<String, String> courses = gradingDAO.getStudentCourses(username);
                request.setAttribute("courses", courses);
                request.setAttribute("operation", "viewCourses");
                break;
                
            case "viewGrades":
                // Fetch student grades
                Map<String, String> grades = gradingDAO.getStudentGrades(username);
                request.setAttribute("grades", grades);
                request.setAttribute("operation", "viewGrades");
                break;
                
            default:
                // Unknown operation, default to view courses
                Map<String, String> defaultCourses = gradingDAO.getStudentCourses(username);
                request.setAttribute("courses", defaultCourses);
                request.setAttribute("operation", "viewCourses");
        }
        
        // Forward to student dashboard
        request.getRequestDispatcher("/WEB-INF/views/student-dashboard.jsp").forward(request, response);
    }
    
    private void handleInstructorOperations(HttpServletRequest request, HttpServletResponse response, 
                                          String username, String operation) 
            throws ServletException, IOException {
        
        // Get parameters that might be needed
        String courseName = request.getParameter("course");
        String studentName = request.getParameter("student");
        
        switch (operation) {
            case "viewCourses":
                // Fetch instructor courses
                List<String> courses = gradingDAO.getInstructorCourses(username);
                request.setAttribute("courses", courses);
                request.setAttribute("operation", "viewCourses");
                break;
                
            case "viewCourseGrades":
                if (courseName != null && !courseName.trim().isEmpty()) {
                    // Fetch grades for specific course
                    Map<String, String> courseGrades = gradingDAO.getCourseGrades(courseName);
                    request.setAttribute("selectedCourse", courseName);
                    request.setAttribute("courseGrades", courseGrades);
                    request.setAttribute("operation", "viewCourseGrades");
                } else {
                    // Show course selection
                    List<String> instructorCourses = gradingDAO.getInstructorCourses(username);
                    request.setAttribute("courses", instructorCourses);
                    request.setAttribute("operation", "selectCourseForGrades");
                }
                break;
                
            case "editStudentGrade":
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
                break;
                
            default:
                // Unknown operation, default to view courses
                List<String> defaultCourses = gradingDAO.getInstructorCourses(username);
                request.setAttribute("courses", defaultCourses);
                request.setAttribute("operation", "viewCourses");
        }
        
        // Forward to instructor dashboard
        request.getRequestDispatcher("/WEB-INF/views/instructor-dashboard.jsp").forward(request, response);
    }
    
    private void handleAdminOperations(HttpServletRequest request, HttpServletResponse response, 
                                      String username, String operation) 
            throws ServletException, IOException {
        
        switch (operation) {
            case "addStudent":
                request.setAttribute("operation", "addStudent");
                break;
                
            case "deleteStudent":
                // Get username parameter if exists
                String studentUsername = request.getParameter("username");
                if (studentUsername != null && !studentUsername.trim().isEmpty()) {
                    // Delete student and set message
                    boolean success = gradingDAO.deleteUser(studentUsername);
                    if (success) {
                        request.setAttribute("message", "Student deleted successfully");
                    } else {
                        request.setAttribute("errorMessage", "Failed to delete student");
                    }
                    // Redirect to view all users
                    response.sendRedirect(request.getContextPath() + "/dashboard?operation=viewAllUsers");
                    return;
                } else {
                    // Show student selection
                    Map<String, String> users = gradingDAO.getUsers();
                    Map<String, String> students = new HashMap<>();
                    for (Map.Entry<String, String> entry : users.entrySet()) {
                        if ("student".equalsIgnoreCase(entry.getValue())) {
                            students.put(entry.getKey(), entry.getValue());
                        }
                    }
                    request.setAttribute("students", students);
                    request.setAttribute("operation", "deleteStudent");
                }
                break;
                
            case "addInstructor":
                request.setAttribute("operation", "addInstructor");
                break;
                
            case "deleteInstructor":
                // Get username parameter if exists
                String instructorUsername = request.getParameter("username");
                if (instructorUsername != null && !instructorUsername.trim().isEmpty()) {
                    // Delete instructor and set message
                    boolean success = gradingDAO.deleteUser(instructorUsername);
                    if (success) {
                        request.setAttribute("message", "Instructor deleted successfully");
                    } else {
                        request.setAttribute("errorMessage", "Failed to delete instructor");
                    }
                    // Redirect to view all users
                    response.sendRedirect(request.getContextPath() + "/dashboard?operation=viewAllUsers");
                    return;
                } else {
                    // Show instructor selection
                    Map<String, String> users = gradingDAO.getUsers();
                    Map<String, String> instructors = new HashMap<>();
                    for (Map.Entry<String, String> entry : users.entrySet()) {
                        if ("instructor".equalsIgnoreCase(entry.getValue())) {
                            instructors.put(entry.getKey(), entry.getValue());
                        }
                    }
                    request.setAttribute("instructors", instructors);
                    request.setAttribute("operation", "deleteInstructor");
                }
                break;
                
            case "addCourse":
                // Fetch instructors for dropdown
                Map<String, String> users = gradingDAO.getUsers();
                Map<String, String> instructors = new HashMap<>();
                for (Map.Entry<String, String> entry : users.entrySet()) {
                    if ("instructor".equalsIgnoreCase(entry.getValue())) {
                        instructors.put(entry.getKey(), entry.getValue());
                    }
                }
                request.setAttribute("instructors", instructors);
                request.setAttribute("operation", "addCourse");
                break;
                
            case "viewAllUsers":
                // Fetch all users
                Map<String, String> allUsers = gradingDAO.getUsers();
                request.setAttribute("users", allUsers);
                request.setAttribute("operation", "viewAllUsers");
                break;
                
            case "viewAllCourses":
                // Fetch all courses
                Map<String, String> courses = gradingDAO.getCourses();
                request.setAttribute("courses", courses);
                request.setAttribute("operation", "viewAllCourses");
                break;
                
            default:
                // Default to view all users
                Map<String, String> defaultUsers = gradingDAO.getUsers();
                request.setAttribute("users", defaultUsers);
                request.setAttribute("operation", "viewAllUsers");
        }
        
        // Forward to admin dashboard
        request.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(request, response);
    }
    
    private void handleAdminPostOperations(HttpServletRequest request, HttpServletResponse response,
                                         String operation)
            throws ServletException, IOException {
        
        switch (operation) {
            case "addStudent":
                // Get parameters
                String studentUsername = request.getParameter("username");
                String studentPassword = request.getParameter("password");
                
                // Validate parameters
                if (studentUsername == null || studentPassword == null ||
                        studentUsername.trim().isEmpty() || studentPassword.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "All fields are required");
                    request.setAttribute("operation", "addStudent");
                    request.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(request, response);
                    return;
                }
                
                // Add student
                boolean studentAdded = gradingDAO.addUser(studentUsername, studentPassword, Role.STUDENT);
                
                // Set success/error message
                if (studentAdded) {
                    request.setAttribute("message", "Student added successfully");
                } else {
                    request.setAttribute("errorMessage", "Failed to add student");
                }
                
                // Redirect to view all users
                response.sendRedirect(request.getContextPath() + "/dashboard?operation=viewAllUsers");
                break;
                
            case "addInstructor":
                // Get parameters
                String instructorUsername = request.getParameter("username");
                String instructorPassword = request.getParameter("password");
                
                // Validate parameters
                if (instructorUsername == null || instructorPassword == null ||
                        instructorUsername.trim().isEmpty() || instructorPassword.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "All fields are required");
                    request.setAttribute("operation", "addInstructor");
                    request.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(request, response);
                    return;
                }
                
                // Add instructor
                boolean instructorAdded = gradingDAO.addUser(instructorUsername, instructorPassword, Role.INSTRUCTOR);
                
                // Set success/error message
                if (instructorAdded) {
                    request.setAttribute("message", "Instructor added successfully");
                } else {
                    request.setAttribute("errorMessage", "Failed to add instructor");
                }
                
                // Redirect to view all users
                response.sendRedirect(request.getContextPath() + "/dashboard?operation=viewAllUsers");
                break;
                
            case "addCourse":
                // Get parameters
                String courseName = request.getParameter("courseName");
                String instructorName = request.getParameter("instructorName");
                
                // Validate parameters
                if (courseName == null || instructorName == null ||
                        courseName.trim().isEmpty() || instructorName.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "All fields are required");
                    request.setAttribute("operation", "addCourse");
                    request.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(request, response);
                    return;
                }
                
                // Add course
                boolean courseAdded = gradingDAO.addCourse(courseName, instructorName);
                
                // Set success/error message
                if (courseAdded) {
                    request.setAttribute("message", "Course added successfully");
                } else {
                    request.setAttribute("errorMessage", "Failed to add course");
                }
                
                // Redirect to view all courses
                response.sendRedirect(request.getContextPath() + "/dashboard?operation=viewAllCourses");
                break;
                
            default:
                // Unknown operation, redirect to dashboard
                response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
    
    private void handleInstructorPostOperations(HttpServletRequest request, HttpServletResponse response,
                                              String operation)
            throws ServletException, IOException {
        
        if ("updateGrade".equals(operation)) {
            // Get parameters
            String courseName = request.getParameter("course");
            String studentName = request.getParameter("student");
            String newGrade = request.getParameter("newGrade");
            
            // Validate parameters
            if (courseName == null || studentName == null || newGrade == null ||
                    courseName.trim().isEmpty() || studentName.trim().isEmpty() || newGrade.trim().isEmpty()) {
                request.setAttribute("errorMessage", "All fields are required");
                response.sendRedirect(request.getContextPath() + "/dashboard?operation=editStudentGrade");
                return;
            }
            
            // Update grade
            boolean success = gradingDAO.updateStudentGrade(courseName, studentName, newGrade);
            
            // Set success/error message
            if (success) {
                request.setAttribute("message", "Grade updated successfully");
            } else {
                request.setAttribute("errorMessage", "Failed to update grade");
            }
            
            // Redirect back to view course grades
            response.sendRedirect(request.getContextPath() + 
                    "/dashboard?operation=viewCourseGrades&course=" + courseName);
        } else {
            // Unknown operation, redirect to dashboard
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
}
