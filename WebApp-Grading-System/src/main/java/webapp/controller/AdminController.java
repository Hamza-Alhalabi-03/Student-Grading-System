package webapp.controller;

import webapp.model.Role;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/admin/*")
public class AdminController extends BaseController {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check authorization
        if (!checkAuthorization(request, response, "admin")) {
            return;
        }
        
        // Process any session messages
        processSessionMessages(request);
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Default to view all users
            viewAllUsers(request, response);
        } else if (pathInfo.equals("/users")) {
            viewAllUsers(request, response);
        } else if (pathInfo.equals("/courses")) {
            viewAllCourses(request, response);
        } else if (pathInfo.equals("/addStudent")) {
            showAddStudentForm(request, response);
        } else if (pathInfo.equals("/addInstructor")) {
            showAddInstructorForm(request, response);
        } else if (pathInfo.equals("/addCourse")) {
            showAddCourseForm(request, response);
        } else if (pathInfo.equals("/deleteStudent")) {
            showDeleteStudentForm(request, response);
        } else if (pathInfo.equals("/deleteInstructor")) {
            showDeleteInstructorForm(request, response);
        } else {
            // Invalid path, redirect to default
            response.sendRedirect(request.getContextPath() + "/admin");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check authorization
        if (!checkAuthorization(request, response, "admin")) {
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            response.sendRedirect(request.getContextPath() + "/admin");
            return;
        }
        
        if (pathInfo.equals("/addStudent")) {
            addStudent(request, response);
        } else if (pathInfo.equals("/addInstructor")) {
            addInstructor(request, response);
        } else if (pathInfo.equals("/addCourse")) {
            addCourse(request, response);
        } else if (pathInfo.equals("/deleteStudent") || pathInfo.equals("/deleteInstructor")) {
            deleteUser(request, response, pathInfo);
        } else {
            // Unknown operation, redirect to dashboard
            response.sendRedirect(request.getContextPath() + "/admin");
        }
    }
    
    /**
     * Displays all users in the system
     */
    private void viewAllUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Fetch all users
        Map<String, String> allUsers = gradingDAO.getUsers();
        request.setAttribute("users", allUsers);
        request.setAttribute("operation", "viewAllUsers");
        
        // Forward to admin dashboard
        request.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(request, response);
    }
    
    /**
     * Displays all courses in the system
     */
    private void viewAllCourses(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Fetch all courses
        Map<String, String> courses = gradingDAO.getCourses();
        request.setAttribute("courses", courses);
        request.setAttribute("operation", "viewAllCourses");
        
        // Forward to admin dashboard
        request.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(request, response);
    }
    
    /**
     * Shows form to add a new student
     */
    private void showAddStudentForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setAttribute("operation", "addStudent");
        request.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(request, response);
    }
    
    /**
     * Shows form to add a new instructor
     */
    private void showAddInstructorForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setAttribute("operation", "addInstructor");
        request.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(request, response);
    }
    
    /**
     * Shows form to add a new course
     */
    private void showAddCourseForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
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
        
        request.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(request, response);
    }
    
    /**
     * Shows form to delete a student
     */
    private void showDeleteStudentForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get username parameter if exists
        String studentUsername = request.getParameter("username");
        if (studentUsername != null && !studentUsername.trim().isEmpty()) {
            // Delete student and set message
            boolean success = gradingDAO.deleteUser(studentUsername);
            
            // Store message in session
            HttpSession session = request.getSession();
            if (success) {
                session.setAttribute("message", "Student deleted successfully");
            } else {
                session.setAttribute("errorMessage", "Failed to delete student");
            }
            
            // Redirect to view all users
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }
        
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
        
        request.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(request, response);
    }
    
    /**
     * Shows form to delete an instructor
     */
    private void showDeleteInstructorForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get username parameter if exists
        String instructorUsername = request.getParameter("username");
        if (instructorUsername != null && !instructorUsername.trim().isEmpty()) {
            // Delete instructor and set message
            boolean success = gradingDAO.deleteUser(instructorUsername);
            
            // Store message in session
            HttpSession session = request.getSession();
            if (success) {
                session.setAttribute("message", "Instructor deleted successfully");
            } else {
                session.setAttribute("errorMessage", "Failed to delete instructor");
            }
            
            // Redirect to view all users
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }
        
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
        
        request.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(request, response);
    }
    
    /**
     * Adds a new student
     */
    private void addStudent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
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
        
        // Store message in session
        HttpSession session = request.getSession();
        if (studentAdded) {
            session.setAttribute("message", "Student added successfully");
        } else {
            session.setAttribute("errorMessage", "Failed to add student");
        }
        
        // Redirect to view all users
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
    
    /**
     * Adds a new instructor
     */
    private void addInstructor(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
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
        
        // Store message in session
        HttpSession session = request.getSession();
        if (instructorAdded) {
            session.setAttribute("message", "Instructor added successfully");
        } else {
            session.setAttribute("errorMessage", "Failed to add instructor");
        }
        
        // Redirect to view all users
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
    
    /**
     * Adds a new course
     */
    private void addCourse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
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
        
        // Store message in session
        HttpSession session = request.getSession();
        if (courseAdded) {
            session.setAttribute("message", "Course added successfully");
        } else {
            session.setAttribute("errorMessage", "Failed to add course");
        }
        
        // Redirect to view all courses
        response.sendRedirect(request.getContextPath() + "/admin/courses");
    }
    
    /**
     * Deletes a user (student or instructor)
     */
    private void deleteUser(HttpServletRequest request, HttpServletResponse response, String pathInfo)
            throws ServletException, IOException {
        
        // Get username parameter
        String username = request.getParameter("username");
        
        // Validate parameters
        if (username == null || username.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + pathInfo);
            return;
        }
        
        // Delete user
        boolean success = gradingDAO.deleteUser(username);
        
        // Store message in session
        HttpSession session = request.getSession();
        if (success) {
            session.setAttribute("message", "User deleted successfully");
        } else {
            session.setAttribute("errorMessage", "Failed to delete user");
        }
        
        // Redirect to view all users
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
} 