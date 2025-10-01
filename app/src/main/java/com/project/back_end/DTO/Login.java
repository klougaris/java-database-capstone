package com.project.back_end.DTO;

public class Login {
    
    private String email;

    private String password;

    // Default constructor (optional, Java provides this automatically)

    public Login(){

    // Getter methods 
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setter methods
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
