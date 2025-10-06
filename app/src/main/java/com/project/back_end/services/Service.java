package com.project.back_end.services;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.repo.PrescriptionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

@Service
public class CentralService {

private final TokenService tokenService;
private final AdminRepository adminRepository;
private final DoctorRepository doctorRepository;
private final PatientRepository patientRepository;
private final DoctorService doctorService;
private final PatientService patientService;


  public CentralService(TokenService tokenService,  
                   AdminRepository adminRepository,
                   PrescriptionRepository prescriptionRepository,
                   AppointmentRepository appointmentRepository,
                   PatientRepository patientRepository,
                   DoctorRepository doctorRepository) {

        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

@Transactional
public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
    Map<String, String> response = new HashMap<>();
    try {
        // Use the TokenService to validate the token
        boolean isValid = tokenService.validateToken(token, user);

        if (!isValid) {
            response.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(response); // Unauthorized
        }

        // Token is valid, return success message
        response.put("message", "Token is valid");
        return ResponseEntity.ok(response);

    } catch (Exception e) {
        System.out.println("Error validating token: " + e.getMessage());
        e.printStackTrace();
        response.put("error", "Internal server error");
        return ResponseEntity.status(500).body(response); // Internal Server Error
    }
}

@Transactional
public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
    Map<String, String> response = new HashMap<>();
    try {
        // 1. Search admin by username
        Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());

        if (admin == null) {
            response.put("error", "Admin not found");
            return ResponseEntity.status(401).body(response); // Unauthorized
        }

        // 2. Check password
        if (!admin.getPassword().equals(receivedAdmin.getPassword())) {
            response.put("error", "Invalid password");
            return ResponseEntity.status(401).body(response); // Unauthorized
        }

        // 3. Generate token
        String token = tokenService.generateToken(admin.getUsername());
        response.put("token", token);

        return ResponseEntity.ok(response); // 200 OK

    } catch (Exception e) {
        System.out.println("Error validating admin: " + e.getMessage());
        e.printStackTrace();
        response.put("error", "Internal server error");
        return ResponseEntity.status(500).body(response); // Internal Server Error
    }
}


@Transactional
public Map<String, Object> filterDoctor(String name, String specialty, String time) {
    Map<String, Object> response = new HashMap<>();
    try {
        // Normalize input
        String doctorName = (name != null) ? name.trim() : "";
        String doctorSpecialty = (specialty != null) ? specialty.trim() : "";
        String availableTime = (time != null) ? time.trim() : "";

        // Determine which filter method to use
        if (!doctorName.isEmpty() && !doctorSpecialty.isEmpty() && !availableTime.isEmpty()) {
            response = doctorService.filterDoctorsByNameSpecilityandTime(doctorName, doctorSpecialty, availableTime);
        } else if (!doctorName.isEmpty() && !doctorSpecialty.isEmpty()) {
            response = doctorService.filterDoctorByNameAndSpecility(doctorName, doctorSpecialty);
        } else if (!doctorName.isEmpty() && !availableTime.isEmpty()) {
            response = doctorService.filterDoctorByNameAndTime(doctorName, availableTime);
        } else if (!doctorSpecialty.isEmpty() && !availableTime.isEmpty()) {
            response = doctorService.filterDoctorByTimeAndSpecility(doctorSpecialty, availableTime);
        } else if (!doctorName.isEmpty()) {
            response = doctorService.findDoctorByName(doctorName);
        } else if (!doctorSpecialty.isEmpty()) {
            response = doctorService.filterDoctorBySpecility(doctorSpecialty);
        } else if (!availableTime.isEmpty()) {
            response = doctorService.filterDoctorsByTime(availableTime);
        } else {
            response.put("count", doctorService.getDoctors().size());
            response.put("doctors", doctorService.getDoctors());
        }

        return response;

    } catch (Exception e) {
        System.out.println("Error filtering doctors: " + e.getMessage());
        e.printStackTrace();
        response.put("count", 0);
        response.put("doctors", Collections.emptyList());
        response.put("error", "Internal server error");
        return response;
    }
}

@Transactional
public int validateAppointment(Appointment appointment) {
    try {
        // 1. Check if doctor exists
        Long doctorId = appointment.getDoctor().getId();
        var doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null) {
            System.out.println("Doctor with ID " + doctorId + " does not exist.");
            return -1;
        }

        // 2. Get available time slots for the doctor
        List<LocalDateTime> availableSlots = doctorService.getDoctorAvailability(doctorId, appointment.getAppointmentDate());

        // 3. Check if requested appointment time matches an available slot
        LocalDateTime requestedTime = appointment.getAppointmentTime();
        if (availableSlots.contains(requestedTime)) {
            return 1; // Valid appointment time
        } else {
            System.out.println("Requested appointment time " + requestedTime + " is unavailable.");
            return 0; // Time unavailable
        }

    } catch (Exception e) {
        System.out.println("Error validating appointment: " + e.getMessage());
        e.printStackTrace();
        return 0; // Treat exception as unavailable
    }
}


@Transactional
public boolean validatePatient(Patient patient) {
    try {
        // Check if a patient exists with the same email or phone
        Patient existingPatient = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());

        if (existingPatient != null) {
            System.out.println("Patient already exists with email " + patient.getEmail() + " or phone " + patient.getPhone());
            return false; // Patient exists, not valid
        } else {
            return true; // Patient does not exist, valid
        }

    } catch (Exception e) {
        System.out.println("Error validating patient: " + e.getMessage());
        e.printStackTrace();
        return false; // Treat exception as invalid to prevent duplicates
    }
}


@Transactional
public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
    Map<String, String> response = new HashMap<>();
    try {
        // 1. Look up patient by email
        Patient patient = patientRepository.findByEmail(login.getEmail());
        if (patient == null) {
            response.put("error", "Invalid email or password");
            return ResponseEntity.status(401).body(response);
        }

        // 2. Compare passwords
        if (!patient.getPassword().equals(login.getPassword())) {
            response.put("error", "Invalid email or password");
            return ResponseEntity.status(401).body(response);
        }

        // 3. Generate JWT token
        String token = tokenService.generateToken(patient.getEmail());
        response.put("token", token);
        return ResponseEntity.ok(response);

    } catch (Exception e) {
        System.out.println("Error validating patient login: " + e.getMessage());
        e.printStackTrace();
        response.put("error", "Internal server error");
        return ResponseEntity.status(500).body(response);
    }
}


@Transactional
public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
    Map<String, Object> response = new HashMap<>();
    try {
        // 1. Extract email from token
        String email = tokenService.extractEmail(token);
        if (email == null || email.isEmpty()) {
            response.put("error", "Invalid token or email not found");
            return ResponseEntity.status(401).body(response);
        }

        // 2. Get patient by email
        Patient patient = patientRepository.findByEmail(email);
        if (patient == null) {
            response.put("error", "Patient not found");
            return ResponseEntity.status(404).body(response);
        }

        Long patientId = patient.getId();
        ResponseEntity<Map<String, Object>> filteredResponse;

        // 3. Determine which filter to apply
        if (condition != null && !condition.isEmpty() && name != null && !name.isEmpty()) {
            // Both condition and doctor name provided
            filteredResponse = patientService.filterByDoctorAndCondition(condition, name, patientId);
        } else if (condition != null && !condition.isEmpty()) {
            // Only condition provided
            filteredResponse = patientService.filterByCondition(condition, patientId);
        } else if (name != null && !name.isEmpty()) {
            // Only doctor name provided
            filteredResponse = patientService.filterByDoctor(name, patientId);
        } else {
            // No filters provided, get all appointments
            filteredResponse = patientService.getPatientAppointment(patientId, token);
        }

        return filteredResponse;

    } catch (Exception e) {
        System.out.println("Error filtering patient appointments: " + e.getMessage());
        e.printStackTrace();
        response.put("error", "Internal server error");
        return ResponseEntity.status(500).body(response);
    }
}



}
