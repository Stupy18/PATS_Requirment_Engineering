package com.pats.pats_backend.service;

import com.pats.pats_backend.dto.AuthResponse;
import com.pats.pats_backend.dto.LoginRequest;
import com.pats.pats_backend.dto.RegisterRequest;
import com.pats.pats_backend.entity.Patient;
import com.pats.pats_backend.entity.Psychologist;
import com.pats.pats_backend.entity.User;
import com.pats.pats_backend.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pats.pats_backend.repo.PatientRepository;
import com.pats.pats_backend.repo.PsychologistRepository;
import com.pats.pats_backend.repo.UserRepository;
import com.pats.pats_backend.security.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PsychologistRepository psychologistRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long profileId = null;
        if (user.getRole() == UserRole.PATIENT) {
            Patient patient = patientRepository.findByUserId(user.getId()).orElse(null);
            if (patient != null) {
                profileId = patient.getId();
            }
        } else if (user.getRole() == UserRole.PSYCHOLOGIST) {
            Psychologist psychologist = psychologistRepository.findByUserId(user.getId()).orElse(null);
            if (psychologist != null) {
                profileId = psychologist.getId();
            }
        }

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name(),
                user.getId(),
                user.getEmail(),
                profileId
        );

        return new AuthResponse(token);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        user.setActive(true);

        user = userRepository.save(user);

        Long profileId = null;

        if (user.getRole() == UserRole.PATIENT) {
            Patient patient = new Patient();
            patient.setUser(user);
            patient.setFirstName(request.getFirstName());
            patient.setLastName(request.getLastName());
            patient.setPhoneNumber(request.getPhoneNumber());
            patient = patientRepository.save(patient);
            profileId = patient.getId();
        } else if (user.getRole() == UserRole.PSYCHOLOGIST) {
            Psychologist psychologist = new Psychologist();
            psychologist.setUser(user);
            psychologist.setFirstName(request.getFirstName());
            psychologist.setLastName(request.getLastName());
            psychologist.setPhoneNumber(request.getPhoneNumber());
            psychologist = psychologistRepository.save(psychologist);
            profileId = psychologist.getId();
        }

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name(),
                user.getId(),
                user.getEmail(),
                profileId
        );

        return new AuthResponse(token);
    }
}