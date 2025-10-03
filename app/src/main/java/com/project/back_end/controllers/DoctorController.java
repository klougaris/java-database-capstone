package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import com.project.back_end.DTO.Login;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private DoctorService doctorService;
    private Service service;

    // Constructor injection for dependencies
    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

  
@GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
public ResponseEntity<Map<String, Object>> getDoctorAvailability(
        @PathVariable String user,
        @PathVariable Long doctorId,
        @PathVariable String date,
        @PathVariable String token) {

    Map<String, Object> response = new HashMap<>();

    // 1. Validate token for the given user type
    boolean isTokenValid = service.validateToken(token, user);
    if (!isTokenValid) {
        response.put("error", "Invalid or expired token");
        return ResponseEntity.status(401).body(response); // Unauthorized
    }

    try {
        // 2. Fetch doctor's availability from DoctorService
        List<LocalDateTime> availableSlots = doctorService.getDoctorAvailability(doctorId, date);

        response.put("doctorId", doctorId);
        response.put("date", date);
        response.put("availableSlots", availableSlots);
        response.put("count", availableSlots.size());

        return ResponseEntity.ok(response); // 200 OK
    } catch (Exception e) {
        System.out.println("Error fetching doctor's availability: " + e.getMessage());
        e.printStackTrace();
        response.put("error", "Internal server error");
        return ResponseEntity.status(500).body(response); // Internal Server Error
    }
}

@GetMapping
public ResponseEntity<Map<String, Object>> getDoctors() {
    Map<String, Object> response = new HashMap<>();
    try {
        // Fetch all doctors from the service
        var doctors = doctorService.getDoctors();

        response.put("doctors", doctors);
        response.put("count", doctors.size());

        return ResponseEntity.ok(response); // 200 OK
    } catch (Exception e) {
        System.out.println("Error fetching doctors: " + e.getMessage());
        e.printStackTrace();
        response.put("error", "Internal server error");
        return ResponseEntity.status(500).body(response); // Internal Server Error
    }
}

@PostMapping("/{token}")
public ResponseEntity<Map<String, String>> saveDoctor(@RequestBody Doctor doctor,
                                                      @PathVariable String token) {
    Map<String, String> response = new HashMap<>();

    // 1. Validate token for admin
    boolean isTokenValid = service.validateToken(token, "admin");
    if (!isTokenValid) {
        response.put("error", "Invalid or expired token");
        return ResponseEntity.status(401).body(response); // Unauthorized
    }

    // 2. Attempt to save doctor
    try {
        int result = doctorService.saveDoctor(doctor);

        switch (result) {
            case -1:
                response.put("error", "Doctor already exists");
                return ResponseEntity.status(409).body(response); // Conflict
            case 1:
                response.put("message", "Doctor added to db");
                return ResponseEntity.status(201).body(response); // Created
            default:
                response.put("error", "Some internal error occurred");
                return ResponseEntity.status(500).body(response); // Internal Server Error
        }
    } catch (Exception e) {
        System.out.println("Error saving doctor: " + e.getMessage());
        e.printStackTrace();
        response.put("error", "Some internal error occurred");
        return ResponseEntity.status(500).body(response); // Internal Server Error
    }
}

@PostMapping("/login")
public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
    try {
        // Delegate login validation to doctorService
        return doctorService.validateDoctor(login);
    } catch (Exception e) {
        System.out.println("Error during doctor login: " + e.getMessage());
        e.printStackTrace();
        Map<String, String> response = new HashMap<>();
        response.put("error", "Internal server error");
        return ResponseEntity.status(500).body(response); // Internal Server Error
    }
}


@PutMapping("/{token}")
public ResponseEntity<Map<String, String>> updateDoctor(@RequestBody Doctor doctor,
                                                        @PathVariable String token) {
    Map<String, String> response = new HashMap<>();

    try {
        // 1. Validate token for admin
        boolean isTokenValid = service.validateToken(token, "admin");
        if (!isTokenValid) {
            response.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(response); // Unauthorized
        }

        // 2. Attempt to update doctor
        boolean updated = doctorService.updateDoctor(doctor);
        if (updated) {
            response.put("message", "Doctor updated");
            return ResponseEntity.ok(response); // 200 OK
        } else {
            response.put("error", "Doctor not found");
            return ResponseEntity.status(404).body(response); // Not Found
        }

    } catch (Exception e) {
        System.out.println("Error updating doctor: " + e.getMessage());
        e.printStackTrace();
        response.put("error", "Some internal error occurred");
        return ResponseEntity.status(500).body(response); // Internal Server Error
    }
}


@DeleteMapping("/{id}/{token}")
public ResponseEntity<Map<String, String>> deleteDoctor(@PathVariable Long id,
                                                        @PathVariable String token) {
    Map<String, String> response = new HashMap<>();

    try {
        // 1. Validate token for admin
        boolean isTokenValid = service.validateToken(token, "admin");
        if (!isTokenValid) {
            response.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(response); // Unauthorized
        }

        // 2. Attempt to delete doctor
        boolean deleted = doctorService.deleteDoctor(id);
        if (deleted) {
            response.put("message", "Doctor deleted successfully");
            return ResponseEntity.ok(response); // 200 OK
        } else {
            response.put("error", "Doctor not found with id " + id);
            return ResponseEntity.status(404).body(response); // Not Found
        }

    } catch (Exception e) {
        System.out.println("Error deleting doctor: " + e.getMessage());
        e.printStackTrace();
        response.put("error", "Some internal error occurred");
        return ResponseEntity.status(500).body(response); // Internal Server Error
    }
}


@GetMapping("/filter/{name}/{time}/{speciality}")
public ResponseEntity<Map<String, Object>> filter(@PathVariable String name,
                                                  @PathVariable String time,
                                                  @PathVariable String speciality) {
    try {
        // Use Service to filter doctors
        Map<String, Object> filteredDoctors = service.filterDoctor(name, speciality, time);
        return ResponseEntity.ok(filteredDoctors); // 200 OK
    } catch (Exception e) {
        System.out.println("Error filtering doctors: " + e.getMessage());
        e.printStackTrace();
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Internal server error");
        response.put("doctors", Collections.emptyList());
        response.put("count", 0);
        return ResponseEntity.status(500).body(response); // Internal Server Error
    }
}


}
