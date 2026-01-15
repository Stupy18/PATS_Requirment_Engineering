package com.pats.pats_backend.service;

import com.pats.pats_backend.entity.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MoodEntryReminderService {
    @Autowired
    private MoodEntryService moodEntryService;

    // Runs every day at 8:00 PM
    @Scheduled(cron = "0 0 20 * * *")
    public void sendDailyCheckinReminders() {
        LocalDate today = LocalDate.now();
        List<Patient> missingPatients = moodEntryService.getPatientsMissingCheckin(today);
        for (Patient patient : missingPatients) {
            // Placeholder for notification logic
            System.out.println("Reminder: Patient " + patient.getId() + " has not completed their daily check-in.");
            // TODO: Integrate with email/SMS notification service
        }
    }
}

