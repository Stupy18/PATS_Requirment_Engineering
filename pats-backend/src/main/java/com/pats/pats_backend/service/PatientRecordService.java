package com.pats.pats_backend.service;

import com.pats.pats_backend.entity.*;
import com.pats.pats_backend.repo.*;
import com.pats.pats_backend.dto.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing Electronic Medical Records (EMR).
 */
@Service
@RequiredArgsConstructor
public class PatientRecordService {

    private final PatientRecordRepository patientRecordRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final AuditLogRepository auditLogRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    // ==================== FR17.1: Patient Record Management ====================

    /**
     * Creates a new medical record for a specific patient.
     * Identity data (name, email) is automatically pulled from the Patient entity.
     */
    @Transactional
    public PatientRecord createPatientRecord(Long patientId, PatientRecordRequest request) {
        // 1. Fetch the existing patient to serve as the source of truth for identity
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // 2. Ensure a record doesn't already exist for this patient (OneToOne
        // relationship)
        if (patientRecordRepository.findByPatientId(patientId).isPresent()) {
            throw new RuntimeException("A medical record already exists for this patient");
        }

        // 3. Map only EMR-specific fields, avoiding duplication of patient core data
        PatientRecord record = new PatientRecord();
        record.setPatient(patient); // Automatically links to firstName, lastName, email, etc.

        record.setCnp(request.getCnp());
        record.setOccupation(request.getOccupation());
        record.setAlternatePhone(request.getAlternatePhone());
        record.setStreetAddress(request.getStreetAddress());
        record.setCity(request.getCity());
        record.setCounty(request.getCounty());
        record.setPostalCode(request.getPostalCode());
        record.setIsActive(true);

        PatientRecord saved = patientRecordRepository.save(record);
        logAudit(saved, "CREATED", "Electronic Medical Record initialized");

        return saved;
    }

    /**
     * Retrieves a patient record by its ID.
     */
    public PatientRecord getPatientRecord(Long id) {
        PatientRecord record = patientRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient record not found"));

        logAudit(record, "VIEWED", "Medical record accessed");
        return record;
    }

    public Optional<PatientRecord> getPatientRecordByPatientId(Long patientId) {
        return patientRecordRepository.findByPatientId(patientId);
    }

    /**
     * Updates EMR-specific information.
     * Core identity data remains untouched to maintain data integrity.
     */
    @Transactional
    public PatientRecord updatePatientRecord(Long id, PatientRecordRequest request) {
        PatientRecord record = patientRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient record not found"));

        // Update only supplementary EMR fields
        record.setCnp(request.getCnp());
        record.setOccupation(request.getOccupation());
        record.setAlternatePhone(request.getAlternatePhone());
        record.setStreetAddress(request.getStreetAddress());
        record.setCity(request.getCity());
        record.setCounty(request.getCounty());
        record.setPostalCode(request.getPostalCode());

        PatientRecord saved = patientRecordRepository.save(record);
        logAudit(saved, "UPDATED", "Medical record information updated");

        return saved;
    }

    // ==================== FR17.6: Emergency Contacts ====================

    /**
     * Adds an emergency contact to a patient's profile.
     */
    @Transactional
    public EmergencyContact addEmergencyContact(Long patientId, EmergencyContactRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        EmergencyContact contact = new EmergencyContact();
        contact.setPatient(patient);
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setRelationship(request.getRelationship());
        contact.setPhoneNumber(request.getPhoneNumber());
        contact.setAlternatePhone(request.getAlternatePhone());
        contact.setEmail(request.getEmail());
        contact.setAddress(request.getAddress());
        contact.setIsPrimary(request.getIsPrimary());
        contact.setPriority(request.getPriority());

        EmergencyContact saved = emergencyContactRepository.save(contact);

        // Log action if a record exists for audit purposes
        patientRecordRepository.findByPatientId(patientId).ifPresent(record -> logAudit(record, "CREATED",
                "Emergency contact added: " + contact.getFirstName() + " " + contact.getLastName()));

        return saved;
    }

    /**
     * Returns all emergency contacts for a patient, ordered by priority.
     */
    public List<EmergencyContact> getEmergencyContacts(Long patientId) {
        return emergencyContactRepository.findByPatientIdOrderByPriorityAsc(patientId);
    }

    /**
     * Removes an emergency contact and logs the action.
     */
    @Transactional
    public void deleteEmergencyContact(Long contactId) {
        EmergencyContact contact = emergencyContactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Emergency contact not found"));

        Long patientId = contact.getPatient().getId();
        emergencyContactRepository.delete(contact);

        patientRecordRepository.findByPatientId(patientId).ifPresent(record -> logAudit(record, "DELETED",
                "Emergency contact removed: " + contact.getFirstName() + " " + contact.getLastName()));
    }

    // ==================== FR17.9: Audit Trail ====================

    /**
     * Retrieves the history of actions performed on a specific patient record.
     */
    public List<AuditLog> getAuditTrail(Long patientRecordId) {
        return auditLogRepository.findByPatientRecordIdOrderByActionTimestampDesc(patientRecordId);
    }

    /**
     * Internal helper to log system actions for compliance and security.
     */
    private void logAudit(PatientRecord record, String action, String details) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            userRepository.findByUsername(username).ifPresent(user -> {
                AuditLog log = new AuditLog();
                log.setUser(user);
                log.setUserEmail(user.getEmail());
                log.setPatientRecord(record);
                log.setAction(action);
                log.setActionDetails(details);
                log.setActionTimestamp(LocalDateTime.now());
                auditLogRepository.save(log);
            });
        } catch (Exception e) {
            // Logging failure should not interrupt the main business transaction
            System.err.println("Audit logging failed: " + e.getMessage());
        }
    }
}