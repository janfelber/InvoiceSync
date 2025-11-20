import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {ApiPaths} from "./api-paths";
import {ApiService} from "../auth/api";

@Injectable({
  providedIn: 'root'
})

export class UserService {

  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.user.BASE;

  constructor(
    private apiService: ApiService
  ) { }

  getCurrentUserInfo(): Promise<any> {
    return this.apiService.instance.get(`${this.baseUrl}${ApiPaths.user.ME}`)

  }

  getCurrentUserFullName(): Promise<any> {
    return this.apiService.instance.get(`${this.baseUrl}${ApiPaths.user.FULL_NAME}`)
  }
}
