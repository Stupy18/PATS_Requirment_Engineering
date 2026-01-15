import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BookAppointmentComponent } from './book-appointment.component';
import { AppointmentService } from '../../services/appointment.service';
import { of, throwError } from 'rxjs';

describe('BookAppointmentComponent', () => {
  let component: BookAppointmentComponent;
  let fixture: ComponentFixture<BookAppointmentComponent>;
  let appointmentService: jasmine.SpyObj<AppointmentService>;

  beforeEach(async () => {
    const appointmentServiceSpy = jasmine.createSpyObj('AppointmentService', [
      'bookAppointment',
      'getAvailableSlots',
      'getAppointmentsByPatient'
    ]);

    await TestBed.configureTestingModule({
      imports: [BookAppointmentComponent, ReactiveFormsModule],
      providers: [
        { provide: AppointmentService, useValue: appointmentServiceSpy }
      ]
    }).compileComponents();

    appointmentService = TestBed.inject(AppointmentService) as jasmine.SpyObj<AppointmentService>;
    fixture = TestBed.createComponent(BookAppointmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  /**
   * Test FR9.2 - Search available slots
   */
  it('should search available slots', () => {
    const mockSlots = [
      {
        id: 1,
        psychologistId: 1,
        patientId: 1,
        appointmentDateTime: '2026-02-01T10:00:00',
        durationMinutes: 60,
        status: 'SCHEDULED',
        type: 'INITIAL'
      }
    ];

    appointmentService.getAvailableSlots.and.returnValue(of(mockSlots));

    component.bookingForm.patchValue({
      psychologistId: 1,
      appointmentDate: '2026-02-01'
    });

    component.searchAvailableSlots();

    expect(appointmentService.getAvailableSlots).toHaveBeenCalled();
    expect(component.availableSlots.length).toBe(1);
    expect(component.availableSlots[0].durationMinutes).toBe(60);
  });

  /**
   * Test FR9.2 & FR9.7 - Book appointment successfully
   */
  it('should book appointment successfully', () => {
    const mockAppointment = {
      id: 1,
      psychologistId: 1,
      patientId: 1,
      appointmentDateTime: '2026-02-01T10:00:00',
      durationMinutes: 60,
      status: 'SCHEDULED',
      type: 'INITIAL'
    };

    appointmentService.bookAppointment.and.returnValue(of(mockAppointment));

    component.bookingForm.patchValue({
      psychologistId: 1,
      appointmentDate: '2026-02-01',
      appointmentTime: '10:00',
      type: 'INITIAL',
      durationMinutes: 60
    });

    component.bookAppointment();

    expect(appointmentService.bookAppointment).toHaveBeenCalled();
    expect(component.successMessage).toContain('successfully');
  });

  /**
   * Test FR9.7 - Prevent double-booking error
   */
  it('should handle double-booking error', () => {
    const error = { error: { message: 'Time slot already booked' } };
    appointmentService.bookAppointment.and.returnValue(throwError(() => error));

    component.bookingForm.patchValue({
      psychologistId: 1,
      appointmentDate: '2026-02-01',
      appointmentTime: '10:00',
      type: 'INITIAL',
      durationMinutes: 60
    });

    component.bookAppointment();

    expect(component.errorMessage).toContain('Time slot already booked');
  });

  /**
   * Test validation for booking form
   */
  it('should show error when form is invalid', () => {
    component.bookingForm.reset();
    component.bookAppointment();

    expect(component.errorMessage).toContain('required');
  });
});
