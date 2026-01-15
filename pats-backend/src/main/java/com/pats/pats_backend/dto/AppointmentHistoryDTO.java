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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public AttendanceStatus getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(AttendanceStatus attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getActualDurationMinutes() {
        return actualDurationMinutes;
    }

    public void setActualDurationMinutes(Integer actualDurationMinutes) {
        this.actualDurationMinutes = actualDurationMinutes;
    }

    public String getExternalCalendarProvider() {
        return externalCalendarProvider;
    }

    public void setExternalCalendarProvider(String externalCalendarProvider) {
        this.externalCalendarProvider = externalCalendarProvider;
    }
}
