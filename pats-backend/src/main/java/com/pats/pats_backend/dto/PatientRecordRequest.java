package com.pats.pats_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientRecordRequest {
    private Long patientId;
    
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    private LocalDate dateOfBirth;
    private String gender;
    private String cnp;
    
    @Email
    @NotBlank
    private String email;
    
    @NotBlank
    private String phoneNumber;
    
    private String alternatePhone;
    private String streetAddress;
    private String city;
    private String county;
    private String postalCode;
    private String occupation;
}