import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {ApiPaths} from "./api-paths";
import {AxiosService} from "../axios.service";

@Injectable({
  providedIn: 'root'
})

export class StatsService {
  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.stats.BASE;

  constructor(
    private axiosService: AxiosService
  ) { }


  getBasicStatsForCompany(params: {companyId: number}): Promise<any> {
    return this.axiosService.request(
      'GET', `${this.baseUrl}${ApiPaths.stats.FIND_BASIC_STATS(params.companyId)}`, null
    )
  }

}
