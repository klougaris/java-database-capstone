package com.project.back_end.controllers;

import com.project.back_end.entities.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private PrescriptionService prescriptionService;
    private Service service;
    private AppointmentService appointmentService;

    public PrescriptionController(PrescriptionService prescriptionService,
                                 Service service, AppointmentService appointmentService) {

        this.prescriptionService = prescriptionService;
        this.service = service;
        this.appointmentService = appointmentService;
     
    }


    @PostMapping("/{token}")
public ResponseEntity<Map<String, String>> savePrescription(@RequestBody Prescription prescription,
                                                            @PathVariable String token) {

    Map<String, String> response = new HashMap<>();

    // 1. Validate token for Doctor
    boolean isTokenValid = service.validateToken(token, "doctor");
    if (!isTokenValid) {
        response.put("error", "Invalid or expired token");
        return ResponseEntity.status(401).body(response);
    }

    // 2. Attempt to save prescription
    try {
        int result = prescriptionService.savePrescription(prescription);

        switch (result) {
            case -1: 
                response.put("error", "Prescription already exists");
                return ResponseEntity.status(409).body(response); // Conflict

            case 1: 
                response.put("message", "Prescription added to DB");
                return ResponseEntity.status(201).body(response); // Created

            default:
                response.put("error", "Some internal error occurred");
                return ResponseEntity.status(500).body(response); // Internal Server Error
        }

    } catch (Exception e) {
        System.out.println("Error saving prescription: " + e.getMessage());
        e.printStackTrace();
        response.put("error", "Some internal error occurred");
        return ResponseEntity.status(500).body(response); // Internal Server Error
    }
}



@GetMapping("/{appointmentId}/{token}")
public ResponseEntity<Map<String, Object>> getPrescription(@PathVariable Long appointmentId,
                                                           @PathVariable String token) {

    Map<String, Object> response = new HashMap<>();

    // 1. Validate token for Doctor
    boolean isTokenValid = service.validateToken(token, "doctor");
    if (!isTokenValid) {
        response.put("error", "Invalid or expired token");
        return ResponseEntity.status(401).body(response); // Unauthorized
    }

    // 2. Attempt to fetch prescription
    try {
        ResponseEntity<Map<String, Object>> prescriptionResponse =
                prescriptionService.getPrescription(appointmentId);

        return prescriptionResponse;

    } catch (Exception e) {
        System.out.println("Error fetching prescription: " + e.getMessage());
        e.printStackTrace();
        response.put("error", "Internal server error");
        return ResponseEntity.status(500).body(response); // Internal Server Error
    }
}



}
