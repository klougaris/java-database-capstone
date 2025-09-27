package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Admin {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long admin_id;


  @NotNull(message = " Username cannot be null.")
  private String username;


  @NotNull(message = "Password cannot be null." )
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;


  public Admin(){}

  public Admin(Long id, String username, String password){
    admin_id = id;
    this.username = username;
    this.password = password;
  }

  public Long getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(Long admin_id) {
        this.admin_id = admin_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // --- Setter only for password (no getter for security) ---
    public void setPassword(String password) {
        this.password = password;
    }

  

}
