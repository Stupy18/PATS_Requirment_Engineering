import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AppointmentService } from '../../services/appointment.service';
import { Appointment, AppointmentHistory } from '../../models/appointment.model';
import {NavbarComponent} from "../../shared/navbar/navbar";

@Component({
  selector: 'app-appointment-history',
  standalone: true,
    imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './appointment-history.component.html',
  styleUrls: ['./appointment-history.component.scss']
})
export class AppointmentHistoryComponent implements OnInit {

  appointmentHistories: AppointmentHistory[] = [];
  appointments: Appointment[] = [];
  loading: boolean = false;
  errorMessage: string = '';
  patientId: number = 1; // Should come from logged-in user
  activeTab: string = 'upcoming'; // upcoming or history

  constructor(private appointmentService: AppointmentService) { }

  ngOnInit(): void {
    this.loadAppointmentHistory();
  }

  /**
   * FR9.8 - Load appointment history
   */
  loadAppointmentHistory(): void {
    this.loading = true;

    this.appointmentService.getPatientAppointmentHistory(this.patientId).subscribe({
      next: (history) => {
        this.appointmentHistories = history;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load appointment history: ' + error.message;
        this.loading = false;
      }
    });

    this.appointmentService.getAppointmentsByPatient(this.patientId).subscribe({
      next: (appointments) => {
        this.appointments = appointments;
      },
      error: (error) => {
        console.error('Failed to load appointments:', error);
      }
    });
  }

  /**
   * Get upcoming appointments
   */
  getUpcomingAppointments(): Appointment[] {
    const now = new Date();
    return this.appointments.filter(apt => {
      const aptDate = new Date(apt.appointmentDateTime);
      return aptDate > now && apt.status !== 'CANCELLED';
    });
  }

  /**
   * Get past appointments
   */
  getPastAppointments(): Appointment[] {
    const now = new Date();
    return this.appointments.filter(apt => {
      const aptDate = new Date(apt.appointmentDateTime);
      return aptDate < now || apt.status === 'CANCELLED';
    });
  }

  /**
   * Get status badge class
   */
  getStatusClass(status: string): string {
    switch (status) {
      case 'SCHEDULED':
      case 'CONFIRMED':
        return 'badge-info';
      case 'COMPLETED':
      case 'ATTENDED':
        return 'badge-success';
      case 'CANCELLED':
      case 'NO_SHOW':
        return 'badge-danger';
      default:
        return 'badge-default';
    }
  }

  /**
   * Get attendance status badge class
   */
  getAttendanceClass(status: string): string {
    switch (status) {
      case 'ATTENDED':
      case 'COMPLETED_EARLY':
      case 'COMPLETED_LATE':
        return 'badge-success';
      case 'NO_SHOW':
        return 'badge-danger';
      case 'CANCELLED':
        return 'badge-warning';
      default:
        return 'badge-default';
    }
  }
}
