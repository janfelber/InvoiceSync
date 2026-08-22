import { Component } from '@angular/core';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';

@Component({
  selector: 'app-registration-success',
  imports: [CommonModule, NgOptimizedImage, RouterLink],
  templateUrl: './registration-success.component.html',
  styleUrl: './registration-success.component.css',
  standalone: true,
})
export class RegistrationSuccessComponent {
  email: string | null = null;

  constructor(route: ActivatedRoute) {
    this.email = route.snapshot.queryParamMap.get('email');
  }
}
