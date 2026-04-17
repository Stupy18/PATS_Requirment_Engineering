package com.pats.pats_backend.controller;

import com.pats.pats_backend.entity.Psychologist;
import com.pats.pats_backend.repo.PsychologistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/psychologists")
@CrossOrigin(origins = "http://localhost:4200")
public class PsychologistController {

    @Autowired
    private PsychologistRepository psychologistRepository;

    @GetMapping
    public List<Psychologist> getAllPsychologists() {
        return psychologistRepository.findAll();
    }
}