import {Component, OnInit} from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import {AuthenticationRequest} from "../../models/authentication-request";
import {AuthenticationReponse} from "../../models/authentication-reponse";
import {CommonModule, NgOptimizedImage} from "@angular/common";
import { FormsModule } from "@angular/forms";
import {AuthenticationService} from "../../services/authentication.service";
import {Router} from "@angular/router";
import { RouterModule } from '@angular/router';
import {AuthService} from "../auth.service";

@Component({
    selector: 'app-login',
  imports: [
    MatIconModule,
    FormsModule,
    CommonModule,
    RouterModule,
    NgOptimizedImage,
  ],
    templateUrl: './login.component.html',
    styleUrl: './login.component.css',
  standalone: true,
})
export class LoginComponent {
  username = '';
  password = '';
  showPassword = false;

  constructor(private auth: AuthService, private router: Router) {}

  login() {
    this.auth.login({
      username: this.username,
      password: this.password
    }).subscribe({
      next: res => {
        const role = this.auth.getUserRole() ?? 'ROLE_USER';

        if (role === 'ROLE_ADMIN') {
          this.router.navigate(['/admin']);
        } else {
          this.router.navigate(['/home']);
        }
      },
      error: err => console.error(err)
    });
  }
}
