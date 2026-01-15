package com.pats.pats_backend.repo;

import com.pats.pats_backend.entity.MoodEntry;
import com.pats.pats_backend.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {
    List<MoodEntry> findByPatient(Patient patient);

    @Query("SELECT m FROM MoodEntry m WHERE m.patient = :patient AND DATE(m.entryTimestamp) = :date")
    Optional<MoodEntry> findByPatientAndDate(@Param("patient") Patient patient, @Param("date") LocalDate date);

    @Query("SELECT m FROM MoodEntry m WHERE DATE(m.entryTimestamp) = :date")
    List<MoodEntry> findAllByDate(@Param("date") LocalDate date);
}

