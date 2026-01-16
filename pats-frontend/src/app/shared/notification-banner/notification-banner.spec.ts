import { TestBed } from '@angular/core/testing';
import { NotificationBannerComponent } from './notification-banner';

describe('NotificationBannerComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotificationBannerComponent]
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(NotificationBannerComponent);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
