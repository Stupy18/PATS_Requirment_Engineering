import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { MoodEntry, MoodEntryRequest } from '../models/mood-entry.model';

@Injectable({
  providedIn: 'root'
})
export class MoodEntryService {
  private apiUrl = 'http://localhost:8080/api/mood';

  constructor(private http: HttpClient) {}

  /**
   * Submit daily emotional check-in
   * FR1.1: Record emotional state on scale 1-10
   * FR1.2: Optional text notes
   * FR1.5: Timestamp entry
   */
  submitCheckin(request: MoodEntryRequest): Observable<MoodEntry> {
    let params = new HttpParams()
      .set('patientId', request.patientId.toString())
      .set('rating', request.rating.toString());
    
    // Only add notes if they exist and are not empty
    if (request.notes && request.notes.trim().length > 0) {
      params = params.set('notes', request.notes.trim());
    }

    console.log('Making POST request to:', `${this.apiUrl}/checkin`);
    console.log('With params:', params.toString());

    return this.http.post<MoodEntry>(`${this.apiUrl}/checkin`, null, { params }).pipe(
      tap(response => console.log('Check-in response:', response)),
      catchError(this.handleError)
    );
  }

  /**
   * Get patient's mood entry history
   * Returns all mood entries for a specific patient
   */
  getPatientHistory(patientId: number): Observable<MoodEntry[]> {
    return this.http.get<MoodEntry[]>(`${this.apiUrl}/history/${patientId}`).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Handle HTTP errors
   */
  private handleError(error: HttpErrorResponse) {
    console.error('HTTP Error occurred:', error);
    
    if (error.status === 0) {
      console.error('Network error - Unable to connect to backend');
      console.error('Check if backend is running on http://localhost:8080');
      console.error('Error details:', error.error);
    } else {
      console.error(`Backend returned code ${error.status}`);
      console.error('Error body:', error.error);
    }
    
    return throwError(() => error);
  }

  /**
   * Check if patient has completed check-in today
   * Helper method to validate if today's entry exists
   */
  hasCompletedTodayCheckin(entries: MoodEntry[]): boolean {
    if (!entries || entries.length === 0) {
      return false;
    }
    
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    return entries.some(entry => {
      const entryDate = new Date(entry.entryTimestamp);
      entryDate.setHours(0, 0, 0, 0);
      return entryDate.getTime() === today.getTime();
    });
  }
}
