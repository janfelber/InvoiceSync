import { Component, OnInit } from '@angular/core';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../auth.service';

type ResetState = 'form' | 'success' | 'invalid-token';

@Component({
  selector: 'app-reset-password',
  imports: [CommonModule, FormsModule, RouterModule, NgOptimizedImage],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.css',
  standalone: true,
})
export class ResetPasswordComponent implements OnInit {
  state: ResetState = 'form';
  newPassword = '';
  confirmPassword = '';
  submitting = false;
  errorMessage = '';
  private token = '';

  constructor(
    private route: ActivatedRoute,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (!token) {
      this.state = 'invalid-token';
      return;
    }

    this.token = token;
  }

  submit() {
    this.errorMessage = '';

    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'Heslá sa nezhodujú.';
      return;
    }

    this.submitting = true;
    this.auth.resetPassword(this.token, this.newPassword).subscribe({
      next: () => {
        this.submitting = false;
        this.state = 'success';
      },
      error: () => {
        this.submitting = false;
        this.state = 'invalid-token';
      }
    });
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}
