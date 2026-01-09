import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, ActivatedRoute } from '@angular/router';
import {AuthService} from '../auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  errorMessage = '';
  loading = false;
  showSuccessMessage = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private route: ActivatedRoute
  ) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    // Check if redirected from registration
    this.route.queryParams.subscribe(params => {
      if (params['registered'] === 'true') {
        this.showSuccessMessage = true;
        // Hide message after 5 seconds
        setTimeout(() => {
          this.showSuccessMessage = false;
        }, 5000);
      }
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.loading = true;
      this.errorMessage = '';

      this.authService.login(this.loginForm.value).subscribe({
        next: () => {
          this.loading = false;
        },
        error: (error) => {
          this.loading = false;
          this.errorMessage = 'Invalid username or password';
          console.error('Login error:', error);
        }
      });
    }
  }
}
