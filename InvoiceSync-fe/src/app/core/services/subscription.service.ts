import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {ApiPaths} from "./api-paths";
import {ApiService} from "../auth/api";
import {SubscriptionRequest} from "../models/subscription-request";

@Injectable({
  providedIn: 'root'
})

export class SubscriptionService {

  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.subscription.BASE;

  constructor(
    private apiService: ApiService
  ) { }


  getUserSubscription(): Promise<any> {
    return this.apiService.instance.get(`${this.baseUrl}${ApiPaths.subscription.USER_PLAN}`);
  }

  getUserLimits(): Promise<any> {
    return this.apiService.instance.get(`${this.baseUrl}${ApiPaths.subscription.USER_LIMITS}`);
  }

  subscribe(plan: "FREE" | "ESSENTIALS" | "PRO" | "ENTERPRISE") {
    return this.apiService.instance.post(`${this.baseUrl}${ApiPaths.subscription.SUBSCRIBE}`, {plan});
  }

  cancelSubscription(): Promise<any> {
    return this.apiService.instance.post(`${this.baseUrl}${ApiPaths.subscription.CANCEL}`);
  }

  getUserDefaultCard(): Promise<any> {
    return this.apiService.instance.get(`${this.baseUrl}${ApiPaths.subscription.GET_USER_DEFAULT_CARD}`)
  }

  getUserBillingHistory(): Promise<any> {
    return this.apiService.instance.get(`${this.baseUrl}${ApiPaths.subscription.GET_USER_BILLING_HISTORY}`)
  }

}
