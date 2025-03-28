package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    @GetMapping
    public String dispatch(
            HttpSession session,
            @RequestParam(required = false) String operation) {
        
        // Check if logged in
        if (session == null || session.getAttribute("username") == null) {
            return "redirect:/login";
        }
        
        String userRole = (String) session.getAttribute("userRole");
        
        // Build the appropriate redirect URL based on role and operation
        return buildRedirectUrl(userRole, operation);
    }
    
    @PostMapping
    public String handlePost(
            HttpSession session,
            @RequestParam(required = false) String operation) {
        // Forward to GET handler
        return dispatch(session, operation);
    }
    
    /**
     * Builds the appropriate redirect URL based on user role and requested operation
     */
    private String buildRedirectUrl(String userRole, String operation) {
        // Default URLs for each role
        if (operation == null) {
            if ("STUDENT".equalsIgnoreCase(userRole)) {
                return "redirect:/student";
            } else if ("INSTRUCTOR".equalsIgnoreCase(userRole)) {
                return "redirect:/instructor";
            } else if ("ADMIN".equalsIgnoreCase(userRole)) {
                return "redirect:/admin";
            } else {
                return "redirect:/login";
            }
        }
        
        // Handle operations for each role
        if ("STUDENT".equalsIgnoreCase(userRole)) {
            switch (operation) {
                case "viewCourses":
                    return "redirect:/student/courses";
                case "viewGrades":
                    return "redirect:/student/grades";
                default:
                    return "redirect:/student";
            }
        } else if ("INSTRUCTOR".equalsIgnoreCase(userRole)) {
            switch (operation) {
                case "viewCourses":
                    return "redirect:/instructor/courses";
                case "viewCourseGrades":
                    return "redirect:/instructor/courseGrades";
                case "editStudentGrade":
                    return "redirect:/instructor/editGrade";
                default:
                    return "redirect:/instructor";
            }
        } else if ("ADMIN".equalsIgnoreCase(userRole)) {
            switch (operation) {
                case "viewAllUsers":
                    return "redirect:/admin/users";
                case "viewAllCourses":
                    return "redirect:/admin/courses";
                case "addStudent":
                    return "redirect:/admin/addStudent";
                case "addInstructor":
                    return "redirect:/admin/addInstructor";
                case "addCourse":
                    return "redirect:/admin/addCourse";
                case "deleteStudent":
                    return "redirect:/admin/deleteStudent";
                case "deleteInstructor":
                    return "redirect:/admin/deleteInstructor";
                default:
                    return "redirect:/admin";
            }
        } else {
            // Unknown role, redirect to login page
            return "redirect:/login";
        }
    }
} 