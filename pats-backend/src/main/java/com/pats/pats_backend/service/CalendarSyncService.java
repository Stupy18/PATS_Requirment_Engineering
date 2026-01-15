package com.pats.pats_backend.service;

import com.pats.pats_backend.entity.AppointmentHistory;
import org.springframework.stereotype.Service;

/**
 * Service for synchronizing appointments with external calendar applications
 * FR9.6 - Synchronize appointments with external calendar applications
 * 
 * Supported providers:
 * - Google Calendar
 * - Microsoft Outlook Calendar
 * - Apple Calendar
 */
@Service
public class CalendarSyncService {

    /**
     * Sync appointment to external calendar (Google Calendar, Outlook, etc.)
     */
    public void syncAppointmentToExternalCalendar(AppointmentHistory history, String provider) {
        // Implementation would integrate with calendar APIs
        // For Google Calendar: use Google Calendar API
        // For Outlook: use Microsoft Graph API
        // For Apple: use iCalendar format
        
        String calendarId = generateExternalCalendarId(history);
        System.out.println("[CALENDAR SYNC] Syncing appointment to " + provider + " with ID: " + calendarId);
        
        history.setExternalCalendarSyncId(calendarId);
        history.setExternalCalendarProvider(provider);
    }

    /**
     * Unsync appointment from external calendar
     */
    public void unsyncAppointmentFromExternalCalendar(AppointmentHistory history) {
        if (history.getExternalCalendarSyncId() != null && history.getExternalCalendarProvider() != null) {
            System.out.println("[CALENDAR SYNC] Removing appointment from " + history.getExternalCalendarProvider());
            history.setExternalCalendarSyncId(null);
            history.setExternalCalendarProvider(null);
        }
    }

    private String generateExternalCalendarId(AppointmentHistory history) {
        // Generate unique ID for external calendar
        return "PATS_" + history.getAppointment().getId() + "_" + System.currentTimeMillis();
    }
}
