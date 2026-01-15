import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PatientRecord } from '../models/patient-record.model';
import { EmergencyContact } from '../models/emergency-contact.model';
import { AuditLog } from '../models/audit-log.model';

@Injectable({
  providedIn: 'root'
})
export class PatientRecordService {
  private apiUrl = 'http://localhost:8080/api/emr';

  constructor(private http: HttpClient) {}

  // FR17.1: Patient Record Operations
  createPatientRecord(record: PatientRecord): Observable<PatientRecord> {
    return this.http.post<PatientRecord>(`${this.apiUrl}/records`, record);
  }

  getPatientRecord(id: number): Observable<PatientRecord> {
    return this.http.get<PatientRecord>(`${this.apiUrl}/records/${id}`);
  }

  updatePatientRecord(id: number, record: PatientRecord): Observable<PatientRecord> {
    return this.http.put<PatientRecord>(`${this.apiUrl}/records/${id}`, record);
  }

  // FR17.6: Emergency Contacts
  addEmergencyContact(patientId: number, contact: EmergencyContact): Observable<EmergencyContact> {
    return this.http.post<EmergencyContact>(
      `${this.apiUrl}/patients/${patientId}/emergency-contacts`,
      contact
    );
  }

  getEmergencyContacts(patientId: number): Observable<EmergencyContact[]> {
    return this.http.get<EmergencyContact[]>(
      `${this.apiUrl}/patients/${patientId}/emergency-contacts`
    );
  }

  deleteEmergencyContact(contactId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/emergency-contacts/${contactId}`);
  }

  // FR17.9: Audit Trail
  getAuditTrail(patientRecordId: number): Observable<AuditLog[]> {
    return this.http.get<AuditLog[]>(
      `${this.apiUrl}/records/${patientRecordId}/audit-trail`
    );
  }
}