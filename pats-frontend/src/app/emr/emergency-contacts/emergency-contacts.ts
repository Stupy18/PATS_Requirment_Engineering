import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PatientRecordService } from '../../services/patient-record.service';
import { EmergencyContact } from '../../models/emergency-contact.model';

@Component({
  selector: 'app-emergency-contacts',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './emergency-contacts.html',
  styleUrls: ['./emergency-contacts.scss']
})
export class EmergencyContactsComponent implements OnInit {
  @Input() patientId!: number;

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

  constructor(private patientRecordService: PatientRecordService) {}

  ngOnInit(): void {
    this.loadContacts();
  }

  loadContacts(): void {
    if (!this.patientId) return;
    
    this.patientRecordService.getEmergencyContacts(this.patientId).subscribe({
      next: (contacts) => {
        this.contacts = contacts;
      },
      error: (error) => {
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
    this.errorMessage = '';
    this.successMessage = '';
    this.newContact.patientId = this.patientId;

    this.patientRecordService.addEmergencyContact(this.patientId, this.newContact).subscribe({
      next: (created) => {
        this.successMessage = 'Emergency contact added successfully!';
        this.contacts.push(created);
        this.showAddForm = false;
        this.resetForm();
      },
      error: (error) => {
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
}