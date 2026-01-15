package com.pats.pats_backend.service;

import com.pats.pats_backend.entity.MoodEntry;
import com.pats.pats_backend.entity.Patient;
import com.pats.pats_backend.repo.MoodEntryRepository;
import com.pats.pats_backend.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class MoodEntryService {
    @Autowired
    private MoodEntryRepository moodEntryRepository;
    @Autowired
    private PatientRepository patientRepository;

    public MoodEntry createMoodEntry(Long patientId, Integer rating, String notes) {
        Patient patient = patientRepository.findById(patientId).orElseThrow();
        LocalDate today = LocalDate.now();
        Optional<MoodEntry> existing = moodEntryRepository.findByPatientAndDate(patient, today);
        if (existing.isPresent()) {
            throw new IllegalStateException("Check-in already submitted for today.");
        }
        MoodEntry entry = new MoodEntry();
        entry.setPatient(patient);
        entry.setEmotionalRating(rating);
        entry.setNotes(notes);
        entry.setEntryTimestamp(LocalDateTime.now());
        return moodEntryRepository.save(entry);
    }

    public List<MoodEntry> getPatientHistory(Long patientId) {
        Patient patient = patientRepository.findById(patientId).orElseThrow();
        return moodEntryRepository.findByPatient(patient);
    }

    public List<MoodEntry> getAllEntriesForDate(LocalDate date) {
        return moodEntryRepository.findAllByDate(date);
    }

    public List<Patient> getPatientsMissingCheckin(LocalDate date) {
        List<Patient> allPatients = patientRepository.findAll();
        return allPatients.stream()
                .filter(p -> moodEntryRepository.findByPatientAndDate(p, date).isEmpty())
                .toList();
    }
}

