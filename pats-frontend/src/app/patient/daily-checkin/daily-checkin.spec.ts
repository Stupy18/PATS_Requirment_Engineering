import { TestBed } from '@angular/core/testing';
import { DailyCheckinComponent } from './daily-checkin';

describe('DailyCheckinComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DailyCheckinComponent]
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(DailyCheckinComponent);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
