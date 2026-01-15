package com.pats.pats_backend.repo;

import com.pats.pats_backend.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPsychologistId(Long psychologistId);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByPsychologistIdAndAppointmentDateTimeBetween(
            Long psychologistId, LocalDateTime start, LocalDateTime end);

    List<Appointment> findByPatientIdAndAppointmentDateTimeBetween(
            Long patientId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM Appointment a WHERE a.psychologist.id = :psychologistId " +
            "AND a.appointmentDateTime >= :startTime AND a.appointmentDateTime <= :endTime " +
            "AND a.status != 'CANCELLED'")
    List<Appointment> findAvailableSlots(
            @Param("psychologistId") Long psychologistId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.psychologist.id = :psychologistId " +
            "AND a.appointmentDateTime = :dateTime AND a.status != 'CANCELLED'")
    long countConflictingAppointments(
            @Param("psychologistId") Long psychologistId,
            @Param("dateTime") LocalDateTime dateTime);

    List<Appointment> findByStatusAndAppointmentDateTimeAfter(String status, LocalDateTime dateTime);
}
