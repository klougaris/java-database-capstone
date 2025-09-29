package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Entity
public class Appointment {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long appointment_id;

  @ManyToOne
  @NotNull(message = "Doctor cannot be null")
  private Doctor doctor;

  @ManyToOne
  @NotNull
  private Patient patient;

  @Future(message = "Appointment time must be in the future.")
  private LocalDateTime appointmentTime;

  
  @NotNull(message="Status cannot be null.")
  private Integer status;


  @Transient
  public LocalDateTime getEndTime() {
    if (appointmentTime == null) {
        return null; // or throw an exception if appointmentTime must always exist
    }
    return appointmentTime.plusHours(1);
  }

  @Transient
  public LocalDate getAppointmentDate() {
    if (appointmentTime == null) {
        return null; // or throw an exception depending on your needs
    }
    return appointmentTime.toLocalDate();
  }

  @Transient
  public LocalTime getAppointmentTimeOnly() {
    if (appointmentTime == null) {
        return null;
    }
    return appointmentTime.toLocalTime();
  }

    public Appointment() {
        
    }

    public Appointment(Doctor doctor, Patient patient, LocalDateTime appointmentTime, Integer status) {
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }


    public Long getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(Long appointment_id) {
        this.appointment_id = appointment_id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

