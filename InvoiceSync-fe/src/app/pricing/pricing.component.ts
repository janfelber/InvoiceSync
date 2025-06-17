import {Component, OnInit} from '@angular/core';
import {SubscriptionService} from "../services/subscription.service";
import {initFlowbite} from "flowbite";
import {Router} from "@angular/router";
import {loadStripe} from "@stripe/stripe-js";

@Component({
  selector: 'app-pricing',
  imports: [],
  templateUrl: './pricing.component.html',
  styleUrl: './pricing.component.css'
})
export class PricingComponent implements OnInit{

  private stripe: any;

  constructor(
    private subscriptionService: SubscriptionService,
    private router: Router
  ) {
    loadStripe('pk_test_51RLMmHLJ07OMo5e7sbeecGQ7cQqTM4Tgg48aI6rP31iTRQXUbH1aVvLRwygvObxwIEjzqiALOR75ojqAm12C9YYk00tjs1CTkq').then(stripe => {
      this.stripe = stripe;
    });
  }

  ngOnInit(): void {
    initFlowbite();
  }

  onChooseSubscription(subscription: string) {
    this.subscriptionService.subscribe({ plan: subscription }).then(async (res) => {
      if (this.stripe) {
        await this.stripe.redirectToCheckout({ sessionId: res.id }); // res.id musí byť session ID
      }
    });
  }

}
