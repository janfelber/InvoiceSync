import {Injectable} from "@angular/core";
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {AxiosService} from "../axios.service";
import {ApiPaths} from "./api-paths";
import {CompanyRequest} from "../servicesss/models/company-request";
import {SubscriptionRequest} from "../servicesss/models/SubscriptionRequest";

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

  subscribe(request: SubscriptionRequest): Promise<any> {
    return this.axiosService.request(
      'POST',
      `${this.baseUrl}${ApiPaths.subscription.SUBSCRIBE}`,
      request
    );
  }
}
