export interface AuditLog {
  id?: number;
  userId: number;
  userEmail: string;
  patientRecordId?: number;
  action: string; // CREATED, VIEWED, UPDATED, DELETED
  actionDetails?: string;
  actionTimestamp: Date;
  ipAddress?: string;
}