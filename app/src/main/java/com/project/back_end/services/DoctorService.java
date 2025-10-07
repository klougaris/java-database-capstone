package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService; // Assume methods exist

    @Autowired
    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 1. Get doctor availability
    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        List<Appointment> appointments = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(
                        doctorId, date.atStartOfDay(), date.atTime(23, 59)
                );

        List<String> bookedSlots = appointments.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString())
                .collect(Collectors.toList());

        // Example: 9AM-5PM hourly slots
        List<String> allSlots = new ArrayList<>();
        for (int hour = 9; hour <= 17; hour++) {
            String slot = LocalTime.of(hour, 0).toString();
            if (!bookedSlots.contains(slot)) allSlots.add(slot);
        }
        return allSlots;
    }

    // 2. Save new doctor
    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) return -1;
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 3. Update existing doctor
    @Transactional
    public int updateDoctor(Doctor doctor) {
        try {
            Optional<Doctor> existing = doctorRepository.findById(doctor.getId());
            if (existing.isEmpty()) return -1;
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 4. Get all doctors
    @Transactional
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    // 5. Delete doctor by ID
    @Transactional
    public int deleteDoctor(long id) {
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findById(id);
            if (doctorOpt.isEmpty()) return -1;
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 6. Validate doctor login
    @Transactional
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());
        if (doctor == null || !doctor.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid email or password");
            return ResponseEntity.badRequest().body(response);
        }

        String token = tokenService.generateToken(doctor.getId(), "doctor");
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    // 7. Find doctors by partial name
    @Transactional
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        result.put("doctors", doctors);
        return result;
    }

    // 8. Filter doctors by name, specialty, and AM/PM
    @Transactional
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        doctors = filterDoctorByTime(doctors, amOrPm);
        result.put("doctors", doctors);
        return result;
    }

    // 9. Filter doctors by name and AM/PM
    @Transactional
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        doctors = filterDoctorByTime(doctors, amOrPm);
        result.put("doctors", doctors);
        return result;
    }

    // 10. Filter doctors by name and specialty
    @Transactional
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        result.put("doctors", doctors);
        return result;
    }

    // 11. Filter doctors by specialty and AM/PM
    @Transactional
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        doctors = filterDoctorByTime(doctors, amOrPm);
        result.put("doctors", doctors);
        return result;
    }

    // 12. Filter doctors by specialty only
    @Transactional
    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        result.put("doctors", doctors);
        return result;
    }

    // 13. Filter doctors by availability AM/PM only
    @Transactional
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findAll();
        doctors = filterDoctorByTime(doctors, amOrPm);
        result.put("doctors", doctors);
        return result;
    }

    // 14. Private helper to filter by AM/PM
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        return doctors.stream().filter(doctor -> {
            List<String> availableTimes = getDoctorAvailability(doctor.getId(), LocalDate.now());
            if (amOrPm.equalsIgnoreCase("AM")) {
                return availableTimes.stream().anyMatch(time -> LocalTime.parse(time).getHour() < 12);
            } else {
                return availableTimes.stream().anyMatch(time -> LocalTime.parse(time).getHour() >= 12);
            }
        }).collect(Collectors.toList());
    }
}
