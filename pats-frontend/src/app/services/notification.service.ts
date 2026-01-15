import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, interval } from 'rxjs';
import { Notification } from '../models/notification.model';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notifications = new BehaviorSubject<Notification[]>([]);
  public notifications$ = this.notifications.asObservable();
  
  private reminderTime = { hour: 20, minute: 0 }; // 8:00 PM
  private hasShownTodayReminder = false;
  
  constructor() {
    // Check daily reminder time every minute
    interval(60000).subscribe(() => {
      this.checkReminderTime();
    });
    
    // Also check on initialization
    this.checkReminderTime();
    
    // Reset reminder flag at midnight
    this.scheduleReminderReset();
  }

  /**
   * Check if it's time to show the reminder (8:00 PM)
   * FR1.4: Send reminder notification if check-in not completed by 8:00 PM
   */
  private checkReminderTime(): void {
    const now = new Date();
    const currentHour = now.getHours();
    const currentMinute = now.getMinutes();
    
    // Check if it's 8:00 PM or later and we haven't shown the reminder today
    if (currentHour >= this.reminderTime.hour && 
        !this.hasShownTodayReminder &&
        currentHour < 24) {
      // The actual check for whether to show the reminder will be done
      // in the dashboard component, which has access to mood entry data
      this.hasShownTodayReminder = true;
    }
  }

  /**
   * Reset reminder flag at midnight
   */
  private scheduleReminderReset(): void {
    const now = new Date();
    const tomorrow = new Date(now);
    tomorrow.setDate(tomorrow.getDate() + 1);
    tomorrow.setHours(0, 0, 0, 0);
    
    const timeUntilMidnight = tomorrow.getTime() - now.getTime();
    
    setTimeout(() => {
      this.hasShownTodayReminder = false;
      this.scheduleReminderReset(); // Schedule next reset
    }, timeUntilMidnight);
  }

  /**
   * Add a notification to the queue
   */
  addNotification(type: 'info' | 'warning' | 'success' | 'error', message: string): void {
    const notification: Notification = {
      id: this.generateId(),
      type,
      message,
      timestamp: new Date(),
      dismissed: false
    };
    
    const current = this.notifications.value;
    this.notifications.next([notification, ...current]);
  }

  /**
   * Show check-in reminder notification
   * FR1.4: Reminder notification implementation
   */
  showCheckinReminder(): void {
    this.addNotification(
      'warning',
      'Daily Check-in Reminder: You haven\'t completed your emotional check-in today. Please take a moment to record how you\'re feeling.'
    );
  }

  /**
   * Dismiss a notification
   */
  dismissNotification(id: string): void {
    const current = this.notifications.value;
    const updated = current.map(n => 
      n.id === id ? { ...n, dismissed: true } : n
    );
    this.notifications.next(updated);
  }

  /**
   * Clear all notifications
   */
  clearAll(): void {
    this.notifications.next([]);
  }

  /**
   * Check if it's past reminder time (8:00 PM)
   */
  isPastReminderTime(): boolean {
    const now = new Date();
    const currentHour = now.getHours();
    return currentHour >= this.reminderTime.hour;
  }

  /**
   * Check if reminder should be shown today
   */
  shouldShowReminderToday(): boolean {
    return this.isPastReminderTime() && !this.hasShownTodayReminder;
  }

  /**
   * Mark that reminder has been shown for today
   */
  markReminderShown(): void {
    this.hasShownTodayReminder = true;
  }

  private generateId(): string {
    return `notif_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }
}
