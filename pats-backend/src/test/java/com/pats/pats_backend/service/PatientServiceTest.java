package com.pats.pats_backend.service;

import com.pats.pats_backend.entity.Patient;
import com.pats.pats_backend.entity.User;
import com.pats.pats_backend.enums.UserRole;
import com.pats.pats_backend.repo.PatientRepository;
import com.pats.pats_backend.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock private PatientRepository patientRepository;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PatientService patientService;

    private Patient testPatient;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("patient1");
        testUser.setEmail("patient1@example.com");
        testUser.setRole(UserRole.PATIENT);
        testUser.setActive(true);

        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setUser(testUser);
        testPatient.setFirstName("John");
        testPatient.setLastName("Doe");
        testPatient.setPhoneNumber("0700000001");
    }

    @Test
    void getAllPatients_returnsAllPatients() {
        Patient p2 = new Patient();
        p2.setId(2L);
        p2.setFirstName("Jane");
        p2.setLastName("Smith");
        when(patientRepository.findAll()).thenReturn(Arrays.asList(testPatient, p2));

        List<Patient> result = patientService.getAllPatients();

        assertEquals(2, result.size());
        verify(patientRepository).findAll();
    }

    @Test
    void getAllPatients_emptyList_returnsEmpty() {
        when(patientRepository.findAll()).thenReturn(Collections.emptyList());

        List<Patient> result = patientService.getAllPatients();

        assertTrue(result.isEmpty());
    }

    @Test
    void getPatientById_existingId_returnsPatient() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));

        Patient result = patientService.getPatientById(1L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
    }

    @Test
    void getPatientById_nonExistingId_throwsException() {
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> patientService.getPatientById(999L));
        assertTrue(ex.getMessage().contains("999"));
    }

    @Test
    void createPatient_success_returnsPatient() {
        Patient patientData = new Patient();
        patientData.setFirstName("New");
        patientData.setLastName("Patient");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("hashed");

        User savedUser = new User();
        savedUser.setId(10L);
        savedUser.setRole(UserRole.PATIENT);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        Patient savedPatient = new Patient();
        savedPatient.setId(10L);
        savedPatient.setUser(savedUser);
        savedPatient.setFirstName("New");
        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        Patient result = patientService.createPatient(patientData, "newuser", "new@example.com", "pass123");

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(userRepository).save(any(User.class));
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void createPatient_usernameExists_throwsException() {
        when(userRepository.existsByUsername("existing")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> patientService.createPatient(new Patient(), "existing", "new@example.com", "pass"));
        assertTrue(ex.getMessage().contains("existing"));
        verify(patientRepository, never()).save(any());
    }

    @Test
    void createPatient_emailExists_throwsException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> patientService.createPatient(new Patient(), "newuser", "taken@example.com", "pass"));
        assertTrue(ex.getMessage().contains("taken@example.com"));
        verify(patientRepository, never()).save(any());
    }

    @Test
    void updatePatient_success_updatesFields() {
        Patient updated = new Patient();
        updated.setFirstName("UpdatedFirst");
        updated.setLastName("UpdatedLast");
        updated.setPhoneNumber("0799999999");
        updated.setCity("Bucharest");
        updated.setCountry("Romania");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);

        Patient result = patientService.updatePatient(1L, updated);

        assertNotNull(result);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void updatePatient_notFound_throwsException() {
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> patientService.updatePatient(999L, new Patient()));
        verify(patientRepository, never()).save(any());
    }

    @Test
    void searchPatients_matchingFirstName_returnsResults() {
        Patient p2 = new Patient();
        p2.setId(2L);
        p2.setFirstName("Jane");
        p2.setLastName("Smith");
        when(patientRepository.findAll()).thenReturn(Arrays.asList(testPatient, p2));

        List<Patient> result = patientService.searchPatients("jo");

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    void searchPatients_matchingLastName_returnsResults() {
        when(patientRepository.findAll()).thenReturn(List.of(testPatient));

        List<Patient> result = patientService.searchPatients("doe");

        assertEquals(1, result.size());
    }

    @Test
    void searchPatients_noMatch_returnsEmpty() {
        when(patientRepository.findAll()).thenReturn(List.of(testPatient));

        List<Patient> result = patientService.searchPatients("xyz");

        assertTrue(result.isEmpty());
    }

    @Test
    void searchPatients_caseInsensitive_returnsResults() {
        when(patientRepository.findAll()).thenReturn(List.of(testPatient));

        List<Patient> result = patientService.searchPatients("JOHN");

        assertEquals(1, result.size());
    }
}
