package com.project.back_end.services;

import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import jakarta.transaction.Transactional;
import jakarta.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class TokenService {

  private final AdminRepository adminRepository;
  private final DoctorRepository doctorRepository;
  private final PatientRepository patientRepository;
  
  // Inject secret key from application properties
  @Value("${jwt.secret}")
  private String secretKey;

 
  // Token expiration: 7 days
  private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000; // 7 days in milliseconds



  public TokenService( AdminRepository adminRepository,
                     DoctorRepository doctorRepository,
                     PatientRepository patientRepository) {


      this.adminRepository = adminRepository;
      this.doctorRepository = doctorRepository;
      this.patientRepository = patientRepository;
    
  }

  private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
  }

  public String generateToken(String identifier) {
        return Jwts.builder()
                .setSubject(identifier)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
  }

  public String extractIdentifier(String token) {
    try {
        // Parse and validate the token
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);

        // Extract the subject (identifier, e.g., email or username)
        return claimsJws.getBody().getSubject();

    } catch (Exception e) {
        System.out.println("Error extracting identifier from token: " + e.getMessage());
        e.printStackTrace();
        return null; // return null if token is invalid or expired
    }
}
  
@Transactional
public boolean validateToken(String token, String userType) {
    try {
        // 1. Extract identifier (email or username) from token
        String identifier = extractIdentifier(token);
        if (identifier == null || identifier.isEmpty()) {
            return false; // invalid token
        }

        // 2. Check existence based on user type
        switch (userType.toLowerCase()) {
            case "admin":
                return adminRepository.findByUsername(identifier) != null;

            case "doctor":
                return doctorRepository.findByEmail(identifier) != null;

            case "patient":
                return patientRepository.findByEmail(identifier) != null;

            default:
                System.out.println("Unknown user type: " + userType);
                return false;
        }

    } catch (Exception e) {
        System.out.println("Error validating token for user type " + userType + ": " + e.getMessage());
        e.printStackTrace();
        return false; // treat exceptions as invalid token
    }
}



}
