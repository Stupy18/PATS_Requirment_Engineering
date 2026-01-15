import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AppointmentService } from '../../services/appointment.service';
import { AvailabilityService } from '../../services/availability.service';
import { Appointment, Availability } from '../../models/appointment.model';

@Component({
  selector: 'app-book-appointment',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './book-appointment.component.html',
  styleUrls: ['./book-appointment.component.scss']
})
export class BookAppointmentComponent implements OnInit {

  bookingForm: FormGroup;
  availableSlots: Appointment[] = [];
  psychologists: any[] = [];
  successMessage: string = '';
  errorMessage: string = '';
  loading: boolean = false;
  patientId: number = 1; // Should come from logged-in user

  appointmentTypes = ['INITIAL', 'FOLLOWUP', 'EMERGENCY', 'VIDEO', 'IN_PERSON'];
  durations = [30, 45, 60, 90];

  constructor(
    private formBuilder: FormBuilder,
    private appointmentService: AppointmentService,
    private availabilityService: AvailabilityService
  ) {
    this.bookingForm = this.formBuilder.group({
      psychologistId: ['', Validators.required],
      appointmentDate: ['', Validators.required],
      appointmentTime: ['', Validators.required],
      type: ['INITIAL', Validators.required],
      durationMinutes: [60, Validators.required],
      notes: ['']
    });
  }

  ngOnInit(): void {
    // Load psychologists list
    // This would typically come from an API call
  }

  /**
   * FR9.2 - Search available slots
   */
  searchAvailableSlots(): void {
    const psychologistId = this.bookingForm.get('psychologistId')?.value;
    const appointmentDate = this.bookingForm.get('appointmentDate')?.value;

    if (!psychologistId || !appointmentDate) {
      this.errorMessage = 'Please select a psychologist and date';
      return;
    }

    this.loading = true;
    const startTime = appointmentDate + 'T00:00:00';
    const endTime = appointmentDate + 'T23:59:59';

    this.appointmentService.getAvailableSlots(psychologistId, startTime, endTime).subscribe({
      next: (slots) => {
        this.availableSlots = slots;
        this.loading = false;
        if (slots.length === 0) {
          this.errorMessage = 'No available slots found for this date';
        } else {
          this.errorMessage = '';
        }
      },
      error: (error) => {
        this.errorMessage = 'Failed to load available slots: ' + error.message;
        this.loading = false;
      }
    });
  }

  /**
   * FR9.2 & FR9.7 - Book appointment
   */
  bookAppointment(): void {
    if (this.bookingForm.invalid) {
      this.errorMessage = 'Please fill in all required fields';
      return;
    }

    const appointmentDate = this.bookingForm.get('appointmentDate')?.value;
    const appointmentTime = this.bookingForm.get('appointmentTime')?.value;
    const appointmentDateTime = appointmentDate + 'T' + appointmentTime;

    const appointment: Appointment = {
      psychologistId: this.bookingForm.get('psychologistId')?.value,
      patientId: this.patientId,
      appointmentDateTime: appointmentDateTime,
      durationMinutes: this.bookingForm.get('durationMinutes')?.value,
      type: this.bookingForm.get('type')?.value,
      appointmentNotes: this.bookingForm.get('notes')?.value,
      status: 'SCHEDULED'
    };

    this.loading = true;
    this.appointmentService.bookAppointment(appointment).subscribe({
      next: (response) => {
        this.successMessage = 'Appointment booked successfully! A confirmation has been sent.';
        this.bookingForm.reset();
        this.availableSlots = [];
        this.loading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = error.error.message || 'Failed to book appointment: ' + error.message;
        this.loading = false;
      }
    });
  }
}
