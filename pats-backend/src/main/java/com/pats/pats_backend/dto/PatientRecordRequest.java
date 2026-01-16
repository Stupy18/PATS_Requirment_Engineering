package com.pats.pats_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for creating or updating a Patient Medical Record.
 * Personal identity data (Name, Email, DOB) is excluded as it is 
 * pulled automatically from the core Patient entity.
 */
@Data
public class PatientRecordRequest {

    // Unique identification number (CNP)
    @NotBlank(message = "CNP is required")
    @Size(min = 13, max = 13, message = "CNP must be exactly 13 characters")
    private String cnp;

    // Professional information
    private String occupation;

    // Contact and Address details specific to the medical file
    private String alternatePhone;
    private String streetAddress;
    private String city;
    private String county;
    private String postalCode;
}