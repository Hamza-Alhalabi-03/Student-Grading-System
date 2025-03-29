package com.example.demo.controllers;

import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {
    
    private final ErrorAttributes errorAttributes;
    
    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }
    
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        WebRequest webRequest = new ServletWebRequest(request);
        Map<String, Object> errorMap = this.errorAttributes.getErrorAttributes(
                webRequest, 
                ErrorAttributeOptions.defaults());
        
        HttpStatus status = HttpStatus.valueOf((Integer) errorMap.get("status"));
        String message = (String) errorMap.get("message");
        String error = (String) errorMap.get("error");
        
        model.addAttribute("status", status.value());
        model.addAttribute("message", (message != null && !message.isEmpty()) ? message : error);
        
        return "error";
    }
} 