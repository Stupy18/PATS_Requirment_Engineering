import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PatientRecordService } from '../../services/patient-record.service';
import { EmergencyContact } from '../../models/emergency-contact.model';
import {NavbarComponent} from '../../shared/navbar/navbar';

@Component({
  selector: 'app-emergency-contacts',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './emergency-contacts.html',
  styleUrls: ['./emergency-contacts.scss']
})
export class EmergencyContactsComponent implements OnInit {
  patientId!: number;

  contacts: EmergencyContact[] = [];
  showAddForm = false;

  newContact: EmergencyContact = {
    patientId: 0,
    firstName: '',
    lastName: '',
    relationship: '',
    phoneNumber: '',
    isPrimary: false,
    priority: 1
  };

  errorMessage = '';
  successMessage = '';

  constructor(
    private patientRecordService: PatientRecordService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Ia patientId din URL params
    this.route.params.subscribe(params => {
      this.patientId = +params['id'];
      console.log('Emergency Contacts - Patient ID:', this.patientId);

      if (this.patientId) {
        this.loadContacts();
      } else {
        this.errorMessage = 'Patient ID is missing!';
      }
    });
  }

  loadContacts(): void {
    if (!this.patientId) {
      console.error('Cannot load contacts - patientId is undefined');
      return;
    }

    console.log('Loading contacts for patient:', this.patientId);

    this.patientRecordService.getEmergencyContacts(this.patientId).subscribe({
      next: (contacts) => {
        console.log('Contacts loaded:', contacts);
        this.contacts = contacts;
      },
      error: (error) => {
        console.error('Error loading contacts:', error);
        this.errorMessage = 'Failed to load emergency contacts';
      }
    });
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (this.showAddForm) {
      this.resetForm();
    }
  }

  resetForm(): void {
    this.newContact = {
      patientId: this.patientId,
      firstName: '',
      lastName: '',
      relationship: '',
      phoneNumber: '',
      isPrimary: false,
      priority: this.contacts.length + 1
    };
    this.errorMessage = '';
    this.successMessage = '';
  }

  onSubmit(): void {
    console.log('Submitting emergency contact for patient:', this.patientId);

    if (!this.patientId) {
      this.errorMessage = 'Patient ID is missing!';
      console.error('Cannot submit - patientId is undefined');
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';
    this.newContact.patientId = this.patientId;

    console.log('Sending request:', this.newContact);

    this.patientRecordService.addEmergencyContact(this.patientId, this.newContact).subscribe({
      next: (created) => {
        console.log('Contact created successfully:', created);
        this.successMessage = 'Emergency contact added successfully!';
        this.contacts.push(created);
        this.showAddForm = false;
        this.resetForm();
      },
      error: (error) => {
        console.error('Error adding contact:', error);
        this.errorMessage = error.error?.message || 'Failed to add emergency contact';
      }
    });
  }

  deleteContact(contactId: number): void {
    if (!confirm('Are you sure you want to delete this emergency contact?')) {
      return;
    }

    this.patientRecordService.deleteEmergencyContact(contactId).subscribe({
      next: () => {
        this.contacts = this.contacts.filter(c => c.id !== contactId);
        this.successMessage = 'Emergency contact deleted successfully!';
      },
      error: (error) => {
        this.errorMessage = 'Failed to delete emergency contact';
      }
    });
  }

  getPriorityBadge(priority?: number): string {
    if (!priority) return 'gray';
    if (priority === 1) return 'green';
    if (priority === 2) return 'blue';
    return 'gray';
  }

  goBack(): void {
    this.router.navigate(['/psychologist/patients']);
  }
}
