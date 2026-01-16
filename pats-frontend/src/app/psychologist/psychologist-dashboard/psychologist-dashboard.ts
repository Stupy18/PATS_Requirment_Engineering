import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from '../../shared/navbar/navbar';
import { AuthService } from '../../auth/auth';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-psychologist-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent],
  templateUrl: './psychologist-dashboard.html',
  styleUrls: ['./psychologist-dashboard.scss']
})
export class PsychologistDashboardComponent implements OnInit {
  currentUser: User | null = null;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  /**
   * Get current date string
   */
  getCurrentDate(): string {
    return new Date().toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  /**
   * Dummy method for non-implemented features - prevents navigation
   */
  doNothing(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    // Could add a toast notification here: "Feature coming soon!"
  }
}
