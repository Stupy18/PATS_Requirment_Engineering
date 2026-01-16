export interface Appointment {
  id?: number;
  psychologistId: number;
  patientId: number;
  appointmentDateTime: string; // ISO format
  durationMinutes: number;
  status: string; // SCHEDULED, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW
  type: string; // INITIAL, FOLLOWUP, EMERGENCY, VIDEO, IN_PERSON
  appointmentNotes?: string;
  originalDateTime?: string;
  rescheduledAt?: string;
  cancelledAt?: string;
  cancellationReason?: string;
  createdAt?: string;
}

export interface AppointmentHistory {
  id?: number;
  appointmentId: number;
  attendanceStatus: string; // ATTENDED, NO_SHOW, CANCELLED, RESCHEDULED, COMPLETED_EARLY, COMPLETED_LATE
  notes?: string;
  actualDurationMinutes?: number;
  externalCalendarProvider?: string;
  externalCalendarSyncId?: string;
  createdAt?: string;
}

export interface AppointmentReminder {
  id?: number;
  appointmentId: number;
  reminderTime: string; // ISO format
  hoursBefore: number; // 24 or 1
  status: string; // PENDING, SENT, COMPLETED, SKIPPED
  sentAt?: string;
  recipientEmail: string;
  reminderType: string; // APPOINTMENT_REMINDER, CANCELLATION_NOTICE, etc.
  createdAt?: string;
}

export interface Availability {
  id?: number;
  psychologistId: number;
  dayOfWeek: string; // MONDAY, TUESDAY, etc.
  startTime: string; // HH:mm format
  endTime: string; // HH:mm format
  specificDate?: string; // YYYY-MM-DD format
  isAvailable: boolean;
  notes?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface RescheduleAppointmentRequest {
  appointmentId: number;
  newDateTime: string; // ISO format
}

export interface CancelAppointmentRequest {
  appointmentId: number;
  cancellationReason: string;
}
