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

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public LocalDateTime getNewDateTime() {
        return newDateTime;
    }

    public void setNewDateTime(LocalDateTime newDateTime) {
        this.newDateTime = newDateTime;
    }
}
