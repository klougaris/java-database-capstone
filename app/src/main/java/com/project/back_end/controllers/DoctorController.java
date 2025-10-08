package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import com.project.back_end.DTO.Login;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.CentralService;
import com.project.back_end.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final CentralService centralService;
    private final TokenService tokenService;

    @Autowired
    public DoctorController(DoctorService doctorService, CentralService centralService, TokenService tokenService) {
        this.doctorService = doctorService;
        this.centralService = centralService;
	this.tokenService = tokenService
    }

    // 1. Get doctor availability
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token
    ) {
        boolean validToken = tokenService.validateToken(token, user);
        if (!validToken) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid or expired token"));
        }

        LocalDate localDate = LocalDate.parse(date);
        List<String> availability = doctorService.getDoctorAvailability(doctorId, localDate);

        return ResponseEntity.ok(Map.of("availability", availability));
    }

    // 2. Get all doctors
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors() {
        List<Doctor> doctors = doctorService.getDoctors();
        return ResponseEntity.ok(Map.of("doctors", doctors));
    }

    // 3. Add new doctor
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> saveDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token
    ) {
        boolean validToken = tokenService.validateToken(token, "admin");
        if (!validToken) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        int result = doctorService.saveDoctor(doctor);
        switch (result) {
            case 1:
                return ResponseEntity.ok(Map.of("message", "Doctor added to db"));
            case -1:
                return ResponseEntity.status(409).body(Map.of("message", "Doctor already exists"));
            default:
                return ResponseEntity.status(500).body(Map.of("message", "Some internal error occurred"));
        }
    }

    // 4. Doctor login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    // 5. Update doctor details
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token
    ) {
        boolean validToken = tokenService.validateToken(token, "admin");
        if (!validToken) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        int result = doctorService.updateDoctor(doctor);
        switch (result) {
            case 1:
                return ResponseEntity.ok(Map.of("message", "Doctor updated"));
            case -1:
                return ResponseEntity.status(404).body(Map.of("message", "Doctor not found"));
            default:
                return ResponseEntity.status(500).body(Map.of("message", "Some internal error occurred"));
        }
    }

    // 6. Delete doctor
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable Long id,
            @PathVariable String token
    ) {
        boolean validToken = tokenService.validateToken(token, "admin");
        if (!validToken) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        int result = doctorService.deleteDoctor(id);
        switch (result) {
            case 1:
                return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully"));
            case -1:
                return ResponseEntity.status(404).body(Map.of("message", "Doctor not found with id"));
            default:
                return ResponseEntity.status(500).body(Map.of("message", "Some internal error occurred"));
        }
    }

    // 7. Filter doctors
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filterDoctors(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality
    ) {
        Map<String, Object> filteredDoctors = centralService.filterDoctor(name, speciality, time);
        return ResponseEntity.ok(filteredDoctors);
    }
}
