import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { AuthResponse } from '../models/auth-response.model';
import { User, LoginRequest, RegisterRequest } from '../models/user.model';


function jwtDecode(token: string): any {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch (error) {
    console.error('Invalid token', error);
    return null;
  }
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    const token = this.getToken();
    if (token) {
      this.setCurrentUserFromToken(token);
    }
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        this.setToken(response.token);
        this.setCurrentUserFromToken(response.token);
        this.redirectByRole();
      })
    );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request).pipe(
      tap(response => {
        this.router.navigate(['/login'], {
          queryParams: { registered: 'true' }
        });
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  private setToken(token: string): void {
    localStorage.setItem('token', token);
  }

  private setCurrentUserFromToken(token: string): void {
    const decoded = jwtDecode(token);
    if (decoded) {
      const user: User = {
        userId: decoded.userId,
        username: decoded.sub,
        email: decoded.email,
        role: decoded.role,
        profileId: decoded.profileId
      };
      this.currentUserSubject.next(user);
    }
  }

  private redirectByRole(): void {
    const user = this.currentUserSubject.value;
    if (user) {
      if (user.role === 'PATIENT') {
        this.router.navigate(['/patient/dashboard']);
      } else if (user.role === 'PSYCHOLOGIST') {
        this.router.navigate(['/psychologist/dashboard']);
      }
    }
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }
}
