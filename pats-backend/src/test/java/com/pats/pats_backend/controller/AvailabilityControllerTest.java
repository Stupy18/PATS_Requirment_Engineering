package com.pats.pats_backend.controller;

import com.pats.pats_backend.entity.Availability;
import com.pats.pats_backend.entity.Psychologist;
import com.pats.pats_backend.repo.PsychologistRepository;
import com.pats.pats_backend.security.CustomUserDetailsService;
import com.pats.pats_backend.security.JwtUtil;
import com.pats.pats_backend.service.AvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvailabilityController.class)
class AvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AvailabilityService availabilityService;

    @MockitoBean
    private PsychologistRepository psychologistRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private Psychologist testPsychologist;
    private Availability testAvailability;

    @BeforeEach
    void setUp() {
        testPsychologist = new Psychologist();
        testPsychologist.setId(1L);
        testPsychologist.setFirstName("Dr.");
        testPsychologist.setLastName("Smith");

        testAvailability = new Availability();
        testAvailability.setId(1L);
        testAvailability.setPsychologist(testPsychologist);
        testAvailability.setDayOfWeek(DayOfWeek.MONDAY);
        testAvailability.setStartTime(LocalTime.of(9, 0));
        testAvailability.setEndTime(LocalTime.of(17, 0));
        testAvailability.setIsAvailable(true);
    }

    @Test
    @WithMockUser
    void createAvailability_validRequest_returns201() throws Exception {
        when(psychologistRepository.findById(1L)).thenReturn(Optional.of(testPsychologist));
        when(availabilityService.createAvailability(any(Availability.class))).thenReturn(testAvailability);

        String json = """
                {
                  "psychologistId": 1,
                  "dayOfWeek": "MONDAY",
                  "startTime": "09:00:00",
                  "endTime": "17:00:00",
                  "isAvailable": true
                }
                """;

        mockMvc.perform(post("/api/availability/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void createAvailability_psychologistNotFound_returns404() throws Exception {
        when(psychologistRepository.findById(999L)).thenReturn(Optional.empty());

        String json = """
                {
                  "psychologistId": 999,
                  "dayOfWeek": "MONDAY",
                  "startTime": "09:00:00",
                  "endTime": "17:00:00"
                }
                """;

        mockMvc.perform(post("/api/availability/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateAvailability_validRequest_returns200() throws Exception {
        when(availabilityService.updateAvailability(eq(1L), any(Availability.class)))
                .thenReturn(testAvailability);

        String json = """
                {
                  "dayOfWeek": "TUESDAY",
                  "startTime": "10:00:00",
                  "endTime": "18:00:00",
                  "isAvailable": true
                }
                """;

        mockMvc.perform(put("/api/availability/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void updateAvailability_notFound_returns400() throws Exception {
        when(availabilityService.updateAvailability(eq(999L), any()))
                .thenThrow(new IllegalArgumentException("Availability not found"));

        String json = """
                {
                  "dayOfWeek": "TUESDAY"
                }
                """;

        mockMvc.perform(put("/api/availability/update/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void deleteAvailability_validId_returns200() throws Exception {
        doNothing().when(availabilityService).deleteAvailability(1L);

        mockMvc.perform(delete("/api/availability/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Availability deleted successfully"));
    }

    @Test
    @WithMockUser
    void getAvailabilityByDay_validRequest_returns200() throws Exception {
        when(availabilityService.getPsychologistAvailabilityByDay(1L, DayOfWeek.MONDAY))
                .thenReturn(List.of(testAvailability));

        mockMvc.perform(get("/api/availability/psychologist/1/day/MONDAY"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getAvailabilityByDay_invalidDay_returns400() throws Exception {
        mockMvc.perform(get("/api/availability/psychologist/1/day/NOTADAY"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getAvailabilityByDate_validRequest_returns200() throws Exception {
        when(availabilityService.getPsychologistAvailabilityByDate(eq(1L), any(LocalDate.class)))
                .thenReturn(List.of(testAvailability));

        mockMvc.perform(get("/api/availability/psychologist/1/date/2026-05-01"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getAllAvailableSlots_returns200() throws Exception {
        when(availabilityService.getAllAvailableSlots(1L)).thenReturn(List.of(testAvailability));

        mockMvc.perform(get("/api/availability/psychologist/1/available"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getAllAvailabilities_returns200() throws Exception {
        when(availabilityService.getPsychologistAvailabilities(1L)).thenReturn(List.of(testAvailability));

        mockMvc.perform(get("/api/availability/psychologist/1/all"))
                .andExpect(status().isOk());
    }
}
