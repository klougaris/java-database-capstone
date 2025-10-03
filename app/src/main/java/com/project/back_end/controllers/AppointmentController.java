package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {


    private AppointmentService appointmentService;
    private Service service;

    // Constructor injection
    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }


@GetMapping("/{date}/{patientName}/{token}")
public ResponseEntity<?> getAppointments(@PathVariable String date,
                                         @PathVariable String patientName,
                                         @PathVariable String token) {
    // 1. Validate the token for doctor
    boolean isTokenValid = service.validateToken(token, "doctor");
    if (!isTokenValid) {
        return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
    }

    // 2. Fetch appointments using AppointmentService
    try {
        List<Map<String, Object>> appointments = appointmentService.getAppointment(date, patientName);
        return ResponseEntity.ok(Map.of(
            "appointments", appointments,
            "count", appointments.size()
        ));
    } catch (Exception e) {
        System.out.println("Error fetching appointments: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
}


@PostMapping("/{token}")
public ResponseEntity<Map<String, String>> bookAppointment(@RequestBody Appointment appointment,
                                                           @PathVariable String token) {
    Map<String, String> response = new HashMap<>();

    // 1. Validate token for patient
    boolean isTokenValid = service.validateToken(token, "patient");
    if (!isTokenValid) {
        response.put("error", "Invalid or expired token");
        return ResponseEntity.status(401).body(response);
    }

    // 2. Validate appointment
    int validationStatus = service.validateAppointment(appointment);
    switch (validationStatus) {
        case -1:
            response.put("error", "Doctor does not exist");
            return ResponseEntity.status(404).body(response);
        case 0:
            response.put("error", "Requested appointment time is unavailable");
            return ResponseEntity.status(409).body(response); // Conflict
        case 1:
            break; // Valid appointment
        default:
            response.put("error", "Unknown validation error");
            return ResponseEntity.status(500).body(response);
    }

    // 3. Book the appointment
    try {
        appointmentService.bookAppointment(appointment);
        response.put("message", "Appointment booked successfully");
        return ResponseEntity.status(201).body(response); // Created
    } catch (Exception e) {
        System.out.println("Error booking appointment: " + e.getMessage());
        e.printStackTrace();
        response.put("error", "Internal server error");
        return ResponseEntity.status(500).body(response);
    }
}

@PutMapping("/{token}")
public ResponseEntity<Map<String, String>> updateAppointment(@RequestBody Appointment appointment,
                                                             @PathVariable String token) {
    Map<String, String> response = new HashMap<>();

    // 1. Validate token for patient
    boolean isTokenValid = service.validateToken(token, "patient");
    if (!isTokenValid) {
        response.put("error", "Invalid or expired token");
        return ResponseEntity.status(401).body(response);
    }

    // 2. Update the appointment
    try {
        boolean updated = appointmentService.updateAppointment(appointment);
        if (updated) {
            response.put("message", "Appointment updated successfully");
            return ResponseEntity.ok(response); // 200 OK
        } else {
            response.put("error", "Failed to update appointment");
            return ResponseEntity.status(400).body(response); // Bad Request
        }
    } catch (Exception e) {
        System.out.println("Error updating appointment: " + e.getMessage());
        e.printStackTrace();
        response.put("error", "Internal server error");
        return ResponseEntity.status(500).body(response); // Internal Server Error
    }
}


@DeleteMapping("/{id}/{token}")
public ResponseEntity<Map<String, String>> cancelAppointment(@PathVariable Long id,
                                                             @PathVariable String token) {
    Map<String, String> response = new HashMap<>();

    // 1. Validate token for patient
    boolean isTokenValid = service.validateToken(token, "patient");
    if (!isTokenValid) {
        response.put("error", "Invalid or expired token");
        return ResponseEntity.status(401).body(response); // Unauthorized
    }

    // 2. Cancel the appointment
    try {
        boolean canceled = appointmentService.cancelAppointment(id);
        if (canceled) {
            response.put("message", "Appointment canceled successfully");
            return ResponseEntity.ok(response); // 200 OK
        } else {
            response.put("error", "Failed to cancel appointment");
            return ResponseEntity.status(400).body(response); // Bad Request
        }
    } catch (Exception e) {
        System.out.println("Error canceling appointment: " + e.getMessage());
        e.printStackTrace();
        response.put("error", "Internal server error");
        return ResponseEntity.status(500).body(response); // Internal Server Error
    }
}



}
