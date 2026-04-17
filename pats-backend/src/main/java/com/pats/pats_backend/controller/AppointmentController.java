    package com.pats.pats_backend.controller;

    import com.pats.pats_backend.dto.AppointmentDTO;
    import com.pats.pats_backend.dto.CancelAppointmentRequest;
    import com.pats.pats_backend.dto.RescheduleAppointmentRequest;
    import com.pats.pats_backend.entity.Appointment;
    import com.pats.pats_backend.entity.AppointmentHistory;
    import com.pats.pats_backend.entity.Patient;
    import com.pats.pats_backend.entity.Psychologist;
    import com.pats.pats_backend.enums.AttendanceStatus;
    import com.pats.pats_backend.repo.PatientRepository;
    import com.pats.pats_backend.repo.PsychologistRepository;
    import com.pats.pats_backend.service.AppointmentService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.Optional;

    @RestController
    @RequestMapping("/api/appointments")
    @CrossOrigin(origins = "http://localhost:4200")
    public class AppointmentController {

        @Autowired
        private AppointmentService appointmentService;

        @Autowired
        private PsychologistRepository psychologistRepository;

        @Autowired
        private PatientRepository patientRepository;

        /**
         * FR9.2 - Get available time slots for a psychologist
         */
        @GetMapping("/available-slots/{psychologistId}")
        public ResponseEntity<?> getAvailableSlots(
                @PathVariable Long psychologistId,
                @RequestParam LocalDateTime startTime,
                @RequestParam LocalDateTime endTime) {
            try {
                List<Appointment> slots = appointmentService.getAvailableSlots(psychologistId, startTime, endTime);
                return ResponseEntity.ok(slots);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
            }
        }

        /**
         * FR9.2/FR9.7 - Book an appointment
         */
        @PostMapping("/book")
        public ResponseEntity<?> bookAppointment(@RequestBody AppointmentDTO appointmentDTO) {
            try {
                Optional<Psychologist> psychologistOpt = psychologistRepository.findById(appointmentDTO.getPsychologistId());
                Optional<Patient> patientOpt = patientRepository.findById(appointmentDTO.getPatientId());

                if (psychologistOpt.isEmpty() || patientOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Psychologist or Patient not found");
                }

                Appointment appointment = new Appointment();
                appointment.setPsychologist(psychologistOpt.get());
                appointment.setPatient(patientOpt.get());
                appointment.setAppointmentDateTime(appointmentDTO.getAppointmentDateTime());
                appointment.setDurationMinutes(appointmentDTO.getDurationMinutes() != null ? appointmentDTO.getDurationMinutes() : 60);
                appointment.setType(appointmentDTO.getType());
                appointment.setAppointmentNotes(appointmentDTO.getAppointmentNotes());

                Appointment bookedAppointment = appointmentService.bookAppointment(appointment);
                return ResponseEntity.status(HttpStatus.CREATED).body(bookedAppointment);
            } catch (IllegalStateException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: " + e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
            }
        }

        /**
         * FR9.4 - Reschedule appointment with 24-hour notice
         */
        @PutMapping("/reschedule")
        public ResponseEntity<?> rescheduleAppointment(@RequestBody RescheduleAppointmentRequest request) {
            try {
                Appointment rescheduledAppointment = appointmentService.rescheduleAppointment(
                        request.getAppointmentId(), request.getNewDateTime());
                return ResponseEntity.ok(rescheduledAppointment);
            } catch (IllegalStateException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: " + e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
            }
        }

        /**
         * FR9.5 - Cancel appointment with notification
         */
        @PostMapping("/cancel")
        public ResponseEntity<?> cancelAppointment(@RequestBody CancelAppointmentRequest request) {
            try {
                appointmentService.cancelAppointment(request.getAppointmentId(), request.getCancellationReason());
                return ResponseEntity.ok("Appointment cancelled successfully");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
            }
        }

        /**
         * FR9.8 - Get appointment history for patient
         */
        @GetMapping("/history/patient/{patientId}")
        public ResponseEntity<?> getPatientAppointmentHistory(@PathVariable Long patientId) {
            try {
                List<AppointmentHistory> history = appointmentService.getPatientAppointmentHistory(patientId);
                return ResponseEntity.ok(history);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
            }
        }

        /**
         * FR9.8 - Get appointment history for psychologist
         */
        @GetMapping("/history/psychologist/{psychologistId}")
        public ResponseEntity<?> getPsychologistAppointmentHistory(@PathVariable Long psychologistId) {
            try {
                List<AppointmentHistory> history = appointmentService.getPsychologistAppointmentHistory(psychologistId);
                return ResponseEntity.ok(history);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
            }
        }

        /**
         * FR9.8 - Record appointment history (attendance status)
         */
        @PostMapping("/history/record")
        public ResponseEntity<?> recordAppointmentHistory(
                @RequestParam Long appointmentId,
                @RequestParam AttendanceStatus attendanceStatus,
                @RequestParam(required = false) String notes,
                @RequestParam(required = false) Integer actualDurationMinutes) {
            try {
                AppointmentHistory history = appointmentService.recordAppointmentHistory(
                        appointmentId, attendanceStatus, notes, actualDurationMinutes);
                return ResponseEntity.status(HttpStatus.CREATED).body(history);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
            }
        }

        /**
         * Get appointments for psychologist
         */
        @GetMapping("/psychologist/{psychologistId}")
        public ResponseEntity<?> getAppointmentsByPsychologist(@PathVariable Long psychologistId) {
            try {
                List<Appointment> appointments = appointmentService.getAppointmentsByPsychologist(psychologistId);
                return ResponseEntity.ok(appointments);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
            }
        }

        /**
         * Get appointments for patient
         */
        @GetMapping("/patient/{patientId}")
        public ResponseEntity<?> getAppointmentsByPatient(@PathVariable Long patientId) {
            try {
                List<Appointment> appointments = appointmentService.getAppointmentsByPatient(patientId);
                return ResponseEntity.ok(appointments);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
            }
        }

        /**
         * Get appointment details
         */
        @GetMapping("/{appointmentId}")
        public ResponseEntity<?> getAppointment(@PathVariable Long appointmentId) {
            try {
                Optional<Appointment> appointment = appointmentService.getAppointment(appointmentId);
                if (appointment.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found");
                }
                return ResponseEntity.ok(appointment.get());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
            }
        }
    }
