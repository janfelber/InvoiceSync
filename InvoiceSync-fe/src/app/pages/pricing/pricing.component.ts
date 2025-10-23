import {Component, OnInit} from '@angular/core';
import {SubscriptionService} from "../../core/services/subscription.service";
import {initFlowbite} from "flowbite";
import {Router} from "@angular/router";
import {loadStripe, Stripe} from "@stripe/stripe-js";
import {environment} from "../../../environments/environment";
import {NgClass} from "@angular/common";

@Component({
  selector: 'app-pricing',
  imports: [
    NgClass
  ],
  templateUrl: './pricing.component.html',
  styleUrl: './pricing.component.css'
})
export class PricingComponent implements OnInit{

  private stripe: Stripe | null = null;

  constructor(
    private subscriptionService: SubscriptionService,
    private router: Router
  ) {
  }

  async ngOnInit() {
    initFlowbite();
    this.getSubscriptionPlan()
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

  protected currentSubscriptionPlan = ''

  getSubscriptionPlan() {
      this.subscriptionService.getUserSubscription().then(response => {
        this.currentSubscriptionPlan = response.data.subscriptionPlan;
        console.log("user subscription", this.currentSubscriptionPlan);
      })
  }

  async subscribeToPlan(plan: 'FREE' | 'ESSENTIALS' | 'PRO' | 'ENTERPRISE') {
    if (plan === this.currentSubscriptionPlan) {
      console.warn('Tento plán už máš aktívny.');
      return;
    }

    try {
      const response = await this.subscriptionService.subscribe(plan);

      if (plan === 'FREE') {
        this.router.navigate(['/web/limiter']);
        return;
      }

      if (this.stripe && response.data?.id) {
        await this.stripe.redirectToCheckout({ sessionId: response.data.id });
      } else {
        console.error('Stripe not loaded or sessionId missing', response);
      }
    } catch (error) {
      console.error('Chyba pri Stripe subscribe:', error);
    }
  }

}
