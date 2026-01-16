package com.pats.pats_backend.controller;

import com.pats.pats_backend.dto.*;
import com.pats.pats_backend.entity.*;
import com.pats.pats_backend.service.PatientRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emr")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PatientRecordController {

    private final PatientRecordService service;

    // ==================== FR17.1: Patient Records ====================

    @PostMapping("/records/patient/{patientId}")
    @PreAuthorize("hasRole('PSYCHOLOGIST')")
    public ResponseEntity<PatientRecord> createRecord(
            @PathVariable Long patientId,
            @Valid @RequestBody PatientRecordRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createPatientRecord(patientId, request));
    }

    @GetMapping("/records/{id}")
    @PreAuthorize("hasAnyRole('PSYCHOLOGIST', 'PATIENT')")
    public ResponseEntity<PatientRecord> getRecord(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPatientRecord(id));
    }

    @GetMapping("/records/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PSYCHOLOGIST', 'PATIENT')")
    public ResponseEntity<PatientRecord> getRecordByPatientId(@PathVariable Long patientId) {
        return service.getPatientRecordByPatientId(patientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/records/{id}")
    @PreAuthorize("hasRole('PSYCHOLOGIST')")
    public ResponseEntity<PatientRecord> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody PatientRecordRequest request) {
        return ResponseEntity.ok(service.updatePatientRecord(id, request));
    }

    // ==================== FR17.6: Emergency Contacts ====================

    @PostMapping("/patients/{patientId}/emergency-contacts")
    @PreAuthorize("hasRole('PSYCHOLOGIST')")
    public ResponseEntity<EmergencyContact> addEmergencyContact(
            @PathVariable Long patientId,
            @Valid @RequestBody EmergencyContactRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addEmergencyContact(patientId, request));
    }

    @GetMapping("/patients/{patientId}/emergency-contacts")
    @PreAuthorize("hasAnyRole('PSYCHOLOGIST', 'PATIENT')")
    public ResponseEntity<List<EmergencyContact>> getEmergencyContacts(@PathVariable Long patientId) {
        return ResponseEntity.ok(service.getEmergencyContacts(patientId));
    }

    @DeleteMapping("/emergency-contacts/{contactId}")
    @PreAuthorize("hasRole('PSYCHOLOGIST')")
    public ResponseEntity<Void> deleteEmergencyContact(@PathVariable Long contactId) {
        service.deleteEmergencyContact(contactId);
        return ResponseEntity.noContent().build();
    }

    // ==================== FR17.9: Audit Trail ====================

    @GetMapping("/records/{recordId}/audit-trail")
    @PreAuthorize("hasRole('PSYCHOLOGIST')")
    public ResponseEntity<List<AuditLog>> getAuditTrail(@PathVariable Long recordId) {
        return ResponseEntity.ok(service.getAuditTrail(recordId));
    }
}