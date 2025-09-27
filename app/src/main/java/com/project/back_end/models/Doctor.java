package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Doctor {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long doctor_id;

  @NotNull(message = "Name cannot be null.")
  @Size(min = 3, max = 100)
  private String name;

  @NotNull(message = "specialty")
  @Size(min = 3, max = 50)
  private String specialty;

  @NotNull(message = "Email cannot be null.")
  @Email(message = "Invalid email format")
  private String email;

  @NotNull(message = "Password cannot be null.")
  @Size(min = 6)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  @NotNull(message = "Phone cannot be null.")
  @Pattern(regexp = "^[0-9]{10}$")
  private String phone;

  @ElementCollection
  private List<String> availableTimes;



    public Doctor() {
    }

    
    public Doctor(String name, String specialty, String email, String phone, List<String> availableTimes) {
        this.name = name;
        this.specialty = specialty;
        this.email = email;
        this.phone = phone;
        if (availableTimes != null) {
            this.availableTimes = availableTimes;
        }
    }

    
    public Doctor(String name, String specialty, String email, String password, String phone, List<String> availableTimes) {
        this.name = name;
        this.specialty = specialty;
        this.email = email;
        this.password = password;
        this.phone = phone;
        if (availableTimes != null) {
            this.availableTimes = availableTimes;
        }
    }

    

    public Long getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(Long doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

  
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getAvailableTimes() {
        return availableTimes;
    }

    public void setAvailableTimes(List<String> availableTimes) {
        this.availableTimes = availableTimes;
    }
}

