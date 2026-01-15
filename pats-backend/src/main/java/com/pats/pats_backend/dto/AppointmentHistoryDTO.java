package com.pats.pats_backend.dto;

import com.pats.pats_backend.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentHistoryDTO {

    private Long id;
    private Long appointmentId;
    private AttendanceStatus attendanceStatus;
    private String notes;
    private Integer actualDurationMinutes;
    private String externalCalendarProvider;
}
