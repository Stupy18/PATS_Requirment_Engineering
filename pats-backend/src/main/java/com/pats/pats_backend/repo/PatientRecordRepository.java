package com.pats.pats_backend.repo;

import com.pats.pats_backend.entity.PatientRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRecordRepository extends JpaRepository<PatientRecord, Long> {
    Optional<PatientRecord> findByPatientId(Long patientId);
    List<PatientRecord> findByIsActiveTrue();
}