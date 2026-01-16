package com.pats.pats_backend.controller;

import com.pats.pats_backend.dto.CreatePatientRequest;
import com.pats.pats_backend.entity.Patient;
import com.pats.pats_backend.service.PatientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PatientController {

    private final PatientService patientService;

    /**
     * Get all patients (for listing/selection)
     */
    @GetMapping
    @PreAuthorize("hasRole('PSYCHOLOGIST')")
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    /**
     * Get patient by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PSYCHOLOGIST', 'PATIENT')")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    /**
     * Create new patient (Psychologist adds patient manually)
     */
    @PostMapping
    @PreAuthorize("hasRole('PSYCHOLOGIST')")
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody CreatePatientRequest request) {
        Patient patientData = new Patient();
        patientData.setFirstName(request.getFirstName());
        patientData.setLastName(request.getLastName());
        patientData.setDateOfBirth(request.getDateOfBirth());
        patientData.setGender(request.getGender());
        patientData.setPhoneNumber(request.getPhoneNumber());
        patientData.setAddress(request.getAddress());
        patientData.setCity(request.getCity());
        patientData.setPostalCode(request.getPostalCode());
        patientData.setCountry(request.getCountry());
        patientData.setBloodType(request.getBloodType());
        patientData.setInsuranceProvider(request.getInsuranceProvider());
        patientData.setInsurancePolicyNumber(request.getInsurancePolicyNumber());

        Patient created = patientService.createPatient(
            patientData,
            request.getUsername(),
            request.getEmail(),
            request.getPassword()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update patient information
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PSYCHOLOGIST')")
    public ResponseEntity<Patient> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody Patient patient) {
        return ResponseEntity.ok(patientService.updatePatient(id, patient));
    }

    /**
     * Search patients by name
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('PSYCHOLOGIST')")
    public ResponseEntity<List<Patient>> searchPatients(@RequestParam String q) {
        return ResponseEntity.ok(patientService.searchPatients(q));
    }
}