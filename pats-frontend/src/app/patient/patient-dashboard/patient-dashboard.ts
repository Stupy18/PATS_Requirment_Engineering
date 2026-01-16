import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { User } from '../../models/user.model';
import { NavbarComponent } from '../../shared/navbar/navbar';
import { NotificationBannerComponent } from '../../shared/notification-banner/notification-banner';
import { AuthService } from '../../auth/auth';
import { MoodEntryService } from '../../services/mood-entry.service';
import { NotificationService } from '../../services/notification.service';
import { MoodEntry } from '../../models/mood-entry.model';

@Component({
  selector: 'app-patient-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent, NotificationBannerComponent],
  templateUrl: './patient-dashboard.html',
  styleUrls: ['./patient-dashboard.scss']
})
export class PatientDashboardComponent implements OnInit {
  currentUser: User | null = null;
  hasCompletedTodayCheckin: boolean = false;
  todayMoodEntry: MoodEntry | null = null;
  isLoadingMood: boolean = true;

  constructor(
    private authService: AuthService,
    private moodEntryService: MoodEntryService,
    private notificationService: NotificationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (user && user.role === 'PATIENT') {
        this.checkTodayMoodEntry();
      }
    });
  }

  /**
   * Check if patient has completed today's mood check-in
   */
  private checkTodayMoodEntry(): void {
    if (this.currentUser?.profileId) {
      this.moodEntryService.getPatientHistory(this.currentUser.profileId).subscribe({
        next: (entries) => {
          this.isLoadingMood = false;
          this.hasCompletedTodayCheckin = this.moodEntryService.hasCompletedTodayCheckin(entries);
          
          if (this.hasCompletedTodayCheckin) {
            // Find today's entry
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            
            this.todayMoodEntry = entries.find(entry => {
              const entryDate = new Date(entry.entryTimestamp);
              entryDate.setHours(0, 0, 0, 0);
              return entryDate.getTime() === today.getTime();
            }) || null;
          } else {
            // FR1.4: Check if reminder should be shown (after 8:00 PM)
            this.checkReminderNotification();
          }
        },
        error: (error) => {
          console.error('Error checking mood entry:', error);
          this.isLoadingMood = false;
        }
      });
    }
  }

  /**
   * FR1.4: Check and show reminder notification if it's past 8:00 PM
   * and patient hasn't completed today's check-in
   */
  private checkReminderNotification(): void {
    if (this.notificationService.shouldShowReminderToday()) {
      this.notificationService.showCheckinReminder();
      this.notificationService.markReminderShown();
    }
  }

  /**
   * Manual trigger for testing reminder notification
   * Can be removed in production
   */
  testReminder(): void {
    this.notificationService.showCheckinReminder();
  }

  /**
   * Navigate to daily check-in page
   */
  goToDailyCheckin(): void {
    this.router.navigate(['/patient/daily-checkin']);
  }

  /**
   * Navigate to mood history page
   */
  goToMoodHistory(): void {
    this.router.navigate(['/patient/mood-history']);
  }

  /**
   * Get emoji for mood rating
   */
  getMoodEmoji(rating: number): string {
    if (rating <= 2) return '😢';
    if (rating <= 4) return '😟';
    if (rating <= 6) return '😐';
    if (rating <= 8) return '🙂';
    return '😄';
  }

  getCurrentDate(): string {
    const options: Intl.DateTimeFormatOptions = {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    };
    return new Date().toLocaleDateString('en-US', options);
  }
}
