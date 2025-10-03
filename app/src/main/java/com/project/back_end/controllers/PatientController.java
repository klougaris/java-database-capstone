package com.project.back_end.controllers;

import com.project.back_end.models.Patient;
import com.project.back_end.DTO.Login;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    // Constructor injection
    public PatientController(PatientService patientService, Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    // 3. Get patient details
    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatient(@PathVariable String token) {
        Map<String, Object> response = new HashMap<>();

        boolean isTokenValid = service.validateToken(token, "patient");
        if (!isTokenValid) {
            response.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(response);
        }

        try {
            String email = service.extractIdentifier(token);
            Patient patient = patientService.getPatientByEmail(email);

            if (patient != null) {
                response.put("patient", patient);
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Patient not found");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            System.out.println("Error fetching patient: " + e.getMessage());
            e.printStackTrace();
            response.put("error", "Internal server error");
            return ResponseEntity.status(500).body(response);
        }
    }

    // 4. Create patient
    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody Patient patient) {
        Map<String, String> response = new HashMap<>();

        try {
            boolean isValid = service.validatePatient(patient);
            if (!isValid) {
                response.put("error", "Patient already exists");
                return ResponseEntity.status(409).body(response); // Conflict
            }

            patientService.createPatient(patient);
            response.put("message", "Patient created successfully");
            return ResponseEntity.status(201).body(response); // Created
        } catch (Exception e) {
            System.out.println("Error creating patient: " + e.getMessage());
            e.printStackTrace();
            response.put("error", "Internal server error");
            return ResponseEntity.status(500).body(response);
        }
    }

    // 5. Patient login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
        try {
            return service.validatePatientLogin(login);
        } catch (Exception e) {
            System.out.println("Error during patient login: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("error", "Internal server error");
            return ResponseEntity.status(500).body(response);
        }
    }

    // 6. Get patient appointments
    @GetMapping("/appointments/{patientId}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointment(@PathVariable Long patientId,
                                                                     @PathVariable String token) {
        Map<String, Object> response = new HashMap<>();

        boolean isTokenValid = service.validateToken(token, "patient");
        if (!isTokenValid) {
            response.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(response);
        }

        try {
            List<Map<String, Object>> appointments = patientService.getPatientAppointment(patientId, token);
            response.put("appointments", appointments);
            response.put("count", appointments.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error fetching appointments: " + e.getMessage());
            e.printStackTrace();
            response.put("error", "Internal server error");
            return ResponseEntity.status(500).body(response);
        }
    }

    // 7. Filter patient appointments
    @GetMapping("/appointments/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointment(@PathVariable String condition,
                                                                        @PathVariable String name,
                                                                        @PathVariable String token) {
        Map<String, Object> response = new HashMap<>();

        boolean isTokenValid = service.validateToken(token, "patient");
        if (!isTokenValid) {
            response.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(response);
        }

        try {
            ResponseEntity<Map<String, Object>> filteredResponse =
                    service.filterPatient(condition, name, token);
            return filteredResponse;
        } catch (Exception e) {
            System.out.println("Error filtering appointments: " + e.getMessage());
            e.printStackTrace();
            response.put("error", "Internal server error");
            return ResponseEntity.status(500).body(response);
        }
    }

}
