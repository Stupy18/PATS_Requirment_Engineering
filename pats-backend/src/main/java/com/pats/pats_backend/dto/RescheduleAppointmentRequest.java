package com.pats.pats_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RescheduleAppointmentRequest {

    private Long appointmentId;
    private LocalDateTime newDateTime;
}
