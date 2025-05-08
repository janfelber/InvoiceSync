import {Component, OnInit} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {loadStripe} from '@stripe/stripe-js';
import {AxiosService} from "../axios.service";
import { CommonModule } from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatDialogWindowComponent} from "../../shared/mat-dialog-window/mat-dialog-window.component";
import {SubscriptionModalComponent} from "../subscription-modal/subscription-modal.component";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-invoice-limiter',
  templateUrl: './invoice-limiter.component.html',
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        SubscriptionModalComponent,
        RouterLink
    ],
  styleUrls: ['./invoice-limiter.component.css']
})
export class InvoiceLimiterComponent implements OnInit{

  private stripe: any;
  subscriptionUpgradeEssentialsOpen = false


  constructor(
    private http: HttpClient,
    private axiosService: AxiosService
  ) {
    loadStripe('pk_test_51RLMmHLJ07OMo5e7sbeecGQ7cQqTM4Tgg48aI6rP31iTRQXUbH1aVvLRwygvObxwIEjzqiALOR75ojqAm12C9YYk00tjs1CTkq').then(stripe => {
      this.stripe = stripe;
    });
  }

  ngOnInit() {
    this.getSubscriptionPlan()
    this.getUserLimits();
  }

  closeUpgradeEssentials() {
    this.subscriptionUpgradeEssentialsOpen = false
    document.body.classList.remove('overflow-hidden');
  }

  openUpgradeEssentials() {
    this.subscriptionUpgradeEssentialsOpen = true
    document.body.classList.add('overflow-hidden');
  }

  userLimit = {
    totalLimit: 0,
    remainingLimit: 0
  };

  subscriptionPlan = {
    subscription: "",
    endDate: ""
  }


  getSubscriptionPlan() {
    this.axiosService.request(
      'GET',
      `/api/v1/subscription/user/plan`,
      null,
    ).then(response => {
      const data = response.data;
      this.subscriptionPlan.subscription = data.subscriptionPlan
      this.subscriptionPlan.endDate = data.endDate
      console.log("sub", data);
    });
  }

  getUserLimits() {
    this.axiosService.request(
      'GET',
      `/api/v1/subscription/user/limit`,
      null,
    ).then(response => {
      this.userLimit = response.data;
    });
  }

  get usedPercentage(): number {
    if (this.userLimit.totalLimit === 0) return 0;
    const used = this.userLimit.totalLimit - this.userLimit.remainingLimit;
    return Math.round((used / this.userLimit.totalLimit) * 100);
  }


  proceedToEssentials() {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    });

    this.http.post<{ id: string }>(
      'http://localhost:8080/stripe/create-checkout-session',
      {},
      {headers: headers}
    ).subscribe(async (res) => {
      if (this.stripe) {
        await this.stripe.redirectToCheckout({sessionId: res.id});
      }
    });
  }


  premium() {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    });

    this.http.post<{ id: string }>(
      'http://localhost:8080/stripe/premium',
      {},
      {headers: headers}
    ).subscribe(async (res) => {
      if (this.stripe) {
        await this.stripe.redirectToCheckout({sessionId: res.id});
      }
    });
  }
}
