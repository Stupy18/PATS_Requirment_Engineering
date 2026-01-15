# Daily Check-in Reminder Feature (FR1.4)

## Overview
This feature implements FR1.4: "The system shall send a reminder notification if the patient has not completed their check-in by 8:00 PM"

## Implementation

### Components Created

1. **NotificationService** (`services/notification.service.ts`)
   - Manages all notifications in the application
   - Checks if it's past 8:00 PM automatically
   - Shows reminder notifications when appropriate
   - Resets reminder flag at midnight

2. **NotificationBannerComponent** (`shared/notification-banner/`)
   - Displays notifications at the top of the page
   - Supports different notification types (info, warning, success, error)
   - Provides action buttons (e.g., "Complete Check-in")
   - Dismissible notifications

3. **Updated Patient Dashboard**
   - Integrates notification system
   - Checks if patient has completed today's check-in
   - Triggers reminder notification if criteria met

## How It Works

### Automatic Trigger (8:00 PM)
1. The NotificationService checks the current time every minute
2. If it's 8:00 PM or later, it marks that reminder time has been reached
3. When the patient dashboard loads and detects no check-in has been completed:
   - It calls `checkReminderNotification()`
   - If past 8:00 PM and reminder hasn't been shown today, it displays the notification

### Manual Testing
For testing purposes, you can manually trigger the reminder:

**Option 1: Using Browser Console**
```javascript
// Open browser console (F12) and run:
const notifService = document.querySelector('app-patient-dashboard').__ngContext__[8].notificationService;
notifService.showCheckinReminder();
```

**Option 2: Temporarily modify the reminder time**
Edit `notification.service.ts` line 13:
```typescript
// Change from 20:00 to current hour for testing
private reminderTime = { hour: 14, minute: 0 }; // 2:00 PM for testing
```

**Option 3: Use the test method**
In patient dashboard TypeScript, the `testReminder()` method can be called from the template.

## Features

### Notification Display
- ⚠️ **Warning icon** for reminder notifications
- **Yellow/orange background** to draw attention
- **"Complete Check-in" button** for direct navigation
- **Dismiss button** (X) to close the notification

### Smart Behavior
- ✅ Only shows after 8:00 PM
- ✅ Only shows if check-in hasn't been completed
- ✅ Shows only once per day
- ✅ Automatically resets at midnight
- ✅ Persists across page navigation (until dismissed)

### Notification Positioning
- Fixed position at top-right of screen
- Appears below navbar (80px from top)
- Slides in with animation
- Responsive on mobile devices

## User Flow

1. **Before 8:00 PM**: No reminder notification
2. **After 8:00 PM without check-in**: 
   - User opens patient dashboard
   - System checks for today's check-in
   - If not completed, reminder notification appears
3. **User clicks "Complete Check-in"**: Navigates to check-in page
4. **User dismisses notification**: Notification removed from view
5. **After completing check-in**: No reminder shown

## Backend Integration

The backend also has a `MoodEntryReminderService` that runs at 8:00 PM daily:
- Location: `pats-backend/service/MoodEntryReminderService.java`
- Scheduled with: `@Scheduled(cron = "0 0 20 * * *")`
- Identifies patients who haven't completed check-in
- Currently logs to console (placeholder for email/SMS integration)

### Future Enhancements
The backend service includes a TODO for:
- Email notifications
- SMS notifications
- Push notifications

## Testing Checklist

- [ ] Notification appears after 8:00 PM if no check-in completed
- [ ] Notification does NOT appear if check-in already completed
- [ ] "Complete Check-in" button navigates to check-in page
- [ ] Dismiss button removes notification
- [ ] Notification reappears on page reload (if not completed and past 8 PM)
- [ ] Notification doesn't appear multiple times in same day
- [ ] Notification appears on dashboard, check-in, and history pages
- [ ] Responsive design works on mobile devices

## Files Modified/Created

### New Files
- `models/notification.model.ts`
- `services/notification.service.ts`
- `services/notification.service.spec.ts`
- `shared/notification-banner/notification-banner.ts`
- `shared/notification-banner/notification-banner.html`
- `shared/notification-banner/notification-banner.scss`
- `shared/notification-banner/notification-banner.spec.ts`

### Modified Files
- `patient/patient-dashboard/patient-dashboard.ts` - Added notification integration
- `patient/patient-dashboard/patient-dashboard.html` - Added notification banner component
- `patient/daily-checkin/daily-checkin.ts` - Added notification banner
- `patient/daily-checkin/daily-checkin.html` - Added notification banner
- `patient/mood-history/mood-history.ts` - Added notification banner
- `patient/mood-history/mood-history.html` - Added notification banner

## Configuration

### Changing Reminder Time
Edit `notification.service.ts`:
```typescript
private reminderTime = { hour: 20, minute: 0 }; // 8:00 PM
```

### Notification Styles
Edit `notification-banner.scss` to customize:
- Colors for different notification types
- Position and sizing
- Animation effects
- Mobile responsiveness

## Notes

- The notification system is extensible for future notification types
- Notifications persist in memory until dismissed or page refresh
- The service automatically handles midnight reset
- Compatible with all patient pages (dashboard, check-in, history)
