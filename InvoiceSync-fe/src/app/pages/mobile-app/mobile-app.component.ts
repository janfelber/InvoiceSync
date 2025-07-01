import { Component, OnInit } from '@angular/core';
import {initFlowbite} from "flowbite";
import {EmailSubscribeService} from "../../core/services/emailsubscribe.service";
import {EmailSubscribeRequest} from "../../core/models/email-subscribe-request";
import {EmailSubscribeType} from "../../core/enums/email-subscribe-type";
import {FormsModule} from "@angular/forms";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-mobile-app',
  templateUrl: './mobile-app.component.html',
  imports: [
    FormsModule
  ],
  styleUrls: ['./mobile-app.component.css']
})
export class MobileAppComponent implements OnInit {
  targetDate = new Date('2025-08-30T00:00:00');
  intervalId: any;

  days: string = '00';
  hours: string = '00';
  minutes: string = '00';
  seconds: string = '00';

  email: string = '';

  constructor(
    private emailSubscribeService: EmailSubscribeService,
    private toastr: ToastrService,
  ) { }

  ngOnInit() {
    this.updateCountdown();
    this.startCountdown();
    initFlowbite();
  }

  updateCountdown() {
    const now = new Date().getTime();
    const distance = this.targetDate.getTime() - now;

    if (distance < 0) {
      clearInterval(this.intervalId);
      this.days = this.hours = this.minutes = this.seconds = '00';
      return;
    }

    const days = Math.floor(distance / (1000 * 60 * 60 * 24));
    const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((distance % (1000 * 60)) / 1000);

    this.days = String(days).padStart(2, '0');
    this.hours = String(hours).padStart(2, '0');
    this.minutes = String(minutes).padStart(2, '0');
    this.seconds = String(seconds).padStart(2, '0');
  }

  startCountdown() {
    this.intervalId = setInterval(() => {
      this.updateCountdown();
    }, 1000);
  }

  onSubscribe() {
    if (!this.email || !this.validateEmail(this.email)) {
      this.toastr.error('Zadaj platný e-mail.');
      return;
    }

    const subscribeRequest: EmailSubscribeRequest = {
      email: this.email,
      type: EmailSubscribeType.NEWSLETTER
    };

    this.emailSubscribeService.subscribe(
      subscribeRequest
    ).then(() => {
      this.toastr.success(
        'Ďakujeme! Pridali sme ťa na odber noviniek.',
        '',
        {
          timeOut: 5000,
          progressBar: true,
          progressAnimation: 'increasing',
          closeButton: true,
          positionClass: 'toast-top-right',
        }
      );
      this.email = '';
    })
      .catch((error) => {
        if (error?.response?.status === 409) {
          this.toastr.info(
            'Tento e-mail je už prihlásený.',
            '',
            {
              timeOut: 3000,
              progressBar: true,
              progressAnimation: 'increasing',
              closeButton: true,
              positionClass: 'toast-top-right',
            }
          );
        } else {
          this.toastr.error(
            'Prihlásenie zlyhalo. Skús to znova neskôr.',
            '',
            {
              timeOut: 3000,
              progressBar: true,
              progressAnimation: 'increasing',
              closeButton: true,
              positionClass: 'toast-top-right',
            }
          );
        }
      });
  }

  private validateEmail(email: string): boolean {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;
    return regex.test(email);
  }
}
