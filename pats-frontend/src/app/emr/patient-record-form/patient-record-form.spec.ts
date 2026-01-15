import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PatientRecordForm } from './patient-record-form';

describe('PatientRecordForm', () => {
  let component: PatientRecordForm;
  let fixture: ComponentFixture<PatientRecordForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatientRecordForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PatientRecordForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
