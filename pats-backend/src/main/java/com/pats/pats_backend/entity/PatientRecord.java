package com.pats.pats_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing the Electronic Medical Record (EMR) for a patient.
 * Core identity data (name, email, etc.) is managed via the 'patient' relationship 
 * to maintain a single source of truth.
 */
@Entity
@Table(name = "patient_records")
@Data // Lombok automatically generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor
@AllArgsConstructor
public class PatientRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "patient_id", nullable = false, unique = true)
    private Patient patient; 

    @Column(name = "cnp", unique = true, length = 13)
    private String cnp;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "alternate_phone")
    private String alternatePhone;

    // Address fields specifically for the Medical Record
    @Column(name = "street_address")
    private String streetAddress;

    @Column(name = "city")
    private String city;

    @Column(name = "county")
    private String county;

    @Column(name = "postal_code")
    private String postalCode;
    
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}