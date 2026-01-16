package com.pats.pats_backend.controller;

import com.pats.pats_backend.dto.AvailabilityDTO;
import com.pats.pats_backend.entity.Availability;
import com.pats.pats_backend.entity.Psychologist;
import com.pats.pats_backend.repo.PsychologistRepository;
import com.pats.pats_backend.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/availability")
@CrossOrigin(origins = "http://localhost:4200")
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private PsychologistRepository psychologistRepository;

    /**
     * FR9.1 - Create availability schedule for psychologist
     */
    @PostMapping("/create")
    public ResponseEntity<?> createAvailability(@RequestBody AvailabilityDTO availabilityDTO) {
        try {
            Optional<Psychologist> psychologistOpt = psychologistRepository.findById(availabilityDTO.getPsychologistId());
            if (psychologistOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Psychologist not found");
            }

            Availability availability = new Availability();
            availability.setPsychologist(psychologistOpt.get());
            availability.setDayOfWeek(availabilityDTO.getDayOfWeek());
            availability.setStartTime(availabilityDTO.getStartTime());
            availability.setEndTime(availabilityDTO.getEndTime());
            availability.setSpecificDate(availabilityDTO.getSpecificDate());
            availability.setIsAvailable(availabilityDTO.getIsAvailable() != null ? availabilityDTO.getIsAvailable() : true);
            availability.setNotes(availabilityDTO.getNotes());

            Availability createdAvailability = availabilityService.createAvailability(availability);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAvailability);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    /**
     * FR9.1 - Update availability schedule
     */
    @PutMapping("/update/{availabilityId}")
    public ResponseEntity<?> updateAvailability(
            @PathVariable Long availabilityId,
            @RequestBody AvailabilityDTO availabilityDTO) {
        try {
            Availability availability = new Availability();
            availability.setDayOfWeek(availabilityDTO.getDayOfWeek());
            availability.setStartTime(availabilityDTO.getStartTime());
            availability.setEndTime(availabilityDTO.getEndTime());
            availability.setSpecificDate(availabilityDTO.getSpecificDate());
            availability.setIsAvailable(availabilityDTO.getIsAvailable());
            availability.setNotes(availabilityDTO.getNotes());

            Availability updatedAvailability = availabilityService.updateAvailability(availabilityId, availability);
            return ResponseEntity.ok(updatedAvailability);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    /**
     * FR9.1 - Delete availability schedule
     */
    @DeleteMapping("/delete/{availabilityId}")
    public ResponseEntity<?> deleteAvailability(@PathVariable Long availabilityId) {
        try {
            availabilityService.deleteAvailability(availabilityId);
            return ResponseEntity.ok("Availability deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    /**
     * FR9.1 - Get availability by day of week
     */
    @GetMapping("/psychologist/{psychologistId}/day/{dayOfWeek}")
    public ResponseEntity<?> getAvailabilityByDay(
            @PathVariable Long psychologistId,
            @PathVariable String dayOfWeek) {
        try {
            DayOfWeek day = DayOfWeek.valueOf(dayOfWeek.toUpperCase());
            List<Availability> availabilities = availabilityService.getPsychologistAvailabilityByDay(psychologistId, day);
            return ResponseEntity.ok(availabilities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    /**
     * FR9.1 - Get availability by specific date
     */
    @GetMapping("/psychologist/{psychologistId}/date/{specificDate}")
    public ResponseEntity<?> getAvailabilityByDate(
            @PathVariable Long psychologistId,
            @PathVariable String specificDate) {
        try {
            LocalDate date = LocalDate.parse(specificDate);
            List<Availability> availabilities = availabilityService.getPsychologistAvailabilityByDate(psychologistId, date);
            return ResponseEntity.ok(availabilities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    /**
     * FR9.1 - Get all available slots for psychologist
     */
    @GetMapping("/psychologist/{psychologistId}/available")
    public ResponseEntity<?> getAllAvailableSlots(@PathVariable Long psychologistId) {
        try {
            List<Availability> availabilities = availabilityService.getAllAvailableSlots(psychologistId);
            return ResponseEntity.ok(availabilities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    /**
     * FR9.1 - Get all availabilities for psychologist
     */
    @GetMapping("/psychologist/{psychologistId}/all")
    public ResponseEntity<?> getAllAvailabilities(@PathVariable Long psychologistId) {
        try {
            List<Availability> availabilities = availabilityService.getPsychologistAvailabilities(psychologistId);
            return ResponseEntity.ok(availabilities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
}
