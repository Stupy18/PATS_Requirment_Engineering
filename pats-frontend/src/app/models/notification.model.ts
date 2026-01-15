export interface Notification {
  id: string;
  type: 'info' | 'warning' | 'success' | 'error';
  message: string;
  timestamp: Date;
  dismissed: boolean;
}

export interface NotificationSettings {
  reminderTime: string; // Format: "HH:mm" (24-hour)
  enableReminders: boolean;
}
