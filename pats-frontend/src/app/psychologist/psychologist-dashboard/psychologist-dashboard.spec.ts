import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PsychologistDashboard } from './psychologist-dashboard';

describe('PsychologistDashboard', () => {
  let component: PsychologistDashboard;
  let fixture: ComponentFixture<PsychologistDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PsychologistDashboard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PsychologistDashboard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
