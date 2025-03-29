package com.example.demo.controllers;

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
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/instructor")
public class InstructorController extends BaseController {
    
    @GetMapping
    public String showDashboard(HttpSession session, HttpServletRequest request, Model model) throws IOException {
        if (!checkAuthorization(session, "instructor")) {
            return "redirect:/login";
        }
        
        processSessionMessages(request, model);
        
        String username = getUsername(session);
        return viewCourses(session, request, model, username);
    }
    
    @GetMapping("/courses")
    public String showCourses(HttpSession session, HttpServletRequest request, Model model) throws IOException {
        if (!checkAuthorization(session, "instructor")) {
            return "redirect:/login";
        }
        
        processSessionMessages(request, model);
        
        String username = getUsername(session);
        return viewCourses(session, request, model, username);
    }
    
    @GetMapping("/courseGrades")
    public String showCourseGrades(
            HttpSession session, 
            HttpServletRequest request, 
            Model model,
            @RequestParam(required = false) String course) throws IOException {
        
        if (!checkAuthorization(session, "instructor")) {
            return "redirect:/login";
        }
        
        processSessionMessages(request, model);
        
        String username = getUsername(session);
        return viewCourseGrades(session, request, model, username, course);
    }
    
    @GetMapping("/editGrade")
    public String showEditGrade(
            HttpSession session, 
            HttpServletRequest request, 
            Model model,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String student) throws IOException {
        
        if (!checkAuthorization(session, "instructor")) {
            return "redirect:/login";
        }
        
        processSessionMessages(request, model);
        
        String username = getUsername(session);
        return editStudentGrade(session, request, model, username, course, student);
    }
    
    @GetMapping("/statistics")
    public String showStatistics(
            HttpSession session, 
            HttpServletRequest request, 
            Model model,
            @RequestParam(required = false) String course) throws IOException {
        
        if (!checkAuthorization(session, "instructor")) {
            return "redirect:/login";
        }
        
        processSessionMessages(request, model);
        
        String username = getUsername(session);
        return viewCourseStatistics(session, request, model, username, course);
    }
    
    @PostMapping("/updateGrade")
    public String updateGrade(
            HttpSession session,
            @RequestParam String course,
            @RequestParam String student,
            @RequestParam String newGrade,
            RedirectAttributes redirectAttributes) {
        
        if (!checkAuthorization(session, "instructor")) {
            return "redirect:/login";
        }
        
        // Validate parameters
        if (course == null || student == null || newGrade == null ||
                course.trim().isEmpty() || student.trim().isEmpty() || newGrade.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "All fields are required");
            return "redirect:/instructor/editGrade";
        }
        
        // Update grade
        boolean success = gradingDAO.updateStudentGrade(course, student, newGrade);
        
        // Set success/error message
        if (success) {
            redirectAttributes.addFlashAttribute("message", "Grade updated successfully");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update grade");
        }
        
        // Redirect back to view course grades
        return "redirect:/instructor/courseGrades?course=" + course;
    }
    
    // API Endpoints
    
    @GetMapping("/api/courses")
    @ResponseBody
    public ResponseEntity<List<String>> getInstructorCourses(HttpSession session) {
        if (!checkAuthorization(session, "instructor")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String username = getUsername(session);
        List<String> courses = gradingDAO.getInstructorCourses(username);
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/api/courseGrades")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getCourseGrades(
            HttpSession session,
            @RequestParam String course) {
        
        if (!checkAuthorization(session, "instructor")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        if (course == null || course.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Map<String, String> grades = gradingDAO.getCourseGrades(course);
        return ResponseEntity.ok(grades);
    }
    
    @GetMapping("/api/statistics")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getCourseStatistics(
            HttpSession session,
            @RequestParam String course) {
        
        if (!checkAuthorization(session, "instructor")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        if (course == null || course.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Map<String, String> statistics = gradingDAO.getCourseStatistics(course);
        return ResponseEntity.ok(statistics);
    }
    
    @PostMapping("/api/updateGrade")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateGradeApi(
            HttpSession session,
            @RequestBody Map<String, String> gradeUpdate) {
        
        if (!checkAuthorization(session, "instructor")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String course = gradeUpdate.get("course");
        String student = gradeUpdate.get("student");
        String newGrade = gradeUpdate.get("newGrade");
        
        // Validate parameters
        if (course == null || student == null || newGrade == null ||
                course.trim().isEmpty() || student.trim().isEmpty() || newGrade.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "All fields are required");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Update grade
        boolean success = gradingDAO.updateStudentGrade(course, student, newGrade);
        
        // Return result
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        if (success) {
            response.put("message", "Grade updated successfully");
        } else {
            response.put("message", "Failed to update grade");
        }
        
        return ResponseEntity.ok(response);
    }

    private String viewCourses(HttpSession session, HttpServletRequest request, Model model, String username) {
        List<String> courses = gradingDAO.getInstructorCourses(username);
        model.addAttribute("courses", courses);
        model.addAttribute("operation", "viewCourses");
        
        return "instructor-dashboard";
    }
    
    private String viewCourseGrades(HttpSession session, HttpServletRequest request, Model model, String username, String courseName) {
        if (courseName != null && !courseName.trim().isEmpty()) {
            Map<String, String> courseGrades = gradingDAO.getCourseGrades(courseName);
            model.addAttribute("selectedCourse", courseName);
            model.addAttribute("courseGrades", courseGrades);
            model.addAttribute("operation", "viewCourseGrades");
        } else {
            List<String> instructorCourses = gradingDAO.getInstructorCourses(username);
            model.addAttribute("courses", instructorCourses);
            model.addAttribute("operation", "selectCourseForGrades");
        }
        
        return "instructor-dashboard";
    }
    
    private String editStudentGrade(HttpSession session, HttpServletRequest request, Model model, String username, String courseName, String studentName) {
        if (courseName != null && studentName != null && 
            !courseName.trim().isEmpty() && !studentName.trim().isEmpty()) {
            // Show edit form for specific student
            model.addAttribute("selectedCourse", courseName);
            model.addAttribute("selectedStudent", studentName);
            model.addAttribute("operation", "editGradeForm");
        } else if (courseName != null && !courseName.trim().isEmpty()) {
            // Show student selection for specific course
            Map<String, String> courseGrades = gradingDAO.getCourseGrades(courseName);
            model.addAttribute("selectedCourse", courseName);
            model.addAttribute("courseGrades", courseGrades);
            model.addAttribute("operation", "selectStudentForGradeEdit");
        } else {
            // Show course selection
            List<String> instructorCourses = gradingDAO.getInstructorCourses(username);
            model.addAttribute("courses", instructorCourses);
            model.addAttribute("operation", "selectCourseForGradeEdit");
        }
        
        return "instructor-dashboard";
    }
    
    private String viewCourseStatistics(HttpSession session, HttpServletRequest request, Model model, String username, String courseName) {
        if (courseName != null && !courseName.trim().isEmpty()) {
            // View statistics for specific course
            Map<String, String> statistics = gradingDAO.getCourseStatistics(courseName);
            model.addAttribute("selectedCourse", courseName);
            model.addAttribute("statistics", statistics);
            model.addAttribute("operation", "viewCourseStatistics");
        } else {
            // Show course selection
            List<String> instructorCourses = gradingDAO.getInstructorCourses(username);
            model.addAttribute("courses", instructorCourses);
            model.addAttribute("operation", "selectCourseForStatistics");
        }
        
        return "instructor-dashboard";
    }
} 