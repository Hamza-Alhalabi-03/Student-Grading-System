package com.example.demo.controllers;

import com.example.demo.models.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping(path = {"/student", "/viewGrades"})
public class StudentController extends BaseController {

    /**
     * Handle legacy /viewGrades URL by redirecting
     */
    @GetMapping("/viewGrades")
    public String handleLegacyViewGrades() {
        return "redirect:/student/grades";
    }
    
    /**
     * Default route for student dashboard
     */
    @GetMapping
    public String showDashboard(HttpSession session, HttpServletRequest request, Model model) throws IOException {
        // Check authorization
        if (!checkAuthorization(session, "student")) {
            return "redirect:/login";
        }
        
        // Process any session messages
        processSessionMessages(request, model);
        
        // Default to courses view
        String username = getUsername(session);
        return viewCourses(session, request, model, username);
    }
    
    /**
     * Shows student's courses
     */
    @GetMapping("/courses")
    public String showCourses(HttpSession session, HttpServletRequest request, Model model) throws IOException {
        // Check authorization
        if (!checkAuthorization(session, "student")) {
            return "redirect:/login";
        }
        
        // Process any session messages
        processSessionMessages(request, model);
        
        String username = getUsername(session);
        return viewCourses(session, request, model, username);
    }
    
    /**
     * Shows student's grades
     */
    @GetMapping("/grades")
    public String showGrades(HttpSession session, HttpServletRequest request, Model model) throws IOException {
        // Check authorization
        if (!checkAuthorization(session, "student")) {
            return "redirect:/login";
        }
        
        // Process any session messages
        processSessionMessages(request, model);
        
        String username = getUsername(session);
        return viewGrades(session, request, model, username);
    }
    
    /**
     * Shows statistics for a course
     */
    @GetMapping("/statistics")
    public String showStatistics(
            HttpSession session, 
            HttpServletRequest request, 
            Model model,
            @RequestParam(required = false) String course) throws IOException {
        
        // Check authorization
        if (!checkAuthorization(session, "student")) {
            return "redirect:/login";
        }
        
        // Process any session messages
        processSessionMessages(request, model);
        
        String username = getUsername(session);
        return viewCourseStatistics(session, request, model, username, course);
    }
    
    /**
     * API endpoint to get student courses
     */
    @GetMapping("/api/courses")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getStudentCourses(HttpSession session) {
        if (!checkAuthorization(session, "student")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String username = getUsername(session);
        Map<String, String> courses = gradingDAO.getStudentCourses(username);
        return ResponseEntity.ok(courses);
    }
    
    /**
     * API endpoint to get student grades
     */
    @GetMapping("/api/grades")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getStudentGrades(HttpSession session) {
        if (!checkAuthorization(session, "student")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String username = getUsername(session);
        Map<String, String> grades = gradingDAO.getStudentGrades(username);
        return ResponseEntity.ok(grades);
    }
    
    /**
     * API endpoint to get course statistics
     */
    @GetMapping("/api/statistics")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getCourseStatistics(
            HttpSession session, 
            @RequestParam String course) {
        
        if (!checkAuthorization(session, "student")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        if (course == null || course.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Map<String, String> statistics = gradingDAO.getCourseStatistics(course);
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * Displays the courses that the student is enrolled in
     */
    private String viewCourses(HttpSession session, HttpServletRequest request, Model model, String username) {
        Map<String, String> courses = gradingDAO.getStudentCourses(username);
        model.addAttribute("courses", courses);
        model.addAttribute("operation", "viewCourses");
        
        return "student-dashboard";
    }
    
    /**
     * Displays the grades for the student
     */
    private String viewGrades(HttpSession session, HttpServletRequest request, Model model, String username) {
        Map<String, String> grades = gradingDAO.getStudentGrades(username);
        model.addAttribute("grades", grades);
        model.addAttribute("operation", "viewGrades");
        
        return "student-dashboard";
    }
    
    /**
     * Displays the statistics for a specific course or shows course selection
     */
    private String viewCourseStatistics(HttpSession session, HttpServletRequest request, Model model, String username, String courseName) {
        if (courseName != null && !courseName.trim().isEmpty()) {
            // View statistics for specific course
            Map<String, String> statistics = gradingDAO.getCourseStatistics(courseName);
            model.addAttribute("selectedCourse", courseName);
            model.addAttribute("statistics", statistics);
            model.addAttribute("operation", "viewCourseStatistics");
        } else {
            // Show course selection
            Map<String, String> courses = gradingDAO.getStudentCourses(username);
            model.addAttribute("courses", courses);
            model.addAttribute("operation", "selectCourseForStatistics");
        }
        
        return "student-dashboard";
    }
} 