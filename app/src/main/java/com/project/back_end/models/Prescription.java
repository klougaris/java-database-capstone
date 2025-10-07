package com.project.back_end.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "prescriptions")
public class Prescription {

    // 1. MongoDB document ID
    @Id
    private String id;

    // 2. Patient's full name
    @NotNull(message="Name can not be null.")
    @Size(min = 3, max = 100)
    private String patientName;

    // 3. Associated appointment ID (reference)
    @NotNull(message="Appointment ID can not be null.")
    private Long appointmentId;

    // 4. Medication name
    @NotNull(message="Medication can not be null.")
    @Size(min = 3, max = 100)
    private String medication;

    // 5. Dosage details
    @NotNull(message="Dosage can not be null.")
    @Size(min = 3, max = 20)
    private String dosage;

    // 6. Optional doctor's notes
    @Size(max = 200)
    private String doctorNotes;

    // Default constructor (required by Spring Data)
    public Prescription() {}

    // Parameterized constructor
    public Prescription(String patientName, Long appointmentId, String medication, String dosage, String doctorNotes) {
        this.patientName = patientName;
        this.appointmentId = appointmentId;
        this.medication = medication;
        this.dosage = dosage;
        this.doctorNotes = doctorNotes;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }
}
