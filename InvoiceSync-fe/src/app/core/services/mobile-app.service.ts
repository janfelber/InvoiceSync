import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {ApiPaths} from "./api-paths";
import {AxiosService} from "../axios.service";

@Injectable({
  providedIn: 'root'
})

export class MobileAppService {

  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.qr_code.BASE;

  constructor(
    private axiosService: AxiosService
  ) { }


  generateLoginQRCode() {
    return this.axiosService.request(
      'POST', `${this.baseUrl}${ApiPaths.qr_code.GENERATE_LOGIN_QR_CODE}`, null
    )
  }

}
