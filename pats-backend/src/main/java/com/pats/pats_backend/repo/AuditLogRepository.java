package com.pats.pats_backend.repo;

import com.pats.pats_backend.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByPatientRecordIdOrderByActionTimestampDesc(Long patientRecordId);
    List<AuditLog> findByUserIdOrderByActionTimestampDesc(Long userId);
}