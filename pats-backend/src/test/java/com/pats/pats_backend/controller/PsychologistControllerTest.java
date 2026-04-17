package com.pats.pats_backend.controller;

import com.pats.pats_backend.entity.Psychologist;
import com.pats.pats_backend.repo.PsychologistRepository;
import com.pats.pats_backend.security.CustomUserDetailsService;
import com.pats.pats_backend.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PsychologistController.class)
class PsychologistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PsychologistRepository psychologistRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser
    void getAllPsychologists_returns200WithList() throws Exception {
        Psychologist p1 = new Psychologist();
        p1.setId(1L);
        p1.setFirstName("Dr.");
        p1.setLastName("Smith");

        Psychologist p2 = new Psychologist();
        p2.setId(2L);
        p2.setFirstName("Dr.");
        p2.setLastName("Jones");

        when(psychologistRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/psychologists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].lastName").value("Smith"))
                .andExpect(jsonPath("$[1].lastName").value("Jones"));
    }

    @Test
    @WithMockUser
    void getAllPsychologists_emptyList_returns200WithEmptyArray() throws Exception {
        when(psychologistRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/psychologists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

}
