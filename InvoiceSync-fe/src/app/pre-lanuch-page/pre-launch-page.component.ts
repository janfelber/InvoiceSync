import { Component } from '@angular/core';
import {NgIf, NgOptimizedImage} from "@angular/common";
import {EmailSubscribeService} from "../services/email-subscribe";

@Component({
  selector: 'app-pre-launch-page',
  imports: [
    NgOptimizedImage,
    NgIf
  ],
  templateUrl: './pre-launch-page.component.html',
  styleUrl: './pre-launch-page.component.css'
})
export class PreLaunchPageComponent {
  constructor(private companyService: EmailSubscribeService) {}

  message: string | null = null;

  onSubmit(email: string) {
    const form = new FormData();
    form.append('email', email);
    form.append('subscribeType', 'Launch Subscribe')

    this.companyService.subscribeToNews(form).then(() => {
      this.message = 'Boli ste úspešne prihlásený na odber.';
    }).catch(err => {
      console.error('Chyba pri odosielaní formulára:', err);
    });
  }

}
