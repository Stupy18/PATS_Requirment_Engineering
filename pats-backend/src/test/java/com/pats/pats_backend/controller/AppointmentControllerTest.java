package com.pats.pats_backend.controller;

import com.pats.pats_backend.entity.Appointment;
import com.pats.pats_backend.entity.AppointmentHistory;
import com.pats.pats_backend.entity.Patient;
import com.pats.pats_backend.entity.Psychologist;
import com.pats.pats_backend.enums.AppointmentStatus;
import com.pats.pats_backend.enums.AttendanceStatus;
import com.pats.pats_backend.repo.PatientRepository;
import com.pats.pats_backend.repo.PsychologistRepository;
import com.pats.pats_backend.security.CustomUserDetailsService;
import com.pats.pats_backend.security.JwtUtil;
import com.pats.pats_backend.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentService appointmentService;

    @MockitoBean
    private PsychologistRepository psychologistRepository;

    @MockitoBean
    private PatientRepository patientRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private Appointment testAppointment;
    private Psychologist testPsychologist;
    private Patient testPatient;

    @BeforeEach
    void setUp() {
        testPsychologist = new Psychologist();
        testPsychologist.setId(1L);
        testPsychologist.setFirstName("Dr.");
        testPsychologist.setLastName("Smith");

        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setFirstName("John");
        testPatient.setLastName("Doe");

        testAppointment = new Appointment();
        testAppointment.setId(1L);
        testAppointment.setPsychologist(testPsychologist);
        testAppointment.setPatient(testPatient);
        testAppointment.setAppointmentDateTime(LocalDateTime.now().plusDays(5));
        testAppointment.setDurationMinutes(60);
        testAppointment.setStatus(AppointmentStatus.SCHEDULED);
    }

    @Test
    @WithMockUser
    void getAvailableSlots_validRequest_returns200() throws Exception {
        when(appointmentService.getAvailableSlots(eq(1L), any(), any()))
                .thenReturn(List.of(testAppointment));

        mockMvc.perform(get("/api/appointments/available-slots/1")
                        .param("startTime", LocalDateTime.now().toString())
                        .param("endTime", LocalDateTime.now().plusDays(7).toString()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void bookAppointment_validRequest_returns201() throws Exception {
        when(psychologistRepository.findById(1L)).thenReturn(Optional.of(testPsychologist));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(appointmentService.bookAppointment(any(Appointment.class))).thenReturn(testAppointment);

        String json = """
                {
                  "psychologistId": 1,
                  "patientId": 1,
                  "appointmentDateTime": "2026-05-10T10:00:00",
                  "durationMinutes": 60
                }
                """;

        mockMvc.perform(post("/api/appointments/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void bookAppointment_psychologistNotFound_returns404() throws Exception {
        when(psychologistRepository.findById(999L)).thenReturn(Optional.empty());
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));

        String json = """
                {
                  "psychologistId": 999,
                  "patientId": 1,
                  "appointmentDateTime": "2026-05-10T10:00:00"
                }
                """;

        mockMvc.perform(post("/api/appointments/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void bookAppointment_doubleBooking_returns409() throws Exception {
        when(psychologistRepository.findById(1L)).thenReturn(Optional.of(testPsychologist));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(appointmentService.bookAppointment(any(Appointment.class)))
                .thenThrow(new IllegalStateException("Time slot already booked"));

        String json = """
                {
                  "psychologistId": 1,
                  "patientId": 1,
                  "appointmentDateTime": "2026-05-10T10:00:00"
                }
                """;

        mockMvc.perform(post("/api/appointments/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    void rescheduleAppointment_validRequest_returns200() throws Exception {
        when(appointmentService.rescheduleAppointment(eq(1L), any())).thenReturn(testAppointment);

        String json = """
                {
                  "appointmentId": 1,
                  "newDateTime": "2026-06-01T10:00:00"
                }
                """;

        mockMvc.perform(put("/api/appointments/reschedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void rescheduleAppointment_insufficientNotice_returns409() throws Exception {
        when(appointmentService.rescheduleAppointment(any(), any()))
                .thenThrow(new IllegalStateException("Cannot reschedule within 24 hours"));

        String json = """
                {
                  "appointmentId": 1,
                  "newDateTime": "2026-06-01T10:00:00"
                }
                """;

        mockMvc.perform(put("/api/appointments/reschedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    void cancelAppointment_validRequest_returns200() throws Exception {
        doNothing().when(appointmentService).cancelAppointment(eq(1L), anyString());

        String json = """
                {
                  "appointmentId": 1,
                  "cancellationReason": "Patient request"
                }
                """;

        mockMvc.perform(post("/api/appointments/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Appointment cancelled successfully"));
    }

    @Test
    @WithMockUser
    void getPatientAppointmentHistory_returns200() throws Exception {
        AppointmentHistory history = new AppointmentHistory();
        history.setId(1L);
        history.setAttendanceStatus(AttendanceStatus.ATTENDED);
        when(appointmentService.getPatientAppointmentHistory(1L)).thenReturn(List.of(history));

        mockMvc.perform(get("/api/appointments/history/patient/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getPsychologistAppointmentHistory_returns200() throws Exception {
        when(appointmentService.getPsychologistAppointmentHistory(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/appointments/history/psychologist/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getAppointmentsByPsychologist_returns200() throws Exception {
        when(appointmentService.getAppointmentsByPsychologist(1L)).thenReturn(List.of(testAppointment));

        mockMvc.perform(get("/api/appointments/psychologist/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getAppointmentsByPatient_returns200() throws Exception {
        when(appointmentService.getAppointmentsByPatient(1L)).thenReturn(List.of(testAppointment));

        mockMvc.perform(get("/api/appointments/patient/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getAppointment_existingId_returns200() throws Exception {
        when(appointmentService.getAppointment(1L)).thenReturn(Optional.of(testAppointment));

        mockMvc.perform(get("/api/appointments/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getAppointment_notFound_returns404() throws Exception {
        when(appointmentService.getAppointment(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/appointments/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void recordAppointmentHistory_returns201() throws Exception {
        AppointmentHistory history = new AppointmentHistory();
        history.setId(1L);
        when(appointmentService.recordAppointmentHistory(eq(1L), eq(AttendanceStatus.ATTENDED), any(), any()))
                .thenReturn(history);

        mockMvc.perform(post("/api/appointments/history/record")
                        .param("appointmentId", "1")
                        .param("attendanceStatus", "ATTENDED"))
                .andExpect(status().isCreated());
    }
}
