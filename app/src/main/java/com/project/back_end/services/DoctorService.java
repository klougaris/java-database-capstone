package com.project.back_end.services;

import com.project.back_end.models.Doctor;
import com.project.back_end.models.Appointment;
import com.project.back_end.dto.Login;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.AppointmentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Collections;
import java.util.stream.Collectors;


@Service
public class DoctorService {


   private DoctorRepository doctorRepository;
   private AppointmentRepository appointmentRepository;
   private TokenService tokenService;

   private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

   
   public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
      
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }
   

// 3. **Add @Transactional Annotation for Methods that Modify or Fetch Database Data**:
//    - Methods like `getDoctorAvailability`, `getDoctors`, `findDoctorByName`, `filterDoctorsBy*` should be annotated with `@Transactional`.
//    - The `@Transactional` annotation ensures that database operations are consistent and wrapped in a single transaction.
//    - Instruction: Add the `@Transactional` annotation above the methods that perform database operations or queries.
   @Transactional
   public List<String> getDoctorAvailability(Long doctorId, LocalDate date){

      // Define Working Hours (Example: 9 AM - 5 PM, with 30 min slots).
      List<String> allSlots = generateTimeSlots(LocalTime.of(9,0), LocalTime.of(17,0), 30);

      // Fetch booked appointments for this doctor on the given date.
      List<Appointment> bookedAppointments =
                        appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, date);

      // Extract booked slot times as formatted strings ("HH:mm")
      Set<String> bookedSlots = bookedAppointments.stream()
            .map(appointment -> appointment.getAppointmentTime().toLocalTime().format(TIME_FORMATTER))
            .collect(Collectors.toSet());

      // Return available slots (all - booked)
      return allSlots.stream()
            .filter(slot -> !bookedSlots.contains(slot))
            .collect(Collectors.toList());
      
      }

   @Transactional
   public int saveDoctor(Doctor doctor) {
       
      try {
        
         System.out.println("Checking if a doctor with email " + doctor.getEmail() + " already exists...");
         Doctor existingDoctor = doctorRepository.findByEmail(doctor.getEmail());
         if (existingDoctor != null) {
            System.out.println("Conflict: Doctor with this email already exists.");
            return -1; // Conflict
        }

        System.out.println("Saving new doctor: " + doctor.getName());
        doctorRepository.save(doctor);
        System.out.println("Doctor saved successfully!");
        return 1; // Success

      } catch (Exception e) {
           System.out.println("An internal error occurred while saving the doctor: " + e.getMessage());
           e.printStackTrace();
           return 0; // Internal error
      }
   
   }

@Transactional
public int updateDoctor(Doctor doctor) {
   
    try {
        System.out.println("Checking if doctor with ID " + doctor.getId() + " exists...");
        Optional<Doctor> existingDoctorOpt = doctorRepository.findById(doctor.getId());

        if (existingDoctorOpt.isEmpty()) {
            System.out.println("Doctor not found.");
            return -1; // Doctor doesn't exist
        }

        Doctor existingDoctor = existingDoctorOpt.get();

        // Update the fields you want (example: name, email, specialty)
        existingDoctor.setName(doctor.getName());
        existingDoctor.setEmail(doctor.getEmail());
        existingDoctor.setSpecialty(doctor.getSpecialty());
        

        doctorRepository.save(existingDoctor);
        System.out.println("Doctor updated successfully!");
        return 1; // Success

    } catch (Exception e) {
        System.out.println("An internal error occurred while updating the doctor: " + e.getMessage());
        e.printStackTrace();
        return 0; // Internal error
    }
   
}

@Transactional
public List<Doctor> getDoctors() {
    try {
        System.out.println("Fetching all doctors...");

        // Fetch all doctors from the repository
        List<Doctor> doctors = doctorRepository.findAll();

        // Eagerly load lazy relationships if needed
        // Example: if Doctor has a collection of appointments or availableTimes
        doctors.forEach(doctor -> {
            if (doctor.getAppointments() != null) {
                doctor.getAppointments().size(); // forces initialization
            }
            if (doctor.getAvailableTimes() != null) {
                doctor.getAvailableTimes().size(); // forces initialization
            }
        });

        System.out.println("Total doctors fetched: " + doctors.size());
        return doctors;

    } catch (Exception e) {
        System.out.println("An error occurred while fetching doctors: " + e.getMessage());
        e.printStackTrace();
        return Collections.emptyList(); // return empty list on error
    }
}

   @Transactional
   public int deleteDoctor(Long id){

      try{

         System.out.println("Checking if doctor with ID: " + id + " exists...");
         Optional<Doctor> doctorOpt = doctorRepository.findById(id);

         if (doctorOpt.isEmpty()) {
            System.out.println("Conflict: Doctor with this id was not found.");
            return -1; // Conflict
         }

         System.out.println("Deleting doctor and their appointments..");
         appointmentRepository.deleteAllByDoctorId(id);
         doctorRepository.deleteById(id);

         System.out.println("Doctor and their appointments deleted successully.");
         return 1;// Success
         
      } catch (Exception e) {

         System.out.println("An error occurred while deleting doctor: " + e.getMessage());
         e.printStackTrace();
         return 0; // Internal error
      }

      
   }

   

@Transactional
public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
    Map<String, String> response = new HashMap<>();
    try {
        System.out.println("Validating doctor with email: " + login.getEmail());

        Doctor doctor = doctorRepository.findByEmail(login.getEmail());
        if (doctor == null) {
            response.put("error", "Doctor not found");
            return ResponseEntity.status(404).body(response);
        }

        // Check password
        if (!doctor.getPassword().equals(login.getPassword())) {
            response.put("error", "Invalid password");
            return ResponseEntity.status(401).body(response);
        }

        // Generate real token using TokenService
        String token = tokenService.generateToken(doctor);
        response.put("token", token);

        System.out.println("Doctor validated successfully. Token generated.");
        return ResponseEntity.ok(response);

    } catch (Exception e) {
        System.out.println("An error occurred during doctor validation: " + e.getMessage());
        e.printStackTrace();
        response.put("error", "Internal server error");
        return ResponseEntity.status(500).body(response);
    }
}

@Transactional
public Map<String, Object> findDoctorByName(String name) {
    Map<String, Object> response = new HashMap<>();
    try {
        System.out.println("Searching for doctors with name containing: " + name);

        List<Doctor> doctors = doctorRepository.findByNameLike(name);

        response.put("count", doctors.size());
        response.put("doctors", doctors);

        System.out.println("Found " + doctors.size() + " doctor(s) matching the name.");
        return response;

    } catch (Exception e) {
        System.out.println("An error occurred while searching for doctors: " + e.getMessage());
        e.printStackTrace();
        response.put("count", 0);
        response.put("doctors", Collections.emptyList());
        response.put("error", "Internal server error");
        return response;
    }
}

@Transactional
public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
    Map<String, Object> response = new HashMap<>();
    try {
        System.out.println("Filtering doctors by name: " + name + ", specialty: " + specialty + ", time: " + amOrPm);

        // Step 1: Fetch doctors by name and specialty
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);

        // Step 2: Filter by availability (AM or PM)
        List<Doctor> filteredDoctors = doctors.stream()
                .filter(doctor -> {
                    // Get availability for today (or a given date – adjust if needed)
                    List<String> availableSlots = getDoctorAvailability(doctor.getId(), LocalDate.now());

                    if (amOrPm.equalsIgnoreCase("AM")) {
                        return availableSlots.stream().anyMatch(slot -> slot.compareTo("12:00") < 0);
                    } else if (amOrPm.equalsIgnoreCase("PM")) {
                        return availableSlots.stream().anyMatch(slot -> slot.compareTo("12:00") >= 0);
                    }
                    return false;
                })
                .collect(Collectors.toList());

        response.put("count", filteredDoctors.size());
        response.put("doctors", filteredDoctors);

        System.out.println("Filtered " + filteredDoctors.size() + " doctor(s).");
        return response;

    } catch (Exception e) {
        System.out.println("An error occurred while filtering doctors: " + e.getMessage());
        e.printStackTrace();
        response.put("count", 0);
        response.put("doctors", Collections.emptyList());
        response.put("error", "Internal server error");
        return response;
    }
}

@Transactional
public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
    Map<String, Object> response = new HashMap<>();
    try {
        System.out.println("Filtering doctors by name: " + name + " and time: " + amOrPm);

        // Step 1: Find doctors by partial name match
        List<Doctor> doctors = doctorRepository.findByNameLike(name);

        // Step 2: Filter doctors by availability (AM/PM)
        List<Doctor> filteredDoctors = doctors.stream()
                .filter(doctor -> {
                    // Get today's availability (could be adjusted if you want date-based filtering)
                    List<String> availableSlots = getDoctorAvailability(doctor.getId(), LocalDate.now());

                    if (amOrPm.equalsIgnoreCase("AM")) {
                        return availableSlots.stream().anyMatch(slot -> slot.compareTo("12:00") < 0);
                    } else if (amOrPm.equalsIgnoreCase("PM")) {
                        return availableSlots.stream().anyMatch(slot -> slot.compareTo("12:00") >= 0);
                    }
                    return false;
                })
                .collect(Collectors.toList());

        response.put("count", filteredDoctors.size());
        response.put("doctors", filteredDoctors);

        System.out.println("Filtered " + filteredDoctors.size() + " doctor(s).");
        return response;

    } catch (Exception e) {
        System.out.println("An error occurred while filtering doctors: " + e.getMessage());
        e.printStackTrace();
        response.put("count", 0);
        response.put("doctors", Collections.emptyList());
        response.put("error", "Internal server error");
        return response;
    }
}

@Transactional
public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
    Map<String, Object> response = new HashMap<>();
    try {
        System.out.println("Filtering doctors by name: " + name + " and specialty: " + specialty);

        // Step 1: Fetch doctors by name and specialty (case-insensitive)
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);

        // Step 2: Build response
        response.put("count", doctors.size());
        response.put("doctors", doctors);

        System.out.println("Filtered " + doctors.size() + " doctor(s).");
        return response;

    } catch (Exception e) {
        System.out.println("An error occurred while filtering doctors: " + e.getMessage());
        e.printStackTrace();
        response.put("count", 0);
        response.put("doctors", Collections.emptyList());
        response.put("error", "Internal server error");
        return response;
    }
}

@Transactional
public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
    Map<String, Object> response = new HashMap<>();
    try {
        System.out.println("Filtering doctors by specialty: " + specialty + " and time: " + amOrPm);

        // Step 1: Fetch doctors by specialty (case-insensitive)
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);

        // Step 2: Filter by availability (AM or PM)
        List<Doctor> filteredDoctors = doctors.stream()
                .filter(doctor -> {
                    // Reuse getDoctorAvailability for today's date
                    List<String> availableSlots = getDoctorAvailability(doctor.getId(), LocalDate.now());

                    if (amOrPm.equalsIgnoreCase("AM")) {
                        return availableSlots.stream().anyMatch(slot -> slot.compareTo("12:00") < 0);
                    } else if (amOrPm.equalsIgnoreCase("PM")) {
                        return availableSlots.stream().anyMatch(slot -> slot.compareTo("12:00") >= 0);
                    }
                    return false;
                })
                .collect(Collectors.toList());

        // Step 3: Build response
        response.put("count", filteredDoctors.size());
        response.put("doctors", filteredDoctors);

        System.out.println("Filtered " + filteredDoctors.size() + " doctor(s).");
        return response;

    } catch (Exception e) {
        System.out.println("An error occurred while filtering doctors: " + e.getMessage());
        e.printStackTrace();
        response.put("count", 0);
        response.put("doctors", Collections.emptyList());
        response.put("error", "Internal server error");
        return response;
    }
}

@Transactional
public Map<String, Object> filterDoctorBySpecility(String specialty) {
    Map<String, Object> response = new HashMap<>();
    try {
        System.out.println("Filtering doctors by specialty: " + specialty);

        // Step 1: Fetch doctors by specialty (case-insensitive)
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);

        // Step 2: Build response
        response.put("count", doctors.size());
        response.put("doctors", doctors);

        System.out.println("Filtered " + doctors.size() + " doctor(s) with specialty: " + specialty);
        return response;

    } catch (Exception e) {
        System.out.println("An error occurred while filtering doctors by specialty: " + e.getMessage());
        e.printStackTrace();
        response.put("count", 0);
        response.put("doctors", Collections.emptyList());
        response.put("error", "Internal server error");
        return response;
    }
}

   @Transactional
public Map<String, Object> filterDoctorsByTime(String amOrPm) {
    Map<String, Object> response = new HashMap<>();
    try {
        System.out.println("Filtering doctors by availability during: " + amOrPm);

        // Step 1: Fetch all doctors
        List<Doctor> doctors = doctorRepository.findAll();

        // Step 2: Filter doctors by AM/PM availability
        List<Doctor> filteredDoctors = doctors.stream()
                .filter(doctor -> {
                    // Use getDoctorAvailability for today's date
                    List<String> availableSlots = getDoctorAvailability(doctor.getId(), LocalDate.now());

                    if (amOrPm.equalsIgnoreCase("AM")) {
                        return availableSlots.stream().anyMatch(slot -> slot.compareTo("12:00") < 0);
                    } else if (amOrPm.equalsIgnoreCase("PM")) {
                        return availableSlots.stream().anyMatch(slot -> slot.compareTo("12:00") >= 0);
                    }
                    return false;
                })
                .collect(Collectors.toList());

        // Step 3: Build response
        response.put("count", filteredDoctors.size());
        response.put("doctors", filteredDoctors);

        System.out.println("Filtered " + filteredDoctors.size() + " doctor(s) available in " + amOrPm);
        return response;

    } catch (Exception e) {
        System.out.println("An error occurred while filtering doctors by time: " + e.getMessage());
        e.printStackTrace();
        response.put("count", 0);
        response.put("doctors", Collections.emptyList());
        response.put("error", "Internal server error");
        return response;
    }
}

   // Private helper method
private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
    return doctors.stream()
            .filter(doctor -> {
                // Get today's availability (you can later refactor to accept a LocalDate)
                List<String> availableSlots = getDoctorAvailability(doctor.getId(), LocalDate.now());

                if (amOrPm.equalsIgnoreCase("AM")) {
                    return availableSlots.stream().anyMatch(slot -> slot.compareTo("12:00") < 0);
                } else if (amOrPm.equalsIgnoreCase("PM")) {
                    return availableSlots.stream().anyMatch(slot -> slot.compareTo("12:00") >= 0);
                }
                return false;
            })
            .collect(Collectors.toList());
}


   private List<String> generateTimeSlots(LocalTime start, LocalTime end, int minutesInterval) {
    List<String> slots = new ArrayList<>();
    LocalTime current = start;
    while (current.isBefore(end)) {
        slots.add(current.format(TIME_FORMATTER)); // ensures "09:00" instead of "9:00"
        current = current.plusMinutes(minutesInterval);
    }
    return slots;
}
   
}
