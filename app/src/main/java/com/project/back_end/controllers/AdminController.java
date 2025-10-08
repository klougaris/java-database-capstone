package com.project.back_end.controllers;

import com.project.back_end.models.Admin;
import com.project.back_end.services.CentralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    private final CentralService centralService;

    // Constructor injection for the service
    @Autowired
    public AdminController(CentralService centralService) {
        this.centralService = service;
    }

    // Endpoint for admin login
    @PostMapping
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin admin) {
        // Delegate authentication to the Service layer
        return service.validateAdmin(admin);
    }
}
