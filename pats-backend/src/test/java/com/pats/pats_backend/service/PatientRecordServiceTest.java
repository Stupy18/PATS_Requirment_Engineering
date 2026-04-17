package com.pats.pats_backend.service;

import com.pats.pats_backend.dto.EmergencyContactRequest;
import com.pats.pats_backend.dto.PatientRecordRequest;
import com.pats.pats_backend.entity.*;
import com.pats.pats_backend.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientRecordServiceTest {

    @Mock private PatientRecordRepository patientRecordRepository;
    @Mock private EmergencyContactRepository emergencyContactRepository;
    @Mock private AuditLogRepository auditLogRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private PatientRecordService service;

    private Patient testPatient;
    private PatientRecord testRecord;
    private PatientRecordRequest recordRequest;

    @BeforeEach
    void setUp() {
        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setFirstName("John");
        testPatient.setLastName("Doe");

        testRecord = new PatientRecord();
        testRecord.setId(1L);
        testRecord.setPatient(testPatient);
        testRecord.setCnp("1234567890123");
        testRecord.setIsActive(true);

        recordRequest = new PatientRecordRequest();
        recordRequest.setCnp("1234567890123");
        recordRequest.setOccupation("Engineer");
        recordRequest.setCity("Bucharest");
        recordRequest.setCounty("Ilfov");
        recordRequest.setPostalCode("010000");
    }

    // ==================== createPatientRecord ====================

    @Test
    void createPatientRecord_success_returnsSavedRecord() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(patientRecordRepository.findByPatientId(1L)).thenReturn(Optional.empty());
        when(patientRecordRepository.save(any(PatientRecord.class))).thenReturn(testRecord);

        PatientRecord result = service.createPatientRecord(1L, recordRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(patientRecordRepository).save(any(PatientRecord.class));
    }

    @Test
    void createPatientRecord_patientNotFound_throwsException() {
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.createPatientRecord(999L, recordRequest));
        assertEquals("Patient not found", ex.getMessage());
        verify(patientRecordRepository, never()).save(any());
    }

    @Test
    void createPatientRecord_alreadyExists_throwsException() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(patientRecordRepository.findByPatientId(1L)).thenReturn(Optional.of(testRecord));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.createPatientRecord(1L, recordRequest));
        assertTrue(ex.getMessage().contains("already exists"));
        verify(patientRecordRepository, never()).save(any());
    }

    // ==================== getPatientRecord ====================

    @Test
    void getPatientRecord_existingId_returnsRecord() {
        when(patientRecordRepository.findById(1L)).thenReturn(Optional.of(testRecord));

        PatientRecord result = service.getPatientRecord(1L);

        assertNotNull(result);
        assertEquals("1234567890123", result.getCnp());
    }

    @Test
    void getPatientRecord_notFound_throwsException() {
        when(patientRecordRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getPatientRecord(999L));
    }

    // ==================== getPatientRecordByPatientId ====================

    @Test
    void getPatientRecordByPatientId_found_returnsOptional() {
        when(patientRecordRepository.findByPatientId(1L)).thenReturn(Optional.of(testRecord));

        Optional<PatientRecord> result = service.getPatientRecordByPatientId(1L);

        assertTrue(result.isPresent());
        assertEquals(testRecord, result.get());
    }

    @Test
    void getPatientRecordByPatientId_notFound_returnsEmpty() {
        when(patientRecordRepository.findByPatientId(99L)).thenReturn(Optional.empty());

        Optional<PatientRecord> result = service.getPatientRecordByPatientId(99L);

        assertFalse(result.isPresent());
    }

    // ==================== updatePatientRecord ====================

    @Test
    void updatePatientRecord_success_updatesFields() {
        PatientRecordRequest updateRequest = new PatientRecordRequest();
        updateRequest.setCnp("9876543210987");
        updateRequest.setOccupation("Doctor");
        updateRequest.setCity("Cluj");

        when(patientRecordRepository.findById(1L)).thenReturn(Optional.of(testRecord));
        when(patientRecordRepository.save(any(PatientRecord.class))).thenReturn(testRecord);

        PatientRecord result = service.updatePatientRecord(1L, updateRequest);

        assertNotNull(result);
        verify(patientRecordRepository).save(any(PatientRecord.class));
    }

    @Test
    void updatePatientRecord_notFound_throwsException() {
        when(patientRecordRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.updatePatientRecord(999L, recordRequest));
        verify(patientRecordRepository, never()).save(any());
    }

    // ==================== Emergency Contacts ====================

    @Test
    void addEmergencyContact_success_returnsSavedContact() {
        EmergencyContactRequest request = new EmergencyContactRequest();
        request.setFirstName("Maria");
        request.setLastName("Doe");
        request.setRelationship("Spouse");
        request.setPhoneNumber("0711111111");

        EmergencyContact saved = new EmergencyContact();
        saved.setId(1L);
        saved.setPatient(testPatient);
        saved.setFirstName("Maria");
        saved.setLastName("Doe");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(emergencyContactRepository.save(any(EmergencyContact.class))).thenReturn(saved);
        when(patientRecordRepository.findByPatientId(1L)).thenReturn(Optional.empty());

        EmergencyContact result = service.addEmergencyContact(1L, request);

        assertNotNull(result);
        assertEquals("Maria", result.getFirstName());
        verify(emergencyContactRepository).save(any(EmergencyContact.class));
    }

    @Test
    void addEmergencyContact_patientNotFound_throwsException() {
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.addEmergencyContact(999L, new EmergencyContactRequest()));
        verify(emergencyContactRepository, never()).save(any());
    }

    @Test
    void getEmergencyContacts_returnsOrderedContacts() {
        EmergencyContact c1 = new EmergencyContact();
        c1.setId(1L);
        c1.setPriority(1);
        EmergencyContact c2 = new EmergencyContact();
        c2.setId(2L);
        c2.setPriority(2);

        when(emergencyContactRepository.findByPatientIdOrderByPriorityAsc(1L))
                .thenReturn(Arrays.asList(c1, c2));

        List<EmergencyContact> result = service.getEmergencyContacts(1L);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getPriority());
    }

    @Test
    void deleteEmergencyContact_success_deletesContact() {
        EmergencyContact contact = new EmergencyContact();
        contact.setId(1L);
        contact.setFirstName("Maria");
        contact.setLastName("Doe");
        contact.setPatient(testPatient);

        when(emergencyContactRepository.findById(1L)).thenReturn(Optional.of(contact));
        when(patientRecordRepository.findByPatientId(1L)).thenReturn(Optional.empty());

        service.deleteEmergencyContact(1L);

        verify(emergencyContactRepository).delete(contact);
    }

    @Test
    void deleteEmergencyContact_notFound_throwsException() {
        when(emergencyContactRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.deleteEmergencyContact(999L));
        verify(emergencyContactRepository, never()).delete(any());
    }

    // ==================== getAuditTrail ====================

    @Test
    void getAuditTrail_returnsLogs() {
        AuditLog log1 = new AuditLog();
        log1.setId(1L);
        log1.setAction("CREATED");
        AuditLog log2 = new AuditLog();
        log2.setId(2L);
        log2.setAction("UPDATED");

        when(auditLogRepository.findByPatientRecordIdOrderByActionTimestampDesc(1L))
                .thenReturn(Arrays.asList(log1, log2));

        List<AuditLog> result = service.getAuditTrail(1L);

        assertEquals(2, result.size());
        assertEquals("CREATED", result.get(0).getAction());
    }

    @Test
    void getAuditTrail_noLogs_returnsEmpty() {
        when(auditLogRepository.findByPatientRecordIdOrderByActionTimestampDesc(1L))
                .thenReturn(List.of());

        List<AuditLog> result = service.getAuditTrail(1L);

        assertTrue(result.isEmpty());
    }
}
