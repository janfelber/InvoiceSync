import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {ApiPaths} from "./api-paths";
import {ApiService} from "../auth/api";

@Injectable({
  providedIn: 'root'
})

export class MobileService {

  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.mobile.BASE;

  constructor(
    private apiService: ApiService,
  ) {
  }

  generateLoginQrCode(): Promise<any> {
    return this.apiService.instance.get(
      `${this.baseUrl}${ApiPaths.mobile.QR_GENERATE}`
    )
  }
}
