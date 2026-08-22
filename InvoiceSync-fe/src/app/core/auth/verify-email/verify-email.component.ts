import { Component, OnInit } from '@angular/core';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../auth.service';

type VerificationState = 'loading' | 'success' | 'error';

@Component({
  selector: 'app-verify-email',
  imports: [CommonModule, RouterModule, NgOptimizedImage],
  templateUrl: './verify-email.component.html',
  styleUrl: './verify-email.component.css',
  standalone: true,
})
export class VerifyEmailComponent implements OnInit {
  state: VerificationState = 'loading';

  constructor(
    private route: ActivatedRoute,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (!token) {
      this.state = 'error';
      return;
    }

    this.auth.verifyEmail(token).subscribe({
      next: () => this.state = 'success',
      error: () => this.state = 'error'
    });
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}
