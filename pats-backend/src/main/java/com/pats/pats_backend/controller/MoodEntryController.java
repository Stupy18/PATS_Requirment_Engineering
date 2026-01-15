package com.pats.pats_backend.controller;

import com.pats.pats_backend.entity.MoodEntry;
import com.pats.pats_backend.service.MoodEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/mood")
public class MoodEntryController {
    @Autowired
    private MoodEntryService moodEntryService;

    // Endpoint for daily check-in
    @PostMapping("/checkin")
    public ResponseEntity<?> submitCheckin(@RequestParam Long patientId,
                                           @RequestParam Integer rating,
                                           @RequestParam(required = false) String notes) {
        try {
            MoodEntry entry = moodEntryService.createMoodEntry(patientId, rating, notes);
            return ResponseEntity.ok(entry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint for history (accessible by patient or psychologist)
    @GetMapping("/history/{patientId}")
    public ResponseEntity<List<MoodEntry>> getHistory(@PathVariable Long patientId) {
        List<MoodEntry> history = moodEntryService.getPatientHistory(patientId);
        return ResponseEntity.ok(history);
    }
}

