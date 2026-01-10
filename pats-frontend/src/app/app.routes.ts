import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login';
import { RegisterComponent } from './auth/register/register';
import { PatientDashboardComponent } from './patient/patient-dashboard/patient-dashboard';
import { PsychologistDashboardComponent } from './psychologist/psychologist-dashboard/psychologist-dashboard';
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
    path: 'psychologist/dashboard',
    component: PsychologistDashboardComponent,
    canActivate: [authGuard, roleGuard],
    data: { role: 'PSYCHOLOGIST' }
  },
  {
    path: '**',
    canActivate: [redirectGuard],
    children: []
  }
];
