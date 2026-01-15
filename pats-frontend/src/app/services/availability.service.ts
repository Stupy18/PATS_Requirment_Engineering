import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Availability } from '../models/appointment.model';

@Injectable({
  providedIn: 'root'
})
export class AvailabilityService {

  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  /**
   * FR9.1 - Create availability schedule for psychologist
   */
  createAvailability(availability: Availability): Observable<Availability> {
    return this.http.post<Availability>(
      `${this.apiUrl}/availability/create`,
      availability
    );
  }

  /**
   * FR9.1 - Update availability schedule
   */
  updateAvailability(availabilityId: number, availability: Availability): Observable<Availability> {
    return this.http.put<Availability>(
      `${this.apiUrl}/availability/update/${availabilityId}`,
      availability
    );
  }

  /**
   * FR9.1 - Delete availability schedule
   */
  deleteAvailability(availabilityId: number): Observable<any> {
    return this.http.delete<any>(
      `${this.apiUrl}/availability/delete/${availabilityId}`
    );
  }

  /**
   * FR9.1 - Get availability by day of week
   */
  getAvailabilityByDay(psychologistId: number, dayOfWeek: string): Observable<Availability[]> {
    return this.http.get<Availability[]>(
      `${this.apiUrl}/availability/psychologist/${psychologistId}/day/${dayOfWeek}`
    );
  }

  /**
   * FR9.1 - Get availability by specific date
   */
  getAvailabilityByDate(psychologistId: number, specificDate: string): Observable<Availability[]> {
    return this.http.get<Availability[]>(
      `${this.apiUrl}/availability/psychologist/${psychologistId}/date/${specificDate}`
    );
  }

  /**
   * FR9.1 - Get all available slots for psychologist
   */
  getAllAvailableSlots(psychologistId: number): Observable<Availability[]> {
    return this.http.get<Availability[]>(
      `${this.apiUrl}/availability/psychologist/${psychologistId}/available`
    );
  }

  /**
   * FR9.1 - Get all availabilities for psychologist
   */
  getAllAvailabilities(psychologistId: number): Observable<Availability[]> {
    return this.http.get<Availability[]>(
      `${this.apiUrl}/availability/psychologist/${psychologistId}/all`
    );
  }
}
