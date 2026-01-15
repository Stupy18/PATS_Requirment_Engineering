package com.pats.pats_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreatePatientRequest {
    // User account info
    @NotBlank
    private String username;
    
    @Email
    @NotBlank
    private String email;
    
    @NotBlank
    private String password; // Temporary password for patient
    
    // Patient profile info
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    private LocalDate dateOfBirth;
    private String gender;
    
    @NotBlank
    private String phoneNumber;
    
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private String bloodType;
    private String insuranceProvider;
    private String insurancePolicyNumber;
}