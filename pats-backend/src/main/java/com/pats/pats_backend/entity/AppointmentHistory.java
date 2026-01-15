package com.pats.pats_backend.entity;

import com.pats.pats_backend.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus attendanceStatus;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "duration_actual_minutes")
    private Integer actualDurationMinutes;

    @Column(name = "external_calendar_sync_id")
    private String externalCalendarSyncId;

    @Column(name = "external_calendar_provider")
    private String externalCalendarProvider; // GOOGLE, OUTLOOK, etc.

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
