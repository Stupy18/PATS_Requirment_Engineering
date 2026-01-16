import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MoodEntryService } from '../../services/mood-entry.service';
import { AuthService } from '../../auth/auth';
import { MoodEntry } from '../../models/mood-entry.model';
import { User } from '../../models/user.model';
import { NavbarComponent } from '../../shared/navbar/navbar';
import { NotificationBannerComponent } from '../../shared/notification-banner/notification-banner';

@Component({
  selector: 'app-mood-history',
  standalone: true,
  imports: [CommonModule, NavbarComponent, NotificationBannerComponent],
  templateUrl: './mood-history.html',
  styleUrls: ['./mood-history.scss']
})
export class MoodHistoryComponent implements OnInit {
  currentUser: User | null = null;
  moodEntries: MoodEntry[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  constructor(
    private moodEntryService: MoodEntryService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe((user: User | null) => {
      this.currentUser = user;
      if (user && user.role === 'PATIENT') {
        this.loadMoodHistory();
      }
    });
  }

  private loadMoodHistory(): void {
    if (this.currentUser?.profileId) {
      this.moodEntryService.getPatientHistory(this.currentUser.profileId).subscribe({
        next: (entries: MoodEntry[]) => {
          this.moodEntries = entries.sort((a: MoodEntry, b: MoodEntry) => 
            new Date(b.entryTimestamp).getTime() - new Date(a.entryTimestamp).getTime()
          );
          this.isLoading = false;
        },
        error: (error: any) => {
          console.error('Error loading mood history:', error);
          this.errorMessage = 'Failed to load mood history';
          this.isLoading = false;
        }
      });
    }
  }

  getEmoji(rating: number): string {
    if (rating <= 2) return '�';
    if (rating <= 4) return '😔';
    if (rating <= 6) return '😐';
    if (rating <= 8) return '😊';
    return '😄';
  }

  getRatingLabel(rating: number): string {
    const labels: { [key: number]: string } = {
      1: 'Very Bad', 2: 'Bad', 3: 'Poor', 4: 'Below Average',
      5: 'Average', 6: 'Above Average', 7: 'Good',
      8: 'Very Good', 9: 'Great', 10: 'Excellent'
    };
    return labels[rating] || 'Unknown';
  }

  goBack(): void {
    this.router.navigate(['/patient/dashboard']);
  }

  goToCheckin(): void {
    this.router.navigate(['/patient/daily-checkin']);
  }
}
