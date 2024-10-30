import { Component } from '@angular/core';
import { MatFormField } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatLabel } from '@angular/material/form-field';
import {AuthenticationRequest} from "../models/authentication-request";
import {AuthenticationReponse} from "../models/authentication-reponse";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import {AuthenticationService} from "../services/authentication.service";
import {Router} from "@angular/router";
import {VerificationRequest} from "../models/verification-request";
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    MatFormField,
    MatIconModule,
    MatLabel,
    FormsModule,
    CommonModule,
    RouterModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  authRequest: AuthenticationRequest = {};
  otpCode: any;
  authResponse: AuthenticationReponse = {};

  constructor(
    private authService: AuthenticationService,
    private router: Router
  ) {
  }

  authenticate() {
    this.authService.login(this.authRequest)
      .subscribe(
        {
          next: (response) => {
            this.authResponse = response;
            if (!this.authResponse.mfaEnabled) {
              localStorage.setItem('token', this.authResponse.access_token as string);
              this.router.navigate(['home']);
            }
          }
        }
      );
  }

  verifyCode() {
    const verifyRequest: VerificationRequest = {
      login: this.authRequest.login,
      code: this.otpCode
    };
    this.authService.verifyCode(verifyRequest)
      .subscribe({
        next: (response) => {
            localStorage.setItem('token', response.access_token as string);
            console.log(response.access_token)
            this.router.navigate(['welcome']);
        }
      })
  }
}
