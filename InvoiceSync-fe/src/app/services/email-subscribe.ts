import {Injectable} from "@angular/core";
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {AxiosService} from "../axios.service";

@Injectable({
  providedIn: 'root'
})

export class EmailSubscribeService {

  private baseUrl = environment.apiUrl + '/api/v1/email/subscribe';

  constructor(
    private axiosService: AxiosService
  ) { }

  subscribeToNews(form: FormData): Promise<any> {
    return this.axiosService.request(
      'POST',
      `${this.baseUrl}/news`,
      form
    );
  }
}
