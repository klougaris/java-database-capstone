package com.project.back_end.controllers;

import com.project.back_end.entities.Prescription;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service service;

    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService, Service service) {
        this.prescriptionService = prescriptionService;
        this.service = service;
    }

    // 1. Save Prescription
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @PathVariable String token,
            @RequestBody Prescription prescription
    ) {
        boolean validToken = service.validateToken(token, "doctor");
        if (!validToken) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid or expired token"));
        }

        try {
            int result = prescriptionService.savePrescription(prescription);
            if (result == 1) {
                return ResponseEntity.ok(Map.of("message", "Prescription saved successfully"));
            } else {
                return ResponseEntity.status(500).body(Map.of("message", "Failed to save prescription"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "An unexpected error occurred"));
        }
    }

    // 2. Get Prescription by Appointment ID
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token
    ) {
        boolean validToken = service.validateToken(token, "doctor");
        if (!validToken) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid or expired token"));
        }

        Map<String, Object> prescription = prescriptionService.getPrescription(appointmentId);
        if (prescription == null || prescription.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "No prescription found for this appointment"));
        }

        return ResponseEntity.ok(prescription);
    }
}
