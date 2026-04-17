package com.pats.pats_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pats.pats_backend.config.SecurityConfig;
import com.pats.pats_backend.dto.EmergencyContactRequest;
import com.pats.pats_backend.dto.PatientRecordRequest;
import com.pats.pats_backend.entity.*;
import com.pats.pats_backend.security.CustomUserDetailsService;
import com.pats.pats_backend.security.JwtUtil;
import com.pats.pats_backend.service.PatientRecordService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientRecordController.class)
@Import({SecurityConfig.class,GlobalExceptionHandler.class})
class PatientRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientRecordService service;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private ObjectMapper objectMapper;
    private PatientRecord testRecord;
    private Patient testPatient;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setFirstName("John");
        testPatient.setLastName("Doe");

        testRecord = new PatientRecord();
        testRecord.setId(1L);
        testRecord.setPatient(testPatient);
        testRecord.setCnp("1234567890123");
        testRecord.setIsActive(true);
    }

    @Test
    void createRecord_validRequest_returns201() throws Exception {
        when(service.createPatientRecord(eq(1L), any(PatientRecordRequest.class))).thenReturn(testRecord);

        String json = """
                {
                  "cnp": "1234567890123",
                  "occupation": "Engineer",
                  "city": "Bucharest"
                }
                """;

        mockMvc.perform(post("/api/emr/records/patient/1")
                        .with(user("testuser").roles("PSYCHOLOGIST"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void createRecord_asPatient_returns403() throws Exception {
        String json = "{\"cnp\": \"1234567890123\"}";

        mockMvc.perform(post("/api/emr/records/patient/1")
                        .with(user("testuser").roles("PATIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void getRecord_existingId_returns200() throws Exception {
        when(service.getPatientRecord(1L)).thenReturn(testRecord);

        mockMvc.perform(get("/api/emr/records/1")
                        .with(user("testuser").roles("PSYCHOLOGIST")))
                .andExpect(status().isOk());
    }

    @Test
    void getRecord_asPatient_returns200() throws Exception {
        when(service.getPatientRecord(1L)).thenReturn(testRecord);

        mockMvc.perform(get("/api/emr/records/1")
                        .with(user("testuser").roles("PATIENT")))
                .andExpect(status().isOk());
    }

    @Test
    void getRecordByPatientId_found_returns200() throws Exception {
        when(service.getPatientRecordByPatientId(1L)).thenReturn(Optional.of(testRecord));

        mockMvc.perform(get("/api/emr/records/patient/1")
                        .with(user("testuser").roles("PSYCHOLOGIST")))
                .andExpect(status().isOk());
    }

    @Test
    void getRecordByPatientId_notFound_returns404() throws Exception {
        when(service.getPatientRecordByPatientId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/emr/records/patient/999")
                        .with(user("testuser").roles("PSYCHOLOGIST")))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateRecord_validRequest_returns200() throws Exception {
        when(service.updatePatientRecord(eq(1L), any(PatientRecordRequest.class))).thenReturn(testRecord);

        String json = """
                {
                  "cnp": "1234567890123",
                  "occupation": "Doctor"
                }
                """;

        mockMvc.perform(put("/api/emr/records/1")
                        .with(user("testuser").roles("PSYCHOLOGIST"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void updateRecord_asPatient_returns403() throws Exception {
        String json = "{\"cnp\": \"1234567890123\"}";

        mockMvc.perform(put("/api/emr/records/1")
                        .with(user("testuser").roles("PATIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void addEmergencyContact_validRequest_returns201() throws Exception {
        EmergencyContact saved = new EmergencyContact();
        saved.setId(1L);
        saved.setFirstName("Maria");
        saved.setPatient(testPatient);

        when(service.addEmergencyContact(eq(1L), any(EmergencyContactRequest.class))).thenReturn(saved);

        String json = """
                {
                  "firstName": "Maria",
                  "lastName": "Doe",
                  "relationship": "Spouse",
                  "phoneNumber": "0711111111"
                }
                """;

        mockMvc.perform(post("/api/emr/patients/1/emergency-contacts")
                        .with(user("testuser").roles("PSYCHOLOGIST"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void getEmergencyContacts_returns200() throws Exception {
        EmergencyContact c1 = new EmergencyContact();
        c1.setId(1L);
        c1.setFirstName("Maria");

        when(service.getEmergencyContacts(1L)).thenReturn(Arrays.asList(c1));

        mockMvc.perform(get("/api/emr/patients/1/emergency-contacts")
                        .with(user("testuser").roles("PSYCHOLOGIST")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deleteEmergencyContact_returns204() throws Exception {
        doNothing().when(service).deleteEmergencyContact(1L);

        mockMvc.perform(delete("/api/emr/emergency-contacts/1")
                        .with(user("testuser").roles("PSYCHOLOGIST")))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAuditTrail_returns200() throws Exception {
        AuditLog log = new AuditLog();
        log.setId(1L);
        log.setAction("CREATED");

        when(service.getAuditTrail(1L)).thenReturn(List.of(log));

        mockMvc.perform(get("/api/emr/records/1/audit-trail")
                        .with(user("testuser").roles("PSYCHOLOGIST")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAuditTrail_asPatient_returns403() throws Exception {
        mockMvc.perform(get("/api/emr/records/1/audit-trail")
                        .with(user("testuser").roles("PATIENT")))
                .andExpect(status().isForbidden());
    }
}
