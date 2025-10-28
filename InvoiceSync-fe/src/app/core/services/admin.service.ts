import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {ApiPaths} from "./api-paths";
import {ApiService} from "../auth/api";

@Injectable({
  providedIn: 'root'
})

export class AdminService {

  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.admin.BASE;

  constructor(
    private apiService: ApiService
  ) { }

  findAllUsers(params: { page: number; size: number }): Promise<any> {
    return this.apiService.instance.get(`${this.baseUrl}${ApiPaths.admin.USERS}?page=${params.page}&size=${params.size}`,
    );
  }

  getUserInfo( userId: string ): Promise<any> {
    return this.apiService.instance.get(`${this.baseUrl}${ApiPaths.admin.USER_BY_ID(userId)}`)
  }
}
