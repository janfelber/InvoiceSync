import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {AxiosService} from "../axios.service";
import {ApiPaths} from "./api-paths";
import {SubscriptionRequest} from "../models/subscription-request";

@Injectable({
  providedIn: 'root'
})

export class SubscriptionService {

  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.subscription.BASE;

  constructor(
    private axiosService: AxiosService
  ) { }

  /**
   * Get user subscription details by ID
   * @returns Promise resolving to user subscription details
   */
  getUserSubscriptionDetail(): Promise<any> {
    const url = `${this.baseUrl}${ApiPaths.subscription.USER_PLAN}`;
    return this.axiosService.request('GET', url, null);
  }

  /**
   * Create a subscription session
   */
  subscribe(request: SubscriptionRequest): Promise<any> {
    const url = `${this.baseUrl}${ApiPaths.subscription.SUBSCRIBE}`;
    return this.axiosService.request('POST', url, request);
  }
}
