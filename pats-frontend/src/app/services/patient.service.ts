import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Patient, CreatePatientRequest } from '../models/patient.model';

@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // Helper method pentru headers cu token
  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // Get all patients
  getAllPatients(): Observable<Patient[]> {
    return this.http.get<Patient[]>(
      `${this.apiUrl}/patients`,
      { headers: this.getHeaders() }
    );
  }

  // Get patient by ID
  getPatientById(id: number): Observable<Patient> {
    return this.http.get<Patient>(
      `${this.apiUrl}/patients/${id}`,
      { headers: this.getHeaders() }
    );
  }

  // Create patient
  createPatient(patient: CreatePatientRequest): Observable<Patient> {
    return this.http.post<Patient>(
      `${this.apiUrl}/patients`,
      patient,
      { headers: this.getHeaders() }
    );
  }

  // Update patient
  updatePatient(id: number, patient: Patient): Observable<Patient> {
    return this.http.put<Patient>(
      `${this.apiUrl}/patients/${id}`,
      patient,
      { headers: this.getHeaders() }
    );
  }

  // Delete patient
  deletePatient(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/patients/${id}`,
      { headers: this.getHeaders() }
    );
  }
}