package com.pats.pats_backend.service;

import com.pats.pats_backend.entity.MoodEntry;
import com.pats.pats_backend.entity.Patient;
import com.pats.pats_backend.entity.User;
import com.pats.pats_backend.repo.MoodEntryRepository;
import com.pats.pats_backend.repo.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoodEntryServiceTest {

    @Mock
    private MoodEntryRepository moodEntryRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private MoodEntryService moodEntryService;

    private Patient testPatient;
    private MoodEntry testMoodEntry;

    @BeforeEach
    void setUp() {
        // Setup test patient
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testpatient");
        
        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setUser(testUser);
        testPatient.setFirstName("John");
        testPatient.setLastName("Doe");

        // Setup test mood entry
        testMoodEntry = new MoodEntry();
        testMoodEntry.setId(1L);
        testMoodEntry.setPatient(testPatient);
        testMoodEntry.setEmotionalRating(7);
        testMoodEntry.setNotes("Feeling good today");
        testMoodEntry.setEntryTimestamp(LocalDateTime.now());
    }

    @Test
    void testCreateMoodEntry_Success() {
        // Arrange
        Long patientId = 1L;
        Integer rating = 7;
        String notes = "Feeling good today";

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(testPatient));
        when(moodEntryRepository.findByPatientAndDate(eq(testPatient), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        when(moodEntryRepository.save(any(MoodEntry.class))).thenReturn(testMoodEntry);

        // Act
        MoodEntry result = moodEntryService.createMoodEntry(patientId, rating, notes);

        // Assert
        assertNotNull(result);
        assertEquals(testPatient, result.getPatient());
        assertEquals(rating, result.getEmotionalRating());
        assertEquals(notes, result.getNotes());
        verify(patientRepository, times(1)).findById(patientId);
        verify(moodEntryRepository, times(1)).findByPatientAndDate(eq(testPatient), any(LocalDate.class));
        verify(moodEntryRepository, times(1)).save(any(MoodEntry.class));
    }

    @Test
    void testCreateMoodEntry_PatientNotFound() {
        // Arrange
        Long patientId = 999L;
        Integer rating = 7;
        String notes = "Test notes";

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            moodEntryService.createMoodEntry(patientId, rating, notes);
        });
        verify(patientRepository, times(1)).findById(patientId);
        verify(moodEntryRepository, never()).save(any(MoodEntry.class));
    }

    @Test
    void testCreateMoodEntry_AlreadySubmittedToday() {
        // Arrange
        Long patientId = 1L;
        Integer rating = 7;
        String notes = "Test notes";

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(testPatient));
        when(moodEntryRepository.findByPatientAndDate(eq(testPatient), any(LocalDate.class)))
                .thenReturn(Optional.of(testMoodEntry));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            moodEntryService.createMoodEntry(patientId, rating, notes);
        });
        assertEquals("Check-in already submitted for today.", exception.getMessage());
        verify(moodEntryRepository, never()).save(any(MoodEntry.class));
    }

    @Test
    void testCreateMoodEntry_WithNullNotes() {
        // Arrange
        Long patientId = 1L;
        Integer rating = 5;
        String notes = null;

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(testPatient));
        when(moodEntryRepository.findByPatientAndDate(eq(testPatient), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        
        MoodEntry moodEntryWithoutNotes = new MoodEntry();
        moodEntryWithoutNotes.setId(2L);
        moodEntryWithoutNotes.setPatient(testPatient);
        moodEntryWithoutNotes.setEmotionalRating(rating);
        moodEntryWithoutNotes.setNotes(null);
        moodEntryWithoutNotes.setEntryTimestamp(LocalDateTime.now());
        
        when(moodEntryRepository.save(any(MoodEntry.class))).thenReturn(moodEntryWithoutNotes);

        // Act
        MoodEntry result = moodEntryService.createMoodEntry(patientId, rating, notes);

        // Assert
        assertNotNull(result);
        assertNull(result.getNotes());
        assertEquals(rating, result.getEmotionalRating());
        verify(moodEntryRepository, times(1)).save(any(MoodEntry.class));
    }

    @Test
    void testGetPatientHistory_Success() {
        // Arrange
        Long patientId = 1L;
        MoodEntry entry1 = new MoodEntry();
        entry1.setId(1L);
        entry1.setPatient(testPatient);
        entry1.setEmotionalRating(7);

        MoodEntry entry2 = new MoodEntry();
        entry2.setId(2L);
        entry2.setPatient(testPatient);
        entry2.setEmotionalRating(8);

        List<MoodEntry> mockHistory = Arrays.asList(entry1, entry2);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(testPatient));
        when(moodEntryRepository.findByPatient(testPatient)).thenReturn(mockHistory);

        // Act
        List<MoodEntry> result = moodEntryService.getPatientHistory(patientId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(entry1, result.get(0));
        assertEquals(entry2, result.get(1));
        verify(patientRepository, times(1)).findById(patientId);
        verify(moodEntryRepository, times(1)).findByPatient(testPatient);
    }

    @Test
    void testGetPatientHistory_PatientNotFound() {
        // Arrange
        Long patientId = 999L;

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            moodEntryService.getPatientHistory(patientId);
        });
        verify(patientRepository, times(1)).findById(patientId);
        verify(moodEntryRepository, never()).findByPatient(any(Patient.class));
    }

    @Test
    void testGetPatientHistory_EmptyHistory() {
        // Arrange
        Long patientId = 1L;

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(testPatient));
        when(moodEntryRepository.findByPatient(testPatient)).thenReturn(Arrays.asList());

        // Act
        List<MoodEntry> result = moodEntryService.getPatientHistory(patientId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(moodEntryRepository, times(1)).findByPatient(testPatient);
    }

    @Test
    void testGetAllEntriesForDate_Success() {
        // Arrange
        LocalDate testDate = LocalDate.of(2026, 1, 15);
        MoodEntry entry1 = new MoodEntry();
        entry1.setId(1L);
        entry1.setEmotionalRating(7);
        entry1.setEntryTimestamp(testDate.atStartOfDay());

        MoodEntry entry2 = new MoodEntry();
        entry2.setId(2L);
        entry2.setEmotionalRating(5);
        entry2.setEntryTimestamp(testDate.atTime(12, 0));

        List<MoodEntry> mockEntries = Arrays.asList(entry1, entry2);

        when(moodEntryRepository.findAllByDate(testDate)).thenReturn(mockEntries);

        // Act
        List<MoodEntry> result = moodEntryService.getAllEntriesForDate(testDate);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(moodEntryRepository, times(1)).findAllByDate(testDate);
    }

    @Test
    void testGetAllEntriesForDate_NoEntries() {
        // Arrange
        LocalDate testDate = LocalDate.of(2026, 1, 15);

        when(moodEntryRepository.findAllByDate(testDate)).thenReturn(Arrays.asList());

        // Act
        List<MoodEntry> result = moodEntryService.getAllEntriesForDate(testDate);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(moodEntryRepository, times(1)).findAllByDate(testDate);
    }

    @Test
    void testGetPatientsMissingCheckin_Success() {
        // Arrange
        LocalDate testDate = LocalDate.now();
        
        Patient patient1 = new Patient();
        patient1.setId(1L);
        patient1.setFirstName("John");
        
        Patient patient2 = new Patient();
        patient2.setId(2L);
        patient2.setFirstName("Jane");
        
        Patient patient3 = new Patient();
        patient3.setId(3L);
        patient3.setFirstName("Bob");

        List<Patient> allPatients = Arrays.asList(patient1, patient2, patient3);

        when(patientRepository.findAll()).thenReturn(allPatients);
        // patient1 has checked in, patient2 and patient3 haven't
        when(moodEntryRepository.findByPatientAndDate(patient1, testDate))
                .thenReturn(Optional.of(testMoodEntry));
        when(moodEntryRepository.findByPatientAndDate(patient2, testDate))
                .thenReturn(Optional.empty());
        when(moodEntryRepository.findByPatientAndDate(patient3, testDate))
                .thenReturn(Optional.empty());

        // Act
        List<Patient> result = moodEntryService.getPatientsMissingCheckin(testDate);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(patient2));
        assertTrue(result.contains(patient3));
        assertFalse(result.contains(patient1));
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void testGetPatientsMissingCheckin_AllCheckedIn() {
        // Arrange
        LocalDate testDate = LocalDate.now();
        
        Patient patient1 = new Patient();
        patient1.setId(1L);

        List<Patient> allPatients = Arrays.asList(patient1);

        when(patientRepository.findAll()).thenReturn(allPatients);
        when(moodEntryRepository.findByPatientAndDate(patient1, testDate))
                .thenReturn(Optional.of(testMoodEntry));

        // Act
        List<Patient> result = moodEntryService.getPatientsMissingCheckin(testDate);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPatientsMissingCheckin_NoPatients() {
        // Arrange
        LocalDate testDate = LocalDate.now();

        when(patientRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Patient> result = moodEntryService.getPatientsMissingCheckin(testDate);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void testCreateMoodEntry_VerifyTimestamp() {
        // Arrange
        Long patientId = 1L;
        Integer rating = 8;
        String notes = "Test";
        LocalDateTime beforeCall = LocalDateTime.now().minusSeconds(1);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(testPatient));
        when(moodEntryRepository.findByPatientAndDate(eq(testPatient), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        when(moodEntryRepository.save(any(MoodEntry.class))).thenAnswer(invocation -> {
            MoodEntry entry = invocation.getArgument(0);
            return entry;
        });

        // Act
        MoodEntry result = moodEntryService.createMoodEntry(patientId, rating, notes);

        // Assert
        assertNotNull(result.getEntryTimestamp());
        assertTrue(result.getEntryTimestamp().isAfter(beforeCall));
        assertTrue(result.getEntryTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}
