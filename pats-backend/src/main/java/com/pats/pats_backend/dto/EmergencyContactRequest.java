package com.pats.pats_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmergencyContactRequest {
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    @NotBlank
    private String relationship;
    
    @NotBlank
    private String phoneNumber;
    
    private String alternatePhone;
    private String email;
    private String address;
    private Boolean isPrimary = false;
    private Integer priority;
}
