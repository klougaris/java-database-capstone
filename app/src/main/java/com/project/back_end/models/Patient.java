package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Patient {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long patient_id;

  @NotNull(message = "Name cannot be null.")
  @Size(min = 3, max = 100)
  private String name;


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

  @NotNull(message = "Address cannot be null")
  @Size(max = 255)
  private String address;

 
    public Patient() {
    }

  
    public Patient(String name, String email, String phone, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    
    public Patient(String name, String email, String password, String phone, String address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
    }

    // --- Getters and Setters ---

    public Long getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(Long patient_id) {
        this.patient_id = patient_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Password setter only (write-only)
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
