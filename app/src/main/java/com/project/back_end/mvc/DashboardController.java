package com.project.back_end.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.project.back_end.services.CentralService;

import java.util.Map;

@Controller
public class DashboardController {

    // 1. Autowired service for token validation
    @Autowired
    private CentralService centralService; // Assume this service exists later

    // 2. Admin Dashboard
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable("token") String token) {
        Map<String, Object> validationResult = centralService.validateToken(token, "admin");

        // If validation result is empty → token is valid
        if (validationResult.isEmpty()) {
            return "admin/adminDashboard"; // Thymeleaf template for admin dashboard
        } else {
            // Invalid token → redirect to login page
            return "redirect:http://localhost:8080";
        }
    }

    // 3. Doctor Dashboard
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable("token") String token) {
        Map<String, Object> validationResult = centralService.validateToken(token, "doctor");

        // If validation result is empty → token is valid
        if (validationResult.isEmpty()) {
            return "doctor/doctorDashboard"; // Thymeleaf template for doctor dashboard
        } else {
            // Invalid token → redirect to login page
            return "redirect:http://localhost:8080";
        }
    }
}
