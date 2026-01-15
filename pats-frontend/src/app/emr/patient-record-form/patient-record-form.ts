import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PatientRecordService } from '../../services/patient-record.service';
import { PatientService } from '../../services/patient.service';
import { PatientRecord } from '../../models/patient-record.model';
import { Patient } from '../../models/patient.model';

@Component({
  selector: 'app-patient-record-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './patient-record-form.html',
  styleUrls: ['./patient-record-form.scss']
})
export class PatientRecordFormComponent implements OnInit {
  record: PatientRecord = {
    patientId: 0,
    cnp: '',
    occupation: '',
    alternatePhone: '',
    streetAddress: '',
    city: '',
    county: '',
    postalCode: ''
  };

  patients: Patient[] = [];
  selectedPatient?: Patient;
  loadingPatients = false;

  errorMessage: string = '';
  successMessage: string = '';
  detailedError: string = '';

  constructor(
    private patientRecordService: PatientRecordService,
    private patientService: PatientService,
    public router: Router
  ) {}

  ngOnInit(): void {
    this.loadPatients();
  }

  loadPatients(): void {
    this.loadingPatients = true;
    this.patientService.getAllPatients().subscribe({
      next: (patients) => {
        this.patients = patients;
        this.loadingPatients = false;
      },
      error: (error) => {
        console.error('Error loading patients:', error);
        this.loadingPatients = false;
      }
    });
  }

  onPatientSelect(): void {
    this.selectedPatient = this.patients.find(p => p.id === Number(this.record.patientId));
    if (this.selectedPatient) {
      this.record.city = this.selectedPatient.city;
      this.record.postalCode = this.selectedPatient.postalCode;
    }
  }

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.detailedError = '';

    // Validate patientId
    if (!this.record.patientId || this.record.patientId === 0) {
      this.errorMessage = 'Please select a patient from the dropdown';
      return;
    }

    console.log('Submitting patient record:', this.record);

    this.patientRecordService.createPatientRecord(this.record).subscribe({
      next: (created) => {
        this.successMessage = `Patient record created successfully!`;
        setTimeout(() => this.router.navigate(['/psychologist/patients']), 1500);
      },
      error: (error) => {
        console.error('Full error object:', error);
        this.errorMessage = error.error?.message || error.message || 'Failed to create patient record';
        
        if (error.error) {
          this.detailedError = JSON.stringify(error.error, null, 2);
        } else {
          this.detailedError = error.message || 'Unknown error';
        }
      }
    });
  }
}