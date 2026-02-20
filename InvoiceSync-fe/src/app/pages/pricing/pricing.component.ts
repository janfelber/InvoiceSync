import {Component, OnInit} from '@angular/core';
import {SubscriptionService} from "../../core/services/subscription.service";
import {initFlowbite} from "flowbite";
import {Router, RouterLink} from "@angular/router";
import {loadStripe, Stripe} from "@stripe/stripe-js";
import {environment} from "../../../environments/environment";
import {NgClass} from "@angular/common";
import {MatDialogWindowComponent} from "../../shared/mat-dialog-window/mat-dialog-window.component";

@Component({
  selector: 'app-pricing',
  imports: [
    NgClass,
    MatDialogWindowComponent,
    RouterLink
  ],
  templateUrl: './pricing.component.html',
  styleUrl: './pricing.component.css'
})
export class PricingComponent implements OnInit {

  private stripe: Stripe | null = null;
  protected currentSubscriptionPlan = ''
  protected pendingPlan: 'FREE' | 'ESSENTIALS' | 'PRO' | 'ENTERPRISE' | null = null;
  showConfirmModal = false;

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
      console.error('Stripe public key is not defined');
      return;
    }

    this.stripe = await loadStripe(stripeKey);
    if (!this.stripe) {
      console.error('Error with loading Stripe');
    }
  }

  getSubscriptionPlan() {
    this.subscriptionService.getUserSubscription().then(response => {
      this.currentSubscriptionPlan = response.data.subscriptionPlan;
    })
  }

  openConfirmModal(plan: 'FREE' | 'ESSENTIALS' | 'PRO' | 'ENTERPRISE') {
    if (plan === this.currentSubscriptionPlan) return;
    this.pendingPlan = plan;
    this.showConfirmModal = true;
  }

  private async performSubscription(plan: 'FREE' | 'ESSENTIALS' | 'PRO' | 'ENTERPRISE') {
    try {
      const response = await this.subscriptionService.subscribe(plan);

      if (plan === 'FREE') {
        this.router.navigate(['/web/subscription']);
        return;
      }

      if (this.stripe && response.data?.id) {
        await this.stripe.redirectToCheckout({sessionId: response.data.id});
      } else {
        console.error('Stripe not loaded or sessionId missing', response);
      }
    } catch (error) {
      console.error('Chyba pri Stripe subscribe:', error);
    }
  }

  confirmChange() {
    if (!this.pendingPlan) return;
    this.performSubscription(this.pendingPlan);
    this.pendingPlan = null;
    this.showConfirmModal = false;
  }

  closeModal() {
    this.showConfirmModal = false;
    this.pendingPlan = null;
  }

  get confirmationMessage(): string {
    if (this.currentSubscriptionPlan === 'NONE') {
      return `Chceš si aktivovať predplatný plán
      <strong class="font-semibold text-blue-600">${this.pendingPlan}</strong>?`;
    }

    return `Chceš zmeniť predplatné z
    <strong class="font-semibold text-blue-600">${this.currentSubscriptionPlan}</strong>
    na
    <strong class="font-semibold text-blue-600">${this.pendingPlan}</strong>?`;
  }

}
