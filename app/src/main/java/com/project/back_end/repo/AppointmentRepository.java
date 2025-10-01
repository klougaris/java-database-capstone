package com.project.back_end.repo;

import com.project.back_end.model.Appointment; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

   @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d LEFT JOIN FETCH d.availabilities av WHERE a.doctor.id = :doctorId AND a.appointmentTime BETWEEN :start AND :end")
   public List<Appointment> findDoctorByIdAndAppointmentTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);

   @Query("SELECT DISTINCT a FROM Appointment a " +
       "LEFT JOIN FETCH a.doctor d " +
       "LEFT JOIN FETCH a.patient p " +
       "WHERE a.doctor.id = :doctorId " +
       "AND LOWER(a.patient.name) LIKE LOWER(CONCAT('%', :patientName, '%')) " +
       "AND a.appointmentTime BETWEEN :start AND :end")
   public List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(Long doctorId, String patientName, LocalDateTime start, LocalDateTime end);

   @Modifying
   @Transactional
   public void deleteAllByDoctorId(Long doctorId);

   public List<Appointment> findByPatientId(Long patientId);

   public List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);

   @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.doctor d " +
           "LEFT JOIN FETCH a.patient p " +
           "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND p.id = :patientId")
   public List<Appointment> filterByDoctorNameAndPatientId(String doctorName, Long patientId);

   @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.doctor d " +
           "LEFT JOIN FETCH a.patient p " +
           "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND p.id = :patientId " +
           "AND a.status = :status")
   public List<Appointment> filterByDoctorNameAndPatientIdAndStatus(String doctorName, Long patientId, int status);

   @Modifying
   @Transactional
   @Query("UPDATE Appointment a SET a.status = :status WHERE a.id = :id")
   public void updateStatus(int status, Long id);


}
