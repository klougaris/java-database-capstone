package com.project.back_end.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.project.back_end.models.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.repo.DoctorRepository;


@Service
public class AppointmentService {

  private AppointmentRepository appointmentRepository;
  private Service service; // your service entity
  private TokenService tokenService;
  private PatientRepository patientRepository;
  private DoctorRepository doctorRepository;

  // Constructor injection
  public AppointmentService(AppointmentRepository appointmentRepository,
                              Service service,
                              TokenService tokenService,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository) {
    
        this.appointmentRepository = appointmentRepository;
        this.service = service;
        this.tokenService = tokenService;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
  }


  @Transactional
  public int bookAppointment(Appointment appointment){

    try {
            appointmentRepository.save(appointment);
            return 1; // Success
        } catch (Exception e) {
            System.err.println("Failed to book appointment: " + e.getMessage());
            return 0; // Failure
        
  }

    
    @Transactional
    public ResponseEntity<Map<String, String>> updateAppoingment(Appointment appointment) {

    Map<String, String> response = new HashMap<>();

    try {

      // Check if appointment exists
      Appointment existingAppointment = appointmentRepository.findById(appointment.getId())
               .orElse(null);

      if(existingAppointment == null) {

        response.put("message", "Appointment not found");
        return ResponseEntity.badRequest().body(response);
      
      }

      // Check if patient ID matches
      if(!existingAppointment.getPatient().getId().equals(appointment.getPatient().getId())) {

        response.put("message", "Patient ID does not match");
        return ResponseEntity.badRequest().body(response);
        
      }

      // Validate the appointment update
      boolean isValid = service.validateAppointment(appointment);
      if(!isValid) {

        response.put("message", "Appointment update is not valid");
        return ResponseEntity.badRequest().body(response);
        
      }

      // Save updated appointment
      appointmentRepository.save(appointment);
      response.put("message", "Appointment updated successfully");
      return ResponseEntity.ok(response);

    } catch (Exception e) {

      response.put("message", "Failed to update appointment: " + e.getMessage());
      return ResponseEntity.internalServerError().body(response);
      
    }

    
  }

  @Transactional 
  public ResponseEntity<Map<String, String>> cancelApppointment(Long id, String token) {

    Map<String, String> response = new HashMap<>();

    try {

      // Find appointment
      Appointment appoinment = appointmentRepository.findById(id).orElse(null);

      if (appointment == null ) {

        response.put("message", "Appointment not found");
        return ResponseEntity.badRequest().body(response);
        
      }

      // Extract patient ID from token
      Long patientIdFromToken = tokenService.getPatientIdFromToken(token);

      if (patientIdFromToken == null) {

        response.put("message", "Invalid token");
        return ResponseEntity.status(401).body(response);
        
      }

      // Ensure the patient owns the appointment
      if (!appointment.getPatient().getId().equals(patientIdFromToken)) {

        response.put("message", "You are not authorized to cancel this appointment");
        return ResponseEntity.status(403).body(response);
        
      }

      // Delete appointment
      appointmentRepository.delete(appointment);

      response.put("message", "Appointment cancelled successfully");
      return ResponseEntity.ok(response);
     
    } catch (Exception e) {
      
      response.put("messsage", "Failed to cancel appointment: " + e.getMessage());
      return ResponseEntity.internalServerError().body(response);
      
    }
  }

  @Transactional
  public Map<String, Object> getAppoinment(String pname, LocalDate date, String token) {

    Map<String, Object> response = new HashMap<>();

    try {

      // Validate token and extract doctor ID
      Long doctorId = tokenService.getDoctorIdFromToken(token);
      if (doctorId == null) {

        response.put("message", "Invalid token");
        return response;
        
      }

      // Fetch appointments for doctor on the given date
      List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                                                                    doctorId, 
                                                                    date.atStartOfDay(),
                                                                    date.plusDays(1).atStartOfDay()
                                                              );


      // Filter by patient name if provided
      if (pname != null && !pname.isBlank()) {
      
        appointments = appointments.stream()
                        .filter(app -> app.getPatient().getName().equalsIgnoreCase(pname))
                        .collect(Collectors.toList());
      }

      response.put("appointments", appointments);
      return response;
      
    } catch (Exception e) {

      response.put("message", "Failed to retrieve appointments: " + e.getMessage());
      return response;
    }
  }

  
  @Transactional
  public int changeStatus(int status){}
// 8. **Change Status Method**:
//    - This method updates the status of an appointment by changing its value in the database.
//    - It should be annotated with `@Transactional` to ensure the operation is executed in a single transaction.
//    - Instruction: Add `@Transactional` before this method to ensure atomicity when updating appointment status.


}
