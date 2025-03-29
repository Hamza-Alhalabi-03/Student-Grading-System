package com.example.demo.controllers;

import com.example.demo.models.Role;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {
    
    @GetMapping
    public String showDashboard(HttpSession session, HttpServletRequest request, Model model) throws IOException {
        if (!checkAuthorization(session, "admin")) {
            return "redirect:/login";
        }
        
        processSessionMessages(request, model);
        
        // Default to view all users
        return viewAllUsers(request, model);
    }
    
    @GetMapping("/users")
    public String showUsers(HttpSession session, HttpServletRequest request, Model model) throws IOException {
        if (!checkAuthorization(session, "admin")) {
            return "redirect:/login";
        }
        
        processSessionMessages(request, model);
        
        return viewAllUsers(request, model);
    }
    
    @GetMapping("/courses")
    public String showCourses(HttpSession session, HttpServletRequest request, Model model) throws IOException {
        if (!checkAuthorization(session, "admin")) {
            return "redirect:/login";
        }
        
        processSessionMessages(request, model);
        
        return viewAllCourses(request, model);
    }
    
    @GetMapping("/addStudent")
    public String showAddStudentForm(HttpSession session, HttpServletRequest request, Model model) throws IOException {
        if (!checkAuthorization(session, "admin")) {
            return "redirect:/login";
        }
        
        processSessionMessages(request, model);
        
        model.addAttribute("operation", "addStudent");
        return "admin-dashboard";
    }
    
    @GetMapping("/addInstructor")
    public String showAddInstructorForm(HttpSession session, HttpServletRequest request, Model model) throws IOException {
        if (!checkAuthorization(session, "admin")) {
            return "redirect:/login";
        }
        
        processSessionMessages(request, model);
        
        model.addAttribute("operation", "addInstructor");
        return "admin-dashboard";
    }
    
    @GetMapping("/addCourse")
    public String showAddCourseForm(HttpSession session, HttpServletRequest request, Model model) throws IOException {
        if (!checkAuthorization(session, "admin")) {
            return "redirect:/login";
        }
        
        processSessionMessages(request, model);
        
        return prepareAddCourseForm(model);
    }
    
    @GetMapping("/deleteStudent")
    public String showDeleteStudentForm(
            HttpSession session, 
            HttpServletRequest request, 
            Model model,
            @RequestParam(required = false) String username,
            RedirectAttributes redirectAttributes) throws IOException {
        
        if (!checkAuthorization(session, "admin")) {
            return "redirect:/login";
        }
        
        processSessionMessages(request, model);
        
        if (username != null && !username.trim().isEmpty()) {
            return processStudentDeletion(username, redirectAttributes);
        }
        
        return prepareStudentDeletionForm(model);
    }
    
    @GetMapping("/deleteInstructor")
    public String showDeleteInstructorForm(
            HttpSession session, 
            HttpServletRequest request, 
            Model model,
            @RequestParam(required = false) String username,
            RedirectAttributes redirectAttributes) throws IOException {
        
        if (!checkAuthorization(session, "admin")) {
            return "redirect:/login";
        }
        
        processSessionMessages(request, model);
        
        if (username != null && !username.trim().isEmpty()) {
            return processInstructorDeletion(username, redirectAttributes);
        }
        
        return prepareInstructorDeletionForm(model);
    }
    
    @PostMapping("/addStudent")
    public String handleAddStudent(
            HttpSession session,
            @RequestParam String username,
            @RequestParam String password,
            Model model,
            RedirectAttributes redirectAttributes) throws IOException {
        
        if (!checkAuthorization(session, "admin")) {
            return "redirect:/login";
        }
        
        // Validate parameters
        if (username == null || password == null ||
                username.trim().isEmpty() || password.trim().isEmpty()) {
            model.addAttribute("errorMessage", "All fields are required");
            model.addAttribute("operation", "addStudent");
            return "admin-dashboard";
        }
        
        boolean studentAdded = gradingDAO.addUser(username, password, Role.STUDENT);
        
        if (studentAdded) {
            redirectAttributes.addFlashAttribute("message", "Student added successfully");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add student");
        }
        
        return "redirect:/admin/users";
    }
    
    @PostMapping("/addInstructor")
    public String handleAddInstructor(
            HttpSession session,
            @RequestParam String username,
            @RequestParam String password,
            Model model,
            RedirectAttributes redirectAttributes) throws IOException {
        
        if (!checkAuthorization(session, "admin")) {
            return "redirect:/login";
        }
        
        // Validate parameters
        if (username == null || password == null ||
                username.trim().isEmpty() || password.trim().isEmpty()) {
            model.addAttribute("errorMessage", "All fields are required");
            model.addAttribute("operation", "addInstructor");
            return "admin-dashboard";
        }
        
        boolean instructorAdded = gradingDAO.addUser(username, password, Role.INSTRUCTOR);
        
        if (instructorAdded) {
            redirectAttributes.addFlashAttribute("message", "Instructor added successfully");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add instructor");
        }
        
        return "redirect:/admin/users";
    }
    
    @PostMapping("/addCourse")
    public String handleAddCourse(
            HttpSession session,
            @RequestParam String courseName,
            @RequestParam String instructorName,
            Model model,
            RedirectAttributes redirectAttributes) throws IOException {
        
        if (!checkAuthorization(session, "admin")) {
            return "redirect:/login";
        }
        
        if (courseName == null || instructorName == null ||
                courseName.trim().isEmpty() || instructorName.trim().isEmpty()) {
            model.addAttribute("errorMessage", "All fields are required");
            model.addAttribute("operation", "addCourse");
            return prepareAddCourseForm(model);
        }
        
        boolean courseAdded = gradingDAO.addCourse(courseName, instructorName);
        
        // Set message
        if (courseAdded) {
            redirectAttributes.addFlashAttribute("message", "Course added successfully");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add course");
        }
        
        return "redirect:/admin/courses";
    }
    
    // API endpoints
    
    @GetMapping("/api/users")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getAllUsers(HttpSession session) {
        if (!checkAuthorization(session, "admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Map<String, String> users = gradingDAO.getUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/api/courses")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getAllCourses(HttpSession session) {
        if (!checkAuthorization(session, "admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Map<String, String> courses = gradingDAO.getCourses();
        return ResponseEntity.ok(courses);
    }
    
    @PostMapping("/api/addStudent")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addStudentApi(
            HttpSession session,
            @RequestBody Map<String, String> studentData) {
        
        if (!checkAuthorization(session, "admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String username = studentData.get("username");
        String password = studentData.get("password");
        
        Map<String, Object> response = new HashMap<>();
        
        // Validate
        if (username == null || password == null ||
                username.trim().isEmpty() || password.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "All fields are required");
            return ResponseEntity.badRequest().body(response);
        }
        
        boolean studentAdded = gradingDAO.addUser(username, password, Role.STUDENT);
        
        response.put("success", studentAdded);
        if (studentAdded) {
            response.put("message", "Student added successfully");
        } else {
            response.put("message", "Failed to add student");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/api/addInstructor")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addInstructorApi(
            HttpSession session,
            @RequestBody Map<String, String> instructorData) {
        
        if (!checkAuthorization(session, "admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String username = instructorData.get("username");
        String password = instructorData.get("password");
        
        Map<String, Object> response = new HashMap<>();
        
        // Validate
        if (username == null || password == null ||
                username.trim().isEmpty() || password.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "All fields are required");
            return ResponseEntity.badRequest().body(response);
        }
        
        boolean instructorAdded = gradingDAO.addUser(username, password, Role.INSTRUCTOR);
        
        response.put("success", instructorAdded);
        if (instructorAdded) {
            response.put("message", "Instructor added successfully");
        } else {
            response.put("message", "Failed to add instructor");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/api/addCourse")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addCourseApi(
            HttpSession session,
            @RequestBody Map<String, String> courseData) {
        
        if (!checkAuthorization(session, "admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String courseName = courseData.get("courseName");
        String instructorName = courseData.get("instructorName");
        
        Map<String, Object> response = new HashMap<>();
        
        // Validate
        if (courseName == null || instructorName == null ||
                courseName.trim().isEmpty() || instructorName.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "All fields are required");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Add course
        boolean courseAdded = gradingDAO.addCourse(courseName, instructorName);
        
        response.put("success", courseAdded);
        if (courseAdded) {
            response.put("message", "Course added successfully");
        } else {
            response.put("message", "Failed to add course");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/api/user/{username}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteUserApi(
            HttpSession session,
            @PathVariable String username) {
        
        if (!checkAuthorization(session, "admin")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Map<String, Object> response = new HashMap<>();
        
        // Validate
        if (username == null || username.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Username is required");
            return ResponseEntity.badRequest().body(response);
        }
        
        boolean userDeleted = gradingDAO.deleteUser(username);
        
        response.put("success", userDeleted);
        if (userDeleted) {
            response.put("message", "User deleted successfully");
        } else {
            response.put("message", "Failed to delete user");
        }
        
        return ResponseEntity.ok(response);
    }

    private String viewAllUsers(HttpServletRequest request, Model model) {
        // Fetch all users
        Map<String, String> allUsers = gradingDAO.getUsers();
        model.addAttribute("users", allUsers);
        model.addAttribute("operation", "viewAllUsers");
        
        return "admin-dashboard";
    }
    
    private String viewAllCourses(HttpServletRequest request, Model model) {
        Map<String, String> courses = gradingDAO.getCourses();
        model.addAttribute("courses", courses);
        model.addAttribute("operation", "viewAllCourses");
        
        return "admin-dashboard";
    }
    
    private String prepareAddCourseForm(Model model) {
        Map<String, String> users = gradingDAO.getUsers();
        Map<String, String> instructors = new HashMap<>();
        
        for (Map.Entry<String, String> entry : users.entrySet()) {
            if ("instructor".equalsIgnoreCase(entry.getValue())) {
                instructors.put(entry.getKey(), entry.getValue());
            }
        }
        
        model.addAttribute("instructors", instructors);
        model.addAttribute("operation", "addCourse");
        
        return "admin-dashboard";
    }
    
    private String prepareStudentDeletionForm(Model model) {
        // Show student selection
        Map<String, String> users = gradingDAO.getUsers();
        Map<String, String> students = new HashMap<>();
        
        for (Map.Entry<String, String> entry : users.entrySet()) {
            if ("student".equalsIgnoreCase(entry.getValue())) {
                students.put(entry.getKey(), entry.getValue());
            }
        }
        
        model.addAttribute("students", students);
        model.addAttribute("operation", "deleteStudent");
        
        return "admin-dashboard";
    }
    
    private String prepareInstructorDeletionForm(Model model) {
        // Show instructor selection
        Map<String, String> users = gradingDAO.getUsers();
        Map<String, String> instructors = new HashMap<>();
        
        for (Map.Entry<String, String> entry : users.entrySet()) {
            if ("instructor".equalsIgnoreCase(entry.getValue())) {
                instructors.put(entry.getKey(), entry.getValue());
            }
        }
        
        model.addAttribute("instructors", instructors);
        model.addAttribute("operation", "deleteInstructor");
        
        return "admin-dashboard";
    }
    
    private String processStudentDeletion(String studentUsername, RedirectAttributes redirectAttributes) {
        // Delete student and set message
        boolean success = gradingDAO.deleteUser(studentUsername);
        
        // Store message
        if (success) {
            redirectAttributes.addFlashAttribute("message", "Student deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete student");
        }
        
        return "redirect:/admin/users";
    }
    
    private String processInstructorDeletion(String instructorUsername, RedirectAttributes redirectAttributes) {
        boolean success = gradingDAO.deleteUser(instructorUsername);
        
        // Store message
        if (success) {
            redirectAttributes.addFlashAttribute("message", "Instructor deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete instructor");
        }
        
        return "redirect:/admin/users";
    }
} 