package com.pats.pats_backend.repo;

import com.pats.pats_backend.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByPsychologistId(Long psychologistId);

    List<Availability> findByPsychologistIdAndDayOfWeek(Long psychologistId, DayOfWeek dayOfWeek);

    List<Availability> findByPsychologistIdAndSpecificDate(Long psychologistId, LocalDate specificDate);

    List<Availability> findByPsychologistIdAndIsAvailableTrue(Long psychologistId);
}
