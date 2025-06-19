import {Component, OnInit} from '@angular/core';
import {SubscriptionService} from "../services/subscription.service";
import {initFlowbite} from "flowbite";
import {Router} from "@angular/router";
import {loadStripe, Stripe} from "@stripe/stripe-js";
import {environment} from "../../environments/environment";
import {SubscriptionRequest} from "../servicesss/models/subscription-request";

@Component({
  selector: 'app-pricing',
  imports: [],
  templateUrl: './pricing.component.html',
  styleUrl: './pricing.component.css'
})
export class PricingComponent implements OnInit{

  private stripe: Stripe | null = null;

  constructor(
    private subscriptionService: SubscriptionService,
    private router: Router
  ) {
    loadStripe('pk_test_51RLMmHLJ07OMo5e7sbeecGQ7cQqTM4Tgg48aI6rP31iTRQXUbH1aVvLRwygvObxwIEjzqiALOR75ojqAm12C9YYk00tjs1CTkq').then(stripe => {
      this.stripe = stripe;
    });
  }

  async ngOnInit() {
    initFlowbite();
    const stripeKey = environment.stripePublicKey;
    if (!stripeKey) {
      console.error('Stripe public key nie je definovaný v environment.ts');
      return;
    }

    this.stripe = await loadStripe(stripeKey);
    if (!this.stripe) {
      console.error('Nepodarilo sa načítať Stripe.js');
    }
  }

  async subscribe(plan: 'FREE' | 'ESSENTIALS' | 'PRO' | 'ENTERPRISE'): Promise<void> {
    try {
      const request: SubscriptionRequest = { plan };
      const response = await this.subscriptionService.subscribe(request);

      if (plan === 'FREE') {
        console.log(response.data.message); // "FREE plan activated"
        this.router.navigate(['/web/limiter']);
        return;
      }

      if (this.stripe) {
        await this.stripe.redirectToCheckout({ sessionId: response.data.id });
      } else {
        console.error('Stripe is not loaded.');
      }
    } catch (error) {
      console.error('Chyba pri Stripe subscribe:', error);
    }
  }

}
