package com.pats.pats_backend.service;

import com.pats.pats_backend.entity.*;
import com.pats.pats_backend.enums.AppointmentStatus;
import com.pats.pats_backend.enums.AttendanceStatus;
import com.pats.pats_backend.repo.AppointmentHistoryRepository;
import com.pats.pats_backend.repo.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentHistoryRepository appointmentHistoryRepository;

    @Autowired
    private AppointmentReminderService appointmentReminderService;

    @Autowired
    private NotificationService notificationService;

    /**
     * FR9.2 - Display available time slots to patients for booking
     */
    @Transactional(readOnly = true)
    public List<Appointment> getAvailableSlots(Long psychologistId, LocalDateTime startTime, LocalDateTime endTime) {
        return appointmentRepository.findAvailableSlots(psychologistId, startTime, endTime);
    }

    /**
     * FR9.7 - Prevent double-booking of time slots
     */
    @Transactional
    public Appointment bookAppointment(Appointment appointment) {
        // Check for double-booking
        long conflictCount = appointmentRepository.countConflictingAppointments(
                appointment.getPsychologist().getId(),
                appointment.getAppointmentDateTime());

        if (conflictCount > 0) {
            throw new IllegalStateException("Time slot already booked for this psychologist");
        }

        // Save appointment
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setCreatedAt(LocalDateTime.now());
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Schedule reminders for 24 hours and 1 hour before
        appointmentReminderService.scheduleReminders(savedAppointment);

        // Send confirmation notification
        notificationService.sendAppointmentConfirmation(savedAppointment);

        return savedAppointment;
    }

    /**
     * FR9.4 - Allow rescheduling with minimum 24-hour notice
     */
    @Transactional
    public Appointment rescheduleAppointment(Long appointmentId, LocalDateTime newDateTime) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            throw new IllegalArgumentException("Appointment not found");
        }

        Appointment appointment = appointmentOpt.get();

        // Check 24-hour notice requirement
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minimumRescheduleTime = now.plusHours(24);

        if (appointment.getAppointmentDateTime().isBefore(minimumRescheduleTime)) {
            throw new IllegalStateException("Cannot reschedule within 24 hours of appointment");
        }

        // Check for double-booking at new time
        long conflictCount = appointmentRepository.countConflictingAppointments(
                appointment.getPsychologist().getId(),
                newDateTime);

        if (conflictCount > 0) {
            throw new IllegalStateException("New time slot already booked");
        }

        // Store original datetime and update
        appointment.setOriginalDateTime(appointment.getAppointmentDateTime());
        appointment.setAppointmentDateTime(newDateTime);
        appointment.setRescheduledAt(LocalDateTime.now());

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        // Reschedule reminders
        appointmentReminderService.rescheduleReminders(updatedAppointment);

        // Send rescheduling notification to both parties
        notificationService.sendRescheduleNotification(updatedAppointment);

        return updatedAppointment;
    }

    /**
     * FR9.5 - Send cancellation notifications to both parties
     */
    @Transactional
    public void cancelAppointment(Long appointmentId, String cancellationReason) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            throw new IllegalArgumentException("Appointment not found");
        }

        Appointment appointment = appointmentOpt.get();
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelledAt(LocalDateTime.now());
        appointment.setCancellationReason(cancellationReason);

        appointmentRepository.save(appointment);

        // Delete pending reminders
        appointmentReminderService.deleteRemindersByAppointmentId(appointmentId);

        // Send cancellation notifications
        notificationService.sendCancellationNotification(appointment);
    }

    /**
     * FR9.8 - Track appointment history including attendance status
     */
    @Transactional
    public AppointmentHistory recordAppointmentHistory(Long appointmentId, AttendanceStatus attendanceStatus,
                                                       String notes, Integer actualDurationMinutes) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            throw new IllegalArgumentException("Appointment not found");
        }

        Appointment appointment = appointmentOpt.get();
        appointment.setStatus(AppointmentStatus.COMPLETED);

        AppointmentHistory history = new AppointmentHistory();
        history.setAppointment(appointment);
        history.setAttendanceStatus(attendanceStatus);
        history.setNotes(notes);
        history.setActualDurationMinutes(actualDurationMinutes);
        history.setCreatedAt(LocalDateTime.now());

        return appointmentHistoryRepository.save(history);
    }

    /**
     * FR9.8 - Get appointment history for patient
     */
    @Transactional(readOnly = true)
    public List<AppointmentHistory> getPatientAppointmentHistory(Long patientId) {
        return appointmentHistoryRepository.findByAppointmentPatientId(patientId);
    }

    /**
     * FR9.8 - Get appointment history for psychologist
     */
    @Transactional(readOnly = true)
    public List<AppointmentHistory> getPsychologistAppointmentHistory(Long psychologistId) {
        return appointmentHistoryRepository.findByAppointmentPsychologistId(psychologistId);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByPsychologist(Long psychologistId) {
        return appointmentRepository.findByPsychologistId(psychologistId);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    @Transactional(readOnly = true)
    public Optional<Appointment> getAppointment(Long appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }
}
