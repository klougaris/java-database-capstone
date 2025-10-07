package com.project.back_end.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Patient {

    // 1. Primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 2. Full name
    @NotNull(message = "Name can not be null.")
    @Size(min = 3, max = 100)
    @Column(nullable = false)
    private String name;

    // 3. Email (must be unique and valid)
    @NotNull(message = "Email can not be null.")
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    // 4. Password (write-only)
    @NotNull(message = "Password can not be null.")
    @Size(min = 6)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    // 5. Phone number (exactly 10 digits)
    @NotNull(message = "Phone can not be null.")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    @Column(nullable = false, unique = true)
    private String phone;

    // 6. Address (required, max 255 chars)
    @NotNull(message = "Address can not be null.")
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String address;

    // Default constructor (required by JPA)
    public Patient() {}

    // Parameterized constructor
    public Patient(String name, String email, String password, String phone, String address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPassword() {
        return password;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
