package com.pats.pats_backend.service;

import com.pats.pats_backend.entity.Appointment;
import com.pats.pats_backend.entity.AppointmentReminder;
import com.pats.pats_backend.repo.AppointmentReminderRepository;
import com.pats.pats_backend.enums.ReminderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentReminderService {

    @Autowired
    private AppointmentReminderRepository appointmentReminderRepository;

    @Autowired
    private NotificationService notificationService;

    /**
     * FR9.3 - Send appointment reminders 24 hours and 1 hour before scheduled time
     */
    @Transactional
    public void scheduleReminders(Appointment appointment) {
        LocalDateTime appointmentTime = appointment.getAppointmentDateTime();

        // Schedule 24-hour reminder
        AppointmentReminder reminder24h = new AppointmentReminder();
        reminder24h.setAppointment(appointment);
        reminder24h.setReminderTime(appointmentTime.minusHours(24));
        reminder24h.setHoursBefore(24);
        reminder24h.setStatus(ReminderStatus.PENDING);
        reminder24h.setReminderType("APPOINTMENT_REMINDER");
        reminder24h.setRecipientEmail(appointment.getPatient().getUser().getEmail());
        appointmentReminderRepository.save(reminder24h);

        // Schedule 1-hour reminder
        AppointmentReminder reminder1h = new AppointmentReminder();
        reminder1h.setAppointment(appointment);
        reminder1h.setReminderTime(appointmentTime.minusHours(1));
        reminder1h.setHoursBefore(1);
        reminder1h.setStatus(ReminderStatus.PENDING);
        reminder1h.setReminderType("APPOINTMENT_REMINDER");
        reminder1h.setRecipientEmail(appointment.getPatient().getUser().getEmail());
        appointmentReminderRepository.save(reminder1h);
    }

    /**
     * FR9.4 - Reschedule reminders when appointment is rescheduled
     */
    @Transactional
    public void rescheduleReminders(Appointment appointment) {
        List<AppointmentReminder> reminders = appointmentReminderRepository.findByAppointmentId(appointment.getId());
        
        for (AppointmentReminder reminder : reminders) {
            if (reminder.getStatus() == ReminderStatus.PENDING) {
                LocalDateTime newReminderTime;
                if (reminder.getHoursBefore() == 24) {
                    newReminderTime = appointment.getAppointmentDateTime().minusHours(24);
                } else if (reminder.getHoursBefore() == 1) {
                    newReminderTime = appointment.getAppointmentDateTime().minusHours(1);
                } else {
                    continue;
                }
                reminder.setReminderTime(newReminderTime);
                appointmentReminderRepository.save(reminder);
            }
        }
    }

    /**
     * Delete reminders for cancelled appointment
     */
    @Transactional
    public void deleteRemindersByAppointmentId(Long appointmentId) {
        appointmentReminderRepository.deleteByAppointmentId(appointmentId);
    }

    /**
     * Get pending reminders that need to be sent
     */
    @Transactional(readOnly = true)
    public List<AppointmentReminder> getPendingReminders() {
        LocalDateTime now = LocalDateTime.now();
        return appointmentReminderRepository.findByStatusAndReminderTimeBefore(ReminderStatus.PENDING, now);
    }

    /**
     * Process and send pending reminders
     */
    @Transactional
    public void processPendingReminders() {
        List<AppointmentReminder> pendingReminders = getPendingReminders();
        
        for (AppointmentReminder reminder : pendingReminders) {
            try {
                notificationService.sendReminderNotification(reminder);
                reminder.setStatus(ReminderStatus.SENT);
                reminder.setSentAt(LocalDateTime.now());
                appointmentReminderRepository.save(reminder);
            } catch (Exception e) {
                // Log error and continue with next reminder
                e.printStackTrace();
            }
        }
    }

    /**
     * Get reminders for specific appointment
     */
    @Transactional(readOnly = true)
    public List<AppointmentReminder> getAppointmentReminders(Long appointmentId) {
        return appointmentReminderRepository.findByAppointmentId(appointmentId);
    }
}
