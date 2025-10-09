package com.project.back_end.services;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String secret; // Secret key from application.properties

    public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    // -------------------------------
    // 🔐 1. Generate JWT Token
    // -------------------------------
    public String generateToken(String identifier) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000); // 7 days

        return Jwts.builder()
                .setSubject(identifier) // email or username
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // -------------------------------
    // 🧩 2. Extract Identifier (email or username)
    // -------------------------------
    public String extractIdentifier(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
        } catch (JwtException e) {
            return null; // Token invalid or expired
        }
    }

    // -------------------------------
    // ✅ 3. Validate Token
    // -------------------------------
    public boolean validateToken(String token, String userType) {
        try {
            String identifier = extractIdentifier(token);
            if (identifier == null) return false;

            switch (userType.toLowerCase()) {
                case "admin":
                    Admin admin = adminRepository.findByUsername(identifier);
                    return admin != null;
                case "doctor":
                    Doctor doctor = doctorRepository.findByEmail(identifier);
                    return doctor != null;
                case "patient":
                    Patient patient = patientRepository.findByEmail(identifier);
                    return patient != null;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    // -------------------------------
    // 🔑 4. Get Signing Key
    // -------------------------------
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // -------------------------------
    // 🧠 5. Helper Methods to Extract IDs
    // -------------------------------

    // 🔸 Get Doctor ID from Token
    public Long getDoctorIdFromToken(String token) {
        String email = extractIdentifier(token);
        if (email == null) return null;

        Doctor doctor = doctorRepository.findByEmail(email);
        return doctor != null ? doctor.getId() : null;
    }

    // 🔸 Get Patient ID from Token
    public Long getPatientIdFromToken(String token) {
        String email = extractIdentifier(token);
        if (email == null) return null;

        Patient patient = patientRepository.findByEmail(email);
        return patient != null ? patient.getId() : null;
    }
}
