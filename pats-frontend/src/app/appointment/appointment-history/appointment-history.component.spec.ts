import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppointmentHistoryComponent } from './appointment-history.component';
import { AppointmentService } from '../../services/appointment.service';
import { of, throwError } from 'rxjs';

describe('AppointmentHistoryComponent', () => {
  let component: AppointmentHistoryComponent;
  let fixture: ComponentFixture<AppointmentHistoryComponent>;
  let appointmentService: jasmine.SpyObj<AppointmentService>;

  beforeEach(async () => {
    const appointmentServiceSpy = jasmine.createSpyObj('AppointmentService', [
      'getPatientAppointmentHistory',
      'getAppointmentsByPatient'
    ]);

    await TestBed.configureTestingModule({
      imports: [AppointmentHistoryComponent],
      providers: [
        { provide: AppointmentService, useValue: appointmentServiceSpy }
      ]
    }).compileComponents();

    appointmentService = TestBed.inject(AppointmentService) as jasmine.SpyObj<AppointmentService>;
    fixture = TestBed.createComponent(AppointmentHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  /**
   * Test FR9.8 - Load appointment history
   */
  it('should load appointment history on init', () => {
    const mockHistory = [
      {
        id: 1,
        appointmentId: 1,
        attendanceStatus: 'ATTENDED',
        notes: 'Good session',
        actualDurationMinutes: 60
      }
    ];

    const mockAppointments = [
      {
        id: 1,
        psychologistId: 1,
        patientId: 1,
        appointmentDateTime: '2025-12-01T10:00:00',
        durationMinutes: 60,
        status: 'COMPLETED',
        type: 'FOLLOWUP'
      }
    ];

    appointmentService.getPatientAppointmentHistory.and.returnValue(of(mockHistory));
    appointmentService.getAppointmentsByPatient.and.returnValue(of(mockAppointments));

    component.ngOnInit();

    expect(appointmentService.getPatientAppointmentHistory).toHaveBeenCalledWith(1);
    expect(component.appointmentHistories.length).toBe(1);
    expect(component.appointments.length).toBe(1);
  });

  /**
   * Test FR9.8 - Filter upcoming appointments
   */
  it('should filter upcoming appointments correctly', () => {
    const futureDate = new Date();
    futureDate.setDate(futureDate.getDate() + 5);
    const pastDate = new Date();
    pastDate.setDate(pastDate.getDate() - 5);

    component.appointments = [
      {
        id: 1,
        psychologistId: 1,
        patientId: 1,
        appointmentDateTime: futureDate.toISOString(),
        durationMinutes: 60,
        status: 'SCHEDULED',
        type: 'FOLLOWUP'
      },
      {
        id: 2,
        psychologistId: 1,
        patientId: 1,
        appointmentDateTime: pastDate.toISOString(),
        durationMinutes: 60,
        status: 'COMPLETED',
        type: 'FOLLOWUP'
      }
    ];

    const upcoming = component.getUpcomingAppointments();

    expect(upcoming.length).toBe(1);
    expect(upcoming[0].id).toBe(1);
  });

  /**
   * Test FR9.8 - Filter past appointments
   */
  it('should filter past appointments correctly', () => {
    const futureDate = new Date();
    futureDate.setDate(futureDate.getDate() + 5);
    const pastDate = new Date();
    pastDate.setDate(pastDate.getDate() - 5);

    component.appointments = [
      {
        id: 1,
        psychologistId: 1,
        patientId: 1,
        appointmentDateTime: futureDate.toISOString(),
        durationMinutes: 60,
        status: 'SCHEDULED',
        type: 'FOLLOWUP'
      },
      {
        id: 2,
        psychologistId: 1,
        patientId: 1,
        appointmentDateTime: pastDate.toISOString(),
        durationMinutes: 60,
        status: 'COMPLETED',
        type: 'FOLLOWUP'
      }
    ];

    const past = component.getPastAppointments();

    expect(past.length).toBe(1);
    expect(past[0].id).toBe(2);
  });

  /**
   * Test FR9.8 - Handle error loading history
   */
  it('should handle error loading history', () => {
    const error = new Error('Server error');
    appointmentService.getPatientAppointmentHistory.and.returnValue(throwError(() => error));
    appointmentService.getAppointmentsByPatient.and.returnValue(of([]));

    component.loadAppointmentHistory();

    expect(component.errorMessage).toContain('Failed');
  });

  /**
   * Test attendance status badge styling
   */
  it('should return correct badge class for attendance status', () => {
    expect(component.getAttendanceClass('ATTENDED')).toBe('badge-success');
    expect(component.getAttendanceClass('NO_SHOW')).toBe('badge-danger');
    expect(component.getAttendanceClass('CANCELLED')).toBe('badge-warning');
  });

  /**
   * Test appointment status badge styling
   */
  it('should return correct badge class for appointment status', () => {
    expect(component.getStatusClass('SCHEDULED')).toBe('badge-info');
    expect(component.getStatusClass('COMPLETED')).toBe('badge-success');
    expect(component.getStatusClass('CANCELLED')).toBe('badge-danger');
  });
});
