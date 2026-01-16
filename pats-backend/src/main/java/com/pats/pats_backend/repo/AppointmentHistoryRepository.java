package com.pats.pats_backend.repo;

import com.pats.pats_backend.entity.AppointmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentHistoryRepository extends JpaRepository<AppointmentHistory, Long> {

    Optional<AppointmentHistory> findByAppointmentId(Long appointmentId);

    List<AppointmentHistory> findByAppointmentPatientId(Long patientId);

    List<AppointmentHistory> findByAppointmentPsychologistId(Long psychologistId);
}
