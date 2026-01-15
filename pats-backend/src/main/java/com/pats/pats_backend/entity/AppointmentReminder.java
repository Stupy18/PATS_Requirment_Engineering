package com.pats.pats_backend.entity;

import com.pats.pats_backend.enums.ReminderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment_reminders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Column(name = "reminder_time", nullable = false)
    private LocalDateTime reminderTime;

    @Column(name = "hours_before")
    private Integer hoursBefore; // 24 or 1 hour before

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReminderStatus status = ReminderStatus.PENDING;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "recipient_email")
    private String recipientEmail;

    @Column(name = "reminder_type")
    private String reminderType; // APPOINTMENT_REMINDER, CANCELLATION_NOTICE, etc.

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
