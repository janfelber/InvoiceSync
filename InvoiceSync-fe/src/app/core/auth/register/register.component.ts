import { Component } from '@angular/core';
import {RegisterRequest} from "../../models/register-request";
import { FormsModule } from "@angular/forms";
import {AuthenticationReponse} from "../../models/authentication-reponse";
import {CommonModule, NgOptimizedImage} from "@angular/common";
import {AuthenticationService} from "../../services/authentication.service";
import {Router, RouterLink} from "@angular/router";
import {VerificationRequest} from "../../models/verification-request";
import {AuthService} from "../auth.service";

@Component({
    selector: 'app-register',
  imports: [
    FormsModule,
    CommonModule,
    RouterLink,
    NgOptimizedImage
  ],
    templateUrl: './register.component.html',
    styleUrl: './register.component.css'
})
export class RegisterComponent {
  fullName = '';
  email = '';
  username = '';
  password = '';

  constructor(private auth: AuthService, private router: Router) {}

  register() {
    this.auth.register({
      fullName: this.fullName,
      username: this.username,
      password: this.password,
      email: this.email

    }).subscribe({
      next: () => console.log('User registered successfully'),
      error: err => console.error('Registration failed', err)
    });
  }
}
