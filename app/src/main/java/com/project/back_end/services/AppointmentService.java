package com.project.back_end.services;

import com.project.back_end.services.TokenService;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService; // Assume methods exist

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              TokenService tokenService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
    }

    // 1. Book a new appointment
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1; // success
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // error
        }
    }

    // 2. Update an existing appointment
    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();
        Optional<Appointment> existingOpt = appointmentRepository.findById(appointment.getId());
        if (existingOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment existing = existingOpt.get();

        // Optional: Use a validateAppointment method (assume exists in service)
        // if (!validateAppointment(appointment)) { ... }

        existing.setAppointmentTime(appointment.getAppointmentTime());
        existing.setStatus(appointment.getStatus());
        existing.setDoctor(appointment.getDoctor());
        existing.setPatient(appointment.getPatient());

        appointmentRepository.save(existing);
        response.put("message", "Appointment updated successfully");
        return ResponseEntity.ok(response);
    }

    // 3. Cancel an existing appointment
    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment appointment = appointmentOpt.get();

        // Assume tokenService validates token and returns user info
        Map<String, Object> userMap = tokenService.validateToken(token, "patient"); 
        if (userMap.isEmpty() || !userMap.get("id").equals(appointment.getPatient().getId())) {
            response.put("message", "Unauthorized to cancel this appointment");
            return ResponseEntity.status(403).body(response);
        }

        appointmentRepository.delete(appointment);
        response.put("message", "Appointment canceled successfully");
        return ResponseEntity.ok(response);
    }

    // 4. Retrieve appointments for a doctor on a specific date, optionally filtered by patient name
    @Transactional
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> result = new HashMap<>();

        // Assume tokenService returns doctor info
        Map<String, Object> doctorMap = tokenService.validateToken(token, "doctor");
        if (doctorMap.isEmpty()) {
            result.put("message", "Invalid token");
            return result;
        }

        Long doctorId = (Long) doctorMap.get("id");

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<Appointment> appointments;
        if (pname == null || pname.isEmpty()) {
            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        } else {
            appointments = appointmentRepository
                    .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(doctorId, pname, start, end);
        }

        result.put("appointments", appointments);
        return result;
    }

    // 5. Change appointment status
    @Transactional
    public void changeStatus(long id, int status) {
        appointmentRepository.updateStatus(status, id);
    }
}
