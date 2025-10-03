package com.project.back_end.services;

import com.project.back_end.models.Patient;
import com.project.back_end.models.Appointment;
import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.repo.AppointmentRepository;

import com.project.back_end.services.TokenService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private PatientRepository patientRepository;
    private AppointmentRepository appointmentRepository;
    private TokenService tokenService;

    public PatientService(PatientRepository patientRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {

        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;

    }


    @Transactional
    public int createPatient(Patient patient) {

        try {

            System.out.println("Checking if patient already exists..");
            Patient testPatient = patientRepository.findById(patient.getId()).orElse(null);

            if(testPatient != null) {
                System.out.println("Patient already exists.");
                return -1; // Conflict
            }
            
            System.out.println("Creating new patient..");
            patientRepository.save(patient);
            return 1; // Success
           
        } catch (Exception e) {

            System.out.println("An error occurred while creating patient." + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

@Transactional
public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
    Map<String, Object> response = new HashMap<>();
    try {
        // 1. Extract email from token
        String email = tokenService.extractEmail(token);
        System.out.println("Decoded email from token: " + email);

        // 2. Find patient by email
        Patient patient = patientRepository.findByEmail(email);
        if (patient == null) {
            response.put("error", "Patient not found");
            return ResponseEntity.status(404).body(response);
        }

        // 3. Validate ID match
        if (!patient.getId().equals(id)) {
            System.out.println("Unauthorized access attempt for patient ID " + id);
            response.put("error", "Unauthorized access");
            return ResponseEntity.status(401).body(response);
        }

        // 4. Fetch appointments
        List<Appointment> appointments = appointmentRepository.findByPatientId(id);

        // 5. Convert to DTOs
        List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(appointment -> new AppointmentDTO(
                        appointment.getId(),
                        appointment.getDoctor().getId(),
                        appointment.getDoctor().getName(),
                        appointment.getPatient().getId(),
                        appointment.getPatient().getName(),
                        appointment.getPatient().getEmail(),
                        appointment.getPatient().getPhone(),
                        appointment.getPatient().getAddress(),
                        appointment.getAppointmentTime(),
                        appointment.getStatus()
                ))
                .collect(Collectors.toList());

        // 6. Return success
        response.put("appointments", appointmentDTOs);
        response.put("count", appointmentDTOs.size());
        return ResponseEntity.ok(response);

    } catch (Exception e) {
        System.out.println("Error retrieving patient appointments: " + e.getMessage());
        e.printStackTrace();
        response.put("error", "Internal server error");
        return ResponseEntity.status(500).body(response);
    }
}

@Transactional
public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
    Map<String, Object> response = new HashMap<>();
    try {
        int status;
        if ("past".equalsIgnoreCase(condition)) {
            status = 1;
        } else if ("future".equalsIgnoreCase(condition)) {
            status = 0;
        } else {
            response.put("error", "Invalid condition. Use 'past' or 'future'.");
            return ResponseEntity.badRequest().body(response);
        }

        List<Appointment> appointments = appointmentRepository.findByPatientIdAndStatus(id, status);

        List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(appointment -> new AppointmentDTO(
                        appointment.getId(),
                        appointment.getDoctor().getId(),
                        appointment.getDoctor().getName(),
                        appointment.getPatient().getId(),
                        appointment.getPatient().getName(),
                        appointment.getPatient().getEmail(),
                        appointment.getPatient().getPhone(),
                        appointment.getPatient().getAddress(),
                        appointment.getAppointmentTime(),
                        appointment.getStatus()
                ))
                .collect(Collectors.toList());

        response.put("appointments", appointmentDTOs);
        response.put("count", appointmentDTOs.size());
        response.put("condition", condition.toLowerCase());

        return ResponseEntity.ok(response);

    } catch (Exception e) {
        System.out.println("Error filtering appointments by condition: " + e.getMessage());
        e.printStackTrace();
        response.put("error", "Internal server error");
        return ResponseEntity.status(500).body(response);
    }
}

@Transactional
public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
    Map<String, Object> response = new HashMap<>();
    try {
        System.out.println("Filtering appointments for patient " + patientId + " by doctor name: " + name);

        List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientId(name, patientId);

        List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(a -> new AppointmentDTO(
                        a.getId(),
                        a.getDoctor().getId(),
                        a.getDoctor().getName(),
                        a.getPatient().getId(),
                        a.getPatient().getName(),
                        a.getPatient().getEmail(),
                        a.getPatient().getPhone(),
                        a.getPatient().getAddress(),
                        a.getAppointmentTime(),
                        a.getStatus()
                ))
                .collect(Collectors.toList());

        response.put("appointments", appointmentDTOs);
        response.put("count", appointmentDTOs.size());

        return ResponseEntity.ok(response);

    } catch (Exception e) {
        System.out.println("Error while filtering appointments by doctor: " + e.getMessage());
        e.printStackTrace();
        response.put("error", "Internal server error");
        return ResponseEntity.status(500).body(response);
    }
}

    
@Transactional
public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
    Map<String, Object> response = new HashMap<>();
    try {
        System.out.println("Filtering appointments for patient " + patientId +
                           " by doctor: " + name + " and condition: " + condition);

        // Determine status based on condition
        int status;
        if ("past".equalsIgnoreCase(condition)) {
            status = 1; // Past
        } else if ("future".equalsIgnoreCase(condition)) {
            status = 0; // Future
        } else {
            response.put("error", "Invalid condition. Use 'past' or 'future'.");
            return ResponseEntity.badRequest().body(response);
        }

        // Fetch filtered appointments
        List<Appointment> appointments =
                appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(name, patientId, status);

        // Convert to DTO
        List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(a -> new AppointmentDTO(
                        a.getId(),
                        a.getDoctor().getId(),
                        a.getDoctor().getName(),
                        a.getPatient().getId(),
                        a.getPatient().getName(),
                        a.getPatient().getEmail(),
                        a.getPatient().getPhone(),
                        a.getPatient().getAddress(),
                        a.getAppointmentTime(),
                        a.getStatus()
                ))
                .collect(Collectors.toList());

        // Build response
        response.put("appointments", appointmentDTOs);
        response.put("count", appointmentDTOs.size());

        return ResponseEntity.ok(response);

    } catch (Exception e) {
        System.out.println("Error filtering appointments by doctor and condition: " + e.getMessage());
        e.printStackTrace();
        response.put("error", "Internal server error");
        return ResponseEntity.status(500).body(response);
    }
}

    @Transactional
public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
    Map<String, Object> response = new HashMap<>();
    try {
        System.out.println("Extracting patient email from token...");

        // Extract email from token
        String email = tokenService.extractEmail(token);
        if (email == null || email.isEmpty()) {
            response.put("error", "Invalid token or email not found");
            return ResponseEntity.status(401).body(response);
        }

        System.out.println("Fetching patient details for email: " + email);

        // Fetch patient by email
        Patient patient = patientRepository.findByEmail(email);
        if (patient == null) {
            response.put("error", "Patient not found");
            return ResponseEntity.status(404).body(response);
        }

        // Build response
        Map<String, Object> patientData = new HashMap<>();
        patientData.put("id", patient.getId());
        patientData.put("name", patient.getName());
        patientData.put("email", patient.getEmail());
        patientData.put("phone", patient.getPhone());
        patientData.put("address", patient.getAddress());
        // Add more fields if needed

        response.put("patient", patientData);

        System.out.println("Patient details fetched successfully.");
        return ResponseEntity.ok(response);

    } catch (Exception e) {
        System.out.println("Error fetching patient details: " + e.getMessage());
        e.printStackTrace();
        response.put("error", "Internal server error");
        return ResponseEntity.status(500).body(response);
    }
}

}
