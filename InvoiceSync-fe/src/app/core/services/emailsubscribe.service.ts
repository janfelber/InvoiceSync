import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {ApiPaths} from "./api-paths";
import {AxiosService} from "../axios.service";
import {EmailSubscribeRequest} from "../models/email-subscribe-request";

@Injectable({
  providedIn: 'root'
})

export class EmailSubscribeService {

  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.emailSubscribe.BASE;

  constructor(
    private axiosService: AxiosService,
  ) { }

  /**
   * Create an email subscribe record in a database
   */
  subscribe(request: EmailSubscribeRequest): Promise<any> {
    const url = `${this.baseUrl}${ApiPaths.emailSubscribe.SUBSCRIBE}`;
    return this.axiosService.request('POST', url, request);
  }
}
