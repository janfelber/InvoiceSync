import { Component } from '@angular/core';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-forgot-password',
  imports: [CommonModule, FormsModule, RouterModule, NgOptimizedImage],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css',
  standalone: true,
})
export class ForgotPasswordComponent {
  email = '';
  sending = false;
  sent = false;

  constructor(private auth: AuthService) {}

  submit() {
    this.sending = true;
    this.auth.forgotPassword(this.email).subscribe({
      next: () => {
        this.sending = false;
        this.sent = true;
      },
      error: () => {
        this.sending = false;
        this.sent = true;
      }
    });
  }
}
