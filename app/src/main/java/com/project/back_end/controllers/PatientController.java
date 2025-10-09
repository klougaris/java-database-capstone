package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.CentralService;
import com.project.back_end.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final CentralService centralService;
    private final TokenService tokenService;

    @Autowired
    public PatientController(PatientService patientService, CentralService centralService, TokenService tokenService) {
        this.patientService = patientService;
        this.centralService = centralService;
        this.tokenService = tokenService;
    }

    // 1. Get Patient Details
    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatient(@PathVariable String token) {
        boolean validToken = tokenService.validateToken(token, "patient");
        if (!validToken) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid or expired token"));
        }

        // Directly return the ResponseEntity from service
        return patientService.getPatientDetails(token);
    }

    // 2. Create a New Patient
    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody Patient patient) {
        int result = patientService.createPatient(patient);

        switch (result) {
            case 1:
                return ResponseEntity.ok(Map.of("message", "Signup successful"));
            case -1:
                return ResponseEntity.status(409).body(Map.of("message", "Patient with email id or phone no already exist"));
            default:
                return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    // 3. Patient Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
        return centralService.validatePatientLogin(login);
    }

    // 4. Get Patient Appointments
    @GetMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointments(
            @PathVariable Long id,
            @PathVariable String token
    ) {
        boolean validToken = tokenService.validateToken(token, "patient");
        if (!validToken) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid or expired token"));
        }

        // Directly return ResponseEntity from service
        return patientService.getPatientAppointment(id, token);
    }

    // 5. Filter Patient Appointments
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointments(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token
    ) {
        boolean validToken = tokenService.validateToken(token, "patient");
        if (!validToken) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid or expired token"));
        }

        // Logic: depending on parameters, call the right filter from PatientService
        String conditionLower = condition.toLowerCase();
        if (name.equalsIgnoreCase("none")) {
            return patientService.filterByCondition(conditionLower, tokenService.getPatientIdFromToken(token));
        } else if (condition.equalsIgnoreCase("none")) {
            return patientService.filterByDoctor(name, tokenService.getPatientIdFromToken(token));
        } else {
            return patientService.filterByDoctorAndCondition(conditionLower, name, tokenService.getPatientIdFromToken(token));
        }
    }
}
