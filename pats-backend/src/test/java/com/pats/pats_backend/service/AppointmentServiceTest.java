package com.pats.pats_backend.service;

import com.pats.pats_backend.entity.*;
import com.pats.pats_backend.enums.AppointmentStatus;
import com.pats.pats_backend.enums.UserRole;
import com.pats.pats_backend.repo.AppointmentRepository;
import com.pats.pats_backend.repo.PatientRepository;
import com.pats.pats_backend.repo.PsychologistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentReminderService appointmentReminderService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AppointmentService appointmentService;

    private Appointment testAppointment;
    private User testPatientUser;
    private User testPsychologistUser;
    private Patient testPatient;
    private Psychologist testPsychologist;

    @BeforeEach
    void setUp() {
        // Setup test user for patient
        testPatientUser = new User();
        testPatientUser.setId(1L);
        testPatientUser.setUsername("patient1");
        testPatientUser.setEmail("patient@test.com");
        testPatientUser.setRole(UserRole.PATIENT);

        // Setup test user for psychologist
        testPsychologistUser = new User();
        testPsychologistUser.setId(2L);
        testPsychologistUser.setUsername("psych1");
        testPsychologistUser.setEmail("psych@test.com");
        testPsychologistUser.setRole(UserRole.PSYCHOLOGIST);

        // Setup patient
        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setUser(testPatientUser);
        testPatient.setFirstName("John");
        testPatient.setLastName("Doe");

        // Setup psychologist
        testPsychologist = new Psychologist();
        testPsychologist.setId(1L);
        testPsychologist.setUser(testPsychologistUser);
        testPsychologist.setFirstName("Dr.");
        testPsychologist.setLastName("Smith");

        // Setup appointment
        testAppointment = new Appointment();
        testAppointment.setId(1L);
        testAppointment.setPsychologist(testPsychologist);
        testAppointment.setPatient(testPatient);
        testAppointment.setAppointmentDateTime(LocalDateTime.now().plusDays(5));
        testAppointment.setDurationMinutes(60);
        testAppointment.setStatus(AppointmentStatus.SCHEDULED);
    }

    /**
     * Test FR9.7 - Prevent double-booking of time slots
     */
    @Test
    void testBookAppointment_PreventDoubleBooking() {
        LocalDateTime appointmentTime = LocalDateTime.now().plusDays(5);
        testAppointment.setAppointmentDateTime(appointmentTime);

        // Simulate a conflicting appointment already booked
        when(appointmentRepository.countConflictingAppointments(1L, appointmentTime)).thenReturn(1L);

        // Should throw exception for double-booking
        assertThrows(IllegalStateException.class, () -> appointmentService.bookAppointment(testAppointment));
    }

    /**
     * Test FR9.2/FR9.7 - Book appointment successfully
     */
    @Test
    void testBookAppointment_Success() {
        LocalDateTime appointmentTime = LocalDateTime.now().plusDays(5);
        testAppointment.setAppointmentDateTime(appointmentTime);

        // Mock no conflicts
        when(appointmentRepository.countConflictingAppointments(1L, appointmentTime)).thenReturn(0L);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // Book appointment
        Appointment bookedAppointment = appointmentService.bookAppointment(testAppointment);

        // Verify
        assertNotNull(bookedAppointment);
        assertEquals(AppointmentStatus.SCHEDULED, bookedAppointment.getStatus());
        verify(appointmentReminderService, times(1)).scheduleReminders(any(Appointment.class));
        verify(notificationService, times(1)).sendAppointmentConfirmation(any(Appointment.class));
    }

    /**
     * Test FR9.4 - Reschedule appointment with 24-hour notice
     */
    @Test
    void testRescheduleAppointment_WithInsufficientNotice() {
        // Appointment scheduled for 12 hours from now (less than 24 hours)
        LocalDateTime appointmentTime = LocalDateTime.now().plusHours(12);
        testAppointment.setAppointmentDateTime(appointmentTime);

        when(appointmentRepository.findById(1L)).thenReturn(java.util.Optional.of(testAppointment));

        // Should throw exception for insufficient notice
        assertThrows(IllegalStateException.class, () -> 
            appointmentService.rescheduleAppointment(1L, LocalDateTime.now().plusDays(10)));
    }

    /**
     * Test FR9.4 - Reschedule appointment successfully
     */
    @Test
    void testRescheduleAppointment_Success() {
        // Appointment scheduled for 3 days from now (sufficient notice)
        LocalDateTime appointmentTime = LocalDateTime.now().plusDays(3);
        testAppointment.setAppointmentDateTime(appointmentTime);

        LocalDateTime newDateTime = LocalDateTime.now().plusDays(5);

        when(appointmentRepository.findById(1L)).thenReturn(java.util.Optional.of(testAppointment));
        when(appointmentRepository.countConflictingAppointments(1L, newDateTime)).thenReturn(0L);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // Reschedule appointment
        Appointment rescheduledAppointment = appointmentService.rescheduleAppointment(1L, newDateTime);

        // Verify
        assertNotNull(rescheduledAppointment);
        verify(appointmentReminderService, times(1)).rescheduleReminders(any(Appointment.class));
        verify(notificationService, times(1)).sendRescheduleNotification(any(Appointment.class));
    }

    /**
     * Test FR9.5 - Cancel appointment and send notifications
     */
    @Test
    void testCancelAppointment_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(java.util.Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // Cancel appointment
        appointmentService.cancelAppointment(1L, "Patient requested cancellation");

        // Verify
        assertEquals(AppointmentStatus.CANCELLED, testAppointment.getStatus());
        assertNotNull(testAppointment.getCancelledAt());
        verify(appointmentReminderService, times(1)).deleteRemindersByAppointmentId(1L);
        verify(notificationService, times(1)).sendCancellationNotification(any(Appointment.class));
    }
}
