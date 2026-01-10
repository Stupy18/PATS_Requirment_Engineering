import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import {AuthService} from '../auth/auth';

export const redirectGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const currentUser = authService.getCurrentUser();

  if (!currentUser) {
    // Not logged in → redirect to login
    router.navigate(['/login']);
    return false;
  }

  // Logged in → redirect to appropriate dashboard
  if (currentUser.role === 'PATIENT') {
    router.navigate(['/patient/dashboard']);
  } else if (currentUser.role === 'PSYCHOLOGIST') {
    router.navigate(['/psychologist/dashboard']);
  } else {
    router.navigate(['/login']);
  }

  return false;
};
