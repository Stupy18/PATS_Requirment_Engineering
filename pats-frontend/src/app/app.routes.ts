import { Routes } from '@angular/router';
import {LoginComponent} from './auth/login/login';
import {RegisterComponent} from './auth/register/register';
import {PatientDashboardComponent} from './patient/patient-dashboard/patient-dashboard';
import {PsychologistDashboardComponent} from './psychologist/psychologist-dashboard/psychologist-dashboard';


export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'patient/dashboard', component: PatientDashboardComponent },
  { path: 'psychologist/dashboard', component: PsychologistDashboardComponent }
];
