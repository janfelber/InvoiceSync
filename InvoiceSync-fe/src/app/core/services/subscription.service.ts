import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {ApiPaths} from "./api-paths";
import {SubscriptionRequest} from "../models/subscription-request";
import {ApiService} from "../auth/api";

@Injectable({
  providedIn: 'root'
})

export class SubscriptionService {

  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.subscription.BASE;

  constructor(
    private apiService: ApiService
  ) {
  }

  /**
   * Get user subscription details by ID
   * @returns Promise resolving to user subscription details
   */
  getUserSubscriptionDetail(): Promise<any> {
    const url = `${this.baseUrl}${ApiPaths.subscription.USER_PLAN}`;
    return this.apiService.instance.get(url);
  }

  /**
   * Create a subscription session
   */
  subscribe(request: SubscriptionRequest): Promise<any> {
    const url = `${this.baseUrl}${ApiPaths.subscription.SUBSCRIBE}`;
    return this.apiService.instance.post(url, request);
  }
}
