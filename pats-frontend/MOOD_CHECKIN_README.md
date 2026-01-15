# Daily Emotional Check-in Feature - Frontend Implementation

## Overview
This implementation provides the frontend for the Daily Emotional Check-in feature as specified in the functional requirements FR1 (3.1.1.1 - 3.1.1.5).

## Features Implemented

### ✅ FR1.1: Emotional Rating Scale
- Patients can record their emotional state on a scale of 1 to 10
- Visual slider and button-based input methods
- Real-time emoji feedback based on rating
- Descriptive labels for each rating level

### ✅ FR1.2: Optional Text Notes
- Textarea for adding optional notes (up to 1000 characters)
- Character counter to track note length
- Notes are submitted with the emotional rating

### ✅ FR1.3: Timestamp Display
- Timestamp information shown on submission
- Timestamps displayed in mood history view
- Both date and time are shown for each entry

### ✅ FR1.4: Reminder Notifications
- Backend reminder service implemented (runs daily at 8 PM)
- Frontend checks submission status to prevent duplicate entries
- Dashboard shows pending/completed status

### ✅ FR1.5: Entry Timestamp
- Each entry is automatically timestamped on submission
- Timestamps are stored and displayed in ISO format
- Historical entries show full date and time information

## File Structure

```
pats-frontend/src/app/
├── models/
│   └── mood-entry.model.ts          # MoodEntry data model and request interface
├── services/
│   ├── mood-entry.service.ts        # Service for mood entry API calls
│   └── mood-entry.service.spec.ts   # Service tests
└── patient/
    ├── daily-checkin/               # Daily check-in component
    │   ├── daily-checkin.ts
    │   ├── daily-checkin.html
    │   ├── daily-checkin.scss
    │   └── daily-checkin.spec.ts
    ├── mood-history/                # Mood history viewer component
    │   ├── mood-history.ts
    │   ├── mood-history.html
    │   ├── mood-history.scss
    │   └── mood-history.spec.ts
    └── patient-dashboard/           # Updated dashboard with check-in integration
        ├── patient-dashboard.ts
        ├── patient-dashboard.html
        └── patient-dashboard.scss
```

## Components

### 1. Daily Check-in Component (`daily-checkin`)
**Location:** `/patient/daily-checkin`

**Features:**
- Emotional rating slider (1-10)
- Alternative button-based rating input
- Real-time emoji and label updates
- Optional notes textarea with character counter
- Validation to prevent duplicate daily submissions
- Success/error messaging
- Auto-redirect to dashboard after successful submission

**Key Methods:**
- `submitCheckin()`: Submits the daily mood entry
- `checkTodaySubmission()`: Verifies if today's entry exists
- `getEmoji()`: Returns emoji based on rating
- `onRatingChange()`: Updates rating value

### 2. Mood History Component (`mood-history`)
**Location:** `/patient/mood-history`

**Features:**
- Chronological list of all mood entries
- Visual rating display with emojis
- Timestamp for each entry
- Optional notes display
- Empty state handling
- Loading states

**Key Methods:**
- `loadMoodHistory()`: Fetches patient's mood entry history
- `getEmoji()`: Returns emoji based on rating
- `getRatingLabel()`: Returns descriptive label for rating

### 3. Patient Dashboard (Updated)
**Features:**
- Check-in status card showing today's status
- Visual indicators for completed/pending check-in
- Direct navigation to daily check-in
- Display of today's mood rating (if completed)
- Quick action buttons

## Services

### MoodEntryService
**Location:** `services/mood-entry.service.ts`

**API Endpoints:**
- `POST /api/mood/checkin` - Submit daily check-in
  - Parameters: `patientId`, `rating`, `notes` (optional)
  
- `GET /api/mood/history/{patientId}` - Get patient's mood history
  - Returns: Array of MoodEntry objects

**Helper Methods:**
- `hasCompletedTodayCheckin()`: Checks if patient has submitted today's entry

## Data Models

### MoodEntry Interface
```typescript
interface MoodEntry {
  id?: number;
  patientId: number;
  emotionalRating: number;        // 1-10 scale
  notes?: string;                  // Optional text notes
  entryTimestamp: string;          // ISO format timestamp
  createdAt?: string;
  updatedAt?: string;
}
```

### MoodEntryRequest Interface
```typescript
interface MoodEntryRequest {
  patientId: number;
  rating: number;                  // 1-10
  notes?: string;                  // Optional
}
```

## Routing

The following routes have been added:

```typescript
{
  path: 'patient/daily-checkin',
  component: DailyCheckinComponent,
  canActivate: [authGuard, roleGuard],
  data: { role: 'PATIENT' }
},
{
  path: 'patient/mood-history',
  component: MoodHistoryComponent,
  canActivate: [authGuard, roleGuard],
  data: { role: 'PATIENT' }
}
```

## User Flow

1. **Patient Dashboard View**
   - Patient sees check-in status card
   - Status shows "Pending" if not completed today
   - Status shows "Complete" with rating if already submitted

2. **Daily Check-in**
   - Click "Complete Check-in" or "Daily Emotional Check-in" button
   - Navigate to `/patient/daily-checkin`
   - Select rating (1-10) using slider or buttons
   - Optionally add notes
   - Submit check-in
   - See success message
   - Auto-redirect to dashboard

3. **Mood History**
   - Access from sidebar navigation
   - View all past mood entries
   - See ratings, timestamps, and notes
   - Create new check-in from history page

## Validation & Error Handling

- **Duplicate Prevention**: System checks if patient has already submitted today's entry
- **Rating Validation**: Ensures rating is between 1-10
- **Authentication**: All routes protected by auth guard
- **Role Verification**: Only patients can access mood entry features
- **Error Messages**: Clear feedback for failed submissions
- **Loading States**: Visual indicators during API calls

## Styling & UX

### Design Features
- Gradient background for check-in pages
- Card-based layouts
- Smooth animations and transitions
- Responsive design (mobile-friendly)
- Color-coded status indicators
- Emoji-based emotional feedback
- Clean, modern interface

### Color Scheme
- Primary: `#667eea` (Purple-blue)
- Success: `#28a745` (Green)
- Pending: `#ffc107` (Yellow)
- Error: `#dc3545` (Red)

## API Integration

### Backend Requirements
The frontend expects the following backend endpoints to be available:

1. **POST** `/api/mood/checkin`
   - Query params: `patientId`, `rating`, `notes`
   - Returns: MoodEntry object

2. **GET** `/api/mood/history/{patientId}`
   - Returns: Array of MoodEntry objects

### Configuration
API base URL is configured in the service:
```typescript
private apiUrl = 'http://localhost:8080/api/mood';
```

## Testing

Test files have been created for all new components and services:
- `daily-checkin.spec.ts`
- `mood-history.spec.ts`
- `mood-entry.service.spec.ts`

Run tests with:
```bash
ng test
```

## Future Enhancements

Potential improvements that could be added:
- Data visualization (charts/graphs of mood trends)
- Weekly/monthly mood summaries
- Export mood history to PDF
- Push notifications for reminders
- Mood insights and patterns analysis
- Integration with psychologist dashboard for viewing patient moods
- Customizable reminder times

## Notes

- The backend reminder service is scheduled to run at 8 PM daily (configured in `MoodEntryReminderService`)
- Only one check-in is allowed per day per patient
- All timestamps are in ISO format for consistency
- The system uses Angular standalone components
- Guards protect routes from unauthorized access
