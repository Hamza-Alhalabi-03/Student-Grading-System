package com.example.demo.controllers;

import com.example.demo.data.GradingSystemDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public abstract class BaseController {
    
    @Autowired
    protected GradingSystemDAO gradingDAO;
    
    /**
     * Verifies if the user is logged in and has the required role
     * @param session The HTTP session
     * @param requiredRole The role required to access this controller
     * @return True if authenticated and authorized, false otherwise
     */
    protected boolean checkAuthorization(HttpSession session, String requiredRole) {
        // Check if logged in
        if (session == null || session.getAttribute("username") == null) {
            return false;
        }
        
        // Check if user has the required role
        String userRole = (String) session.getAttribute("userRole");
        return requiredRole.equalsIgnoreCase(userRole);
    }
    
    /**
     * Gets the username from the session
     * @param session The HTTP session
     * @return The username or null if not found
     */
    protected String getUsername(HttpSession session) {
        if (session != null) {
            return (String) session.getAttribute("username");
        }
        return null;
    }
    
    /**
     * Processes session flash messages, transferring them to model attributes
     * @param request The HTTP request
     * @param model The Spring model
     */
    protected void processSessionMessages(HttpServletRequest request, org.springframework.ui.Model model) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            // Process success message
            if (session.getAttribute("message") != null) {
                model.addAttribute("message", session.getAttribute("message"));
                session.removeAttribute("message");
            }
            
            // Process error message
            if (session.getAttribute("errorMessage") != null) {
                model.addAttribute("errorMessage", session.getAttribute("errorMessage"));
                session.removeAttribute("errorMessage");
            }
        }
    }
    
    /**
     * Sets status to Forbidden for unauthorized access
     */
    protected void setForbiddenStatus(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
    }
} 