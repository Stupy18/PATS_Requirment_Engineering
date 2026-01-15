import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PatientRecordService } from '../../services/patient-record.service';
import { AuditLog } from '../../models/audit-log.model';

@Component({
  selector: 'app-audit-log-viewer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './audit-log-viewer.html',
  styleUrls: ['./audit-log-viewer.scss']
})
export class AuditLogViewerComponent implements OnInit {
  @Input() patientRecordId?: number;

  logs: AuditLog[] = [];
  loading = false;
  errorMessage = '';

  constructor(private patientRecordService: PatientRecordService) {}

  ngOnInit(): void {
    if (this.patientRecordId) {
      this.loadAuditLogs();
    }
  }

  loadAuditLogs(): void {
    if (!this.patientRecordId) return;

    this.loading = true;
    this.errorMessage = '';

    this.patientRecordService.getAuditTrail(this.patientRecordId).subscribe({
      next: (logs) => {
        this.logs = logs;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load audit logs';
        this.loading = false;
        console.error('Audit log error:', error);
      }
    });
  }

  formatDate(date: Date): string {
    return new Date(date).toLocaleString('ro-RO', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  }

  getActionBadgeClass(action: string): string {
    switch (action) {
      case 'CREATED': return 'badge-success';
      case 'UPDATED': return 'badge-info';
      case 'VIEWED': return 'badge-secondary';
      case 'DELETED': return 'badge-danger';
      default: return 'badge-default';
    }
  }

  getActionIcon(action: string): string {
    switch (action) {
      case 'CREATED': return '✓';
      case 'UPDATED': return '✎';
      case 'VIEWED': return '👁';
      case 'DELETED': return '✖';
      default: return '•';
    }
  }
}