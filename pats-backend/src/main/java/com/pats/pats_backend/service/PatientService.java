package com.pats.pats_backend.service;

import com.pats.pats_backend.entity.Patient;
import com.pats.pats_backend.entity.User;
import com.pats.pats_backend.enums.UserRole;
import com.pats.pats_backend.repo.PatientRepository;
import com.pats.pats_backend.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing patients - allows psychologist to add new patients
 */
@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get all patients for listing/selection
     */
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    /**
     * Get patient by ID
     */
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
    }

    /**
     * Create new patient (User + Patient profile)
     * This is what psychologist will use to add patients manually
     */
    @Transactional
    public Patient createPatient(Patient patientData, String username, String email, String password) {
        // Check if username/email already exists
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }

        // Create User account
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.PATIENT);
        user.setActive(true);
        user = userRepository.save(user);

        // Create Patient profile
        patientData.setUser(user);
        return patientRepository.save(patientData);
    }

    /**
     * Update patient information
     */
    @Transactional
    public Patient updatePatient(Long id, Patient updatedData) {
        Patient existing = patientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));

        // Update fields
        existing.setFirstName(updatedData.getFirstName());
        existing.setLastName(updatedData.getLastName());
        existing.setDateOfBirth(updatedData.getDateOfBirth());
        existing.setGender(updatedData.getGender());
        existing.setPhoneNumber(updatedData.getPhoneNumber());
        existing.setAddress(updatedData.getAddress());
        existing.setCity(updatedData.getCity());
        existing.setPostalCode(updatedData.getPostalCode());
        existing.setCountry(updatedData.getCountry());
        existing.setBloodType(updatedData.getBloodType());
        existing.setInsuranceProvider(updatedData.getInsuranceProvider());
        existing.setInsurancePolicyNumber(updatedData.getInsurancePolicyNumber());

        return patientRepository.save(existing);
    }

    /**
     * Search patients by name
     */
    public List<Patient> searchPatients(String searchTerm) {
        // You can implement custom search in repository
        return patientRepository.findAll().stream()
            .filter(p -> 
                p.getFirstName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                p.getLastName().toLowerCase().contains(searchTerm.toLowerCase())
            )
            .toList();
    }
}