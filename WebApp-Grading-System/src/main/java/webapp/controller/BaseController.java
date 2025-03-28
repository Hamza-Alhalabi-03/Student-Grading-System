package webapp.controller;

import webapp.data.GradingSystemDAO;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public abstract class BaseController extends HttpServlet {
    protected GradingSystemDAO gradingDAO;
    
    public void init() {
        gradingDAO = new GradingSystemDAO();
    }
    
    /**
     * Verifies if the user is logged in and has the required role
     * @param request The HTTP request
     * @param response The HTTP response
     * @param requiredRole The role required to access this controller
     * @return True if authenticated and authorized, false otherwise
     * @throws IOException If redirection fails
     */
    protected boolean checkAuthorization(HttpServletRequest request, HttpServletResponse response, String requiredRole) 
            throws IOException {
        HttpSession session = request.getSession(false);
        
        // Check if logged in
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        
        // Check if user has the required role
        String userRole = (String) session.getAttribute("userRole");
        if (!requiredRole.equalsIgnoreCase(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return false;
        }
        
        return true;
    }
    
    /**
     * Gets the username from the session
     * @param request The HTTP request
     * @return The username or null if not found
     */
    protected String getUsername(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (String) session.getAttribute("username");
        }
        return null;
    }
    
    /**
     * Processes session flash messages, transferring them to request attributes and then clearing them
     * @param request The HTTP request
     */
    protected void processSessionMessages(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            // Process success message
            if (session.getAttribute("message") != null) {
                request.setAttribute("message", session.getAttribute("message"));
                session.removeAttribute("message");
            }
            
            // Process error message
            if (session.getAttribute("errorMessage") != null) {
                request.setAttribute("errorMessage", session.getAttribute("errorMessage"));
                session.removeAttribute("errorMessage");
            }
        }
    }
} 