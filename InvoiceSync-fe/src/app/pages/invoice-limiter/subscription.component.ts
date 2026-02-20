import {Component, OnInit} from '@angular/core';
import {CommonModule} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Router, RouterLink} from "@angular/router";
import {SubscriptionService} from "../../core/services/subscription.service";
import {initFlowbite} from "flowbite";
import {MatDialogWindowComponent} from "../../shared/mat-dialog-window/mat-dialog-window.component";

@Component({
  selector: 'app-subscription',
  templateUrl: './subscription.component.html',
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterLink,
    MatDialogWindowComponent
  ],
  styleUrls: ['./subscription.component.css']
})
export class SubscriptionComponent implements OnInit {

  loadingSubscriptionInfo = false;
  subscriptionUpgradeEssentialsOpen = false
  showConfirmModal = false;


  constructor(
    private subscriptionService: SubscriptionService,
    private router: Router,
  ) {
  }

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
    this.loadingSubscriptionInfo = true;
    const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));


    Promise.all([
      this.subscriptionService.getUserSubscription(),
      delay(700)
    ])
      .then(([response]) => {
        const data = response.data;
        this.subscriptionPlan.subscription = data.subscriptionPlan
        this.subscriptionPlan.endDate = data.endDate
        this.subscriptionPlan.price = data.subscriptionPrice
        this.subscriptionPlan.features = data.features
      })
      .finally(() => {
        this.loadingSubscriptionInfo = false;
      })
  }

  getUserLimits() {
    this.subscriptionService.getUserLimits()
      .then(response => {
        this.userLimit = response.data;
      });
  }

  confirmCancelSubscription() {
    this.subscriptionService.cancelSubscription()
      .then(() => {
        this.getSubscriptionPlan();
        this.getUserLimits();
      });
  }

  openConfirmModal() {
    this.showConfirmModal = true;
  }

  closeConfirmModal() {
    this.showConfirmModal = false;
  }

  protected readonly RouterLink = RouterLink;
}
