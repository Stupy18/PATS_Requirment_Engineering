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

// ==================== FR17.1: Patient Record ====================
@Service
@RequiredArgsConstructor
public class PatientRecordService {

    private final PatientRecordRepository patientRecordRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final AuditLogRepository auditLogRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Transactional
    public PatientRecord createPatientRecord(PatientRecordRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
            .orElseThrow(() -> new RuntimeException("Patient not found"));

        PatientRecord record = new PatientRecord();
        record.setPatient(patient);
        record.setFirstName(request.getFirstName());
        record.setLastName(request.getLastName());
        record.setDateOfBirth(request.getDateOfBirth());
        record.setGender(request.getGender());
        record.setCnp(request.getCnp());
        record.setEmail(request.getEmail());
        record.setPhoneNumber(request.getPhoneNumber());
        record.setAlternatePhone(request.getAlternatePhone());
        record.setStreetAddress(request.getStreetAddress());
        record.setCity(request.getCity());
        record.setCounty(request.getCounty());
        record.setPostalCode(request.getPostalCode());
        record.setOccupation(request.getOccupation());

        PatientRecord saved = patientRecordRepository.save(record);
        
        // FR17.9: Log creation
        logAudit(saved, "CREATED", "Patient record created");
        
        return saved;
    }

    public PatientRecord getPatientRecord(Long id) {
        PatientRecord record = patientRecordRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Patient record not found"));
        
        // FR17.9: Log access
        logAudit(record, "VIEWED", "Patient record accessed");
        
        return record;
    }

    @Transactional
    public PatientRecord updatePatientRecord(Long id, PatientRecordRequest request) {
        PatientRecord record = patientRecordRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Patient record not found"));

        record.setFirstName(request.getFirstName());
        record.setLastName(request.getLastName());
        record.setEmail(request.getEmail());
        record.setPhoneNumber(request.getPhoneNumber());
        record.setAlternatePhone(request.getAlternatePhone());
        record.setCity(request.getCity());
        record.setCounty(request.getCounty());

        PatientRecord saved = patientRecordRepository.save(record);
        
        // FR17.9: Log update
        logAudit(saved, "UPDATED", "Patient record updated");
        
        return saved;
    }

    // ==================== FR17.6: Emergency Contacts ====================

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
        
        // FR17.9: Log creation
        PatientRecord record = patientRecordRepository.findByPatientId(patientId).orElse(null);
        if (record != null) {
            logAudit(record, "CREATED", "Emergency contact added: " + 
                contact.getFirstName() + " " + contact.getLastName());
        }
        
        return saved;
    }

    public List<EmergencyContact> getEmergencyContacts(Long patientId) {
        return emergencyContactRepository.findByPatientIdOrderByPriorityAsc(patientId);
    }

    @Transactional
    public void deleteEmergencyContact(Long contactId) {
        EmergencyContact contact = emergencyContactRepository.findById(contactId)
            .orElseThrow(() -> new RuntimeException("Emergency contact not found"));
        
        PatientRecord record = patientRecordRepository.findByPatientId(contact.getPatient().getId()).orElse(null);
        if (record != null) {
            logAudit(record, "DELETED", "Emergency contact removed: " + 
                contact.getFirstName() + " " + contact.getLastName());
        }
        
        emergencyContactRepository.delete(contact);
    }

    // ==================== FR17.9: Audit Trail ====================

    public List<AuditLog> getAuditTrail(Long patientRecordId) {
        return auditLogRepository.findByPatientRecordIdOrderByActionTimestampDesc(patientRecordId);
    }

    private void logAudit(PatientRecord record, String action, String details) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username).orElse(null);
            
            if (user != null) {
                AuditLog log = new AuditLog();
                log.setUser(user);
                log.setUserEmail(user.getEmail());
                log.setPatientRecord(record);
                log.setAction(action);
                log.setActionDetails(details);
                log.setActionTimestamp(LocalDateTime.now());
                
                auditLogRepository.save(log);
            }
        } catch (Exception e) {
            // Don't fail the main operation if audit logging fails
            System.err.println("Failed to log audit: " + e.getMessage());
        }
    }
}