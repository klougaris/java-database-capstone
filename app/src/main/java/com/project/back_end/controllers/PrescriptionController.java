package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final TokenService tokenService;

    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService, TokenService tokenService) {
        this.prescriptionService = prescriptionService;
        this.tokenService = tokenService;
    }

    // 1. Save Prescription
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @PathVariable String token,
            @RequestBody Prescription prescription
    ) {
        boolean validToken = tokenService.validateToken(token, "doctor");
        if (!validToken) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid or expired token"));
        }

        // PrescriptionService already returns ResponseEntity<Map<String, String>>
        return prescriptionService.savePrescription(prescription);
    }

    // 2. Get Prescription by Appointment ID
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token
    ) {
        boolean validToken = tokenService.validateToken(token, "doctor");
        if (!validToken) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid or expired token"));
        }

        // PrescriptionService already returns ResponseEntity<Map<String, Object>>
        return prescriptionService.getPrescription(appointmentId);
    }
}
