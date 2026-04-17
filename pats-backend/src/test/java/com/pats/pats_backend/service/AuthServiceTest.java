package com.pats.pats_backend.service;

import com.pats.pats_backend.dto.AuthResponse;
import com.pats.pats_backend.dto.LoginRequest;
import com.pats.pats_backend.dto.RegisterRequest;
import com.pats.pats_backend.entity.Patient;
import com.pats.pats_backend.entity.Psychologist;
import com.pats.pats_backend.entity.User;
import com.pats.pats_backend.enums.UserRole;
import com.pats.pats_backend.repo.PatientRepository;
import com.pats.pats_backend.repo.PsychologistRepository;
import com.pats.pats_backend.repo.UserRepository;
import com.pats.pats_backend.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private PsychologistRepository psychologistRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User patientUser;
    private Patient patient;
    private User psychologistUser;
    private Psychologist psychologist;

    @BeforeEach
    void setUp() {
        patientUser = new User();
        patientUser.setId(1L);
        patientUser.setUsername("john");
        patientUser.setEmail("john@example.com");
        patientUser.setRole(UserRole.PATIENT);

        patient = new Patient();
        patient.setId(10L);
        patient.setUser(patientUser);
        patient.setFirstName("John");
        patient.setLastName("Doe");

        psychologistUser = new User();
        psychologistUser.setId(2L);
        psychologistUser.setUsername("dr.smith");
        psychologistUser.setEmail("smith@example.com");
        psychologistUser.setRole(UserRole.PSYCHOLOGIST);

        psychologist = new Psychologist();
        psychologist.setId(20L);
        psychologist.setUser(psychologistUser);
        psychologist.setFirstName("Dr.");
        psychologist.setLastName("Smith");
    }

    // ==================== login ====================

    @Test
    void login_patient_returnsToken() {
        LoginRequest request = new LoginRequest("john", "pass");
        when(authenticationManager.authenticate(any())).thenReturn(
                new UsernamePasswordAuthenticationToken("john", "pass"));
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(patientUser));
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.of(patient));
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong(), anyString(), anyLong()))
                .thenReturn("mocked-jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        verify(authenticationManager).authenticate(any());
        verify(jwtUtil).generateToken(eq("john"), eq("PATIENT"), eq(1L), eq("john@example.com"), eq(10L));
    }

    @Test
    void login_psychologist_returnsToken() {
        LoginRequest request = new LoginRequest("dr.smith", "pass");
        when(authenticationManager.authenticate(any())).thenReturn(
                new UsernamePasswordAuthenticationToken("dr.smith", "pass"));
        when(userRepository.findByUsername("dr.smith")).thenReturn(Optional.of(psychologistUser));
        when(psychologistRepository.findByUserId(2L)).thenReturn(Optional.of(psychologist));
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong(), anyString(), anyLong()))
                .thenReturn("psych-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        verify(jwtUtil).generateToken(eq("dr.smith"), eq("PSYCHOLOGIST"), eq(2L), eq("smith@example.com"), eq(20L));
    }

    @Test
    void login_invalidCredentials_throwsException() {
        LoginRequest request = new LoginRequest("john", "wrongpassword");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void login_patientWithNoProfile_usesNullProfileId() {
        LoginRequest request = new LoginRequest("john", "pass");
        when(authenticationManager.authenticate(any())).thenReturn(
                new UsernamePasswordAuthenticationToken("john", "pass"));
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(patientUser));
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong(), anyString(), isNull()))
                .thenReturn("token-no-profile");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        verify(jwtUtil).generateToken(eq("john"), eq("PATIENT"), eq(1L), eq("john@example.com"), isNull());
    }

    // ==================== register ====================

    @Test
    void register_newPatient_success() {
        RegisterRequest request = new RegisterRequest(
                "newpatient", "new@example.com", "password123", "PATIENT", "Jane", "Doe", "0700000000");
        when(userRepository.existsByUsername("newpatient")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

        User savedUser = new User();
        savedUser.setId(5L);
        savedUser.setUsername("newpatient");
        savedUser.setEmail("new@example.com");
        savedUser.setRole(UserRole.PATIENT);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");

        Patient savedPatient = new Patient();
        savedPatient.setId(50L);
        savedPatient.setUser(savedUser);
        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong(), anyString(), anyLong()))
                .thenReturn("register-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("register-token", response.getToken());
        verify(patientRepository).save(any(Patient.class));
        verify(psychologistRepository, never()).save(any());
    }

    @Test
    void register_newPsychologist_success() {
        RegisterRequest request = new RegisterRequest(
                "newpsych", "psych@example.com", "password123", "PSYCHOLOGIST", "Dr.", "Brown", null);
        when(userRepository.existsByUsername("newpsych")).thenReturn(false);
        when(userRepository.existsByEmail("psych@example.com")).thenReturn(false);

        User savedUser = new User();
        savedUser.setId(6L);
        savedUser.setUsername("newpsych");
        savedUser.setEmail("psych@example.com");
        savedUser.setRole(UserRole.PSYCHOLOGIST);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");

        Psychologist savedPsych = new Psychologist();
        savedPsych.setId(60L);
        savedPsych.setUser(savedUser);
        when(psychologistRepository.save(any(Psychologist.class))).thenReturn(savedPsych);
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong(), anyString(), anyLong()))
                .thenReturn("psych-register-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        verify(psychologistRepository).save(any(Psychologist.class));
        verify(patientRepository, never()).save(any());
    }

    @Test
    void register_duplicateUsername_throwsException() {
        RegisterRequest request = new RegisterRequest(
                "john", "other@example.com", "pass", "PATIENT", "A", "B", null);
        when(userRepository.existsByUsername("john")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertTrue(ex.getMessage().contains("Username is already taken"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_duplicateEmail_throwsException() {
        RegisterRequest request = new RegisterRequest(
                "uniqueuser", "john@example.com", "pass", "PATIENT", "A", "B", null);
        when(userRepository.existsByUsername("uniqueuser")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertTrue(ex.getMessage().contains("Email is already in use"));
        verify(userRepository, never()).save(any());
    }
}
