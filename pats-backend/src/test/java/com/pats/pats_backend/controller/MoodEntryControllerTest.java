package com.pats.pats_backend.controller;

import com.pats.pats_backend.entity.MoodEntry;
import com.pats.pats_backend.entity.Patient;
import com.pats.pats_backend.security.CustomUserDetailsService;
import com.pats.pats_backend.security.JwtUtil;
import com.pats.pats_backend.service.MoodEntryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MoodEntryController.class)
class MoodEntryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MoodEntryService moodEntryService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private MoodEntry testEntry;
    private Patient testPatient;

    @BeforeEach
    void setUp() {
        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setFirstName("John");
        testPatient.setLastName("Doe");

        testEntry = new MoodEntry();
        testEntry.setId(1L);
        testEntry.setPatient(testPatient);
        testEntry.setEmotionalRating(7);
        testEntry.setNotes("Feeling well");
        testEntry.setEntryTimestamp(LocalDateTime.now());
    }

    @Test
    @WithMockUser
    void submitCheckin_validRequest_returns200() throws Exception {
        when(moodEntryService.createMoodEntry(1L, 7, "Feeling well")).thenReturn(testEntry);

        mockMvc.perform(post("/api/mood/checkin")
                        .param("patientId", "1")
                        .param("rating", "7")
                        .param("notes", "Feeling well"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void submitCheckin_withoutNotes_returns200() throws Exception {
        when(moodEntryService.createMoodEntry(1L, 5, null)).thenReturn(testEntry);

        mockMvc.perform(post("/api/mood/checkin")
                        .param("patientId", "1")
                        .param("rating", "5"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void submitCheckin_alreadySubmittedToday_returns400() throws Exception {
        when(moodEntryService.createMoodEntry(anyLong(), anyInt(), any()))
                .thenThrow(new IllegalStateException("Check-in already submitted for today."));

        mockMvc.perform(post("/api/mood/checkin")
                        .param("patientId", "1")
                        .param("rating", "7"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Check-in already submitted for today."));
    }

    @Test
    @WithMockUser
    void submitCheckin_patientNotFound_returns400() throws Exception {
        when(moodEntryService.createMoodEntry(anyLong(), anyInt(), any()))
                .thenThrow(new NoSuchElementException("Patient not found"));

        mockMvc.perform(post("/api/mood/checkin")
                        .param("patientId", "999")
                        .param("rating", "7"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getHistory_existingPatient_returns200WithList() throws Exception {
        MoodEntry entry2 = new MoodEntry();
        entry2.setId(2L);
        entry2.setPatient(testPatient);
        entry2.setEmotionalRating(8);

        when(moodEntryService.getPatientHistory(1L)).thenReturn(Arrays.asList(testEntry, entry2));

        mockMvc.perform(get("/api/mood/history/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser
    void getHistory_emptyHistory_returns200WithEmptyList() throws Exception {
        when(moodEntryService.getPatientHistory(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/mood/history/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
