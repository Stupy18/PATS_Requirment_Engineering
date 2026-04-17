import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AppointmentService } from '../../services/appointment.service';
import { AvailabilityService } from '../../services/availability.service';
import { Appointment, Availability } from '../../models/appointment.model';
import {NavbarComponent} from "../../shared/navbar/navbar";

@Component({
  selector: 'app-availability-schedule',
  standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule, NavbarComponent],
  templateUrl: './availability-schedule.component.html',
  styleUrls: ['./availability-schedule.component.scss']
})
export class AvailabilityScheduleComponent implements OnInit {

  availabilityForm: FormGroup;
  availabilities: Availability[] = [];
  psychologistId: number = 1; // Should come from logged-in user
  daysOfWeek = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];
  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private formBuilder: FormBuilder,
    private availabilityService: AvailabilityService
  ) {
    this.availabilityForm = this.formBuilder.group({
      dayOfWeek: ['', Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required],
      notes: ['']
    });
  }

  ngOnInit(): void {
    this.loadAvailabilities();
  }

  /**
   * FR9.1 - Load existing availabilities
   */
  loadAvailabilities(): void {
    this.availabilityService.getAllAvailabilities(this.psychologistId).subscribe({
      next: (availabilities) => {
        this.availabilities = availabilities;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load availabilities: ' + error.message;
      }
    });
  }

  /**
   * FR9.1 - Create new availability schedule
   */
  createAvailability(): void {
    if (this.availabilityForm.invalid) {
      this.errorMessage = 'Please fill in all required fields';
      return;
    }

    const availability: Availability = {
      psychologistId: this.psychologistId,
      dayOfWeek: this.availabilityForm.value.dayOfWeek,
      startTime: this.availabilityForm.value.startTime,
      endTime: this.availabilityForm.value.endTime,
      isAvailable: true,
      notes: this.availabilityForm.value.notes
    };

    this.availabilityService.createAvailability(availability).subscribe({
      next: (response) => {
        this.successMessage = 'Availability created successfully';
        this.availabilityForm.reset();
        this.loadAvailabilities();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = 'Failed to create availability: ' + error.message;
      }
    });
  }

  /**
   * FR9.1 - Delete availability schedule
   */
  deleteAvailability(availabilityId: number | undefined): void {
    if (!availabilityId || !confirm('Are you sure you want to delete this availability?')) {
      return;
    }

    this.availabilityService.deleteAvailability(availabilityId).subscribe({
      next: () => {
        this.successMessage = 'Availability deleted successfully';
        this.loadAvailabilities();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = 'Failed to delete availability: ' + error.message;
      }
    });
  }
}
