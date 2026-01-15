import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { AvailabilityScheduleComponent } from './availability-schedule.component';
import { AvailabilityService } from '../../services/availability.service';
import { of, throwError } from 'rxjs';

describe('AvailabilityScheduleComponent', () => {
  let component: AvailabilityScheduleComponent;
  let fixture: ComponentFixture<AvailabilityScheduleComponent>;
  let availabilityService: jasmine.SpyObj<AvailabilityService>;

  beforeEach(async () => {
    const availabilityServiceSpy = jasmine.createSpyObj('AvailabilityService', [
      'createAvailability',
      'updateAvailability',
      'deleteAvailability',
      'getAllAvailabilities'
    ]);

    await TestBed.configureTestingModule({
      imports: [AvailabilityScheduleComponent, ReactiveFormsModule],
      providers: [
        { provide: AvailabilityService, useValue: availabilityServiceSpy }
      ]
    }).compileComponents();

    availabilityService = TestBed.inject(AvailabilityService) as jasmine.SpyObj<AvailabilityService>;
    fixture = TestBed.createComponent(AvailabilityScheduleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  /**
   * Test FR9.1 - Load availabilities on init
   */
  it('should load availabilities on init', () => {
    const mockAvailabilities = [
      {
        id: 1,
        psychologistId: 1,
        dayOfWeek: 'MONDAY',
        startTime: '09:00',
        endTime: '17:00',
        isAvailable: true
      }
    ];

    availabilityService.getAllAvailabilities.and.returnValue(of(mockAvailabilities));

    component.ngOnInit();

    expect(availabilityService.getAllAvailabilities).toHaveBeenCalledWith(1);
    expect(component.availabilities.length).toBe(1);
  });

  /**
   * Test FR9.1 - Create availability successfully
   */
  it('should create availability successfully', () => {
    const mockAvailability = {
      id: 1,
      psychologistId: 1,
      dayOfWeek: 'MONDAY',
      startTime: '09:00',
      endTime: '17:00',
      isAvailable: true
    };

    availabilityService.createAvailability.and.returnValue(of(mockAvailability));
    availabilityService.getAllAvailabilities.and.returnValue(of([mockAvailability]));

    component.availabilityForm.patchValue({
      dayOfWeek: 'MONDAY',
      startTime: '09:00',
      endTime: '17:00'
    });

    component.createAvailability();

    expect(availabilityService.createAvailability).toHaveBeenCalled();
    expect(component.successMessage).toContain('successfully');
  });

  /**
   * Test FR9.1 - Delete availability
   */
  it('should delete availability when confirmed', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    availabilityService.deleteAvailability.and.returnValue(of(null));
    availabilityService.getAllAvailabilities.and.returnValue(of([]));

    component.deleteAvailability(1);

    expect(availabilityService.deleteAvailability).toHaveBeenCalledWith(1);
    expect(component.successMessage).toContain('deleted');
  });

  /**
   * Test FR9.1 - Handle error when creating availability
   */
  it('should handle error when creating availability fails', () => {
    const error = new Error('Server error');
    availabilityService.createAvailability.and.returnValue(throwError(() => error));

    component.availabilityForm.patchValue({
      dayOfWeek: 'MONDAY',
      startTime: '09:00',
      endTime: '17:00'
    });

    component.createAvailability();

    expect(component.errorMessage).toContain('Failed');
  });

  /**
   * Test form validation
   */
  it('should validate form before creating availability', () => {
    component.availabilityForm.reset();
    component.createAvailability();

    expect(component.errorMessage).toContain('required');
    expect(availabilityService.createAvailability).not.toHaveBeenCalled();
  });
});
