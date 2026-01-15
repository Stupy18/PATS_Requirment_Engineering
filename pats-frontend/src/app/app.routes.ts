import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login';
import { RegisterComponent } from './auth/register/register';
import { PatientDashboardComponent } from './patient/patient-dashboard/patient-dashboard';
import { DailyCheckinComponent } from './patient/daily-checkin/daily-checkin';
import { MoodHistoryComponent } from './patient/mood-history/mood-history';
import { PsychologistDashboardComponent } from './psychologist/psychologist-dashboard/psychologist-dashboard';
import { BookAppointmentComponent } from './appointment/book-appointment/book-appointment.component';
import { AvailabilityScheduleComponent } from './appointment/availability-schedule/availability-schedule.component';
import { AppointmentHistoryComponent } from './appointment/appointment-history/appointment-history.component';
import { authGuard } from './guards/auth-guard';
import { roleGuard } from './guards/role.guard';
import { redirectGuard } from './guards/redirect.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: 'patient/dashboard',
    component: PatientDashboardComponent,
    canActivate: [authGuard, roleGuard],
    data: { role: 'PATIENT' }
  },
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
  },
  {
    path: 'psychologist/dashboard',
    component: PsychologistDashboardComponent,
    canActivate: [authGuard, roleGuard],
    data: { role: 'PSYCHOLOGIST' }
  },
  {
    path: 'appointments/book',
    component: BookAppointmentComponent,
    canActivate: [authGuard, roleGuard],
    data: { role: 'PATIENT' }
  },
  {
    path: 'appointments/availability',
    component: AvailabilityScheduleComponent,
    canActivate: [authGuard, roleGuard],
    data: { role: 'PSYCHOLOGIST' }
  },
  {
    path: 'appointments/history',
    component: AppointmentHistoryComponent,
    canActivate: [authGuard, roleGuard],
    data: { role: 'PATIENT' }
  },
  {
    path: '**',
    canActivate: [redirectGuard],
    children: []
  }
];
