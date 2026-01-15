package com.pats.pats_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelAppointmentRequest {

    private Long appointmentId;
    private String cancellationReason;
}
