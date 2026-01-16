package com.pats.pats_backend.service;

import com.pats.pats_backend.entity.Availability;
import com.pats.pats_backend.entity.Psychologist;
import com.pats.pats_backend.entity.User;
import com.pats.pats_backend.enums.UserRole;
import com.pats.pats_backend.repo.AvailabilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AvailabilityServiceTest {

    @Mock
    private AvailabilityRepository availabilityRepository;

    @InjectMocks
    private AvailabilityService availabilityService;

    private Availability testAvailability;
    private Psychologist testPsychologist;

    @BeforeEach
    void setUp() {
        // Setup psychologist
        User user = new User();
        user.setId(1L);
        user.setUsername("psych1");
        user.setEmail("psych@test.com");
        user.setRole(UserRole.PSYCHOLOGIST);

        testPsychologist = new Psychologist();
        testPsychologist.setId(1L);
        testPsychologist.setUser(user);
        testPsychologist.setFirstName("Dr.");
        testPsychologist.setLastName("Smith");

        // Setup availability
        testAvailability = new Availability();
        testAvailability.setId(1L);
        testAvailability.setPsychologist(testPsychologist);
        testAvailability.setDayOfWeek(DayOfWeek.MONDAY);
        testAvailability.setStartTime(LocalTime.of(9, 0));
        testAvailability.setEndTime(LocalTime.of(17, 0));
        testAvailability.setIsAvailable(true);
    }

    /**
     * Test FR9.1 - Create availability schedule
     */
    @Test
    void testCreateAvailability_Success() {
        when(availabilityRepository.save(any(Availability.class))).thenReturn(testAvailability);

        Availability createdAvailability = availabilityService.createAvailability(testAvailability);

        assertNotNull(createdAvailability);
        assertEquals(DayOfWeek.MONDAY, createdAvailability.getDayOfWeek());
        assertEquals(LocalTime.of(9, 0), createdAvailability.getStartTime());
        assertEquals(LocalTime.of(17, 0), createdAvailability.getEndTime());
        verify(availabilityRepository, times(1)).save(any(Availability.class));
    }

    /**
     * Test FR9.1 - Update availability schedule
     */
    @Test
    void testUpdateAvailability_Success() {
        when(availabilityRepository.findById(1L)).thenReturn(Optional.of(testAvailability));
        when(availabilityRepository.save(any(Availability.class))).thenReturn(testAvailability);

        Availability updatedAvailability = new Availability();
        updatedAvailability.setDayOfWeek(DayOfWeek.TUESDAY);
        updatedAvailability.setStartTime(LocalTime.of(10, 0));
        updatedAvailability.setEndTime(LocalTime.of(18, 0));

        Availability result = availabilityService.updateAvailability(1L, updatedAvailability);

        assertNotNull(result);
        verify(availabilityRepository, times(1)).findById(1L);
        verify(availabilityRepository, times(1)).save(any(Availability.class));
    }

    /**
     * Test FR9.1 - Delete availability schedule
     */
    @Test
    void testDeleteAvailability_Success() {
        availabilityService.deleteAvailability(1L);
        verify(availabilityRepository, times(1)).deleteById(1L);
    }

    /**
     * Test FR9.1 - Get availability by day of week
     */
    @Test
    void testGetPsychologistAvailabilityByDay() {
        List<Availability> availabilities = Arrays.asList(testAvailability);
        when(availabilityRepository.findByPsychologistIdAndDayOfWeek(1L, DayOfWeek.MONDAY))
                .thenReturn(availabilities);

        List<Availability> result = availabilityService.getPsychologistAvailabilityByDay(1L, DayOfWeek.MONDAY);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(DayOfWeek.MONDAY, result.get(0).getDayOfWeek());
    }

    /**
     * Test FR9.1 - Get availability by specific date
     */
    @Test
    void testGetPsychologistAvailabilityByDate() {
        LocalDate testDate = LocalDate.now();
        testAvailability.setSpecificDate(testDate);

        List<Availability> availabilities = Arrays.asList(testAvailability);
        when(availabilityRepository.findByPsychologistIdAndSpecificDate(1L, testDate))
                .thenReturn(availabilities);

        List<Availability> result = availabilityService.getPsychologistAvailabilityByDate(1L, testDate);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * Test FR9.1 - Check if time slot is available
     */
    @Test
    void testIsTimeSlotAvailable_True() {
        List<Availability> availabilities = Arrays.asList(testAvailability);
        when(availabilityRepository.findByPsychologistIdAndDayOfWeek(1L, DayOfWeek.MONDAY))
                .thenReturn(availabilities);

        boolean result = availabilityService.isTimeSlotAvailable(
                1L, DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));

        assertTrue(result);
    }

    /**
     * Test FR9.1 - Check if time slot is not available
     */
    @Test
    void testIsTimeSlotAvailable_False() {
        List<Availability> availabilities = Arrays.asList(testAvailability);
        when(availabilityRepository.findByPsychologistIdAndDayOfWeek(1L, DayOfWeek.MONDAY))
                .thenReturn(availabilities);

        // Request time outside available hours (before 9 AM)
        boolean result = availabilityService.isTimeSlotAvailable(
                1L, DayOfWeek.MONDAY, LocalTime.of(7, 0), LocalTime.of(8, 0));

        assertFalse(result);
    }
}
