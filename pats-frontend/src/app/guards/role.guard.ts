import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../auth/auth';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const currentUser = authService.getCurrentUser();
  const expectedRole = route.data['role'] as string;


  if (!currentUser) {
    router.navigate(['/login']);
    return false;
  }

  if (currentUser.role === expectedRole) {
    return true;
  }


  // Redirect to correct dashboard
  if (currentUser.role === 'PATIENT') {
    router.navigate(['/patient/dashboard']);
  } else if (currentUser.role === 'PSYCHOLOGIST') {
    router.navigate(['/psychologist/dashboard']);
  } else {
    router.navigate(['/login']);
  }

  return false;
};
