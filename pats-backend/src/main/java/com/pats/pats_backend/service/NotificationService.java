package com.pats.pats_backend.service;

import com.pats.pats_backend.entity.Appointment;
import com.pats.pats_backend.entity.AppointmentReminder;
import org.springframework.stereotype.Service;

/**
 * Service for handling notifications and reminders
 * Handles email/SMS sending for:
 * - Appointment confirmations
 * - Appointment reminders (24h and 1h before)
 * - Cancellation notifications
 * - Rescheduling notifications
 * - External calendar sync
 */
@Service
public class NotificationService {

    /**
     * FR9.3 - Send appointment reminder
     */
    public void sendReminderNotification(AppointmentReminder reminder) {
        // Implementation would integrate with email/SMS service
        // Example: EmailService.send(reminder.getRecipientEmail(), ...)
        String subject = "Appointment Reminder - " + reminder.getHoursBefore() + " hours before";
        String message = buildReminderMessage(reminder);
        
        // Send email (to be integrated with actual email service)
        System.out.println("[NOTIFICATION] Sending reminder to: " + reminder.getRecipientEmail());
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
    }

    /**
     * FR9.5 - Send cancellation notification
     */
    public void sendCancellationNotification(Appointment appointment) {
        String patientEmail = appointment.getPatient().getUser().getEmail();
        String psychologistEmail = appointment.getPsychologist().getUser().getEmail();
        
        String subject = "Appointment Cancelled";
        String message = buildCancellationMessage(appointment);
        
        // Send to patient
        System.out.println("[NOTIFICATION] Sending cancellation to patient: " + patientEmail);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
        
        // Send to psychologist
        System.out.println("[NOTIFICATION] Sending cancellation to psychologist: " + psychologistEmail);
        System.out.println("Subject: " + subject);
    }

    /**
     * Send appointment confirmation
     */
    public void sendAppointmentConfirmation(Appointment appointment) {
        String patientEmail = appointment.getPatient().getUser().getEmail();
        String subject = "Appointment Scheduled - Confirmation";
        String message = buildConfirmationMessage(appointment);
        
        System.out.println("[NOTIFICATION] Sending confirmation to: " + patientEmail);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
    }

    /**
     * FR9.4 - Send reschedule notification
     */
    public void sendRescheduleNotification(Appointment appointment) {
        String patientEmail = appointment.getPatient().getUser().getEmail();
        String psychologistEmail = appointment.getPsychologist().getUser().getEmail();
        
        String subject = "Appointment Rescheduled";
        String message = buildRescheduleMessage(appointment);
        
        // Send to patient
        System.out.println("[NOTIFICATION] Sending reschedule notice to patient: " + patientEmail);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
        
        // Send to psychologist
        System.out.println("[NOTIFICATION] Sending reschedule notice to psychologist: " + psychologistEmail);
    }

    private String buildReminderMessage(AppointmentReminder reminder) {
        Appointment appointment = reminder.getAppointment();
        return String.format(
            "Reminder: You have an appointment with %s on %s at %s.",
            appointment.getPsychologist().getFirstName() + " " + appointment.getPsychologist().getLastName(),
            appointment.getAppointmentDateTime().toLocalDate(),
            appointment.getAppointmentDateTime().toLocalTime()
        );
    }

    private String buildCancellationMessage(Appointment appointment) {
        return String.format(
            "Your appointment on %s at %s has been cancelled. Reason: %s",
            appointment.getAppointmentDateTime().toLocalDate(),
            appointment.getAppointmentDateTime().toLocalTime(),
            appointment.getCancellationReason()
        );
    }

    private String buildConfirmationMessage(Appointment appointment) {
        return String.format(
            "Your appointment with %s has been scheduled for %s at %s.",
            appointment.getPsychologist().getFirstName() + " " + appointment.getPsychologist().getLastName(),
            appointment.getAppointmentDateTime().toLocalDate(),
            appointment.getAppointmentDateTime().toLocalTime()
        );
    }

    private String buildRescheduleMessage(Appointment appointment) {
        return String.format(
            "Your appointment has been rescheduled from %s to %s at %s.",
            appointment.getOriginalDateTime() != null ? appointment.getOriginalDateTime().toLocalDate() : "unknown",
            appointment.getAppointmentDateTime().toLocalDate(),
            appointment.getAppointmentDateTime().toLocalTime()
        );
    }
}
