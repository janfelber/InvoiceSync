import { Component } from '@angular/core';
import {RegisterRequest} from "../models/register-request";
import { FormsModule } from "@angular/forms";
import {AuthenticationReponse} from "../models/authentication-reponse";
import { CommonModule } from "@angular/common";
import {AuthenticationService} from "../services/authentication.service";
import {Router, RouterLink} from "@angular/router";
import {VerificationRequest} from "../models/verification-request";

@Component({
  selector: 'app-register',
  standalone: true,
    imports: [
        FormsModule,
        CommonModule,
        RouterLink
    ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  registerRequest: RegisterRequest = {};
  authResponse: AuthenticationReponse = {};
  message: string = '';
  otpCode: string = '';

  constructor(
    private authService: AuthenticationService,
    private router: Router
  ) {
  }

  registerUser() {
    this.message = '';
    this.authService.register(this.registerRequest)
      .subscribe({
        next: (response) => {
          if (response) {
            this.authResponse = response;
          } else {
            // inform the user
            this.message = 'Account created successfully\nYou will be redirected to the Login page in 3 seconds';
            setTimeout(() => {
              this.router.navigate(['login']);
            }, 3000)
          }
        }
      });
  }

  verifyTfa() {
    this.message = '';
    const verifyRequest: VerificationRequest = {
      login: this.registerRequest.login,
      code: this.otpCode
    };
    this.authService.verifyCode(verifyRequest)
      .subscribe({
        next: (response) => {
          this.message = 'Account created successfully\nYou will be redirected to the welcome page in 3 seconds';
          setTimeout(() => {
            localStorage.setItem('token', response.access_token as string);
            this.router.navigate(['welcome']);
          }, 3000)
        }
      });
  }
}
