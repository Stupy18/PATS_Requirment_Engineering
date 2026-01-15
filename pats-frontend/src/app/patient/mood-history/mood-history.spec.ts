import { TestBed } from '@angular/core/testing';
import { MoodHistoryComponent } from './mood-history';

describe('MoodHistoryComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MoodHistoryComponent]
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(MoodHistoryComponent);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
