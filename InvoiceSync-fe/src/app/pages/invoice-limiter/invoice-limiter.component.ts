import {Component, OnInit} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {loadStripe} from '@stripe/stripe-js';
import {AxiosService} from "../../core/axios.service";
import { CommonModule } from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatDialogWindowComponent} from "../../shared/mat-dialog-window/mat-dialog-window.component";
import {SubscriptionModalComponent} from "../../shared/subscription-modal/subscription-modal.component";
import {Router, RouterLink} from "@angular/router";
import {SubscriptionService} from "../../core/services/subscription.service";
import {initFlowbite} from "flowbite";

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
    private subscriptionService: SubscriptionService,
    private router: Router,
    private axiosService: AxiosService
  ) {
    loadStripe('pk_test_51RLMmHLJ07OMo5e7sbeecGQ7cQqTM4Tgg48aI6rP31iTRQXUbH1aVvLRwygvObxwIEjzqiALOR75ojqAm12C9YYk00tjs1CTkq').then(stripe => {
      this.stripe = stripe;
    });
  }

  limits = [
    {
      label: 'Spracovania',
      used: 20,
      total: 50,
      color: 'blue'
    },
    {
      label: 'Vytvorené faktúry',
      used: 20,
      total: 50,
      color: 'amber'
    },
    {
      label: 'Blokované faktúry',
      used: 20,
      total: 50,
      color: 'red'
    }
  ];

  ngOnInit() {
    this.getSubscriptionPlan()
    this.getUserLimits();
    initFlowbite();
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
    usedLimit: 0,
    totalLimit: 0,
    invoiceCreateLimit: 0,
    invoiceCreateUsed: 0,
    invoiceExportLimit: 0,
    invoiceExportUsed: 0,
    receiptExportLimit: 0,
    receiptExportUsed: 0
  }

  subscriptionPlan = {
    subscription: "",
    endDate: "",
    price: "",
    features: []
  }


  goToPricing(): void {
    this.closeUpgradeEssentials()
    this.router.navigate(['/pricing']);
  }


  getSubscriptionPlan() {
    this.subscriptionService.getUserSubscriptionDetail()
    .then(response => {
      const data = response.data;
      this.subscriptionPlan.subscription = data.subscriptionPlan
      this.subscriptionPlan.endDate = data.endDate
      this.subscriptionPlan.price = data.subscriptionPrice
      this.subscriptionPlan.features = data.features
      console.log("subscription", data);
    });
  }

  getUserLimits() {
    this.axiosService.request(
      'GET',
      `/subscription/user/limit`,
      null,
    ).then(response => {
      this.userLimit = response.data;
      console.log("user limit", response.data);
    });
  }

  get usedPercentage(): number {
    if (this.userLimit.totalLimit === 0) return 0;
    return Math.round((this.userLimit.usedLimit / this.userLimit.totalLimit) * 100);
  }


  proceedToEssentials() {

    this.http.post<{ id: string }>(
      'http://localhost:8080/stripe/create-checkout-session',
      {},
    ).subscribe(async (res) => {
      if (this.stripe) {
        await this.stripe.redirectToCheckout({sessionId: res.id});
      }
    });
  }


  async subscribe(plan: 'FREE' | 'ESSENTIALS' | 'PRO' | 'ENTERPRISE'): Promise<void> {

    try {
      const response = await this.axiosService.request(
        'POST',
        'http://localhost:8080/subscription/subscribe',
        { plan },
        {
          'Content-Type': 'application/json'
        }
      );

      if (this.stripe) {
        await this.stripe.redirectToCheckout({ sessionId: response.data.id });
      }
    } catch (error) {
      console.error('Chyba pri Stripe subscribe:', error);
    }
  }

  protected readonly RouterLink = RouterLink;
}
