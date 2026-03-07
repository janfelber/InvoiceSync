import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {ApiPaths} from "./api-paths";
import {ApiService} from "../auth/api";

@Injectable({
  providedIn: 'root'
})

export class AdminService {

  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.admin.BASE;
  private featureUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.feature.BASE;

  constructor(
    private apiService: ApiService
  ) {
  }

  findAllUsers(params: { page: number; size: number }): Promise<any> {
    return this.apiService.instance.get(`${this.baseUrl}${ApiPaths.admin.USERS}?page=${params.page}&size=${params.size}`,
    );
  }

  getUserInfo(userId: string): Promise<any> {
    return this.apiService.instance.get(`${this.baseUrl}${ApiPaths.admin.USER_BY_ID(userId)}`)
  }

  getFeatures(userId: string): Promise<any> {
    return this.apiService.instance.get(`${this.featureUrl}${ApiPaths.feature.USER_BY_ID(userId)}`)
  }

  updateUserFeatures(userId: string, featureIds: any ): Promise<any> {
    return this.apiService.instance.put(
      `${this.baseUrl}${ApiPaths.admin.UPDATE_FEATURES(userId)}`,
      { featureIds }
    );
  }

  getUsersActivity(): Promise<any> {
    return this.apiService.instance.get(
      `${this.baseUrl}${ApiPaths.admin.USERS_ACTIVITY}`
    )
  }

}
