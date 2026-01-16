package com.pats.pats_backend.repo;

import com.pats.pats_backend.entity.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {
    List<EmergencyContact> findByPatientId(Long patientId);
    Optional<EmergencyContact> findByPatientIdAndIsPrimaryTrue(Long patientId);
    List<EmergencyContact> findByPatientIdOrderByPriorityAsc(Long patientId);
}
