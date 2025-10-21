import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {ApiPaths} from "./api-paths";
import {ApiService} from "../auth/api";

@Injectable({
  providedIn: 'root'
})

export class StatsService {
  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.stats.BASE;

  constructor(
    private apiService: ApiService
  ) {
  }


  getBasicStatsForCompany(params: { companyId: number }): Promise<any> {
    return this.apiService.instance.get(
      `${this.baseUrl}${ApiPaths.stats.FIND_BASIC_STATS(params.companyId)}`
    )
  }

}
