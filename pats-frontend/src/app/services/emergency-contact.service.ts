import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EmergencyContact } from '../models/emergency-contact.model';

@Injectable({
  providedIn: 'root'
})
export class EmergencyContactService {
  private apiUrl = 'http://localhost:8080/api/emr';

  constructor(private http: HttpClient) {}

  // Helper method to get headers with JWT token
  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // Add emergency contact
  addEmergencyContact(patientId: number, contact: EmergencyContact): Observable<EmergencyContact> {
    return this.http.post<EmergencyContact>(
      `${this.apiUrl}/patients/${patientId}/emergency-contacts`,
      contact,
      { headers: this.getHeaders() }
    );
  }

  // Get all emergency contacts for a patient
  getEmergencyContacts(patientId: number): Observable<EmergencyContact[]> {
    return this.http.get<EmergencyContact[]>(
      `${this.apiUrl}/patients/${patientId}/emergency-contacts`,
      { headers: this.getHeaders() }
    );
  }

  // Delete emergency contact
  deleteEmergencyContact(contactId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/emergency-contacts/${contactId}`,
      { headers: this.getHeaders() }
    );
  }
}