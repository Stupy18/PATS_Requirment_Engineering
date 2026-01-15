import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Patient, CreatePatientRequest } from '../models/patient.model';

@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private apiUrl = 'http://localhost:8080/api/patients';

  constructor(private http: HttpClient) {}

  // Get all patients
getAllPatients(): Observable<Patient[]> {
  const token = localStorage.getItem('token');
  const headers = { 'Authorization': `Bearer ${token}` };
  
  return this.http.get<Patient[]>(this.apiUrl, { headers });
}

  // Get patient by ID
  getPatientById(id: number): Observable<Patient> {
    return this.http.get<Patient>(`${this.apiUrl}/${id}`);
  }

  // Create new patient
createPatient(request: CreatePatientRequest): Observable<Patient> {
  const token = localStorage.getItem('token');
  return this.http.post<Patient>(this.apiUrl, request, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
}

  // Update patient
  updatePatient(id: number, patient: Patient): Observable<Patient> {
    return this.http.put<Patient>(`${this.apiUrl}/${id}`, patient);
  }

  // Search patients
  searchPatients(query: string): Observable<Patient[]> {
    return this.http.get<Patient[]>(`${this.apiUrl}/search?q=${query}`);
  }
}