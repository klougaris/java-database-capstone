package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CentralService {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @Autowired
    public CentralService(TokenService tokenService,
                          AdminRepository adminRepository,
                          DoctorRepository doctorRepository,
                          PatientRepository patientRepository,
                          DoctorService doctorService,
                          PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    // 1. Validate token for a user
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean valid = tokenService.validateToken(token, user);
            if (!valid) {
                response.put("message", "Token is invalid or expired");
                return ResponseEntity.status(401).body(response);
            }
            response.put("message", "Token is valid");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Internal Server Error");
            return ResponseEntity.status(500).body(response);
        }
    }

    // 2. Validate admin login credentials
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        try {
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());
            if (admin == null) {
                response.put("message", "Admin not found");
                return ResponseEntity.status(401).body(response);
            }

            if (!admin.getPassword().equals(receivedAdmin.getPassword())) {
                response.put("message", "Invalid password");
                return ResponseEntity.status(401).body(response);
            }

            String token = tokenService.generateToken(admin.getUsername());
            response.put("token", token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Internal Server Error");
            return ResponseEntity.status(500).body(response);
        }
    }

    // 3. Filter doctors by name, specialty, and time
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        Map<String, Object> response = new HashMap<>();
        try {
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Error filtering doctors");
            return response;
        }
    }

    // 4. Validate appointment availability
    public int validateAppointment(Appointment appointment) {
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findById(appointment.getDoctorId());
            if (!doctorOpt.isPresent()) return -1;

            List<String> availableSlots = doctorService.getDoctorAvailability(
                    appointment.getDoctorId(), appointment.getAppointmentTime().toLocalDate()
            );

            String requestedTime = appointment.getAppointmentTime().toLocalTime().toString();
            if (availableSlots.contains(requestedTime)) return 1;
            return 0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 5. Validate if patient exists
    public boolean validatePatient(Patient patient) {
        try {
            Patient existing = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
            return existing == null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 6. Validate patient login credentials
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            Patient patient = patientRepository.findByEmail(login.getIdentifier());
            if (patient == null) {
                response.put("message", "Patient not found");
                return ResponseEntity.status(401).body(response);
            }

            if (!patient.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid password");
                return ResponseEntity.status(401).body(response);
            }

            String token = tokenService.generateToken(patient.getEmail());
            response.put("token", token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Internal Server Error");
            return ResponseEntity.status(500).body(response);
        }
    }

    // 7. Filter patient appointments
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        try {
            Map<String, Object> response = new HashMap<>();
            if (condition != null && !condition.isEmpty() && name != null && !name.isEmpty()) {
                return patientService.filterByDoctorAndCondition(condition, name, tokenService.getIdFromToken(token));
            } else if (condition != null && !condition.isEmpty()) {
                return patientService.filterByCondition(condition, tokenService.getIdFromToken(token));
            } else if (name != null && !name.isEmpty()) {
                return patientService.filterByDoctor(name, tokenService.getIdFromToken(token));
            } else {
                return patientService.getPatientAppointment(tokenService.getIdFromToken(token), token);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error filtering patient appointments");
            return ResponseEntity.status(500).body(error);
        }
    }
}
