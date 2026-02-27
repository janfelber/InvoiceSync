import { Component } from '@angular/core';
import {RegisterRequest} from "../../models/register-request";
import { FormsModule } from "@angular/forms";
import {AuthenticationReponse} from "../../models/authentication-reponse";
import {CommonModule, NgOptimizedImage} from "@angular/common";
import {AuthenticationService} from "../../services/authentication.service";
import {Router, RouterLink} from "@angular/router";
import {VerificationRequest} from "../../models/verification-request";
import {AuthService} from "../auth.service";
import {toast} from "ngx-sonner";

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
  phoneNumber = '';
  ico = '';
  icoTouched = false;
  showPassword = false;

  constructor(private auth: AuthService, private router: Router) {}

  private getErrorMessage(err: any): string {
    switch (err.status) {
      case 404: return 'IČO sa nenašlo v registri. Kontaktujte podporu na support@invoicesync.sk';
      case 409: {
        const field = err.error?.field;
        if (field === 'email') return 'Účet s touto e-mailovou adresou už existuje.';
        if (field === 'username') return 'Toto prihlasovacie meno už existuje.'
        return 'Účet s týmto IČO už existuje. Ak si myslíte, že ide o chybu, kontaktujte podporu na support@invoicesync.sk';
      }
      case 422: return 'IČO je už registrované na inom účte.';
      case 0:   return 'Chyba pripojenia. Skontrolujte internet.';
      default:  return 'Registrácia zlyhala. Skúste znova neskôr.';
    }
  }

  register() {
    this.auth.register({
      fullName: this.fullName,
      username: this.username,
      registrationNumber: this.ico,
      password: this.password,
      email: this.email,
      phoneNumber: this.phoneNumber

    }).subscribe({
      next: () => {
        toast.success('Účet bol úspešne vytvorený', { duration: 3000 });
        this.router.navigate(['/login']);
      },
      error: err => {
        const message = this.getErrorMessage(err);
        toast.error(message);
        console.error('Registration failed', err);
      }
    });
  }
}
