import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PatientService } from '../../services/patient.service';
import { CreatePatientRequest } from '../../models/patient.model';
import {NavbarComponent} from '../../shared/navbar/navbar';

@Component({
  selector: 'app-add-patient',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './patient.html',
  styleUrls: ['./patient.scss']
})
export class AddPatientComponent {
  patientData: CreatePatientRequest = {
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    phoneNumber: '',
    country: 'Romania'
  };

  errorMessage = '';
  successMessage = '';
  isSubmitting = false;

  constructor(
    private patientService: PatientService,
    public router: Router
  ) {}

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.isSubmitting = true;

    console.log('Creating patient:', this.patientData);

    this.patientService.createPatient(this.patientData).subscribe({
      next: (created) => {
        console.log('Patient created successfully:', created);
        this.successMessage = `Patient ${created.firstName} ${created.lastName} created successfully! (ID: ${created.id})`;
        this.isSubmitting = false;

        // Redirect to patient list or EMR form after 2 seconds
        setTimeout(() => {
          this.router.navigate(['/psychologist/patients']);
        }, 2000);
      },
      error: (error) => {
        console.error('Error creating patient:', error);
        this.errorMessage = error.error?.message || error.error || 'Failed to create patient';
        this.isSubmitting = false;
      }
    });
  }

  generateUsername(): void {
    // Auto-generate username from first and last name
    if (this.patientData.firstName && this.patientData.lastName) {
      this.patientData.username = (
        this.patientData.firstName.toLowerCase() +
        '.' +
        this.patientData.lastName.toLowerCase()
      ).replace(/\s+/g, '');
    }
  }

  generatePassword(): void {
    // Generate a random temporary password
    const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789';
    let password = '';
    for (let i = 0; i < 10; i++) {
      password += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    this.patientData.password = password;
  }
}
