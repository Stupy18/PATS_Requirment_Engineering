package com.pats.pats_backend.dto;

import com.pats.pats_backend.enums.AppointmentStatus;
import com.pats.pats_backend.enums.AppointmentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {

    private Long id;
    private Long psychologistId;
    private Long patientId;
    private LocalDateTime appointmentDateTime;
    private Integer durationMinutes;
    private AppointmentStatus status;
    private AppointmentType type;
    private String appointmentNotes;
    private LocalDateTime createdAt;
}
