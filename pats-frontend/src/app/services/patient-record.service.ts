import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PatientRecord } from '../models/patient-record.model';
import { EmergencyContact } from '../models/emergency-contact.model';
import { AuditLog } from '../models/audit-log.model';

@Injectable({
  providedIn: 'root',
})
export class PatientRecordService {
  private apiUrl = 'http://localhost:8080/api/emr';

  constructor(private http: HttpClient) {}

  // Helper method to generate headers with token
  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }

  // FR17.1: Patient Record Operations
  createPatientRecord(record: PatientRecord): Observable<PatientRecord> {
    return this.http.post<PatientRecord>(
      `${this.apiUrl}/records/patient/${record.patientId}`,
      record,
      { headers: this.getHeaders() }
    );
  }

  getPatientRecord(id: number): Observable<PatientRecord> {
    return this.http.get<PatientRecord>(`${this.apiUrl}/records/${id}`, {
      headers: this.getHeaders(),
    });
  }

  getPatientRecordByPatientId(patientId: number): Observable<PatientRecord> {
    return this.http.get<PatientRecord>(`${this.apiUrl}/records/patient/${patientId}`, {
      headers: this.getHeaders(),
    });
  }

  updatePatientRecord(id: number, record: PatientRecord): Observable<PatientRecord> {
    return this.http.put<PatientRecord>(`${this.apiUrl}/records/${id}`, record, {
      headers: this.getHeaders(),
    });
  }

  // FR17.6: Emergency Contacts
  addEmergencyContact(patientId: number, contact: EmergencyContact): Observable<EmergencyContact> {
    return this.http.post<EmergencyContact>(
      `${this.apiUrl}/patients/${patientId}/emergency-contacts`,
      contact,
      { headers: this.getHeaders() }
    );
  }

  getEmergencyContacts(patientId: number): Observable<EmergencyContact[]> {
    return this.http.get<EmergencyContact[]>(
      `${this.apiUrl}/patients/${patientId}/emergency-contacts`,
      { headers: this.getHeaders() }
    );
  }

  deleteEmergencyContact(contactId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/emergency-contacts/${contactId}`, {
      headers: this.getHeaders(),
    });
  }

  // FR17.9: Audit Trail
  getAuditTrail(patientRecordId: number): Observable<AuditLog[]> {
    return this.http.get<AuditLog[]>(`${this.apiUrl}/records/${patientRecordId}/audit-trail`, {
      headers: this.getHeaders(),
    });
  }
}
