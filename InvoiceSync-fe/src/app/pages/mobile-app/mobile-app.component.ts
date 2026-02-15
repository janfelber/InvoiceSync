import {Component, OnDestroy, OnInit} from '@angular/core';
import {initFlowbite} from "flowbite";
import {EmailSubscribeService} from "../../core/services/emailsubscribe.service";
import {EmailSubscribeRequest} from "../../core/models/email-subscribe-request";
import {EmailSubscribeType} from "../../core/enums/email-subscribe-type";
import {FormsModule} from "@angular/forms";
import {NgIf} from "@angular/common";
import {ToastrService} from "ngx-toastr";
import {QRCodeComponent} from "angularx-qrcode";
import {MobileService} from "../../core/services/mobile.service";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-mobile-app',
  templateUrl: './mobile-app.component.html',
  imports: [
    FormsModule,
    NgIf,
    QRCodeComponent,
    RouterLink
  ],
  styleUrls: ['./mobile-app.component.css']
})
export class MobileAppComponent implements OnInit, OnDestroy {
  targetDate = new Date('2026-01-01T00:00:00');
  intervalId: any;

  days: string = '00';
  hours: string = '00';
  minutes: string = '00';
  seconds: string = '00';

  qrData: string | null = null;
  qrTimeLeft: number = 0;
  qrIntervalId: any;
  qrLoading: boolean = false;
  qrHasExpired: boolean = false;

  email: string = '';

  constructor(
    private emailSubscribeService: EmailSubscribeService,
    private toastr: ToastrService,
    private mobileService: MobileService,
  ) { }

  ngOnInit() {
    this.updateCountdown();
    this.startCountdown();
    initFlowbite();
  }

  get qrMinutes(): string {
    return String(Math.floor(this.qrTimeLeft / 60)).padStart(2, '0');
  }

  get qrSeconds(): string {
    return String(this.qrTimeLeft % 60).padStart(2, '0');
  }

  get qrExpired(): boolean {
    return this.qrHasExpired;
  }

  generateLoginQrCode() {
    this.qrLoading = true;
    this.qrHasExpired = false;
    this.mobileService.generateLoginQrCode().then(result => {
      this.qrData = result.data.qrData;
      this.startQrCountdown(result.data.expiresIn ?? 60);
    }).finally(() => {
      this.qrLoading = false;
    });
  }

  startQrCountdown(seconds: number) {
    clearInterval(this.qrIntervalId);
    this.qrTimeLeft = seconds;

    this.qrIntervalId = setInterval(() => {
      this.qrTimeLeft--;
      if (this.qrTimeLeft <= 0) {
        clearInterval(this.qrIntervalId);
        this.qrData = null;
        this.qrHasExpired = true;
      }
    }, 1000);
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

  ngOnDestroy() {
    clearInterval(this.intervalId);
    clearInterval(this.qrIntervalId);
  }

  private validateEmail(email: string): boolean {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;
    return regex.test(email);
  }
}
