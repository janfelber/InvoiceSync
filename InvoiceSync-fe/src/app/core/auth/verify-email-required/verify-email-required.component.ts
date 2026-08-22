import { Component } from '@angular/core';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-verify-email-required',
  imports: [CommonModule, NgOptimizedImage],
  templateUrl: './verify-email-required.component.html',
  styleUrl: './verify-email-required.component.css',
  standalone: true,
})
export class VerifyEmailRequiredComponent {
  sent = false;
  sending = false;

  constructor(private auth: AuthService) {}

  resend() {
    this.sending = true;
    this.auth.resendVerificationEmail().subscribe({
      next: () => {
        this.sending = false;
        this.sent = true;
      },
      error: () => {
        this.sending = false;
      }
    });
  }

  logout() {
    this.auth.logout();
  }
}
