import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PatientRecordService } from '../../services/patient-record.service';
import { PatientService } from '../../services/patient.service';
import { PatientRecord } from '../../models/patient-record.model';
import { Patient } from '../../models/patient.model';
import {NavbarComponent} from '../../shared/navbar/navbar';

@Component({
  selector: 'app-patient-record-form',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './patient-record-form.html',
  styleUrls: ['./patient-record-form.scss']
})
export class PatientRecordFormComponent implements OnInit {
  patientId!: number;
  recordId?: number;
  isEditMode = false;
  isLoading = true;

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

  selectedPatient?: Patient;

  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private patientRecordService: PatientRecordService,
    private patientService: PatientService,
    public router: Router
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.patientId = +params['id'];

      if (!this.patientId) {
        this.errorMessage = 'Patient ID is missing!';
        this.isLoading = false;
        return;
      }

      console.log('Patient ID from URL:', this.patientId);
      this.record.patientId = this.patientId;

      this.loadPatientData();
      this.loadExistingRecord();
    });
  }

  loadPatientData(): void {
    this.patientService.getPatientById(this.patientId).subscribe({
      next: (patient) => {
        console.log('Patient data loaded:', patient);
        this.selectedPatient = patient;

        if (!this.isEditMode && !this.record.city) {
          this.record.city = patient.city;
          this.record.postalCode = patient.postalCode;
        }
      },
      error: (error) => {
        console.error('Error loading patient:', error);
        this.errorMessage = 'Failed to load patient information';
        this.isLoading = false;
      }
    });
  }

  loadExistingRecord(): void {
    this.patientRecordService.getPatientRecordByPatientId(this.patientId).subscribe({
      next: (existingRecord) => {
        console.log('Existing medical record found:', existingRecord);

        this.recordId = existingRecord.id;
        this.record = { ...existingRecord };
        this.isEditMode = true;
        this.isLoading = false;

        this.successMessage = '✓ Medical record loaded. Update information below.';
      },
      error: (error) => {
        console.log('No existing record found:', error);

        if (error.status === 404) {
          this.isEditMode = false;
          this.isLoading = false;
        } else {
          this.errorMessage = 'Error loading medical record';
          this.isLoading = false;
        }
      }
    });
  }

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    console.log(`${this.isEditMode ? 'Updating' : 'Creating'} medical record:`, this.record);

    if (this.isEditMode && this.recordId) {
      // UPDATE
      this.patientRecordService.updatePatientRecord(this.recordId, this.record).subscribe({
        next: (updated) => {
          console.log('Record updated successfully:', updated);
          this.successMessage = '✓ Medical record updated successfully!';
          this.record = { ...updated };

          setTimeout(() => this.router.navigate(['/psychologist/patients']), 1500);
        },
        error: (error) => {
          console.error('Error updating record:', error);
          this.errorMessage = error.error?.message || 'Failed to update medical record';
        }
      });
    } else {
      // CREATE
      this.patientRecordService.createPatientRecord(this.record).subscribe({
        next: (created) => {
          console.log('Record created successfully:', created);
          this.successMessage = '✓ Medical record initialized successfully!';

          this.recordId = created.id;
          this.record = { ...created };
          this.isEditMode = true;

          setTimeout(() => this.router.navigate(['/psychologist/patients']), 1500);
        },
        error: (error) => {
          console.error('Error creating record:', error);
          this.errorMessage = error.error?.message || 'Failed to create medical record';
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/psychologist/patients']);
  }
}
