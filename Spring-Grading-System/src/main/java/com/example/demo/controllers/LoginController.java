package com.example.demo.controllers;

import com.example.demo.data.GradingSystemDAO;
import com.example.demo.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {
    private final GradingSystemDAO gradingDAO;

    @Autowired
    public LoginController(GradingSystemDAO gradingDAO) {
        this.gradingDAO = gradingDAO;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, 
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        
        if (gradingDAO.validateUser(username, password)) {
            // Set 30 minutes session timeout
            session.setMaxInactiveInterval(30 * 60);

            User user = gradingDAO.getUser(username, password);
            
            session.setAttribute("username", username);
            session.setAttribute("userRole", user.getRole().toString());

            // Redirect to dashboard
            return "redirect:/dashboard";
        } else {
            model.addAttribute("errorMessage", "Invalid username or password");
            return "login";
        }
    }
    
    // API endpoint for login
    @PostMapping("/login/api")
    public ResponseEntity<Map<String, Object>> loginApi(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        Map<String, Object> response = new HashMap<>();
        
        if (gradingDAO.validateUser(username, password)) {
            User user = gradingDAO.getUser(username, password);
            
            response.put("success", true);
            response.put("username", username);
            response.put("role", user.getRole().toString());
            
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Invalid username or password");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}