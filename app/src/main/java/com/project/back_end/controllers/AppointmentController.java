package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.TokenService;
import com.project.back_end.services.CentralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final TokenService tokenService;
    private final CentralService centralService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, TokenService tokenService, CentralService centralService) {
        this.appointmentService = appointmentService;
        this.tokenService = tokenService;
        this.centralService = centralService;
    }

    // GET: Fetch appointments by date and optional patient name for doctors
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @PathVariable String date,
            @PathVariable String patientName,
            @PathVariable String token
    ) {
        // Validate token for doctor
        boolean validToken = tokenService.validateToken(token, "doctor");
        if (!validToken) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid or expired token"));
        }

        LocalDate appointmentDate = LocalDate.parse(date);
        Map<String, Object> appointments = appointmentService.getAppointment(patientName, appointmentDate, token);
        return ResponseEntity.ok(appointments);
    }

    // POST: Book a new appointment
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token
    ) {
        // Validate token for patient
        boolean validToken = tokenService.validateToken(token, "patient");
        if (!validToken) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid or expired token"));
        }

        int valid = centralService.validateAppointment(appointment);
        if (valid == -1) {
            return ResponseEntity.badRequest().body(Map.of("message", "Doctor does not exist"));
        } else if (valid == 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "Appointment time not available"));
        }

        int result = appointmentService.bookAppointment(appointment);
        if (result == 1) {
            return ResponseEntity.status(201).body(Map.of("message", "Appointment booked successfully"));
        } else {
            return ResponseEntity.status(500).body(Map.of("message", "Error booking appointment"));
        }
    }

    // PUT: Update an existing appointment
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token
    ) {
        // Validate token for patient
        boolean validToken = tokenService.validateToken(token, "patient");
        if (!validToken) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid or expired token"));
        }

        return appointmentService.updateAppointment(appointment);
    }

    // DELETE: Cancel an appointment
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable Long id,
            @PathVariable String token
    ) {
        // Validate token for patient
        boolean validToken = tokenService.validateToken(token, "patient");
        if (!validToken) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid or expired token"));
        }

        return appointmentService.cancelAppointment(id, token);
    }
}
