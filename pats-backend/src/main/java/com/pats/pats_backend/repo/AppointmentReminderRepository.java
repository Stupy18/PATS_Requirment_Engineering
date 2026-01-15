package com.pats.pats_backend.repo;

import com.pats.pats_backend.entity.AppointmentReminder;
import com.pats.pats_backend.enums.ReminderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentReminderRepository extends JpaRepository<AppointmentReminder, Long> {

    List<AppointmentReminder> findByAppointmentId(Long appointmentId);

    List<AppointmentReminder> findByStatusAndReminderTimeBefore(ReminderStatus status, LocalDateTime reminderTime);

    List<AppointmentReminder> findByStatus(ReminderStatus status);

    void deleteByAppointmentId(Long appointmentId);
}
