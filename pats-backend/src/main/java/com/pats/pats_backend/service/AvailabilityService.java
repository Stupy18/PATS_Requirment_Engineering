package com.pats.pats_backend.service;

import com.pats.pats_backend.entity.Availability;
import com.pats.pats_backend.repo.AvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    /**
     * FR9.1 - The system shall allow psychologists to define their availability schedule
     */
    @Transactional
    public Availability createAvailability(Availability availability) {
        availability.setCreatedAt(java.time.LocalDateTime.now());
        availability.setUpdatedAt(java.time.LocalDateTime.now());
        return availabilityRepository.save(availability);
    }

    /**
     * FR9.1 - Update psychologist availability
     */
    @Transactional
    public Availability updateAvailability(Long availabilityId, Availability availability) {
        Optional<Availability> existingOpt = availabilityRepository.findById(availabilityId);
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Availability not found");
        }

        Availability existing = existingOpt.get();
        existing.setDayOfWeek(availability.getDayOfWeek());
        existing.setStartTime(availability.getStartTime());
        existing.setEndTime(availability.getEndTime());
        existing.setSpecificDate(availability.getSpecificDate());
        existing.setIsAvailable(availability.getIsAvailable());
        existing.setNotes(availability.getNotes());
        existing.setUpdatedAt(java.time.LocalDateTime.now());

        return availabilityRepository.save(existing);
    }

    /**
     * FR9.1 - Delete availability slot
     */
    @Transactional
    public void deleteAvailability(Long availabilityId) {
        availabilityRepository.deleteById(availabilityId);
    }

    /**
     * FR9.1 - Get availability schedule for psychologist by day
     */
    @Transactional(readOnly = true)
    public List<Availability> getPsychologistAvailabilityByDay(Long psychologistId, DayOfWeek dayOfWeek) {
        return availabilityRepository.findByPsychologistIdAndDayOfWeek(psychologistId, dayOfWeek);
    }

    /**
     * FR9.1 - Get availability schedule for psychologist by specific date
     */
    @Transactional(readOnly = true)
    public List<Availability> getPsychologistAvailabilityByDate(Long psychologistId, LocalDate specificDate) {
        return availabilityRepository.findByPsychologistIdAndSpecificDate(psychologistId, specificDate);
    }

    /**
     * FR9.1 - Get all available slots for psychologist
     */
    @Transactional(readOnly = true)
    public List<Availability> getAllAvailableSlots(Long psychologistId) {
        return availabilityRepository.findByPsychologistIdAndIsAvailableTrue(psychologistId);
    }

    /**
     * FR9.1 - Get all availability schedules for psychologist
     */
    @Transactional(readOnly = true)
    public List<Availability> getPsychologistAvailabilities(Long psychologistId) {
        return availabilityRepository.findByPsychologistId(psychologistId);
    }

    /**
     * Check if time slot is available
     */
    @Transactional(readOnly = true)
    public boolean isTimeSlotAvailable(Long psychologistId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        List<Availability> availabilities = availabilityRepository.findByPsychologistIdAndDayOfWeek(psychologistId, dayOfWeek);
        
        for (Availability availability : availabilities) {
            if (availability.getIsAvailable() &&
                !startTime.isBefore(availability.getStartTime()) &&
                !endTime.isAfter(availability.getEndTime())) {
                return true;
            }
        }
        return false;
    }
}
