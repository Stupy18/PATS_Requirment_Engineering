package com.pats.pats_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pats.pats_backend.config.SecurityConfig;
import com.pats.pats_backend.entity.Patient;
import com.pats.pats_backend.entity.User;
import com.pats.pats_backend.enums.UserRole;
import com.pats.pats_backend.security.CustomUserDetailsService;
import com.pats.pats_backend.security.JwtUtil;
import com.pats.pats_backend.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
@Import({SecurityConfig.class,GlobalExceptionHandler.class})
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;


    private ObjectMapper objectMapper;
    private Patient testPatient;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        User user = new User();
        user.setId(1L);
        user.setUsername("patient1");
        user.setRole(UserRole.PATIENT);

        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setUser(user);
        testPatient.setFirstName("John");
        testPatient.setLastName("Doe");
        testPatient.setPhoneNumber("0700000001");
    }

    @Test
    void getAllPatients_asPsychologist_returns200() throws Exception {
        Patient p2 = new Patient();
        p2.setId(2L);
        p2.setFirstName("Jane");
        p2.setLastName("Smith");
        when(patientService.getAllPatients()).thenReturn(Arrays.asList(testPatient, p2));

        mockMvc.perform(get("/api/patients")
                .with(user("testuser").roles("PSYCHOLOGIST")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllPatients_asPatient_returns403() throws Exception {
        mockMvc.perform(get("/api/patients")
                .with(user("testuser").roles("PATIENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPatientById_existingId_returns200() throws Exception {
        when(patientService.getPatientById(1L)).thenReturn(testPatient);

        mockMvc.perform(get("/api/patients/1")
                        .with(user("testuser").roles("PSYCHOLOGIST")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void getPatientById_notFound_returns500() throws Exception {
        when(patientService.getPatientById(999L)).thenThrow(new RuntimeException("Patient not found"));

        mockMvc.perform(get("/api/patients/999")
                        .with(user("testuser").roles("PSYCHOLOGIST")))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void createPatient_validRequest_returns201() throws Exception {
        when(patientService.createPatient(any(Patient.class), anyString(), anyString(), anyString()))
                .thenReturn(testPatient);

        String json = """
                {
                  "username": "newpatient",
                  "email": "new@example.com",
                  "password": "password123",
                  "firstName": "Jane",
                  "lastName": "Smith",
                  "phoneNumber": "0711111111"
                }
                """;

        mockMvc.perform(post("/api/patients")
                        .with(user("testuser").roles("PSYCHOLOGIST"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void createPatient_asPatient_returns403() throws Exception {
        String json = """
                {
                  "username": "newpatient",
                  "email": "new@example.com",
                  "password": "password123",
                  "firstName": "Jane",
                  "lastName": "Smith",
                  "phoneNumber": "0711111111"
                }
                """;

        mockMvc.perform(post("/api/patients")
                        .with(user("testuser").roles("PATIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void updatePatient_validRequest_returns200() throws Exception {
        when(patientService.updatePatient(eq(1L), any(Patient.class))).thenReturn(testPatient);

        String json = """
                {
                  "firstName": "Updated",
                  "lastName": "Name",
                  "phoneNumber": "0799999999"
                }
                """;

        mockMvc.perform(put("/api/patients/1")
                        .with(user("testuser").roles("PSYCHOLOGIST"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void searchPatients_returnsMatchingResults() throws Exception {
        when(patientService.searchPatients("john")).thenReturn(List.of(testPatient));

        mockMvc.perform(get("/api/patients/search").param("q", "john")
                        .with(user("testuser").roles("PSYCHOLOGIST")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void searchPatients_noResults_returnsEmptyList() throws Exception {
        when(patientService.searchPatients("xyz")).thenReturn(List.of());

        mockMvc.perform(get("/api/patients/search").param("q", "xyz")
                        .with(user("testuser").roles("PSYCHOLOGIST")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
