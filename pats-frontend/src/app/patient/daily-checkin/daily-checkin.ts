import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MoodEntryService } from '../../services/mood-entry.service';
import { AuthService } from '../../auth/auth';
import { MoodEntry, MoodEntryRequest } from '../../models/mood-entry.model';
import { User } from '../../models/user.model';
import { NavbarComponent } from '../../shared/navbar/navbar';
import { NotificationBannerComponent } from '../../shared/notification-banner/notification-banner';

@Component({
  selector: 'app-daily-checkin',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, NotificationBannerComponent],
  templateUrl: './daily-checkin.html',
  styleUrls: ['./daily-checkin.scss']
})
export class DailyCheckinComponent implements OnInit {
  currentUser: User | null = null;
  emotionalRating: number = 5;
  notes: string = '';
  isSubmitting: boolean = false;
  hasSubmittedToday: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';
  
  // For visual scale representation
  ratings = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
  ratingLabels: { [key: number]: string } = {
    1: 'Very Bad',
    2: 'Bad',
    3: 'Poor',
    4: 'Below Average',
    5: 'Average',
    6: 'Above Average',
    7: 'Good',
    8: 'Very Good',
    9: 'Great',
    10: 'Excellent'
  };

  constructor(
    private moodEntryService: MoodEntryService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe((user: User | null) => {
      this.currentUser = user;
      if (user && user.role === 'PATIENT') {
        this.checkTodaySubmission();
      }
    });
  }

  /**
   * Check if the patient has already submitted today's check-in
   */
  private checkTodaySubmission(): void {
    if (this.currentUser?.profileId) {
      this.moodEntryService.getPatientHistory(this.currentUser.profileId).subscribe({
        next: (entries: MoodEntry[]) => {
          this.hasSubmittedToday = this.moodEntryService.hasCompletedTodayCheckin(entries);
        },
        error: (error: any) => {
          console.error('Error checking today\'s submission:', error);
        }
      });
    }
  }

  /**
   * Submit the daily emotional check-in
   * Implements FR1.1, FR1.2, FR1.5
   */
  submitCheckin(): void {
    if (!this.currentUser?.profileId) {
      this.errorMessage = 'User not authenticated';
      return;
    }

    // Validate rating (FR1.1 - scale of 1 to 10)
    if (this.emotionalRating < 1 || this.emotionalRating > 10) {
      this.errorMessage = 'Please select a rating between 1 and 10';
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    const request: MoodEntryRequest = {
      patientId: this.currentUser.profileId,
      rating: this.emotionalRating,
      notes: this.notes.trim() || undefined // FR1.2 - optional notes
    };

    console.log('Submitting mood check-in:', {
      patientId: request.patientId,
      rating: request.rating,
      hasNotes: !!request.notes
    });

    this.moodEntryService.submitCheckin(request).subscribe({
      next: (entry: MoodEntry) => {
        this.successMessage = 'Your daily check-in has been submitted successfully!';
        this.hasSubmittedToday = true;
        this.isSubmitting = false;
        
        // Navigate back to dashboard after 2 seconds
        setTimeout(() => {
          this.router.navigate(['/patient/dashboard']);
        }, 2000);
      },
      error: (error: any) => {
        this.isSubmitting = false;
        console.error('Error submitting check-in:', error);
        
        // More detailed error handling
        if (error.status === 0) {
          this.errorMessage = 'Unable to connect to the server. Please ensure the backend is running.';
        } else if (error.status === 400) {
          this.errorMessage = error.error || 'Invalid check-in data. Please try again.';
        } else if (error.status === 404) {
          this.errorMessage = 'Check-in service not found. Please contact support.';
        } else if (error.error && typeof error.error === 'string') {
          this.errorMessage = error.error;
        } else if (error.error && error.error.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = 'Failed to submit check-in. Please try again.';
        }
      }
    });
  }

  /**
   * Update rating value
   */
  onRatingChange(value: number): void {
    this.emotionalRating = value;
  }

  /**
   * Get current date for display
   */
  getCurrentDate(): string {
    const options: Intl.DateTimeFormatOptions = {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    };
    return new Date().toLocaleDateString('en-US', options);
  }

  /**
   * Get emoji representation based on rating
   */
  getEmoji(rating: number): string {
    if (rating <= 2) return '�';
    if (rating <= 4) return '😔';
    if (rating <= 6) return '😐';
    if (rating <= 8) return '😊';
    return '😄';
  }

  /**
   * Navigate back to dashboard
   */
  goBack(): void {
    this.router.navigate(['/patient/dashboard']);
  }
}
