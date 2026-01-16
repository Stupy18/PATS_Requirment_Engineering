import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Appointment,
  AppointmentHistory,
  Availability,
  RescheduleAppointmentRequest,
  CancelAppointmentRequest,
  AppointmentReminder
} from '../models/appointment.model';

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {

  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  /**
   * FR9.2 - Get available time slots for a psychologist
   */
  getAvailableSlots(psychologistId: number, startTime: string, endTime: string): Observable<Appointment[]> {
    let params = new HttpParams();
    params = params.set('startTime', startTime);
    params = params.set('endTime', endTime);

    return this.http.get<Appointment[]>(
      `${this.apiUrl}/appointments/available-slots/${psychologistId}`,
      { params }
    );
  }

  /**
   * FR9.2/FR9.7 - Book an appointment
   */
  bookAppointment(appointment: Appointment): Observable<Appointment> {
    return this.http.post<Appointment>(
      `${this.apiUrl}/appointments/book`,
      appointment
    );
  }

  /**
   * FR9.4 - Reschedule appointment with 24-hour notice
   */
  rescheduleAppointment(request: RescheduleAppointmentRequest): Observable<Appointment> {
    return this.http.put<Appointment>(
      `${this.apiUrl}/appointments/reschedule`,
      request
    );
  }

  /**
   * FR9.5 - Cancel appointment with notification
   */
  cancelAppointment(request: CancelAppointmentRequest): Observable<any> {
    return this.http.post<any>(
      `${this.apiUrl}/appointments/cancel`,
      request
    );
  }

  /**
   * FR9.8 - Get appointment history for patient
   */
  getPatientAppointmentHistory(patientId: number): Observable<AppointmentHistory[]> {
    return this.http.get<AppointmentHistory[]>(
      `${this.apiUrl}/appointments/history/patient/${patientId}`
    );
  }

  /**
   * FR9.8 - Get appointment history for psychologist
   */
  getPsychologistAppointmentHistory(psychologistId: number): Observable<AppointmentHistory[]> {
    return this.http.get<AppointmentHistory[]>(
      `${this.apiUrl}/appointments/history/psychologist/${psychologistId}`
    );
  }

  /**
   * FR9.8 - Record appointment history (attendance status)
   */
  recordAppointmentHistory(
    appointmentId: number,
    attendanceStatus: string,
    notes?: string,
    actualDurationMinutes?: number
  ): Observable<AppointmentHistory> {
    let params = new HttpParams();
    params = params.set('appointmentId', appointmentId.toString());
    params = params.set('attendanceStatus', attendanceStatus);
    if (notes) {
      params = params.set('notes', notes);
    }
    if (actualDurationMinutes) {
      params = params.set('actualDurationMinutes', actualDurationMinutes.toString());
    }

    return this.http.post<AppointmentHistory>(
      `${this.apiUrl}/appointments/history/record`,
      {},
      { params }
    );
  }

  /**
   * Get appointments for psychologist
   */
  getAppointmentsByPsychologist(psychologistId: number): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(
      `${this.apiUrl}/appointments/psychologist/${psychologistId}`
    );
  }

  /**
   * Get appointments for patient
   */
  getAppointmentsByPatient(patientId: number): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(
      `${this.apiUrl}/appointments/patient/${patientId}`
    );
  }

  /**
   * Get appointment details
   */
  getAppointment(appointmentId: number): Observable<Appointment> {
    return this.http.get<Appointment>(
      `${this.apiUrl}/appointments/${appointmentId}`
    );
  }
}
